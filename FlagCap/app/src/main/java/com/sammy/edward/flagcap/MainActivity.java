package com.sammy.edward.flagcap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener{

    Button startCounterButton;
    Button startGameMenuButton;
    Button startLocationTrackerButton;
    Button buttonGetData;
    Button buttonToBrowser;
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t = (TextView)findViewById(R.id.textView);

        startCounterButton = (Button) findViewById(R.id.start_counter);
        startCounterButton.setOnClickListener(this);

        startGameMenuButton = (Button) findViewById(R.id.start_gamemenu);
        startGameMenuButton.setOnClickListener(this);

        startLocationTrackerButton = (Button) findViewById(R.id.start_location_tracker);
        startLocationTrackerButton.setOnClickListener(this);

        buttonGetData = (Button)findViewById(R.id.buttonGetData);
        buttonGetData.setOnClickListener(this);

        buttonToBrowser = (Button)findViewById(R.id.buttonToBrowser);
        buttonToBrowser.setOnClickListener(this);


    }

    @Override
    protected void onResume(){
        ImageView myImageView= (ImageView)findViewById(R.id.imageView);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        myImageView.startAnimation(myFadeInAnimation); //Set animation to your ImageView
        super.onResume();
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
        } else if ( v == buttonGetData) {
            Intent intent = new Intent(MainActivity.this, SetNameActivity.class);
            startActivityForResult(intent, 0);
        } else if (v == buttonToBrowser) {
            Uri address = Uri.parse("http://developer.android.com/");
            Intent androidDocs = new Intent(Intent.ACTION_VIEW, address);
            startActivity(androidDocs);
        }
    }
}
