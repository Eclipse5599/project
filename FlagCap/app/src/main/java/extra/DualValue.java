package extra;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Sammy on 2015-09-04.
 */
public class DualValue implements Serializable {

    public double lat;
    public double longi;

    public DualValue(double lat, double longi){
        this.lat = lat;
        this.longi = longi;
    }

    // Parcelling part
    public DualValue(Parcel in){
        double[] data = new double[2];
        in.readDoubleArray(data);
        this.lat = data[0];
        this.longi = data[1];
    }
}

