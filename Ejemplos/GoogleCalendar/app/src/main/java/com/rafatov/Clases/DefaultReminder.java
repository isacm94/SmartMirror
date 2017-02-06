package com.rafatov.Clases;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafa on 05/02/2017.
 */

public class DefaultReminder {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("minutes")
    @Expose
    private Integer minutes;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

}
