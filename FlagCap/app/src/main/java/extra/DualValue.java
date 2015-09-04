package extra;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sammy on 2015-09-04.
 */
public class DualValue implements Parcelable {

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

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDoubleArray(new double[]{this.lat,
                this.longi});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public DualValue createFromParcel(Parcel in) {
            return new DualValue(in);
        }

        public DualValue[] newArray(int size) {
            return new DualValue[size];
        }
    };
}

