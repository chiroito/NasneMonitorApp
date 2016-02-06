package chiroito.nasne.io.hdd;

import chiroito.nasne.iface.bean.NasneHddBean;


import chiroito.nasne.io.NasneMonitorIOResult;

import com.librato.metrics.DefaultHttpPoster;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;

import java.util.List;

import java.util.Properties;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

public class StoreHddVolumeLibratoTest {
    public StoreHddVolumeLibratoTest() {
    }

    /**
     * @see StoreHddVolumeLibrato#store(java.util.Collection<chiroito.nasne.common.bean.NasneHddBean>)
     */
    @Test
    public void failCase() {

        StoreHddVolumeLibrato storeHddVolume = new StoreHddVolumeLibrato();
        storeHddVolume.setPoster(new DefaultHttpPoster("http://localhost/", "DummyMail", "DummyToken"));

        List<NasneHddBean> events = new ArrayList<>();
        events.add(createBean("a"));
        events.add(createBean("b"));

        NasneMonitorIOResult result = storeHddVolume.store(events);
        Assert.assertTrue(result.getErrorException() != null);
        Assert.assertEquals(0, result.getSuccessEvents().size());
        Assert.assertEquals(2, result.getErrorEvents().size());
    }

    @Test
    public void successCase() {

        StoreHddVolumeLibrato storeHddVolume = new StoreHddVolumeLibrato();
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("librato.properties")) {
            Properties prop = new Properties();
            prop.load(is);
            String email = prop.getProperty("email");
            String token = prop.getProperty("apiToken");
            storeHddVolume.setPoster(new DefaultHttpPoster("https://metrics-api.librato.com/v1/metrics", email, token,15000,15000));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot read librato.properties");
            Assert.fail();
        }

        List<NasneHddBean> events = new ArrayList<>();
        events.add(createBean("a"));
        events.add(createBean("b"));

        NasneMonitorIOResult result = storeHddVolume.store(events);
        Assert.assertEquals(null, result.getErrorException());
        result.getErrorException().printStackTrace();
        Assert.assertEquals(2, result.getSuccessEvents().size());
        Assert.assertEquals(0, result.getErrorEvents().size());
    }

    private static final NasneHddBean createBean(String serialNumber) {
        NasneHddBean bean = new NasneHddBean();
        bean.setSerialNumber(serialNumber);
        return bean;
    }
}
