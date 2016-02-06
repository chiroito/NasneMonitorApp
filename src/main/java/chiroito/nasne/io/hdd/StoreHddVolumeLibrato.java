package chiroito.nasne.io.hdd;

import chiroito.nasne.iface.bean.NasneHddBean;

import chiroito.nasne.io.NasneMonitorIOException;
import chiroito.nasne.io.NasneMonitorIOResult;
import chiroito.nasne.io.StoreHddVolume;

import com.bea.wlevs.ede.api.DisposableBean;
import com.bea.wlevs.ede.api.InitializingBean;

import com.librato.metrics.BatchResult;
import com.librato.metrics.DefaultHttpPoster;
import com.librato.metrics.HttpPoster;
import com.librato.metrics.LibratoBatch;

import com.librato.metrics.PostResult;

import com.librato.metrics.Sanitizer;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * HDD情報をLibratoへ格納するクラス
 * @author Chihiro.Ito
 */
public class StoreHddVolumeLibrato implements StoreHddVolume, InitializingBean, DisposableBean {

    private static final String LIBRATO_API_URL = "https://metrics-api.librato.com/v1/metrics";

    private String email;
    private String apiToken;
    private String propertyFilePath;
    private long timeout = 5;
    private String agentIdentifier = "NasneMonitor";
    private String url = LIBRATO_API_URL;

    private HttpPoster poster;

    public StoreHddVolumeLibrato() {
        super();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // ファイルが指定されていたらファイルに保存されている値でメールとトークンを上書きする
        if (this.propertyFilePath != null) {
            try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(this.propertyFilePath)) {
                Properties prop = new Properties();
                prop.load(is);
                this.setEmail(prop.getProperty("email"));
                this.setApiToken(prop.getProperty("apiToken"));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Cannot read " + this.propertyFilePath);
            }
        }

        this.poster = new DefaultHttpPoster(this.url, this.email, this.apiToken);
    }

    @Override
    public void destroy() throws Exception {
        this.poster.close();
    }

    @Override
    public NasneMonitorIOResult store(Collection<NasneHddBean> events) {

        // データの送信
        LibratoBatch batch =
            new LibratoBatch(24, Sanitizer.LAST_PASS, this.timeout, TimeUnit.SECONDS, this.agentIdentifier,
                             this.poster);
        for (NasneHddBean hdd : events) {
            batch.addGaugeMeasurement("FreeVolumeSize", hdd.getSerialNumber(), hdd.getFreeVolumeSize());
            batch.addGaugeMeasurement("TotalVolumeSize", hdd.getSerialNumber(), hdd.getTotalVolumeSize());
            batch.addGaugeMeasurement("UsedVolumeSize", hdd.getSerialNumber(), hdd.getUsedVolumeSize());
        }
        BatchResult batchResult = batch.post("FreeVolumeSize");

        return this.createResult(events, batchResult);
    }

    /**
     * Librato の結果を Nasne 監視システム用の結果に変換する
     * @param hdds ポストしたHDD情報
     * @param batchResult Librato へ通信した結果
     * @return Nasne 監視システム用の送信結果
     */
    private NasneMonitorIOResult createResult(Collection<NasneHddBean> hdds, BatchResult batchResult) {

        // 失敗したポストがなければ全て成功
        if (batchResult.getFailedPosts().size() == 0) {
            NasneMonitorIOResult result =
                new NasneMonitorIOResult(hdds.stream().collect(Collectors.toList()), Collections.emptyList(), null,
                                         null);
            return result;
        }

        // 失敗したポストは失敗したイベントとして取得する
        StringBuilder reasonDesc = new StringBuilder();
        List<Object> errorHdds = new ArrayList<>(hdds.size());
        NasneMonitorIOException failedCause = new NasneMonitorIOException();
        Map<String, NasneHddBean> successHddMap =
            hdds.stream().collect(Collectors.toMap(h -> h.getSerialNumber(), h -> h));

        for (PostResult failedPost : batchResult.getFailedPosts()) {

            List<Map<String, String>> gaugesPayloads = (List<Map<String, String>>) failedPost.getData().get("gauges");
            gaugesPayloads.stream().map(p -> p.get("name")).map(successHddMap::remove).filter(h -> h !=
                                                                                              null).forEach(errorHdds::add);

            failedCause.addSuppressed(failedPost.getException());
            reasonDesc.append(failedPost.toString()).append(", ");
        }

        // 残ったHDDを成功したイベントとする
        List<Object> successEvents = successHddMap.values().stream().collect(Collectors.toList());

        //
        NasneMonitorIOResult result =
            new NasneMonitorIOResult(successEvents, errorHdds, reasonDesc.toString(), failedCause);
        return result;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setAgentIdentifier(String agentIdentifier) {
        this.agentIdentifier = agentIdentifier;
    }

    public void setPropertyFilePath(String propertyFilePath) {
        this.propertyFilePath = propertyFilePath;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPoster(HttpPoster poster) {
        this.poster = poster;
    }
}
