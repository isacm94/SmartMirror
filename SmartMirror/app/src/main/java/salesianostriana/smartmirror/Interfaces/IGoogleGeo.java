package salesianostriana.smartmirror.Interfaces;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import salesianostriana.smartmirror.Pojos.GoogleGeocode.Geocode;

/**
 * Created by Rafa on 11/02/2017.
 */

public interface IGoogleGeo {

    public final static String ENDPOINT = "https://maps.googleapis.com/";
    public final static String APIKEY = "AIzaSyBgFOAHOIs579TXXZOozyBIs7F5oAh9eL0";

    @GET("maps/api/geocode/json")
    Call<Geocode> getLocalidad(@Query("latlng")String latlong);
}
