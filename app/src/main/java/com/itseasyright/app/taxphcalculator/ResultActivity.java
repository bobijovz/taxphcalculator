package com.itseasyright.app.taxphcalculator;

import android.databinding.DataBindingUtil;
import android.databinding.tool.DataBinder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.itseasyright.app.taxphcalculator.Entities.BirSalaryDeductions;
import com.itseasyright.app.taxphcalculator.databinding.ActivityResultBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nico on 9/8/2016.
 */
public class ResultActivity extends AppCompatActivity {
    ActivityResultBinding binder;

    Double taxableIncome;
    Double totalContribution, totalAllowance, grossSalary, netIncome, withholding, totalMisc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = DataBindingUtil.setContentView(ResultActivity.this, R.layout.activity_result);
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat df = (DecimalFormat)nf;
        df.applyPattern("###,###,###.00");

        Bundle b = getIntent().getExtras();
        grossSalary = b.getDouble("grosssalary");
        taxableIncome = b.getDouble("taxableincome");
        totalContribution = b.getDouble("totalcontribution");
        totalAllowance = b.getDouble("totalallowance");
        totalMisc = b.getDouble("totalmisc");
        withholding = b.getDouble("withholding");
        netIncome = b.getDouble("netIncome");

        binder.tvGrossSalary.setText(df.format(grossSalary));
        binder.tvTaxableIncome.setText(df.format(taxableIncome));
        binder.tvTotalContribution.setText(df.format(totalContribution));
        binder.tvTotalMisc.setText(df.format(totalMisc));
        binder.tvTotalAllowance.setText(df.format(totalAllowance));
        binder.tvWithholdingTax.setText(df.format(withholding));
        binder.tvNetSalary.setText(df.format(netIncome));

    }

}
