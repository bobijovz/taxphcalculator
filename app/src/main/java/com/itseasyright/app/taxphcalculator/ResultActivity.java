package com.itseasyright.app.taxphcalculator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Nico on 9/8/2016.
 */
public class ResultActivity extends AppCompatActivity {

    TextView tvTaxableIncome, tvTotalContribution, tvTotalAllowance, tvGrossSalary;
    Double taxableIncome;
    Double totalContribution, totalAllowance, grossSalary;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tvGrossSalary = (TextView) findViewById(R.id.tv_gross_salary);
        tvTaxableIncome = (TextView) findViewById(R.id.tv_taxable_income);
        tvTotalContribution = (TextView) findViewById(R.id.tv_total_contribution);
        tvTotalAllowance = (TextView) findViewById(R.id.tv_total_allowance);

        Bundle b =getIntent().getExtras();
        grossSalary = b.getDouble("grosssalary");
        taxableIncome = b.getDouble("taxableincome");
        totalContribution = b.getDouble("totalcontribution");
        totalAllowance = b.getDouble("totalallowance");

        tvGrossSalary.setText(grossSalary.toString());
        tvTaxableIncome.setText(taxableIncome.toString());
        tvTotalContribution.setText(totalContribution.toString());
        tvTotalAllowance.setText(totalAllowance.toString());
    }
}
