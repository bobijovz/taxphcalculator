package com.itseasyright.app.taxphcalculator;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.itseasyright.app.taxphcalculator.Entities.DateModifiedLogs;
import com.itseasyright.app.taxphcalculator.Entities.Philhealth;
import com.itseasyright.app.taxphcalculator.Entities.Sss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by dione on 7 Sep 2016.
 */
public class ReadFileFromServerAsync extends AsyncTask<String, Integer, String> {
    private DateModifiedLogs dateModifiedLogs;
    private Philhealth philhealth;
    private Sss sss;
    private boolean isModified = false;
    private Context context;
    private ITaxCalculator iTaxCalculator;

    public ReadFileFromServerAsync(Context context, ITaxCalculator iTaxCalculator) {
        this.context = context;
        this.iTaxCalculator = iTaxCalculator;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }

    @Override
    protected String doInBackground(String... strings) {
        return readTextFileFromPasteBin();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        iTaxCalculator.OnFinishLoadingData(true);
    }

    private String readTextFileFromPasteBin() {
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

    private void splitData(String data) {
        String[] headersArray = data.split("\\|\\|"); // SAMPLE DATA  DATE_MODIFIED:09-07-2016||PHILHEALTH:8000-100.00||SSS:||BIR:
        for (int i = 0; i < headersArray.length; i++) {
            dateModifiedLogs = new DateModifiedLogs();
            int recordCount = dateModifiedLogs.listAll(DateModifiedLogs.class).size();
            int dateModifiedRecordCount = dateModifiedLogs.listAll(DateModifiedLogs.class).size();
            int philhealthRecordCount = philhealth.listAll(Philhealth.class).size();
            int sssRecordCount = sss.listAll(Sss.class).size();
            switch (headersArray[i].split(":")[0]){ // SAMPLE DATA : DATE_MODIFIED:09-07-2016
                case "DATE_MODIFIED":
                    syncDateModified(headersArray[0].split(":"), dateModifiedRecordCount); // OUTPUT [DATE_MODIFIED][09-07-2016]
                    break;
                case "PHILHEALTH":
                    syncPhilhealth(headersArray[1].split(":"), philhealthRecordCount);
                    break;
                case "SSS":
                    syncSss(headersArray[2].split(":"), sssRecordCount);
                    break;
                case "BIR":
                    syncBir();
                    break;
                default:
                    break;

            }
        }
    }

    private void syncDateModified(String[] headersArray, int dateModifiedRecordCount) {
        String dateModifiedValue = headersArray[1]; // OUTPUT : 09-07-2016
        if (dateModifiedRecordCount < 1) {
            saveDateModifiedLog(dateModifiedValue); // Save Automatically if no data saved
            isModified = true;
        } else {
            DateModifiedLogs dateModifiedLastRecord = dateModifiedLogs.findById(DateModifiedLogs.class, dateModifiedRecordCount);
            if (dateModifiedLastRecord.getDateModified().equals(dateModifiedValue)) {
                //No modification on the data
                isModified = false;
            } else {
                //Have Modification on data
                updateDateModifiedLog(dateModifiedLastRecord, dateModifiedValue);
                isModified = true;
            }
        }
    }

    private void syncPhilhealth(String[] philhealthArray, int philhealthRecordCount) {
        if (isModified){
            String[] philhealthData = philhealthArray[1].split(","); // OUTPUT [0-100],[9000-112.5]
            if (philhealthRecordCount < 1){ // Save Automatically if no data saved
                for (int i=0; i<philhealthData.length; i++){
                    savePhilhealth(Double.valueOf(philhealthData[i].split("-")[0]), Double.valueOf(philhealthData[i].split("-")[1])); // savePhilhealth(0, 100)
                }
            }else{  //update records
                List<Philhealth> philhealthList = dateModifiedLogs.listAll(Philhealth.class);
                for (int i=0; i<philhealthList.size();i++){
                    philhealth = Philhealth.findById(Philhealth.class, philhealthList.get(i).getId());
                    updatePhilhealth(philhealth, Double.valueOf(philhealthData[i].split("-")[0]), Double.valueOf(philhealthData[i].split("-")[1]));
                }
            }
        }
    }
    private void syncSss(String[] sssArray, int sssRecordCount) {
        if (isModified){
            String[] sssData = sssArray[1].split(","); // OUTPUT 1000-36.3-83.7,1250-54.5-120.5
            if (sssRecordCount < 1){ // Save Automatically if no data saved
                for (int i=0; i<sssData.length; i++){
                    saveSss(Double.valueOf(sssData[i].split("-")[0]), Double.valueOf(sssData[i].split("-")[1]), Double.valueOf(sssData[i].split("-")[2])); // saveSss(1000, 36.3, 83.7)
                }
            }else{ //update records
                List<Sss> sssList = sss.listAll(Sss.class);
                for (int i=0; i<sssList.size();i++){
                    sss = Sss.findById(Sss.class, sssList.get(i).getId());
                    updateSss(sss, Double.valueOf(sssData[i].split("-")[0]), Double.valueOf(sssData[i].split("-")[1]), Double.valueOf(sssData[i].split("-")[2]));
                }
            }
        }
    }
    private void syncBir() {
        if (isModified){
            // code
        }
    }

    private void saveSss(double salaryRange, double eE, double eR){
        sss = new Sss(salaryRange, eE, eR);
        sss.save();
    }

    private void updateSss(Sss sss, double salaryRange, double eE, double eR){
        sss.setSalaryRange(salaryRange);
        sss.seteE(eE);
        sss.seteR(eR);
        sss.save();
    }
    private void savePhilhealth(double baseSalary, double share){
        philhealth = new Philhealth(baseSalary, share);
        philhealth.save();
    }

    private void updatePhilhealth(Philhealth philhealth, double baseSalary, double share){
        philhealth.setBaseSalary(baseSalary);
        philhealth.setShare(share);
        philhealth.save();
    }

    private void deletePhilhealthRecord(Philhealth philhealth){
        philhealth.delete();
    }

    private void updateDateModifiedLog(DateModifiedLogs dateModifiedLogs, String newValue) {
        dateModifiedLogs.setDateModified(newValue);
        dateModifiedLogs.save();
        Log.d("UPDATED", "YES");
    }

    private void saveDateModifiedLog(String dateModified) {
        dateModifiedLogs = new DateModifiedLogs(dateModified);
        dateModifiedLogs.save();
        Log.d("SAVED", "YES");
    }

}
