package com.sammy.edward.flagcap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener{

    Button startCounterButton;
    Button startGameMenuButton;
    Button startLocationTrackerButton;
    Button b;
    Button b2;
    Button buttonGetData;
    TextView t;
    EditText e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t = (TextView)findViewById(R.id.textView);
        e = (EditText)findViewById(R.id.editText);

        b = (Button)findViewById(R.id.button);
        b.setOnClickListener(this);

        b2 = (Button)findViewById(R.id.button2);
        b2.setOnClickListener(this);

        startCounterButton = (Button) findViewById(R.id.start_counter);
        startCounterButton.setOnClickListener(this);

        startGameMenuButton = (Button) findViewById(R.id.start_gamemenu);
        startGameMenuButton.setOnClickListener(this);

        startLocationTrackerButton = (Button) findViewById(R.id.start_location_tracker);
        startLocationTrackerButton.setOnClickListener(this);

        buttonGetData = (Button)findViewById(R.id.buttonGetData);
        buttonGetData.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 0 && resultCode == Activity.RESULT_OK){
            String enteredData = data.getStringExtra("Data");
            t.setText(enteredData);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onClick(View v) {
        if(v == startCounterButton) {
            Intent intent = new Intent(MainActivity.this, CounterActivity.class);
            startActivity(intent);
        } else if (v == startGameMenuButton) {
            Intent intent = new Intent(MainActivity.this, StartMenuActivity.class);
            startActivity(intent);
        } else if (v == startLocationTrackerButton) {
            Intent intent = new Intent(MainActivity.this, LocationActivity.class);
            startActivity(intent);
        } else if(v == b) {
            t.setText(e.getText());
        } else if (v == b2) {
            Intent intent = new Intent(MainActivity.this, MainActivityB.class);
            Bundle b = new Bundle();
            b.putString("greeting", "hello");
            intent.putExtra("greetingbudle", b);
            intent.putExtra("message", "world");
            intent.putExtra("showAll", true);
            intent.putExtra("numItems", 5);

            startActivity(intent);
        } else if ( v == buttonGetData) {
            Intent intent = new Intent(MainActivity.this, ActivityC.class);
            startActivityForResult(intent, 0);
        }
    }
}
