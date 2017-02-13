package salesianostriana.smartmirror;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import salesianostriana.smartmirror.Interfaces.IGoogleGeo;
import salesianostriana.smartmirror.Interfaces.IOpenWeatherAPI;
import salesianostriana.smartmirror.Pojos.GoogleGeocode.Geocode;
import salesianostriana.smartmirror.Pojos.OpenWeather.Prediccion;

import static android.R.attr.value;
import static android.app.Activity.RESULT_OK;
import static org.joda.time.DateTimeFieldType.dayOfMonth;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    TextView textViewHora, textViewFecha, textViewLugar, textViewTemperatura, textViewMensaje;
    ImageView imageViewIconoTiempo;

    String TAG = "INFO";
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private GoogleApiClient mGoogleApiClient;
    String latitud = "37.380378", longitud = "-6.007132", localidad = "";
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().hide();//Oculta action bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//App pantalla completa
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//Permite que la pantalla no se bloquee

        lista = (ListView) findViewById(R.id.lista);
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
                                getPrediccionActual();
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

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        getResultsFromApi();



    }

    /* Actualiza la hora y la fecha actual, es actualizada cada 1 segundo.*/
    private void actualizaHoraFecha() {
        DateTime dateTimeNow = DateTime.now();

        //HORA
        String formatHour = "HH:mm:ss";
        String hora = dateTimeNow.toString(formatHour);
        textViewHora.setText(hora);

        //FECHA
        String formatDate = "EEEE\nd MMMM";
        String month = null;
        switch(dateTimeNow.getMonthOfYear()){
            case 1:
                month="Enero";
                break;
            case 2:
                month="Febrero";
                break;
            case 3:
                month="Marzo";
                break;
            case 4:
                month="Abril";
                break;
            case 5:
                month="Mayo";
                break;
            case 6:
                month="Junio";
                break;
            case 7:
                month="Julio";
                break;
            case 8:
                month="Agosto";
                break;
            case 9:
                month="Septiembre";
                break;
            case 10:
                month="Octubre";
                break;
            case 11:
                month="Noviembre";
                break;
            case 12:
                month="Diciembre";
                break;
        }

        switch (dateTimeNow.getMonthOfYear()){
            case 1:
        }
        int dayMonth = dateTimeNow.getDayOfMonth();
        String day = null;
        switch(dateTimeNow.getDayOfWeek()){
            case 1:
                day="Lunes";
                break;
            case 2:
                day="Martes";
                break;
            case 3:
                day="Miércoles";
                break;
            case 4:
                day="Jueves";
                break;
            case 5:
                day="Viernes";
                break;
            case 6:
                day="Sábado";
                break;
            case 7:
                day="Domingo";
                break;
        }
        dateTimeNow.monthOfYear().getName();
        String fecha = dateTimeNow.toString(formatDate);
        textViewFecha.setText(day+"\n"+dayMonth+" "+month);
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

                    Log.i(TAG, "URL enviada: " + response.raw().request().url());

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


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                        }
                    }).check();
            return;
        }
        Location lastLocation =
                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        updateLocation(lastLocation);
        //}
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
            Retrofit retrofit = RetrofitApplication.getRetrofitGoogleGeo();

            IGoogleGeo service = retrofit.create(IGoogleGeo.class);

            Call<Geocode> call = service.getLocalidad(loc.getLatitude() + "," + loc.getLongitude());

            call.enqueue(new Callback<Geocode>() {
                @Override
                public void onResponse(Response<Geocode> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        textViewLugar.setText(response.body().getResults().get(0).getAddressComponents().get(0).getShortName());
                    } else {
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.i("loc", t.getMessage());

                }
            });
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


    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
        } else {

            new MakeRequestTask(mCredential).execute();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         *
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            com.google.api.client.util.DateTime now = new com.google.api.client.util.DateTime(System.currentTimeMillis());

            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(5)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {
                String str;
                org.joda.time.DateTime dateTimeJoda;

                com.google.api.client.util.DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.

                    start = event.getStart().getDate();
                    dateTimeJoda = new org.joda.time.DateTime(start.getValue());
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM");
                    str = dateTimeJoda.toString(fmt);

                } else {
                    dateTimeJoda = new org.joda.time.DateTime(start.getValue());
                    DateTimeFormatter fmt2 = DateTimeFormat.forPattern("HH:mm  dd/MM");
                    str = dateTimeJoda.toString(fmt2);
                }

                eventStrings.add(
                        String.format("(%s) %s", str, event.getSummary()));
            }
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(List<String> output) {

            if (output == null || output.size() == 0) {
                ArrayList<String> array = new ArrayList<>();
                array.add("No hay Eventos.");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.listaviewcustom, android.R.id.text1, array);
                lista.setAdapter(adapter);
            } else {

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.listaviewcustom, android.R.id.text1, output);
                lista.setAdapter(adapter);
            }
        }

        @Override
        protected void onCancelled() {

            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {

                }
            } else {

            }
        }




    }

}

