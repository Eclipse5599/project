package extra;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Sammy on 2015-09-04.
 */
public class MyOutput {

    public static void displayShortMessage(String message, Context context){
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
