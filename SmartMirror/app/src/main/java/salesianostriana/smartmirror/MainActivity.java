package salesianostriana.smartmirror;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import salesianostriana.smartmirror.Interfaces.IOpenWeatherAPI;
import salesianostriana.smartmirror.Pojos.OpenWeather.Prediccion;

public class MainActivity extends AppCompatActivity {

    TextView textViewHora, textViewFecha, textViewLugar, textViewTemperatura, textViewMensaje;
    ImageView imageViewIconoTiempo;

    String TAG = "INFO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GPSTracker tracker = new GPSTracker(this);
        if (!tracker.canGetLocation()) {
            tracker.showSettingsAlert();
        } else {
            double latitude = tracker.getLatitude();
            double longitude = tracker.getLongitude();

            Log.d(TAG, latitude+", "+longitude);
        }

        getSupportActionBar().hide();//Oculta action bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//App pantalla completa

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


        //Actualiza prediccion cada 5 minutos
        int minutos = 5;
        final int milisegundos = minutos * 60000;

        Thread threadPrediccionActual = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getPrediccionActual();
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

    private void getPrediccionActual() {
        Retrofit retrofit = ((RetrofitApplication) getApplication()).getRetrofitOpenWeather();

        final IOpenWeatherAPI service = retrofit.create(IOpenWeatherAPI.class);

        //Rociana
        //String latitud = String.valueOf(37.3082252);
        //String longitud = String.valueOf(-6.6005947);


        //Salesianos triana
        String latitud = String.valueOf(37.380378);
        String longitud = String.valueOf(-6.007132);

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
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getBaseContext(), "Error onFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("¡ERROR!", "MapsFragment onFailure: " + t.getMessage());
            }
        });


    }

    private void actualizaVista(Prediccion prediccionActual) {
        textViewLugar.setText(prediccionActual.getName());

        textViewTemperatura.setText(prediccionActual.getMain().getTemp() + "º");

        imageViewIconoTiempo.setImageResource(prediccionActual.getWeather().get(0).getDrawableIcon());

        textViewMensaje.setText(prediccionActual.getWeather().get(0).getMensaje());

    }
}
