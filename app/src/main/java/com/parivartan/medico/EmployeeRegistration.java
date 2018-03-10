package com.parivartan.medico;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.parivartan.medico.model.PatientDetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by deepak on 07-02-2017.
 */

public class EmployeeRegistration extends AppCompatActivity {

    //Firebase Variables
    private FirebaseAuth mAuth;

    public EditText mEmail;
    private EditText mPassword;

    private TextView mLogin;
    private Button mRegister;
    private EditText mName;
    private ProgressDialog progressDialog;
    SharedPreferences sharedpreferences;


    public static final MediaType MEDIA_TYPE =
            MediaType.parse("application/json");

    OkHttpClient client;

    JSONObject postdata;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_employee_reg);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        client = new OkHttpClient();

        mAuth = FirebaseAuth.getInstance();

        mEmail = (EditText) findViewById(R.id.reg_email);
        mPassword = (EditText) findViewById(R.id.reg_password);
        mRegister = (Button) findViewById(R.id.register);
        mLogin = (TextView) findViewById(R.id.goto_login);


        TextView textView = (TextView)findViewById(R.id.emp_title);
        textView.setText("Signup");
        progressDialog = new ProgressDialog(this);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),EmployeeLogin.class));
            }
        });
    }
    private void registerUser(){
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        final String userid = email.split("@")[0];

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("email",email);
        editor.putString("password",password);
        editor.putString("username",userid);
        editor.apply();

        Log.e("email",email);
        Log.e("password",password);
        Log.e("Username ", userid);

        if(TextUtils.isEmpty(email)){
            mEmail.setError("Enter Email");
            return;
        }

        if(TextUtils.isEmpty(password)){
            mPassword.setError("Enter Password");
            return;
        }
        progressDialog.setMessage("Registering Please Wait");
        progressDialog.show();

        postdata = new JSONObject();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    g
                    try {
                        postdata.put("User_Name", userid);
                        postdata.put("email", email);
                    } catch(JSONException e){
                        e.printStackTrace();
                    }

                    RequestBody body = RequestBody.create(MEDIA_TYPE,
                            postdata.toString());

                    final Request request = new Request.Builder()
                            .url("https://api.illiteracy22.hasura-app.io/User_Personal_Details/upload_user_details")
                            .post(body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("cache-control", "no-cache")
                            .build();


                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            String mMessage = e.getMessage().toString();
                            Log.w("failure Response", mMessage);
                            //call.cancel();
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, Response response) throws IOException {

                            String mMessage = response.body().string();
                            if (response.isSuccessful()){
                                try {
                                    JSONObject json = new JSONObject(mMessage);
                                    Log.e("JSON",json.toString());
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    });



                    Toast.makeText(EmployeeRegistration.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
                    finish();

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("email",email);
                    intent.putExtra("username",userid);
                    startActivity(intent);


            }else{
                    Toast.makeText(EmployeeRegistration.this, "Registration Error", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}


