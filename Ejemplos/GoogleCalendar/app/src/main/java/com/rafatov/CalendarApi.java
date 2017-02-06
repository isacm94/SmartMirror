package com.rafatov;

import com.rafatov.Clases.Calendar;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Rafa on 05/02/2017.
 */

public interface CalendarApi {

    @GET("calendar/v3/calendars/{id}/events")
    Call<Calendar> getEvents(@Path("id")String idCalendar);
}
