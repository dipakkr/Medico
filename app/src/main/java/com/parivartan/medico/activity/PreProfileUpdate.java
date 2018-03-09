package com.parivartan.medico.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.parivartan.medico.R;

/**
 * Created by root on 3/7/18.
 */

public class PreProfileUpdate extends AppCompatActivity {

    EditText email, name, age, weight, gender,height, username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_update);

        username = (EditText)findViewById(R.id.user_username);
        email = (EditText)findViewById(R.id.user_email);
        name = (EditText)findViewById(R.id.user_name);
        weight = (EditText)findViewById(R.id.user_weight);
        age = (EditText)findViewById(R.id.user_age);
        gender = (EditText)findViewById(R.id.user_gender);
        height = (EditText)findViewById(R.id.user_height);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pre_update,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.bt_skip){

        }
        return super.onOptionsItemSelected(item);
    }

}
