package com.itseasyright.app.taxphcalculator;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import com.itseasyright.app.taxphcalculator.Entities.BirSalaryDeductions;
import com.itseasyright.app.taxphcalculator.Entities.Philhealth;
import com.itseasyright.app.taxphcalculator.Entities.Sss;
import com.itseasyright.app.taxphcalculator.databinding.ActivityMainBinding;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
    private ActivityMainBinding binder;
    private String[] salaryPeriodArray;
    private String[] employmentStatusArray;
    private String[] civilStatusArray;
    private String selectedCivilStatus = "zeroexemption";
    private BirSalaryDeductions birContrib;
    private Double sssContrib = 0.0;
    private Double phContrib = 0.0;
    private Double pagibigContrib = 100.0;
    private Double grossSalary = 0.0;
    private Double totalContrib = 0.0;
    private Double totalAllowance = 0.0;
    private Double taxableIncome = 0.0;
    private Double mealAllowance = 0.0;
    private Double transpoAllowance = 0.0;
    private Double colAllowance = 0.0;
    private Double otherAllowance = 0.0;
    private Double taxShieldedAllowance = 0.0;
    private Double ee;
    private Double er;
    private Double withholdingTax = 0.0;
    private Double netIncome = 0.0;
    private Double totalMisc = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
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
        checkValues();
        binder.spinnerCivilStatus.setSelection(1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_calculate:
                if (binder.edittextBasicSalary.length() > 4) {
                    computeContribution();
                    computeResult();
                } else {
                    Snackbar.make(binder.getRoot(),"Something went wrong, please try again", Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_reset:
                resetFields();
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
        Log.d("you have entered", binder.edittextBasicSalary.getText().toString());
        checkValues();
        if (binder.edittextBasicSalary.getText().length() == 0) {
            Toast.makeText(MainActivity.this, "Please indicate Basic Salary.", Toast.LENGTH_SHORT).show();
            binder.btnCalculate.setEnabled(false);
            grossSalary = 0.0;
        } else {
            grossSalary = Double.parseDouble(binder.edittextBasicSalary.getText().toString());
            binder.btnCalculate.setEnabled(true);
        }
        //computeContribution();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_salary_period:
            case R.id.spinner_employment_status:
                computeContribution();
                break;
            case R.id.spinner_civil_status:
                if(i == 0){
                    selectedCivilStatus = "zeroexemption";
                }else if (i == 2 || i == 1){
                    selectedCivilStatus = "zerodependents";
                } else if(i == 3){
                    //one dependent
                    selectedCivilStatus = "onedependents";
                } else if(i == 4){
                    //two dependent
                    selectedCivilStatus = "twodependents";
                } else if(i == 5){
                    //three dependent
                    selectedCivilStatus = "threedependents";
                } else if(i == 6){
                    //four dependent
                    selectedCivilStatus = "fourdependents";
                }
                computeContribution();
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
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void checkValues() {
        if (binder.edittextBasicSalary.length() == 0) {
            binder.spinnerCivilStatus.setEnabled(false);
            binder.spinnerEmploymentStatus.setEnabled(false);
            binder.spinnerSalaryPeriod.setEnabled(false);
        } else {
            binder.spinnerCivilStatus.setEnabled(true);
            binder.spinnerEmploymentStatus.setEnabled(true);
            binder.spinnerSalaryPeriod.setEnabled(true);
        }

    }

    public void computeResult() {
        totalContrib = sssContrib + phContrib + pagibigContrib;
        taxableIncome = grossSalary - totalContrib;
        Log.d("taxableIncome", String.valueOf(taxableIncome));
        Double taxPercent = birContrib.getPercentage() * .01;
        Log.d("taxpercent", String.valueOf(taxPercent));
        Double temp1 = taxableIncome - birContrib.getSalaryFloor();
        Log.d("temp1", String.valueOf(temp1));
        Double temp2 = temp1 * taxPercent;
        Log.d("temp2", String.valueOf(temp2));
        withholdingTax = temp2 + birContrib.getExemption();
        Log.d("withholding", String.valueOf(withholdingTax));
        netIncome = taxableIncome - withholdingTax;
        Log.d("netIncome", String.valueOf(netIncome));
        computeTotalAllowance();
        computeTotalMisc();

        Intent intent = new Intent(this, ResultActivity.class);
        Bundle b = new Bundle();
        b.putDouble("grosssalary", grossSalary);
        b.putDouble("taxableincome", taxableIncome);
        b.putDouble("totalcontribution", totalContrib);
        b.putDouble("totalallowance", totalAllowance);
        b.putDouble("totalmisc", totalMisc);
        b.putDouble("withholding", withholdingTax);
        b.putDouble("netIncome",netIncome);
        intent.putExtras(b);
        startActivity(intent);

    }

    public void computeContribution() {
        if (binder.edittextBasicSalary.length() > 4) {
            String basic_salary = String.valueOf(binder.edittextBasicSalary.getText());
            List<Sss> sssList = Sss.findWithQuery(Sss.class, "select * from sss where salary_range < ? order by id DESC limit 1", basic_salary);
            List<Philhealth> philhealthList = Philhealth.findWithQuery(Philhealth.class, "select * from philhealth where base_salary < ? order by id DESC limit 1", basic_salary);
            List<BirSalaryDeductions> birList = BirSalaryDeductions.findWithQuery(BirSalaryDeductions.class, "select * from bir_salary_deductions where salary_ceiling >= ? and salary_floor <= ? and marital_status = ?", basic_salary, basic_salary, selectedCivilStatus);
            if (sssList != null && philhealthList != null && birList != null){
                ee = sssList.get(0).geteE();
                er = sssList.get(0).geteR();
                sssContrib = binder.spinnerEmploymentStatus.getSelectedItemPosition() == 0 ? ee : er;
                phContrib = philhealthList.get(0).getShare();
                birContrib = birList.get(0);
               // withholdingTax = taxableIncome - birContrib.getSalaryFloor();
                Log.d("bir", String.valueOf(birList.get(0).getId()));

                if (binder.spinnerSalaryPeriod.getSelectedItemPosition() == 1) {
                    binder.textviewSssContrib.setText(String.valueOf(sssContrib));
                    binder.textviewPhilhealthContrib.setText(String.valueOf(phContrib));
                    binder.textviewPagibigContrib.setText(String.valueOf(pagibigContrib));
                } else {
                    binder.textviewSssContrib.setText(String.valueOf(String.valueOf(sssContrib / 2)));
                    binder.textviewPhilhealthContrib.setText(String.valueOf(phContrib / 2));
                    binder.textviewPagibigContrib.setText(String.valueOf(pagibigContrib));
                }
            } else {
                Snackbar.make(binder.getRoot(),"Something went wrong, please try again", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void computeTotalAllowance() {
        if (!binder.edittextMeal.getText().toString().isEmpty()) {
            mealAllowance = Double.parseDouble(binder.edittextMeal.getText().toString());
        }

        if (!binder.edittextTransportation.getText().toString().isEmpty()) {
            transpoAllowance = Double.parseDouble(binder.edittextTransportation.getText().toString());
        }

        if (!binder.edittextCola.getText().toString().isEmpty()) {
            colAllowance = Double.parseDouble(binder.edittextCola.getText().toString());
        }

        if (!binder.edittextOther.getText().toString().isEmpty()) {
            otherAllowance = Double.parseDouble(binder.edittextOther.getText().toString());
        }

        if (!binder.edittextTaxShielded.getText().toString().isEmpty()) {
            taxShieldedAllowance = Double.parseDouble(binder.edittextTaxShielded.getText().toString());
        }
        totalAllowance = mealAllowance + transpoAllowance + colAllowance + otherAllowance + taxShieldedAllowance;
    }

    public void computeTotalMisc() {

    }

    public void resetFields() {
//        binder.edittextBasicSalary.setText("0.0");
//        binder.textviewSssContrib.setText("0.0");
//        binder.textviewPhilhealthContrib.setText("0.0");
//        binder.textviewPagibigContrib.setText("0.0");
//        binder.edittextMeal.setText(String.valueOf(mealAllowance));
//        binder.edittextTransportation.setText(String.valueOf(transpoAllowance));
//        binder.edittextCola.setText(String.valueOf(colAllowance));
//        binder.edittextOther.setText(String.valueOf(otherAllowance));
//        binder.edittextTaxShielded.setText(String.valueOf(taxShieldedAllowance));
    }
}
