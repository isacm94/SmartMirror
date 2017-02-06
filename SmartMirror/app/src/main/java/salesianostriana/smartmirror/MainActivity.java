package salesianostriana.smartmirror;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import salesianostriana.smartmirror.Interfaces.IOpenWeatherAPI;
import salesianostriana.smartmirror.Pojos.OpenWeather.Prediccion;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    TextView textViewHora, textViewFecha, textViewLugar, textViewTemperatura, textViewMensaje;
    ImageView imageViewIconoTiempo;

    String TAG = "INFO";
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private GoogleApiClient mGoogleApiClient;
    String latitud = "", longitud = "", localidad = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();//Oculta action bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//App pantalla completa
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//Permite que la pantalla no se bloquee

        textViewHora = (TextView) findViewById(R.id.text_view_hora);
        textViewFecha = (TextView) findViewById(R.id.text_view_fecha);
        textViewLugar = (TextView) findViewById(R.id.text_view_lugar);
        textViewTemperatura = (TextView) findViewById(R.id.text_view_temperatura);
        textViewMensaje = (TextView) findViewById(R.id.text_view_mensaje);
        imageViewIconoTiempo = (ImageView) findViewById(R.id.icono_tiempo);

        //Aplica la fuente a todos los textos
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/montserrat/Montserrat-Regular.ttf");
        textViewHora.setTypeface(font);
        textViewFecha.setTypeface(font);
        textViewFecha.setTypeface(font);
        textViewLugar.setTypeface(font);
        textViewTemperatura.setTypeface(font);
        textViewMensaje.setTypeface(font);

        //Actualiza hora y fecha cada segundo
        Thread threadHoraFecha = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                actualizaHoraFecha();
                            }
                        });
                        Thread.sleep(1000);//cada segundo
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        threadHoraFecha.start();


        //Actualiza prediccion y ubicacion cada 5 minutos
        int minutos = 1;
        final int milisegundos = minutos * 60000;

        Thread threadPrediccionActual = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //UBICACIÓN
                                mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                                        .enableAutoManage(MainActivity.this, MainActivity.this)
                                        .addConnectionCallbacks(MainActivity.this)
                                        .addApi(LocationServices.API)
                                        .build();
                            }
                        });
                        Thread.sleep(milisegundos);
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        threadPrediccionActual.start();

    }

    /* Actualiza la hora y la fecha actual, es actualizada cada 1 segundo.*/
    private void actualizaHoraFecha() {
        DateTime dateTimeNow = DateTime.now();

        //HORA
        String formatHour = "HH:mm:ss";
        String hora = dateTimeNow.toString(formatHour);
        textViewHora.setText(hora);

        //FECHA
        String formatDate = "EEEE, d MMMM";
        String fecha = dateTimeNow.toString(formatDate);
        textViewFecha.setText(fecha);
    }

    /*Consulta la predicción actual según las coordenadas actuales*/
    private void getPrediccionActual() {
        Retrofit retrofit = ((RetrofitApplication) getApplication()).getRetrofitOpenWeather();

        final IOpenWeatherAPI service = retrofit.create(IOpenWeatherAPI.class);

        final Call<Prediccion> call = service.getPrediccionActual(latitud, longitud);

        call.enqueue(new Callback<Prediccion>() {
            @Override
            public void onResponse(Response<Prediccion> response, Retrofit retrofit) {
                if (response.isSuccess()) {

                    final Prediccion prediccionActual = response.body();

                    actualizaVista(prediccionActual);

                    Log.d(TAG, "URL enviada: " + response.raw().request().url());

                } else {
                    Toast.makeText(getBaseContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("¡ERROR!", "MainActivity getPrediccionActual: " + response.code());
                    Log.d(TAG, "URL enviada: " + response.raw().request().url());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getBaseContext(), "Error onFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("¡ERROR!", "MainActivity onFailure: " + t.getMessage());
            }
        });
    }

    /*Actualiza la información en la vista según la predicción actual consultada*/
    private void actualizaVista(Prediccion prediccionActual) {
        if (!localidad.isEmpty())
            textViewLugar.setText(localidad);
        else
            textViewLugar.setText(prediccionActual.getName());

        textViewTemperatura.setText(prediccionActual.getMain().getTemp() + "º");

        imageViewIconoTiempo.setImageResource(prediccionActual.getWeather().get(0).getDrawableIcon());

        textViewMensaje.setText(prediccionActual.getWeather().get(0).getMensaje());

        Log.d(TAG, prediccionActual.getName() + ", "
                + prediccionActual.getMain().getTemp() + "º, " + prediccionActual.getWeather().get(0).getIcon() + ", "
                + prediccionActual.getWeather().get(0).getDescription() + ", "
                + prediccionActual.getWeather().get(0).getMensaje() + ", "
                + localidad);
    }

    /*UBICACIÓN*/
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(TAG, "Error grave al conectar con Google Play Services");
    }

    /*Conectado correctamente a Google Play Services*/
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            updateLocation(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services

        Log.e(TAG, "Se ha interrumpido la conexión con Google Play Services");
    }

    /*Actualiza las coordenadas actuales*/
    private void updateLocation(Location loc) {
        if (loc != null) {
            latitud = String.valueOf(loc.getLatitude());
            longitud = String.valueOf(loc.getLongitude());
            Log.d(TAG, "UBICACIÓN ACTUAL: " + latitud + ", " + longitud);
            setLocation(loc);

        } else {
            Log.d(TAG, "Ubicación desconocida");
            Toast.makeText(this, "Ubicación desconocida, se asignará la ubicación por defecto", Toast.LENGTH_SHORT).show();
            latitud = String.valueOf(37.380378);//Salesianos triana
            longitud = String.valueOf(-6.007132);
        }

        getPrediccionActual();

        mGoogleApiClient.stopAutoManage(this);
        mGoogleApiClient.disconnect();
    }

    /*Obtener la localidad a partir de la latitud y la longitud*/
    public void setLocation(Location loc) {

        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = null;
            try {
                list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!list.isEmpty()) {
                Address address = list.get(0);
                Log.d(TAG, "Localidad: " + address.getLocality());
                localidad = address.getLocality();
            }
        }
    }

    /*Pide los permisos de localización*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permiso concedido

                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                updateLocation(lastLocation);

            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.
                Log.e(TAG, "Permiso denegado");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
}
