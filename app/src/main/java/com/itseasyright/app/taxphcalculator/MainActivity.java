package com.itseasyright.app.taxphcalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.itseasyright.app.taxphcalculator.Entities.BirSalaryDeductions;
import com.itseasyright.app.taxphcalculator.Entities.Philhealth;
import com.itseasyright.app.taxphcalculator.Entities.Sss;
import com.itseasyright.app.taxphcalculator.databinding.ActivityMainBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
    private ActivityMainBinding binder;

    private String selectedCivilStatus = "zeroexemption";
    private BirSalaryDeductions birContrib;
    private Double basicSalary = 0.0;
    private Double grossSalaryDeducted = 0.0;


    private Double sssContrib = 0.0;
    private Double phContrib = 0.0;
    private Double pagibigContrib = 100.0;
    private Double totalContrib = 0.0;

    private Double totalAllowance = 0.0;
    private Double taxableIncome = 0.0;

    private Double totalCalculation = 0.0;

    private Double ee;
    private Double er;
    private Double withholdingTax = 0.0;
    private Double netIncome = 0.0;

    private Double totalMisc = 0.0;
    private Double otPay = 0.0;
    private Double nightDiffPay = 0.0;
    private Double holidayPay = 0.0;

    private Double totalWageDeduct = 0.0;

    private Double dailyRate = 0.0;
    private DecimalFormat df;

    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        df = (DecimalFormat) nf;
        df.applyPattern("###,###,###.00");

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        binder.basic.headerBasic.setOnClickListener(this);
        binder.misc1.headerMisc.setOnClickListener(this);
        binder.misc2.headerDeductions.setOnClickListener(this);
        binder.allowance.headerAllowance.setOnClickListener(this);
        binder.calculations.headerCalculations.setOnClickListener(this);
        binder.basic.spinnerCivilStatus.setOnItemSelectedListener(this);
        binder.basic.spinnerEmploymentStatus.setOnItemSelectedListener(this);
        binder.basic.spinnerSalaryPeriod.setOnItemSelectedListener(this);
        binder.basic.spinnerWorkingDays.setOnItemSelectedListener(this);
        binder.basic.edittextBasicSalary.addTextChangedListener(this);
        collapse(binder.misc1.contentMisc);
        collapse(binder.allowance.contentAllowance);
        binder.calculations.containerCalculations.setVisibility(View.GONE);
        expand(binder.basic.contentBasic);
        checkValues();
        binder.basic.spinnerCivilStatus.setSelection(1);
        basicSalary = Double.valueOf(binder.basic.edittextBasicSalary.getText().toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            binder.calculations.containerCalculations.setVisibility(View.GONE);
            resetFields();
            return true;
        } else if (id == R.id.action_calculate) {

            if (binder.basic.edittextBasicSalary.length() > 4) {
                binder.calculations.containerCalculations.setVisibility(View.VISIBLE);
                expandCalculations();
                computeContribution();
                computeResult();

            } else {
                Snackbar.make(binder.getRoot(), "Something went wrong, please try again", Snackbar.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void computeResult() {

        grossSalaryDeducted = (basicSalary + totalMisc) - totalWageDeduct;
        totalContrib = sssContrib + phContrib + pagibigContrib;
        taxableIncome = grossSalaryDeducted - totalContrib;
        Double taxPercent = birContrib.getPercentage() * .01;
        Double temp1 = taxableIncome - birContrib.getSalaryFloor();
        Double temp2 = temp1 * taxPercent;
        withholdingTax = temp2 + birContrib.getExemption();
        netIncome = taxableIncome - withholdingTax;


//        basicSalary
//        totalMisc
//        totalWageDeduct
//        grossSalaryDeducted
//        withholdingTax
//        sssContrib
//        phContrib
//        pagibigContrib
//        totalAllowance
//        netIncome
//

        binder.calculations.tvCalculationBasicSalary.setText(df.format(basicSalary));
        binder.calculations.tvCalculationTotalMisc.setText(df.format(totalMisc));
        binder.calculations.tvCalculationWageDeduction.setText(df.format(totalWageDeduct));
        binder.calculations.tvCalculationGrossSalary.setText(df.format(grossSalaryDeducted));
        binder.calculations.tvCalculationTax.setText(df.format(withholdingTax));
        binder.calculations.tvCalculationSss.setText(df.format(sssContrib));
        binder.calculations.tvCalculationPhil.setText(df.format(phContrib));
        binder.calculations.tvCalculationPagibig.setText(df.format(pagibigContrib));
        binder.calculations.tvCalculationAllowance.setText(df.format(totalAllowance));
        binder.calculations.tvCalculationNetPay.setText(df.format(netIncome));



//        Intent intent = new Intent(this, ResultActivity.class);
//        Bundle b = new Bundle();
//        b.putDouble("grosssalary", basicSalary);
//        b.putDouble("taxableincome", taxableIncome);
//        b.putDouble("totalcontribution", totalContrib);
//        b.putDouble("totalallowance", totalAllowance);
//        b.putDouble("totalmisc", totalMisc);
//        b.putDouble("withholding", withholdingTax);
//        b.putDouble("netIncome", netIncome);
//        intent.putExtras(b);
//        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btn_calculate:
//                if (binder.basic.edittextBasicSalary.length() > 4) {
//                    computeContribution();
//                    computeResult();
//                } else {
//                    Snackbar.make(binder.getRoot(), "Something went wrong, please try again", Snackbar.LENGTH_LONG).show();
//                }
//                break;
//            case R.id.btn_reset:
//                resetFields();
//                break;
            case R.id.header_basic:
                if (binder.basic.contentBasic.getVisibility() == View.GONE) {
                    expandBasic();
                } else {
                    collapseBasic();
                }

                break;
            case R.id.header_misc:
                if (binder.misc1.contentMisc.getVisibility() == View.GONE) {
                    expandMisc();
                } else {
                    collapseMisc();
                }

                break;
            case R.id.header_deductions:
                if (binder.misc2.contentDeductions.getVisibility() == View.GONE) {
                    expandDeduct();
                } else {
                    collapseDeduct();
                }
                break;
            case R.id.header_allowance:
                if (binder.allowance.contentAllowance.getVisibility() == View.GONE) {
                    expandAllowance();
                } else {
                    collapseAllowance();
                }
                break;

            case R.id.header_calculations:
//                if(binder.calculations.contentCalculations.getVisibility() == View.GONE){
                    expandCalculations();
//                }
//                else{
//                    collapseCalculations();
//                }
                break;
        }
    }

    public void collapseBasic() {
        if (binder.basic.edittextBasicSalary.length() > 0) {
            binder.basic.btnHeaderLabel.setText("BASIC SALARY  : ");
            binder.basic.tvHeaderTotal.setText(df.format(Double.valueOf(binder.basic.edittextBasicSalary.getText().toString())));
            binder.basic.tvHeaderTotal.setVisibility(View.VISIBLE);
            basicSalary = Double.valueOf(binder.basic.edittextBasicSalary.getText().toString());
        }
        collapse(binder.basic.contentBasic);
        imm.hideSoftInputFromWindow(binder.getRoot().getWindowToken(), 0);
    }

    public void expandBasic() {
        binder.basic.btnHeaderLabel.setText("BASIC DETAILS");
        binder.basic.tvHeaderTotal.setVisibility(View.GONE);
        collapseMisc();
        collapseDeduct();
        collapseAllowance();
        expand(binder.basic.contentBasic);
        binder.calculations.containerCalculations.setVisibility(View.GONE);


    }

    public void collapseMisc() {
        Double hourlyRate = dailyRate / 8;
        Double nightDiffRate = binder.misc1.edittextNightDiffRate.length() > 0 ? Double.valueOf(binder.misc1.edittextNightDiffRate.getText().toString()) : 1.30;
        Double otHrs = binder.misc1.edittextOvertimePay.getText().length() > 0 ?  Double.valueOf(binder.misc1.edittextOvertimePay.getText().toString()) : 0.0;
        Double nightDiffHrs = binder.misc1.edittextNightDifferential.getText().length() > 0 ? Double.valueOf(binder.misc1.edittextNightDifferential.getText().toString()) : 0.0;
        Double holidayCount = binder.misc1.edittextHolidayPay.length() > 0 ? Double.valueOf(binder.misc1.edittextHolidayPay.getText().toString()) : 0.0;
        Double holidayRate = binder.misc1.spinnerHolidayType.getSelectedItemPosition() == 0 ? 1.0 : 0.30;
        holidayPay = (holidayCount * dailyRate) * holidayRate;
        otPay = otHrs * 1.25 * hourlyRate;
        nightDiffPay = nightDiffHrs * nightDiffRate * hourlyRate ;

        totalMisc = otPay + nightDiffPay + holidayPay;
        binder.misc1.tvHeaderMiscLabel.setText("MISCELLANEOUS  : ");
        binder.misc1.tvHeaderMiscTotal.setText(df.format(totalMisc));
        binder.misc1.tvHeaderMiscTotal.setVisibility(View.VISIBLE);
        collapse(binder.misc1.contentMisc);
        imm.hideSoftInputFromWindow(binder.getRoot().getWindowToken(), 0);

    }

    public void expandMisc() {
        expand(binder.misc1.contentMisc);
        binder.misc1.tvHeaderMiscLabel.setText("MISCELLANEOUS");
        binder.misc1.tvHeaderMiscTotal.setVisibility(View.GONE);

        collapseAllowance();
        collapseBasic();
        collapseDeduct();
        binder.calculations.containerCalculations.setVisibility(View.GONE);

    }

    public void collapseDeduct() {
        Integer absentCount = binder.misc2.edittextAbsent.length() > 0 ? Integer.valueOf(binder.misc2.edittextAbsent.getText().toString()) : 0;
        Double absentDeduct = absentCount * dailyRate;
        Double tardinessCount = binder.misc2.edittextTardiness.length() > 0 ? Double.valueOf(binder.misc2.edittextTardiness.getText().toString()) : 0;
        Double tardinessDeduct = (tardinessCount/60) * (dailyRate/8);
        Log.d("tardiness",tardinessDeduct.toString());
        Double undertimeCount = binder.misc2.edittextUndertime.length() > 0 ? Double.valueOf(binder.misc2.edittextUndertime.getText().toString()) : 0;
        Double undertimeDeduct = (undertimeCount/60) * (dailyRate/8);
        Log.d("undertime",undertimeDeduct.toString());
        totalWageDeduct = absentDeduct + tardinessDeduct + undertimeDeduct;

        binder.misc2.tvHeaderDeductLabel.setText("DEDUCTIONS  : ");
        binder.misc2.tvHeaderDeductTotal.setText(df.format(totalWageDeduct));
        binder.misc2.tvHeaderDeductTotal.setVisibility(View.VISIBLE);
        collapse(binder.misc2.contentDeductions);
        imm.hideSoftInputFromWindow(binder.getRoot().getWindowToken(), 0);

    }

    public void expandDeduct() {
        expand(binder.misc2.contentDeductions);
        binder.misc2.tvHeaderDeductLabel.setText("WAGE DEDUCTIONS");
        binder.misc2.tvHeaderDeductTotal.setVisibility(View.GONE);

        collapseAllowance();
        collapseBasic();
        collapseMisc();
        binder.calculations.containerCalculations.setVisibility(View.GONE);

    }

    public void collapseAllowance() {
        Double mealAllowance = binder.allowance.edittextMeal.length() > 0 ? Double.valueOf(binder.allowance.edittextMeal.getText().toString()) : 0;
        Double transpoAllowance = binder.allowance.edittextTransportation.length() > 0 ? Double.valueOf(binder.allowance.edittextTransportation.getText().toString()) : 0;
        Double cola = binder.allowance.edittextCola.length() > 0 ? Double.valueOf(binder.allowance.edittextCola.getText().toString()) : 0;
        Double taxShield = binder.allowance.edittextTaxShielded.length() > 0 ? Double.valueOf(binder.allowance.edittextTaxShielded.getText().toString()) : 0;

        totalAllowance = mealAllowance + transpoAllowance + cola + taxShield;

        binder.allowance.tvHeaderAllowanceLabel.setText("ALLOWANCE  : ");
        binder.allowance.tvHeaderAllowanceTotal.setText(df.format(totalAllowance));
        binder.allowance.tvHeaderAllowanceTotal.setVisibility(View.VISIBLE);
        collapse(binder.allowance.contentAllowance);
        imm.hideSoftInputFromWindow(binder.getRoot().getWindowToken(), 0);

    }

    public void expandAllowance() {
        binder.allowance.tvHeaderAllowanceLabel.setText("ALLOWANCE");
        binder.allowance.tvHeaderAllowanceTotal.setVisibility(View.GONE);

        collapseMisc();
        expand(binder.allowance.contentAllowance);
        collapseBasic();
        collapseDeduct();
        binder.calculations.containerCalculations.setVisibility(View.GONE);

    }

    public void collapseCalculations(){
        binder.calculations.tvHeaderCalculationsLabel.setText("CALCULATIONS  : ");
        binder.calculations.tvHeaderCalculationsTotal.setText(df.format(totalCalculation));
        binder.calculations.tvHeaderCalculationsTotal.setVisibility(View.VISIBLE);
        collapse(binder.calculations.contentCalculations);
        imm.hideSoftInputFromWindow(binder.getRoot().getWindowToken(), 0);
    }

    public void expandCalculations()
    {
        binder.calculations.tvHeaderCalculationsLabel.setText("CALCULATIONS");
        binder.calculations.tvHeaderCalculationsTotal.setVisibility(View.GONE);

        collapseAllowance();
        expand(binder.calculations.contentCalculations);
        collapseMisc();
        collapseBasic();
        collapseDeduct();
        collapseBasic();

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
        if (binder.basic.edittextBasicSalary.length() <= 4) {
            binder.basic.spinnerCivilStatus.setEnabled(false);
            binder.basic.spinnerEmploymentStatus.setEnabled(false);
            binder.basic.spinnerSalaryPeriod.setEnabled(false);
            binder.basic.edittextBasicSalary.setError("Please input valid basic salary");
//            binder.btnCalculate.setEnabled(false);
            binder.misc1.headerMisc.setEnabled(false);
            binder.misc2.headerDeductions.setEnabled(false);
            binder.allowance.headerAllowance.setEnabled(false);
            binder.basic.spinnerWorkingDays.setEnabled(false);
            basicSalary = 0.0;
        } else {
            binder.basic.edittextBasicSalary.setError(null);
            binder.basic.spinnerCivilStatus.setEnabled(true);
            binder.basic.spinnerEmploymentStatus.setEnabled(true);
            binder.basic.spinnerSalaryPeriod.setEnabled(true);
//            binder.btnCalculate.setEnabled(true);
            binder.misc1.headerMisc.setEnabled(true);
            binder.misc2.headerDeductions.setEnabled(true);
            binder.allowance.headerAllowance.setEnabled(true);
            binder.basic.spinnerWorkingDays.setEnabled(true);
        }

    }



    public void computeContribution() {
        if (binder.basic.edittextBasicSalary.length() > 4) {
            String basic_salary = String.valueOf(binder.basic.edittextBasicSalary.getText());
            List<Sss> sssList = Sss.findWithQuery(Sss.class, "select * from sss where salary_range < ? order by id DESC limit 1", basic_salary);
            List<Philhealth> philhealthList = Philhealth.findWithQuery(Philhealth.class, "select * from philhealth where base_salary <= ? order by id DESC limit 1", basic_salary);
            List<BirSalaryDeductions> birList = BirSalaryDeductions.findWithQuery(BirSalaryDeductions.class, "select * from bir_salary_deductions where salary_ceiling >= ? and salary_floor <= ? and marital_status = ?", basic_salary, basic_salary, selectedCivilStatus);
            if (binder.basic.spinnerWorkingDays.getSelectedItemPosition() == 0){
                dailyRate = (basicSalary * 12)/261;
            } else {
                dailyRate = (basicSalary * 12)/313;
            }
            binder.basic.textviewDailyRate.setText(df.format(dailyRate));
            if (sssList != null && philhealthList != null && birList != null) {
                ee = sssList.get(0).geteE();
                er = sssList.get(0).geteR();
                sssContrib = binder.basic.spinnerEmploymentStatus.getSelectedItemPosition() == 0 ? ee : er;
                phContrib = philhealthList.get(0).getShare();
                birContrib = birList.get(0);
                // withholdingTax = taxableIncome - birContrib.getSalaryFloor();
                Log.d("bir", String.valueOf(birList.get(0).getId()));

                if (binder.basic.spinnerSalaryPeriod.getSelectedItemPosition() == 1) {
                    binder.basic.textviewSssContrib.setText(df.format(sssContrib));
                    binder.basic.textviewPhilhealthContrib.setText(df.format(phContrib));
                    binder.basic.textviewPagibigContrib.setText(df.format(pagibigContrib));
                } else {
                    binder.basic.textviewSssContrib.setText(df.format(sssContrib / 2));
                    binder.basic.textviewPhilhealthContrib.setText(df.format(phContrib / 2));
                    binder.basic.textviewPagibigContrib.setText(df.format(pagibigContrib));
                }
            } else {
                Snackbar.make(binder.getRoot(), "Something went wrong, please try again", Snackbar.LENGTH_LONG).show();
            }
        }
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        checkValues();
        if (editable.length() > 4) {
            basicSalary = Double.parseDouble(editable.toString());
            computeContribution();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_salary_period:
            case R.id.spinner_employment_status:
            case R.id.spinner_working_days:
                computeContribution();
                break;
            case R.id.spinner_civil_status:
                if (i == 0) {
                    selectedCivilStatus = "zeroexemption";
                } else if (i == 2 || i == 1) {
                    selectedCivilStatus = "zerodependents";
                } else if (i == 3) {
                    //one dependent
                    selectedCivilStatus = "onedependents";
                } else if (i == 4) {
                    //two dependent
                    selectedCivilStatus = "twodependents";
                } else if (i == 5) {
                    //three dependent
                    selectedCivilStatus = "threedependents";
                } else if (i == 6) {
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
}
