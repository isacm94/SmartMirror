package salesianostriana.smartmirror.Interfaces;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import salesianostriana.smartmirror.Pojos.OpenWeather.Prediccion;

/**
 * API de OpenWeather --> https://openweathermap.org/api
 */
public interface IOpenWeatherAPI {
    String ENDPOINT = "http://api.openweathermap.org/";

    String KEY_OPEN_WEATHER_API = "0d664d5c76a364c4e18b207ac25a0ef4";

    //******** 5 DÍAS/8 HORAS *******
    //@GET("/data/2.5/forecast")
    //Call<RootPredicciones> getPredicciones(@Query("lat") String latitud, @Query("lon") String longitud);//Información meteorológica de 5 días de la latitud y longitud


    //******** Actual ***********
    @GET("data/2.5/weather")
    Call<Prediccion> getPrediccionActual(@Query("lat") String latitud, @Query("lon") String longitud);//Información meteorológica actual de la latitud y longitud
}
