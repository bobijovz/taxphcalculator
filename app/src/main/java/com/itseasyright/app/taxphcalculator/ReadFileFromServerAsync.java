package com.itseasyright.app.taxphcalculator;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.itseasyright.app.taxphcalculator.Entities.DateModifiedLogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by dione on 7 Sep 2016.
 */
public class ReadFileFromServerAsync extends AsyncTask<String, String, String> {
    private DateModifiedLogs dateModifiedLogs;
    private boolean isModified = false;
    private Context context;
    public ReadFileFromServerAsync(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        return readTextFileFromPasteBin();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    private String readTextFileFromPasteBin(){
        String data = "";
        try {
            // Create a URL for the desired page
            final String PASTEBIN_DATA_URL = "http://pastebin.com/raw/WEcruBTD";
            URL url = new URL(PASTEBIN_DATA_URL);
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                // str is one line of text; readLine() strips the newline character(s)
                data = str;
            }
            splitData(data);
            in.close();
        } catch (IOException e) {
            Log.e("ERROR_MESSAGE", e.getLocalizedMessage());
            data = e.getLocalizedMessage();
        }
        return data;
    }

    private void splitData(String data){
        String[] headersArray = data.split("\\|\\|"); // SAMPLE DATA  DATE_MODIFIED:09-07-2016||PHILHEALTH:8000-100.00||SSS:||BIR:
        for (int i=0;i<headersArray.length;i++){
            dateModifiedLogs = new DateModifiedLogs();
            int recordCount = dateModifiedLogs.listAll(DateModifiedLogs.class).size();
            switch (headersArray[i].split(":")[0]){ // SAMPLE DATA : DATE_MODIFIED:09-07-2016
                case "DATE_MODIFIED":
                    syncDateModified(headersArray[0].split(":"), recordCount); // OUTPUT [DATE_MODIFIED][09-07-2016]
                    break;
                case "PHILHEALTH":
                    syncPhilhealth();
                    break;
                case "SSS":
                    syncSss();
                    break;
                case "BIR":
                    syncBir();
                    break;
                default:
                    break;

            }
        }
    }

    private void syncDateModified(String[] headersArray, int recordCount){
        String dateModifiedValue = headersArray[1]; // OUTPUT : 09-07-2016
        if (recordCount<1){
            saveDateModifiedLog(dateModifiedValue); // Save Automatically if no data saved
        }else{
            DateModifiedLogs dateModifiedLastRecord = dateModifiedLogs.findById(DateModifiedLogs.class, recordCount);
            if (dateModifiedLastRecord.getDateModified().equals(dateModifiedValue)){
                //No modification on the data
                isModified = false;
            }else{
                //Have Modification on data
                updateDateModifiedLog(dateModifiedLastRecord, dateModifiedValue);
                isModified = true;
            }
        }
    }

    private void syncPhilhealth(){
        if (isModified){
            // code
        }
    }
    private void syncSss(){
        if (isModified){
            // code
        }
    }
    private void syncBir(){
        if (isModified){
            // code
        }
    }

    private void updateDateModifiedLog(DateModifiedLogs dateModifiedLogs, String newValue){
        dateModifiedLogs.setDateModified(newValue);
        dateModifiedLogs.save();
        Log.d("UPDATED", "YES");
    }

    private void saveDateModifiedLog(String dateModified){
        dateModifiedLogs = new DateModifiedLogs(dateModified);
        dateModifiedLogs.save();
        Log.d("SAVED", "YES");
    }

}
