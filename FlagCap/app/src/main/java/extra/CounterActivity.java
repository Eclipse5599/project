package extra;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sammy.edward.flagcap.R;

public class CounterActivity extends Activity implements View.OnClickListener{

    //The values to be stored
    static final String STATE_THE_COUNT = "countInCounter";

    //Variables to be used
    Button incButton;
    Button decButton;
    TextView theCounter;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        //Intent intent = getIntent();

        incButton = (Button) findViewById(R.id.incButton);
        incButton.setOnClickListener(this);
        decButton = (Button) findViewById(R.id.decButton);
        decButton.setOnClickListener(this);
        theCounter = (TextView) findViewById(R.id.count_display);

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        count = sharedPref.getInt(STATE_THE_COUNT, 0);

        theCounter.setText("" + count);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        saveToKeyFile();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_THE_COUNT, count);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(incButton)){
            count++;
        } else if (v.equals(decButton)) {
            count--;
        }
        theCounter.setText("" + count);
    }


    public void saveToKeyFile() {
        SharedPreferences sharedPref = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(STATE_THE_COUNT, count);
        editor.commit();
    }
}
