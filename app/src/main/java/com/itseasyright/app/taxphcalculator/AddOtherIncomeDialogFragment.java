package com.itseasyright.app.taxphcalculator;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.itseasyright.app.taxphcalculator.databinding.DialogAddOtherIncomeBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by jovijovs on 29/09/2016.
 */

public class AddOtherIncomeDialogFragment extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
    private IncomeDialogInterface dialogInterface;
    private DialogAddOtherIncomeBinding binder;
    private Integer rate = 0;
    private Double dailyRate = 0.0;
    private Double hourlyRate = 0.0;
    private DecimalFormat df;
    private String[] some_array;
    private Integer selectedType = 0;
    private Double income = 0.0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_save:
                if (income > 0) {
                    dialogInterface.onSaved(new IncomeModel(
                            binder.spinnerOtherIncomeType.getSelectedItemPosition(),
                            income));
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getDialog().getContext(),"Error, Invalid input!",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_close_dialog:
                getDialog().dismiss();
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedType = i;
        switch (i) {
            case 0:  //Regular Day w/ OT
                rate = 125;
                showOTPay();

                break;
            case 1:  //Regular Day w/ NightDiff
                rate = 100;
                showOTPay();

                break;
            case 2:  //Regular Holiday
                rate = 200;
                showHolidayPay(i);


                break;
            case 3:  //Regular Holiday w/ OT
                rate = 260;
                showOTPay();

                break;
            case 4:   //Regular Holiday w/ NightDIff
                rate = 200;
                showOTPay();

                break;
            case 5:  //Special Holiday
                rate = 130;
                showHolidayPay(i);

                break;
            case 6:  //Special Holiday w/ OT
                rate = 169;
                showOTPay();

                break;
            case 7:  //Special Holiday w/ NightDiff
                rate = 130;
                showOTPay();

                break;
            case 8:  //Rest Day
                rate = 130;
                showHolidayPay(i);

                break;
            case 9:  //Rest Day w/ OT
                rate = 169;
                showOTPay();

                break;
            case 10:  //Rest Day w/ NightDiff
                rate = 169;
                showOTPay();

                break;
            case 11: //Custom Rate OT
                showOTPay();
                binder.edittextRate.setText("");
                binder.edittextRate.setEnabled(true);

                break;
            case 12: //Custom Rate NightDiff
                showOTPay();
                binder.edittextRate.setText("");
                binder.edittextRate.setEnabled(true);

                break;
        }
    }

    public void showHolidayPay(int i) {
        Double temp = dailyRate * (rate * .01);
        income = i == 8 ? temp : dailyRate * ((rate - 100) * .01);
        binder.containerCount.setVisibility(View.GONE);
        binder.containerRate.setVisibility(View.GONE);
        binder.tvFormula.setText("(Daily Rate X "
                .concat(df.format(rate * .01))
                .concat(" = ")
                .concat(some_array[i])
                .concat(" Pay"));

        binder.tvComputation.setText("( "
                .concat(df.format(dailyRate))
                .concat(" X ")
                .concat(rate.toString())
                .concat("% ) = ")
                .concat(df.format(temp)));
    }

    public void showOTPay() {
        binder.edittextRate.setText(String.valueOf(rate));
        binder.edittextRate.setEnabled(false);
        binder.containerCount.setVisibility(View.VISIBLE);
        binder.containerRate.setVisibility(View.VISIBLE);
    }

    public void computeOTPay() {
        rate = binder.edittextRate.length() > 0 ? Integer.parseInt(binder.edittextRate.getText().toString()) : 0;
        income = (hourlyRate * (rate * .01)) * getValue(binder.edittextCount);
        binder.tvFormula.setText("( Rate/hr X "
                .concat(rate.toString())
                .concat("% ) X OT hrs = OT Pay"));

        binder.tvComputation.setText("( "
                .concat(df.format(hourlyRate))
                .concat(" X ")
                .concat(df.format(rate * .01))
                .concat(" ) X ")
                .concat(getValue(binder.edittextCount).toString())
                .concat(" = ")
                .concat(df.format(income)));
    }

    public void computeNightDiffPay() {
        rate = binder.edittextRate.length() > 0 ? Integer.parseInt(binder.edittextRate.getText().toString()) : 0;
        income = (((hourlyRate * (rate * .01)) * .10) * getValue(binder.edittextCount));
        binder.tvFormula.setText("( Rate/hr X "
                .concat(rate.toString())
                .concat("% X 10% ) X NightDiff hrs = NightDiff Pay"));

        binder.tvComputation.setText("(( "
                .concat(df.format(hourlyRate))
                .concat(" X ")
                .concat(df.format(rate * .01))
                .concat(" ) X .10) X ")
                .concat(getValue(binder.edittextCount).toString())
                .concat(" = ")
                .concat(df.format(income)));

    }

    public Double getValue(View view) {
        EditText temp = (EditText) view;
        return temp.length() > 0 ? Double.valueOf(temp.getText().toString()) : 0.0;
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        switch (selectedType) {
            case 0:
            case 3:
            case 6:
            case 9:
            case 11:
                computeOTPay();
                break;
            case 1:
            case 4:
            case 7:
            case 10:
            case 12:
                computeNightDiffPay();
                break;
        }

    }

    public interface IncomeDialogInterface {
        void onSaved(IncomeModel income);
    }

    public static AddOtherIncomeDialogFragment newInstance(IncomeDialogInterface dialogInterface) {
        AddOtherIncomeDialogFragment frag = new AddOtherIncomeDialogFragment();
        frag.dialogInterface = dialogInterface;
        return frag;
    }

    public void setDailyRate(Double dailyRate) {
        this.dailyRate = dailyRate;
        this.hourlyRate = dailyRate / 8;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binder = DataBindingUtil.inflate(inflater, R.layout.dialog_add_other_income, container, false);
        initialize();
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        df = (DecimalFormat) nf;
        df.applyPattern("###,###,###.00");
        some_array = getResources().getStringArray(R.array.other_income_selection);
        return binder.getRoot();
    }


    private void initialize() {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getDialog().getWindow().setDimAmount(0.8f);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binder.buttonCloseDialog.setOnClickListener(this);
        binder.buttonSave.setOnClickListener(this);
        binder.spinnerOtherIncomeType.setOnItemSelectedListener(this);
        binder.edittextCount.addTextChangedListener(this);
        binder.edittextRate.addTextChangedListener(this);

    }
}
