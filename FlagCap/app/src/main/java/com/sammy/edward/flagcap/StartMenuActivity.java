package com.sammy.edward.flagcap;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartMenuActivity extends Activity implements View.OnClickListener {

    Button startButton;
    Button highscoreButton;
    Button optionsButton;
    Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startButton = (Button) findViewById(R.id.startmenu_start_button);
        startButton.setOnClickListener(this);

        highscoreButton = (Button) findViewById(R.id.startmenu_highscore_button);
        highscoreButton.setOnClickListener(this);

        optionsButton = (Button) findViewById(R.id.startmenu_options_button);
        optionsButton.setOnClickListener(this);

        exitButton = (Button) findViewById(R.id.startmenu_exit_button);
        exitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == startButton) {

        } else if (v == highscoreButton) {

        } else if (v == optionsButton) {

        } else if (v == exitButton) {

        }
    }
}
