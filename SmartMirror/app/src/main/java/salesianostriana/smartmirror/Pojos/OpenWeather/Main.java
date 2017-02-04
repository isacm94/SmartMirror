
package salesianostriana.smartmirror.Pojos.OpenWeather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {

    //********** ATRIBUTOS **********
    @SerializedName("temp")
    @Expose
    private Double temp;

    @SerializedName("temp_min")
    @Expose
    private Double tempMin;

    @SerializedName("temp_max")
    @Expose
    private Double tempMax;


    //********** GETTERS & SETTERS **********
    /**
     * @return The temp
     */
    public int getTemp() {
        return roundDouble(temp);
    }

    /**
     * @param temp The temp
     */
    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public int getTempMin() {
        return roundDouble(tempMin);
    }

    public void setTempMin(Double tempMin) {
        this.tempMin = tempMin;
    }

    public int getTempMax() {
        return roundDouble(tempMax);
    }

    public void setTempMax(Double tempMax) {
        this.tempMax = tempMax;
    }

    //********** MÉTODOS **********
    private int roundDouble(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return d < 0 ? -i : i;
        } else {
            return d < 0 ? -(i + 1) : i + 1;
        }
    }
}
