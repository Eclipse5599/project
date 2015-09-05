package extra;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by Sammy on 2015-09-05.
 */
public class Converter {

    public static ArrayList<DualValue> convertMarkerListToDV(ArrayList<Marker> mList){

        if(mList == null){
            return null;
        }

        ArrayList<DualValue> dvList = new ArrayList<>();
        for (Marker mark : mList){
            if(mark == null)
                continue;
            LatLng pos = mark.getPosition();
            DualValue dv = new DualValue(pos.latitude,pos.longitude);
            dvList.add(dv);
        }
        return dvList;

    }
}
