package chiroito.nasne.io;

import java.util.Collection;
import java.util.List;

/**
 * Nasne監視システムのIO結果
 * @author Chihiro.Ito
 */
public class NasneMonitorIOResult {
    
    private final List<Object> successEvents;
    private final List<Object> errorEvents;
    private final String errorReason;
    private final Exception errorException;


    public NasneMonitorIOResult(List<Object> successEvents, List<Object> errorEvents, String errorReason,
                         Exception errorException) {
        this.successEvents = successEvents;
        this.errorEvents = errorEvents;
        this.errorReason = errorReason;
        this.errorException = errorException;
    }

    public List<Object> getSuccessEvents() {
        return successEvents;
    }

    public List<Object> getErrorEvents() {
        return errorEvents;
    }

    public Exception getErrorException() {
        return errorException;
    }

    public String getErrorReason() {
        return errorReason;
    }
}