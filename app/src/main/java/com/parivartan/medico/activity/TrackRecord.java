package com.parivartan.medico.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;
import com.parivartan.medico.MainActivity;
import com.parivartan.medico.R;
import com.parivartan.medico.model.PatientHistory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 3/7/18.
 */

public class TrackRecord extends AppCompatActivity {

    public static final String LOG_TAG = TrackRecord.class.getName();

    private EditText mAllergens;
    private EditText mResistance;
    private CheckBox mPregnancy;
    private CheckBox mDiabetes;
    private CheckBox mHighBloodPressure;
    private CheckBox mHighCholestrol;
    private EditText mOther;
    private EditText mGeneticDiseaseEditText;

    String username;
    List<String> allergens;
    List<String> resistance;
    boolean pregnancy;
    boolean diabetes;
    boolean highbloodPressure;
    boolean highCholestrol;
    List<String> others;
    List<String> geneticDisesase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAllergens = (EditText) findViewById(R.id.allergens_edit_text);
        mResistance = (EditText) findViewById(R.id.resistance_edit_text);
        mPregnancy = (CheckBox) findViewById(R.id.pregnancy_checkbox);
        mDiabetes =  (CheckBox) findViewById(R.id.diabetes_checkbox);
        mHighBloodPressure =  (CheckBox) findViewById(R.id.highBloodPressure_checkbox);
        mHighCholestrol =  (CheckBox) findViewById(R.id.highCholesterol_checkbox);
        mOther = (EditText) findViewById(R.id.other_edit_text);
        mGeneticDiseaseEditText = (EditText) findViewById(R.id.genetic_edit_text);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = MainActivity.username;
    }

    private void extractUserInputsFromUI() {
        allergens = Arrays.asList(mAllergens.getText().toString().trim().split(","));
        resistance = Arrays.asList(mResistance.getText().toString().trim().split(","));
        pregnancy = mPregnancy.isChecked();
        diabetes = mDiabetes.isChecked();
        highbloodPressure = mHighBloodPressure.isChecked();
        highCholestrol = mHighCholestrol.isChecked();
        others = Arrays.asList(mOther.getText().toString().trim().split(","));
        geneticDisesase = Arrays.asList(mGeneticDiseaseEditText.getText().toString().trim().split(","));
    }

    private void saveUserDetails() {
        /*PatientHistory patientHistory = new PatientHistory(username, allergens, resistance,
                                            pregnancy, diabetes, highbloodPressure, highCholestrol, others, geneticDisesase);

        Gson gson = new Gson();
        String json = gson.toJson(patientHistory);

        URL url;
        HttpURLConnection client = null;
        try {
            url = new URL("https://api.illiteracy22.hasura-app.io/User_History_Upload/upload_user_history");
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        try {
            try {
                client = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }finally {

        }*/
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_track_record, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                extractUserInputsFromUI();
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener saveButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveUserDetails();
                                finish();
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(saveButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
