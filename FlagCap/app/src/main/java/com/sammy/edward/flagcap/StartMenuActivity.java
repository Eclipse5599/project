package com.sammy.edward.flagcap;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import extra.CounterActivity;

public class StartMenuActivity extends Activity implements View.OnClickListener {

    Button startButton;
    Button highScoreButton;
    Button optionsButton;
    Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startmenu);

        startButton = (Button) findViewById(R.id.startmenu_start_button);
        startButton.setOnClickListener(this);

        highScoreButton = (Button) findViewById(R.id.startmenu_highscore_button);
        highScoreButton.setOnClickListener(this);

        optionsButton = (Button) findViewById(R.id.startmenu_options_button);
        optionsButton.setOnClickListener(this);

        exitButton = (Button) findViewById(R.id.startmenu_exit_button);
        exitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == startButton) {
            Intent intent = new Intent(StartMenuActivity.this, GameActivity.class);
            startActivity(intent);
        } else if (v == highScoreButton) {

        } else if (v == optionsButton) {

        } else if (v == exitButton) {

        }
    }
}
