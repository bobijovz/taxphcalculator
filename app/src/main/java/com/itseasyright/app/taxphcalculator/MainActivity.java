package com.itseasyright.app.taxphcalculator;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itseasyright.app.taxphcalculator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
    private ActivityMainBinding binder;
    private String[] salaryPeriodArray;
    private String[] employmentStatusArray;
    private String[] civilStatusArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = DataBindingUtil.setContentView(MainActivity.this,R.layout.activity_main);
        salaryPeriodArray = getResources().getStringArray(R.array.string_array_salary_period);
        employmentStatusArray = getResources().getStringArray(R.array.string_array_employment_status);
        civilStatusArray = getResources().getStringArray(R.array.string_array_civil_status);
        binder.btnCalculate.setOnClickListener(this);
        binder.btnReset.setOnClickListener(this);
        binder.headerBasic.setOnClickListener(this);
        binder.headerMisc.setOnClickListener(this);
        binder.headerAllowance.setOnClickListener(this);
        binder.spinnerCivilStatus.setOnItemSelectedListener(this);
        binder.spinnerEmploymentStatus.setOnItemSelectedListener(this);
        binder.spinnerSalaryPeriod.setOnItemSelectedListener(this);
        binder.edittextBasicSalary.addTextChangedListener(this);
        collapse(binder.contentMisc);
        collapse(binder.contentAllowance);
        expand(binder.contentBasic);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_calculate:
                Toast.makeText(this,"try",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ResultActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_reset:

                break;
            case R.id.header_basic:
                collapse(binder.contentMisc);
                collapse(binder.contentAllowance);
                expand(binder.contentBasic);
                break;
            case R.id.header_misc:
                expand(binder.contentMisc);
                collapse(binder.contentAllowance);
                collapse(binder.contentBasic);
                break;
            case R.id.header_allowance:
                collapse(binder.contentMisc);
                expand(binder.contentAllowance);
                collapse(binder.contentBasic);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.d("you have entered",binder.edittextBasicSalary.getText().toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(adapterView.getId()){
            case R.id.spinner_salary_period:
                if (i == 0){
                    Log.d("position",salaryPeriodArray[i]);
                } else {
                    Log.d("position",salaryPeriodArray[i]);
                }
                break;
            case R.id.spinner_employment_status:
                if (i == 0){
                    Log.d("position",employmentStatusArray[i]);
                } else {
                    Log.d("position",employmentStatusArray[i]);
                }
                break;
            case R.id.spinner_civil_status:
                Log.d("position",civilStatusArray[i]);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


}
