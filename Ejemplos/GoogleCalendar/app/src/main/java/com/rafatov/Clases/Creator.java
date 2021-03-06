package com.rafatov.Clases;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafa on 05/02/2017.
 */

public class Creator {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("displayName")
    @Expose
    private String displayName;
    @SerializedName("self")
    @Expose
    private Boolean self;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getSelf() {
        return self;
    }

    public void setSelf(Boolean self) {
        this.self = self;
    }
}
