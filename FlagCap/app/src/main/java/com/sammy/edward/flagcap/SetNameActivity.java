package com.sammy.edward.flagcap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetNameActivity extends Activity implements View.OnClickListener{

    Button buttonSend;
    Button buttonCancel;
    EditText e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setnameactivity_layout);

        e = (EditText)findViewById(R.id.editText);
        e.setOnClickListener(new View.OnClickListener() {
            Boolean enabled = true;

            @Override
            public void onClick(View v) {
                if (enabled) {
                    e.setText("");
                    enabled = false;
                }
            }
        });
        e.setText("Enter nickname here");

        buttonSend = (Button)findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(this);
        buttonCancel = (Button)findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(this);
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
    public void onClick(View v) {
        if(v == buttonSend) {
            Intent result = new Intent();
            result.putExtra("Data", e.getText().toString());
            setResult(Activity.RESULT_OK,result);
            finish();
        } else if (v == buttonCancel) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }
}