package com.itseasyright.app.taxphcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by dione on 7 Sep 2016.
 */
public class SyncDataActivity extends AppCompatActivity implements ITaxCalculator {

    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        new ReadFileFromServerAsync(getApplicationContext(), this).execute();

        //sss query
//        List<Sss> sssList = Sss.findWithQuery(Sss.class, "select * from sss where salary_range > ? limit 1", "800");
//        for (int i=0;i<sssList.size();i++){
//            Log.d("sss_details", String.valueOf(sssList.get(i).getSalaryRange()));
//            Log.d("sss_details", String.valueOf(sssList.get(i).geteE()));
//            Log.d("sss_details", String.valueOf(sssList.get(i).geteR()));
//        }

        //philhealth query
//        List<Philhealth> philhealthList = Philhealth.findWithQuery(Philhealth.class, "select * from philhealth where base_salary < ? order by id DESC limit 1", "10100");
//        for (int i=0;i<philhealthList.size();i++){
//            Log.d("philhealth_details", String.valueOf(philhealthList.get(i).getBaseSalary()));
//            Log.d("philhealth_details", String.valueOf(philhealthList.get(i).getShare()));
//        }
    }

    @Override
    public void OnFinishLoadingData(boolean result) {
        if (result) {
            progressBar.setProgress(100);
            Intent mainClass = new Intent(SyncDataActivity.this, MainActivity.class);
            startActivity(mainClass);
            Toast.makeText(SyncDataActivity.this, "Data is up to date.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}
