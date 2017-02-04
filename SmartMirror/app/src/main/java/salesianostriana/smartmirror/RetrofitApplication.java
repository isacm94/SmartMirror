package salesianostriana.smartmirror;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import salesianostriana.smartmirror.Interfaces.IOpenWeatherAPI;

/**
 * Created by Isabel on 08/12/2016.
 */

public class RetrofitApplication extends Application {

    private static Retrofit retrofitGooglePlaces, retrofitOpenWeather;

    @Override
    public void onCreate() {
        super.onCreate();

        //********* GOOGLE PLACES API ***********
        /*Gson gsonPlaces = new GsonBuilder()
                .create();

        retrofitGooglePlaces = new Retrofit.Builder()
                .baseUrl(IGooglePlacesAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gsonPlaces))
                .client(initHttpGooglePlaces())
                .build();*/

        //********* OPEN WEATHER API ***********
        JodaTimeAndroid.init(this);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .create();

        retrofitOpenWeather = new Retrofit.Builder()
                .baseUrl(IOpenWeatherAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(initHttpOpenWeather())
                .build();

    }

    //********* GOOGLE PLACES API ***********
    /*private static OkHttpClient initHttpGooglePlaces() {

        Interceptor interceptor = new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Interceptor.Chain chain) throws IOException {

                Request original = chain.request();

                HttpUrl originalHttpUrl = original.httpUrl();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("key", IGooglePlacesAPI.KEY_GOOGLE_PLACE_API)
                        .build();

                Request newRequest = chain.request().newBuilder()
                        .url(url)
                        .build();

                return chain.proceed(newRequest);
            }
        };

        OkHttpClient client = new OkHttpClient();

        client.interceptors().add(interceptor);

        return client;
    }*/

    //********* OPEN WEATHER API ***********
    private static OkHttpClient initHttpOpenWeather() {

        Interceptor interceptor = new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Interceptor.Chain chain) throws IOException {

                Request original = chain.request();

                HttpUrl originalHttpUrl = original.httpUrl();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("units", "metric")
                        .addQueryParameter("lang", "es")
                        .addQueryParameter("appid", IOpenWeatherAPI.KEY_OPEN_WEATHER_API)
                        .build();

                Request newRequest = chain.request().newBuilder()
                        .url(url)
                        .cacheControl(CacheControl.FORCE_NETWORK)//limpia cache?
                        .build();


                return chain.proceed(newRequest);
            }
        };

        OkHttpClient client = new OkHttpClient();

        client.interceptors().add(interceptor);

        return client;

    }

    //********* GOOGLE PLACES API ***********
    /*public static Retrofit getRetrofitGooglePlaces() {
        return retrofitGooglePlaces;
    }*/

    //********* OPEN WEATHER API ***********
    public static Retrofit getRetrofitOpenWeather() {
        return retrofitOpenWeather;
    }


    private static class DateTimeTypeConverter
            implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
        @Override
        public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                return new DateTime(json.getAsString());
            } catch (IllegalArgumentException e) {
                // May be it came in formatted as a java.util.Date, so try that
                Date date = context.deserialize(json, Date.class);
                return new DateTime(date);
            }
        }
    }
}
