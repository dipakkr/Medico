package com.parivartan.medico.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parivartan.medico.MainActivity;
import com.parivartan.medico.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

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
    EditText usernameEditText, emailEditText, nameEditText, weightEditText, genderEditText, heightEditText;
    EditText phoneEditText, addressEditText, stateEditText, pinCodeEditText;
    static EditText ageEditText, dobEditText;
    String mEmail, mUsername, mPatientName, mGender, mAge, mWeight, mHeight, mDOB, mPhone, mAddress, mState, mPinCode;
    private int code;
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

        dobEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
            }
        });
        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

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
                                    Intent intent = getIntent();
                                    if (intent.getBooleanExtra("checkNext", false)) {
                                        Intent newIntent = new Intent(PreProfileUpdate.this, TrackRecord.class);
                                        newIntent.putExtra("checkSkip", true);
                                        startActivity(newIntent);
                                    }
                                    finish();
                                }
                            };

                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(saveButtonClickListener);
                }
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        final Calendar c = Calendar.getInstance();
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            c.set(year, month, day);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            dobEditText.setText(format.format(c.getTime()));
            getAge(year,month,day);
        }
    }

    private static void getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        ageEditText.setText(Integer.toString(age));
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
        if (TextUtils.isEmpty(mPhone)) {
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
            postdata.put("Session_token",FirebaseVariables.user.getUid());
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
                        code = json.getInt("code");
                        Log.e("JSON", code+"");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if(code==401){
            Toast.makeText(PreProfileUpdate.this, "Session Expires", Toast.LENGTH_SHORT).show();
        } else if (code == 404) {
            Toast.makeText(PreProfileUpdate.this, "User not found", Toast.LENGTH_SHORT).show();
        }else if(code==200){
            FirebaseVariables.mDatabaseReference.child(FirebaseVariables.user.getUid()).child("profile").setValue(true);
            Toast.makeText(PreProfileUpdate.this, "Profile Details Uploaded", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if (intent.getBooleanExtra("checkNext", false)) {
            Toast.makeText(this, "This form is mandatory to fill", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}
