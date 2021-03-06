package com.rafatov.Clases;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafa on 05/02/2017.
 */

public class End {

    @SerializedName("dateTime")
    @Expose
    private String dateTime;
    @SerializedName("timeZone")
    @Expose
    private String timeZone;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
