package com.parivartan.medico.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parivartan.medico.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by root on 3/7/18.
 */

public class PreProfileUpdate extends AppCompatActivity {

    public static final MediaType MEDIA_TYPE =
            MediaType.parse("application/json");
    EditText usernameEditText, emailEditText, nameEditText, dobEditText, ageEditText, weightEditText, genderEditText, heightEditText;
    EditText phoneEditText, addressEditText, stateEditText, pinCodeEditText;
    String mEmail, mUsername, mPatientName, mGender, mAge, mWeight, mHeight, mDOB, mPhone, mAddress, mState, mPinCode;
    OkHttpClient client;
    JSONObject postdata;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_update);

        client = new OkHttpClient();

        usernameEditText = (EditText) findViewById(R.id.user_username);
        emailEditText = (EditText) findViewById(R.id.user_email);
        nameEditText = (EditText) findViewById(R.id.user_name);
        genderEditText = (EditText) findViewById(R.id.user_gender);
        dobEditText = (EditText) findViewById(R.id.user_dob);
        ageEditText = (EditText) findViewById(R.id.user_age);
        heightEditText = (EditText) findViewById(R.id.user_height);
        weightEditText = (EditText) findViewById(R.id.user_weight);
        phoneEditText = (EditText) findViewById(R.id.user_phone);
        addressEditText = (EditText) findViewById(R.id.user_address);
        stateEditText = (EditText) findViewById(R.id.user_state);
        pinCodeEditText = (EditText) findViewById(R.id.user_pin_code);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEmail = preferences.getString("email", "");
        mUsername = preferences.getString("username", "");

        Log.e("EMAIL", mEmail);
        usernameEditText.setText(mUsername);
        emailEditText.setText(mEmail);

        Button updateProfileButton = (Button) findViewById(R.id.update_patient);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extractUserInputsFromUI();
                if (checkUserInputs()) {
                    // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                    // Create a click listener to handle the user confirming that
                    // changes should be discarded.
                    DialogInterface.OnClickListener saveButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    saveUserDetails();
                                    /*Intent intent = getIntent();
                                    if(!intent.getBooleanExtra("history",true)){
                                        startActivity(new Intent(PreProfileUpdate.this, TrackRecord.class));
                                    }*/
                                    finish();
                                }
                            };

                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(saveButtonClickListener);
                }
            }
        });
    }

    private void extractUserInputsFromUI() {
        mPatientName = nameEditText.getText().toString().trim();
        mGender = genderEditText.getText().toString().trim();
        mDOB = dobEditText.getText().toString().trim();
        mAge = ageEditText.getText().toString().trim();
        mHeight = heightEditText.getText().toString().trim();
        mWeight = weightEditText.getText().toString().trim();
        mPhone = phoneEditText.getText().toString().trim();
        mAddress = addressEditText.getText().toString().trim();
        mState = stateEditText.getText().toString().trim();
        mPinCode = pinCodeEditText.getText().toString().trim();
    }

    private boolean checkUserInputs() {
        int flag = 1;

        if (TextUtils.isEmpty(mPatientName)) {
            nameEditText.setError("You can't leave this empty.");
            flag = 0;
        }
        if (TextUtils.isEmpty(mGender)) {
            genderEditText.setError("You can't leave this empty.");
            flag = 0;
        }
        if (TextUtils.isEmpty(mDOB)) {
            dobEditText.setError("You can't leave this empty.");
            flag = 0;
        }
        if (TextUtils.isEmpty(mAge)) {
            ageEditText.setError("You can't leave this empty.");
            flag = 0;
        }
        if (TextUtils.isEmpty(mWeight)) {
            weightEditText.setError("You can't leave this empty.");
            flag = 0;
        }
        if (TextUtils.isEmpty(mHeight)) {
            heightEditText.setError("You can't leave this empty.");
            flag = 0;
        }
        if (mPhone.length()!=10) {
            phoneEditText.setError("You can't leave this empty.");
            flag = 0;
        }
        if (TextUtils.isEmpty(mAddress)) {
            addressEditText.setError("You can't leave this empty.");
            flag = 0;
        }
        if (TextUtils.isEmpty(mState)) {
            stateEditText.setError("You can't leave this empty.");
            flag = 0;
        }
        if (TextUtils.isEmpty(mPinCode)) {
            pinCodeEditText.setError("You can't leave this empty.");
            flag = 0;
        }

        if (flag == 0) {
            return false;
        } else {
            return true;
        }
    }

    private void saveUserDetails() {

        postdata = new JSONObject();

        try {
            postdata.put("User_Name", mUsername);
            postdata.put("name", mPatientName);
            postdata.put("gender", mGender);
            postdata.put("dob", mDOB);
            postdata.put("age", mAge);
            postdata.put("heght", mHeight);
            postdata.put("weight", mWeight);
            postdata.put("email", mEmail);
            postdata.put("phoneNo", mPhone);
            postdata.put("address", mAddress);
            postdata.put("state", mState);
            postdata.put("pinCode", mPinCode);

        } catch (JSONException e) {
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
        FirebaseVariables.mDatabaseReference.child(FirebaseVariables.user.getUid()).child("profile").setValue(true);
        Toast.makeText(PreProfileUpdate.this, "Profile Details Uploaded", Toast.LENGTH_SHORT).show();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener saveButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save your changes and quit editing?");
        builder.setPositiveButton("Save", saveButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
