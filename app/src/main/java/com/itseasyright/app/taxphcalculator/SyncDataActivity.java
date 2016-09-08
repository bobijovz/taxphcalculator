package com.itseasyright.app.taxphcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dione on 7 Sep 2016.
 */
public class SyncDataActivity extends AppCompatActivity implements ITaxCalculator {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);
        new ReadFileFromServerAsync(getApplicationContext(), this).execute();
    }

    @Override
    public void OnFinishLoadingData(boolean result) {
        if (result) {
            Intent mainClass = new Intent(SyncDataActivity.this, MainActivity.class);
            startActivity(mainClass);
            finish();
        }

    }
}
