package com.rafatov.Clases;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafa on 05/02/2017.
 */

public class Reminders {

    @SerializedName("useDefault")
    @Expose
    private Boolean useDefault;

    public Boolean getUseDefault() {
        return useDefault;
    }

    public void setUseDefault(Boolean useDefault) {
        this.useDefault = useDefault;
    }
}
