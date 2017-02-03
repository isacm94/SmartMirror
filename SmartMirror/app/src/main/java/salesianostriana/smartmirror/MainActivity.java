package salesianostriana.smartmirror;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textViewHora, textViewFecha, textViewLugar, textViewTemperatura, textViewMensaje, textViewCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();//Oculta action bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//App pantalla completa

        textViewHora = (TextView) findViewById(R.id.text_view_hora);
        textViewFecha = (TextView) findViewById(R.id.text_view_fecha);
        textViewLugar = (TextView) findViewById(R.id.text_view_lugar);
        textViewTemperatura = (TextView) findViewById(R.id.text_view_temperatura);
        textViewMensaje = (TextView) findViewById(R.id.text_view_mensaje);
        textViewCal = (TextView) findViewById(R.id.text_view_cal);

        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/noto_sans/NotoSans-Regular.ttf");
        textViewHora.setTypeface(font);
        textViewFecha.setTypeface(font);
        textViewLugar.setTypeface(font);
        textViewTemperatura.setTypeface(font);
        textViewMensaje.setTypeface(font);
        textViewCal.setTypeface(font);
    }
}
