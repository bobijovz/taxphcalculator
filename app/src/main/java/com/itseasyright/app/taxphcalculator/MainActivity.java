package com.itseasyright.app.taxphcalculator;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.itseasyright.app.taxphcalculator.databinding.ActivityMainBinding;

public class MainActivity extends Activity implements View.OnClickListener {
    private ActivityMainBinding binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binder.btnCalculate.setOnClickListener(this);
        binder.btnReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_calculate:
                Toast.makeText(this,"try",Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_reset:

                break;
        }
    }

}
