package com.parivartan.medico.activity;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.parivartan.medico.model.PatientDetail;

/**
 * Created by user on 08-Dec-17.
 */

/**
 * Loads a list of by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class PatientDetailLoader extends AsyncTaskLoader<PatientDetail> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = PatientDetailLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link PatientDetailLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public PatientDetailLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * This is on a background thread.
     */
    @Override
    public PatientDetail loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of.
        PatientDetail patientDetail = PatientDetailQueryUtils.fetchPatienDetailData(mUrl);
        return patientDetail;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
