package chiroito.nasne.io;

import chiroito.nasne.iface.bean.NasneHddBean;

import java.util.Collection;

/**
 * HDD情報を格納するためのインターフェース
 * @author Chihiro.Ito
 */
public interface StoreHddVolume {

    /**
     * HDD 情報を格納する
     * @param hdds 格納するHDD情報
     * @return 格納結果
     */
    public NasneMonitorIOResult store(Collection<NasneHddBean> hdds);
}
