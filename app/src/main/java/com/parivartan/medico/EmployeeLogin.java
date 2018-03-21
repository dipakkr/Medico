package com.parivartan.medico;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.parivartan.medico.activity.FirebaseVariables;

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

public class EmployeeLogin extends AppCompatActivity {

    public static EditText mEtemail, mEtPass;
    private Button mLogin;
    private Button mBack;
    private ProgressDialog progressDialog;

    public static final MediaType MEDIA_TYPE =
            MediaType.parse("application/json");
    OkHttpClient client;

    JSONObject postdata;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_login);

        FirebaseVariables.mFirebaseAuth = FirebaseAuth.getInstance();

       /* if(FirebaseVariables.mFirebaseAuth.getCurrentUser() !=null){
            //user is logged in
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }*/

        client = new OkHttpClient();

        mEtemail = (EditText) findViewById(R.id.login_email);
        mEtPass = (EditText) findViewById(R.id.login_pass);
        mLogin = (Button) findViewById(R.id.bt_login);
        mBack = (Button) findViewById(R.id.back);

        progressDialog = new ProgressDialog(this);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loginUser() {
        final String email = mEtemail.getText().toString();
        String password = mEtPass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging in Please Wait");
        progressDialog.show();

        FirebaseVariables.mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseVariables.user = FirebaseVariables.mFirebaseAuth.getCurrentUser();
                    updateSession(email);
                    finish();
                    Toast.makeText(EmployeeLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("email", email);
                    final String userid = email.split("@")[0];
                    intent.putExtra("username", userid);
                    startActivity(intent);
                } else {
                    Toast.makeText(EmployeeLogin.this, "Login Error", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void updateSession(String email) {
        postdata = new JSONObject();

        try {
            postdata.put("User_Name", email.split("@")[0]);
            postdata.put("Session_token", FirebaseVariables.user.getUid());
            postdata.put("pass", 1997);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE,
                postdata.toString());

        final Request request = new Request.Builder()
                .url("https://api-illiteracy22.azurewebsites.net/Auth/update_session_ID")
                //.url("https://api.illiteracy22.hasura-app.io/Auth/update_session_ID")
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
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(mMessage);
                        Log.e("JSON", json.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
