package io.streamer.oose;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = (EditText)findViewById(R.id.name);
                EditText pass = (EditText)findViewById(R.id.pass);
                EditText phone = (EditText)findViewById(R.id.phone);

                if(name.getText().toString().length()==0){
                    name.setError("Please fill name");
                    return;
                }

                if(pass.getText().toString().length()==0){
                    pass.setError("Please fill password");
                    return;
                }

                if(phone.getText().toString().length()!=10){
                    phone.setError("Please fill phone number carefully");
                    return;
                }

                Util.signUp(name.getText().toString(),pass.getText().toString(),phone.getText().toString(),view);
                Util.saveDetails(name.getText().toString(), pass.getText().toString(),phone.getText().toString(),getBaseContext());

            }
        });
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
        int Id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (Id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View v){
        EditText name = (EditText)findViewById(R.id.name);
        EditText pass = (EditText)findViewById(R.id.pass);

        if(name.getText().toString().length()==0){
            name.setError("Please fill name");
            return;
        }

        if(pass.getText().toString().length()==0){
            pass.setError("Please fill password");
            return;
        }
        Util.login(name.getText().toString(), pass.getText().toString(), Util.loadPhone(getBaseContext()), v, this);
    }
}
