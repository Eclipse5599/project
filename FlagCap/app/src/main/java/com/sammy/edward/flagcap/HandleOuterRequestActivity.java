package com.sammy.edward.flagcap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


public class HandleOuterRequestActivity extends Activity {

    TextView t;
    Boolean impossibleCondition = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = (TextView)findViewById(R.id.textView);
        Intent intent = getIntent();


        if(intent != null){
            String action = intent.getAction();
            String type = intent.getType();
            if(Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)){
                t.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
            } else if (impossibleCondition){
                //handle intent data from MainActivityA
                Bundle bundle = intent.getBundleExtra("greetingbudle");
                String greeting = bundle.getString("greeting");
                String message = intent.getStringExtra("message");
                Boolean showAll = intent.getBooleanExtra("showall", false);
                int numItems = intent.getIntExtra("numItems", 0);
                t.setText("This is activityB" + greeting + " " + message + " " + showAll + " " + numItems);
                final EditText e = (EditText)findViewById(R.id.editText);



                /*b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(HandleOuterRequestActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);


                        startActivity(intent);
                    }
                });*/
            }
        }





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
}
