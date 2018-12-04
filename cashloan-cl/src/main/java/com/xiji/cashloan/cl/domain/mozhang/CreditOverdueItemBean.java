package com.xiji.cashloan.cl.domain.mozhang;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author wnb
 * @date 2018/12/03
 * @version 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditOverdueItemBean {
    /**
     * overdue_times : 0
     * overdue_months : 0
     */

    @JsonProperty("overdue_times")
    private int overdueTimes;
    @JsonProperty("overdue_months")
    private int overdueMonths;

    public int getOverdueTimes() {
        return overdueTimes;
    }

    public void setOverdueTimes(int overdueTimes) {
        this.overdueTimes = overdueTimes;
    }

    public int getOverdueMonths() {
        return overdueMonths;
    }

    public void setOverdueMonths(int overdueMonths) {
        this.overdueMonths = overdueMonths;
    }
}
