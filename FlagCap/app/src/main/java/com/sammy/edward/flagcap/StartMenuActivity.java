package com.sammy.edward.flagcap;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartMenuActivity extends Activity implements View.OnClickListener {

    Button newGameButton;
    Button continueButton;
    Button highScoreButton;
    Button optionsButton;
    Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startmenu);

        newGameButton = (Button) findViewById(R.id.startmenu_new_button);
        newGameButton.setOnClickListener(this);

        continueButton = (Button) findViewById(R.id.startmenu_continue_button);
        continueButton.setOnClickListener(this);

        highScoreButton = (Button) findViewById(R.id.startmenu_highscore_button);
        highScoreButton.setOnClickListener(this);

        optionsButton = (Button) findViewById(R.id.startmenu_options_button);
        optionsButton.setOnClickListener(this);

        exitButton = (Button) findViewById(R.id.startmenu_exit_button);
        exitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == newGameButton) {
            Intent intent = new Intent(StartMenuActivity.this, GameActivity.class);
            intent.putExtra(Constants.GAMECODE, Constants.NEW_GAME_CODE);
            startActivity(intent);
        } else if (v == continueButton) {
            Intent intent = new Intent(StartMenuActivity.this, GameActivity.class);
            intent.putExtra(Constants.GAMECODE, Constants.CONTINUE_GAME_CODE);
            startActivity(intent);
        } else if (v == highScoreButton) {

        } else if (v == optionsButton) {

        } else if (v == exitButton) {

        }
    }
}
