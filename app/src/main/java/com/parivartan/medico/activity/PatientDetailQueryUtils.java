package com.parivartan.medico.activity;

import com.parivartan.medico.model.PatientDetail;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Helper methods related to requesting and receiving.
 */
public final class PatientDetailQueryUtils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = PatientDetailQueryUtils.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link PatientDetailQueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name PatientDetailQueryUtils (and an object instance of PatientDetailQueryUtils is not needed).
     */
    private PatientDetailQueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of objects.
     */
    public static PatientDetail fetchPatienDetailData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of
        PatientDetail patientDetail = extractFeatureFromJson(jsonResponse);

        // Return the list of
        return patientDetail;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of objects that has been built up from
     * parsing the given JSON response.
     */
    private static PatientDetail extractFeatureFromJson(String patientDetailJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(patientDetailJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding
        PatientDetail patientDetail = new PatientDetail();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(patientDetailJSON);
            int code = baseJsonResponse.getInt("code");
            Log.e("Yash", code+"");

            if(code==401){

            } else if (code == 404) {

            }else if(code==200){
                JSONObject resultJsonResponse = baseJsonResponse.getJSONObject("result");

                patientDetail.setUsername(resultJsonResponse.getString("User_Name"));
                patientDetail.setName(resultJsonResponse.getString("name"));
                patientDetail.setGender(resultJsonResponse.getString("gender"));
                patientDetail.setDob(resultJsonResponse.getString("dob"));
                patientDetail.setAge(resultJsonResponse.getString("age"));
                patientDetail.setHeight(resultJsonResponse.getString("heght"));
                patientDetail.setWeight(resultJsonResponse.getString("weight"));
                patientDetail.setEmail(resultJsonResponse.getString("email"));
                patientDetail.setPhone(resultJsonResponse.getString("phoneNo"));
                patientDetail.setAddress(resultJsonResponse.getString("address"));
                patientDetail.setState(resultJsonResponse.getString("state"));
                patientDetail.setPincode(resultJsonResponse.getString("pinCode"));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("PatientDetailQueryUtils", "Problem parsing the JSON results", e);
        }

        // Return the list of
        return patientDetail;
    }
}