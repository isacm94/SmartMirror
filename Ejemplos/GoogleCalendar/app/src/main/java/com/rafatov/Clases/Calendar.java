package com.rafatov.Clases;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rafa on 05/02/2017.
 */

public class Calendar {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("etag")
    @Expose
    private String etag;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("timeZone")
    @Expose
    private String timeZone;
    @SerializedName("accessRole")
    @Expose
    private String accessRole;
    @SerializedName("defaultReminders")
    @Expose
    private List<DefaultReminder> defaultReminders = null;
    @SerializedName("nextSyncToken")
    @Expose
    private String nextSyncToken;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getAccessRole() {
        return accessRole;
    }

    public void setAccessRole(String accessRole) {
        this.accessRole = accessRole;
    }

    public List<DefaultReminder> getDefaultReminders() {
        return defaultReminders;
    }

    public void setDefaultReminders(List<DefaultReminder> defaultReminders) {
        this.defaultReminders = defaultReminders;
    }

    public String getNextSyncToken() {
        return nextSyncToken;
    }

    public void setNextSyncToken(String nextSyncToken) {
        this.nextSyncToken = nextSyncToken;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
