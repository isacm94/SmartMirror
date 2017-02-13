
package salesianostriana.smartmirror.Pojos.OpenWeather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import salesianostriana.smartmirror.R;


public class Weather {

    //********** ATRIBUTOS **********
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("main")
    @Expose
    private String main;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("icon")
    @Expose
    private String icon;


    //********** GETTERS & SETTERS **********

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The main
     */
    public String getMain() {
        return main;
    }

    /**
     * @param main The main
     */
    public void setMain(String main) {
        this.main = main;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description.substring(0, 1).toUpperCase() + description.substring(1);//Primera letra en mayúsculas
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon The icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }


    //********** MÉTODOS **********
    public int getDrawableIcon() {

        switch (icon) {
            case "01d":
                return R.drawable.icon_01d;

            case "01n":
                return R.drawable.icon_01n;

            case "02d":
                return R.drawable.icon_02d;

            case "02n":
                return R.drawable.icon_02n;

            case "03d":
                return R.drawable.icon_03d;

            case "03n":
                return R.drawable.icon_03n;

            case "04d":
                return R.drawable.icon_04d;

            case "04n":
                return R.drawable.icon_04n;

            case "09d":
                return R.drawable.icon_09d;

            case "09n":
                return R.drawable.icon_09n;

            case "10d":
                return R.drawable.icon_10d;

            case "10n":
                return R.drawable.icon_10n;

            case "11d":
                return R.drawable.icon_11d;

            case "11n":
                return R.drawable.icon_11n;

            case "13d":
                return R.drawable.icon_13d;

            case "13n":
                return R.drawable.icon_13n;

            case "50d":
                return R.drawable.icon_50d;

            case "50n":
                return R.drawable.icon_50n;

            default:
                return R.drawable.icon_01d;
        }
    }

    public String getMensaje() {
        String mensaje = "";

        if (id >= 200 && id <= 299)//2xx - Tormenta
            mensaje = "¡Hoy hay tormenta, ten cuidado!";
        else if (id >= 300 && id <= 399)//3xx - Llovizna
            mensaje = "¡Van a caer unas gotitas!";
        else if (id >= 500 && id <= 599)//5xx - Lluvia
            mensaje = "¡Que no se te olvide el paraguas!";
        else if (id >= 600 && id <= 699)//6xx - Nieve
            mensaje = "¡Hoy nieva!";
        else if (id == 800)//800- Clear
            mensaje = "¡Hoy hace un día estupendo!";
        else if (id > 800 && id <= 899)//80x - Cloud
            mensaje = "¡El día está nublado!";
        else if(id== 701){
            mensaje = "¡Cuidado! Hay niebla compañero";
        } else if(id== 711){
            mensaje = "¡El día está nublado!";
        } else if(id== 721){
            mensaje = "Vaya día más neblino compadre!";
        } else if(id== 731){
            mensaje = "cuidado no vaya a ser que te Torbenillees";
        } else if(id== 741){
            mensaje = "¡Cuidado! Hay niebla compañero";
        }else if(id== 751){
            mensaje = "Tapate la boca que hace un día arenoso";
        }else if(id== 761){
            mensaje = "Vaya día más polvoroso uff";
        }else if(id== 771){
            mensaje = "Si fuera tu cogería un chubasquero porque hay chubascos";
        }else if(id== 781){
            mensaje = "oyes eso *fiuuuu* es un TORNADO!!";
        }else
            mensaje = "Vaya día más...";
        return mensaje;
    }
}
