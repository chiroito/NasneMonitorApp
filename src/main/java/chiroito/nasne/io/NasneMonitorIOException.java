package chiroito.nasne.io;

import java.io.IOException;

/**
 * Nasne監視システム用のI/O例外
 * @author Chihiro.Ito
 */
public class NasneMonitorIOException extends IOException {
    
    public NasneMonitorIOException(Throwable throwable) {
        super(throwable);
    }

    public NasneMonitorIOException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public NasneMonitorIOException(String string) {
        super(string);
    }

    public NasneMonitorIOException() {
        super();
    }
}
