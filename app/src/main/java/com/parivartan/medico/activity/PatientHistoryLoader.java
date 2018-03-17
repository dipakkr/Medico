package com.parivartan.medico.activity;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.parivartan.medico.model.PatientHistory;

/**
 * Created by user on 08-Dec-17.
 */

/**
 * Loads a list of by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class PatientHistoryLoader extends AsyncTaskLoader<PatientHistory> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = PatientHistoryLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link PatientHistoryLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public PatientHistoryLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * This is on a background thread.
     */
    @Override
    public PatientHistory loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of.
        PatientHistory patientHistory = PatientHistoryQueryUtils.fetchPatientHistoryData(mUrl);
        return patientHistory;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
