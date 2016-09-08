package com.itseasyright.app.taxphcalculator;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.itseasyright.app.taxphcalculator.Entities.Philhealth;
import com.itseasyright.app.taxphcalculator.Entities.Sss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by dione on 7 Sep 2016.
 */
public class SyncDataActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);
        new ReadFileFromServerAsync(this).execute();

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


}
