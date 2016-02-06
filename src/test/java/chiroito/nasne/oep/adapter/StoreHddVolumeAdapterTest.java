package chiroito.nasne.oep.adapter;

import chiroito.nasne.iface.bean.NasneHddBean;
import chiroito.nasne.io.NasneMonitorIOException;
import chiroito.nasne.io.NasneMonitorIOResult;
import chiroito.nasne.io.StoreHddVolume;

import com.bea.wlevs.ede.api.EventRejectedException;

import java.util.ArrayList;
import java.util.Collection;

import java.util.Collections;
import java.util.List;

import java.util.stream.Collectors;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

public class StoreHddVolumeAdapterTest {
    public StoreHddVolumeAdapterTest() {
    }

    /**
     * @see StoreHddVolumeAdapter#onInsertEvents(java.util.Collection<java.lang.Object>)
     */
    @Test
    public void testSuccessOnInsertEvents() {
        StoreHddVolumeAdapter adapter = new StoreHddVolumeAdapter();
        adapter.setStoreHddVolume(new StoreHddVolumeSuccessMock());

        List<Object> events = new ArrayList<>();
        events.add(createBean("a"));
        events.add(createBean("b"));
        adapter.onInsertEvents(events);
    }

    @Test
    public void testFailOnInsertEvents() {
        StoreHddVolumeAdapter adapter = new StoreHddVolumeAdapter();
        adapter.setStoreHddVolume(new StoreHddVolumeFailMock());

        List<Object> events = new ArrayList<>();
        events.add(createBean("a"));
        events.add(createBean("b"));
        try {
            adapter.onInsertEvents(events);
            Assert.fail();
        } catch (EventRejectedException e) {
            List<Object> rejectedEvents = e.getRejectedEvents();
            Assert.assertEquals("a", ((NasneHddBean) rejectedEvents.get(0)).getSerialNumber());
            Assert.assertEquals("b", ((NasneHddBean) rejectedEvents.get(1)).getSerialNumber());
        } catch (Throwable t) {
            Assert.fail();
        }
    }


    private static final NasneHddBean createBean(String serialNumber) {
        NasneHddBean bean = new NasneHddBean();
        bean.setSerialNumber(serialNumber);
        return bean;
    }

    class StoreHddVolumeSuccessMock implements StoreHddVolume {

        @Override
        public NasneMonitorIOResult store(Collection<NasneHddBean> hdds) {
            // TODO このメソッドを実装
            return new NasneMonitorIOResult(hdds.stream().collect(Collectors.toList()), Collections.emptyList(), null,
                                            null);
        }
    }

    class StoreHddVolumeFailMock implements StoreHddVolume {

        @Override
        public NasneMonitorIOResult store(Collection<NasneHddBean> hdds) {
            NasneMonitorIOException failedCause = new NasneMonitorIOException();
            return new NasneMonitorIOResult(Collections.emptyList(), hdds.stream().collect(Collectors.toList()), "null",
                                            failedCause);
        }
    }
}
