package com.parivartan.medico;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parivartan.medico.activity.PreProfileUpdate;
import com.parivartan.medico.model.PatientDetail;

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
    private DatabaseReference mDatabase;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_employee_reg);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

        mEmail = (EditText) findViewById(R.id.reg_email);
        mPassword = (EditText) findViewById(R.id.reg_password);
        mRegister = (Button) findViewById(R.id.register);
        mLogin = (TextView) findViewById(R.id.goto_login);

        mDatabase = FirebaseDatabase.getInstance().getReference("patientDetails");

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

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("email",email);
        editor.putString("password",password);
        editor.apply();
        final String userid = email.split("@")[0];

        Log.e("email","");
        Log.e("password","");
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

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    PatientDetail patientDetail = new PatientDetail(email,"","","","");
                    mDatabase.child(userid).setValue(patientDetail);

                    Toast.makeText(EmployeeRegistration.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(EmployeeRegistration.this,PreProfileUpdate.class));

            }else{
                    Toast.makeText(EmployeeRegistration.this, "Registration Error", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}


