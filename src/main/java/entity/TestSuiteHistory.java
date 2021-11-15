package entity;

import java.util.Date;

public class TestSuiteHistory {

    private Date prevFailure;

    private Date prevExecution;

    private boolean hasFailed;

    public boolean isHasFailed() {
        return hasFailed;
    }

    public void setHasFailed(boolean hasFailed) {
        this.hasFailed = hasFailed;
    }

    public Date getPrevFailure() {
        return prevFailure;
    }

    public void setPrevFailure(Date prevFailure) {
        this.prevFailure = prevFailure;
    }

    public Date getPrevExecution() {
        return prevExecution;
    }

    public void setPrevExecution(Date prevExecution) {
        this.prevExecution = prevExecution;
    }
}

