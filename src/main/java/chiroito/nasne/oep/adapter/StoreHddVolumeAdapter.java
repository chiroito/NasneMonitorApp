package chiroito.nasne.oep.adapter;

import chiroito.nasne.iface.bean.NasneHddBean;

import chiroito.nasne.io.NasneMonitorIOResult;
import chiroito.nasne.io.StoreHddVolume;

import com.bea.wlevs.ede.api.BatchStreamSink;
import com.bea.wlevs.ede.api.EventRejectedException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HDD情報を格納するOSXのアダプタ
 * @author Chihiro.Ito
 */
public class StoreHddVolumeAdapter implements BatchStreamSink {

    /**
     * HDD 情報を格納を担当する
     */
    private StoreHddVolume storeHddVolume;

    public StoreHddVolumeAdapter() {
        super();
    }

    @Override
    public void onInsertEvents(Collection<Object> events) throws EventRejectedException {

        // イベントとしてきた HDD 情報を格納する
        List<NasneHddBean> hddList =
            events.stream().filter(event -> event instanceof
                                   NasneHddBean).map(event -> (NasneHddBean) event).collect(Collectors.toList());
        NasneMonitorIOResult result = this.storeHddVolume.store(hddList);

        // 格納に失敗していたら失敗した HDD 情報を使って例外を投げる
        if (result.getErrorEvents().size() > 0) {
            EventRejectedException e =
                new EventRejectedException(EventRejectedException.Reason.LARGE_BATCH, result.getErrorEvents(),
                                           result.getErrorReason());
            e.addSuppressed(result.getErrorException());
            throw e;
        }
    }

    @Override
    public void onInsertEvent(Object event) throws EventRejectedException {
        // イベントが1つ来たときもバッチ用の処理を流用する
        List<Object> events = Collections.singletonList(event);
        this.onInsertEvents(events);
    }

    public void setStoreHddVolume(StoreHddVolume storeHddVolume) {
        this.storeHddVolume = storeHddVolume;
    }
}
