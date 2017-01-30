package uk.ac.cam.km662.hackcambridge2017;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kamile on 30/01/2017.
 */

public class GetBotResponseAsync extends AsyncTask<String, String, String> {

    URL url;
    //read the chat bot response
    private String readStream(InputStream in) {
        char[] buf = new char[2048];
        Reader r = null;
        try {
            r = new InputStreamReader(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder s = new StringBuilder();
        while (true) {
            int n = 0;
            try {
                n = r.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (n < 0)
                break;
            s.append(buf, 0, n);
        }

        Log.w("streamValue",s.toString());
        return s.toString();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... args) {
        String responseValue = "";
        try {
            url = new URL(args[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            System.err.println("Got the well formed Url");
            String basicAuth = "Bearer " + args[1];
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            int responseCode = urlConnection.getResponseCode(); //can call this instead of con.connect()
            if (responseCode >= 400 && responseCode <= 499) {
                throw new Exception("Bad authentication status: " + responseCode); //provide a more meaningful exception message
            }
            else {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                responseValue = readStream(in);
                Log.w("responseMsg ",responseValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

        return responseValue;
    }

    @Override
    protected void onPostExecute(String args) {

    }
}
