package com.itseasyright.app.taxphcalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.itseasyright.app.taxphcalculator.Entities.BirSalaryDeductions;
import com.itseasyright.app.taxphcalculator.Entities.Philhealth;
import com.itseasyright.app.taxphcalculator.Entities.Sss;
import com.itseasyright.app.taxphcalculator.databinding.ActivityMainBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher, AddOtherIncomeDialogFragment.IncomeDialogInterface {

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
    private Double ee;
    private Double er;
    private Double withholdingTax = 0.0;
    private Double netIncome = 0.0;
    private Double totalMisc = 0.0;
    private Double totalWageDeduct = 0.0;
    private Double dailyRate = 0.0;
    private DecimalFormat df;
    private OtherIncomeAdapter adapter;
    private Paint p = new Paint();

    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        df = (DecimalFormat) nf;
        df.applyPattern("###,###,###.00");

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binder.basic.headerBasic.setOnClickListener(this);
        binder.misc1.headerMisc.setOnClickListener(this);
        binder.misc2.headerDeductions.setOnClickListener(this);
        binder.allowance.headerAllowance.setOnClickListener(this);
        binder.calculations.headerCalculations.setOnClickListener(this);
        binder.basic.spinnerCivilStatus.setOnItemSelectedListener(this);
        binder.basic.spinnerEmploymentStatus.setOnItemSelectedListener(this);
        binder.basic.spinnerWorkingDays.setOnItemSelectedListener(this);
        binder.basic.edittextBasicSalary.addTextChangedListener(this);
        binder.misc1.imagebuttonAddIncome.setOnClickListener(this);
        binder.calculations.containerCalculations.setVisibility(View.GONE);
        collapse(binder.misc1.contentMisc);
        collapse(binder.allowance.contentAllowance);
        expand(binder.basic.contentBasic);
        checkValues();
        binder.basic.spinnerCivilStatus.setSelection(1);
        basicSalary = getViewValue(binder.basic.edittextBasicSalary);
        binder.calculations.spinnerCalculationsFrequency.setSelection(2);

        adapter = new OtherIncomeAdapter(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binder.misc1.recyclerviewOtherIncomeList.setLayoutManager(llm);
        binder.misc1.recyclerviewOtherIncomeList.setAdapter(adapter);
        initSwipe();
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
            if (getViewValue(binder.basic.edittextBasicSalary) > 9999) {
                binder.calculations.containerCalculations.setVisibility(View.VISIBLE);
                expandCalculations();
                computeContribution();
                computeResult();
            } else {
                Snackbar.make(binder.getRoot(), "Please input valid basic salary", Snackbar.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void computeResult() {

        double monthly = 1;
        double semiMonthly = 2;
        double yearly = 12;

        grossSalaryDeducted = (basicSalary + totalMisc) - totalWageDeduct;
        totalContrib = sssContrib + phContrib + pagibigContrib;
        taxableIncome = grossSalaryDeducted - totalContrib;
        Double taxPercent = birContrib.getPercentage() * .01;
        Double temp1 = taxableIncome - birContrib.getSalaryFloor();
        Double temp2 = temp1 * taxPercent;
        withholdingTax = temp2 + birContrib.getExemption();
        netIncome = taxableIncome - withholdingTax;

        if (binder.calculations.spinnerCalculationsFrequency.getSelectedItemPosition() == 0) {
            if (binder.basic.spinnerWorkingDays.getSelectedItemPosition() == 0) {
                setSalaryFrequencyDaily(12, 261);
            } else {
                setSalaryFrequencyDaily(12, 313);
            }
        }
        if (binder.calculations.spinnerCalculationsFrequency.getSelectedItemPosition() == 1) {
            setSalaryFrequencyDiv(semiMonthly);

        }
        if (binder.calculations.spinnerCalculationsFrequency.getSelectedItemPosition() == 2) {
            setSalaryFrequencyDiv(monthly);
        }
        if (binder.calculations.spinnerCalculationsFrequency.getSelectedItemPosition() == 3) {
            setSalaryFrequencyMult(yearly);
        }

        binder.calculations.spinnerCalculationsFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                computeResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void setSalaryFrequencyDaily(double a, double b) {
        binder.calculations.tvCalculationBasicSalary.setText(df.format((basicSalary * a) / b));
        binder.calculations.tvCalculationTotalMisc.setText(df.format((totalMisc * a) / b));
        binder.calculations.tvCalculationWageDeduction.setText(df.format((totalWageDeduct * a) / b));
        binder.calculations.tvCalculationGrossSalary.setText(df.format((grossSalaryDeducted * a) / b));
        binder.calculations.tvCalculationTax.setText(df.format((withholdingTax * a) / b));
        binder.calculations.tvCalculationSss.setText(df.format((sssContrib * a) / b));
        binder.calculations.tvCalculationPhil.setText(df.format((phContrib * a) / b));
        binder.calculations.tvCalculationPagibig.setText(df.format((pagibigContrib * a) / b));
        binder.calculations.tvCalculationAllowance.setText(df.format((totalAllowance * a) / b));
        binder.calculations.tvCalculationNetPay.setText(df.format((netIncome * a) / b));
    }

    public void setSalaryFrequencyDiv(double frequency) {
        binder.calculations.tvCalculationBasicSalary.setText(df.format(basicSalary / frequency));
        binder.calculations.tvCalculationTotalMisc.setText(df.format(totalMisc / frequency));
        binder.calculations.tvCalculationWageDeduction.setText(df.format(totalWageDeduct / frequency));
        binder.calculations.tvCalculationGrossSalary.setText(df.format(grossSalaryDeducted / frequency));
        binder.calculations.tvCalculationTax.setText(df.format(withholdingTax / frequency));
        binder.calculations.tvCalculationSss.setText(df.format(sssContrib / frequency));
        binder.calculations.tvCalculationPhil.setText(df.format(phContrib / frequency));
        binder.calculations.tvCalculationPagibig.setText(df.format(pagibigContrib / frequency));
        binder.calculations.tvCalculationAllowance.setText(df.format(totalAllowance / frequency));
        binder.calculations.tvCalculationNetPay.setText(df.format(netIncome / frequency));
    }

    public void setSalaryFrequencyMult(double frequency) {
        binder.calculations.tvCalculationBasicSalary.setText(df.format(basicSalary * frequency));
        binder.calculations.tvCalculationTotalMisc.setText(df.format(totalMisc * frequency));
        binder.calculations.tvCalculationWageDeduction.setText(df.format(totalWageDeduct * frequency));
        binder.calculations.tvCalculationGrossSalary.setText(df.format(grossSalaryDeducted * frequency));
        binder.calculations.tvCalculationTax.setText(df.format(withholdingTax * frequency));
        binder.calculations.tvCalculationSss.setText(df.format(sssContrib * frequency));
        binder.calculations.tvCalculationPhil.setText(df.format(phContrib * frequency));
        binder.calculations.tvCalculationPagibig.setText(df.format(pagibigContrib * frequency));
        binder.calculations.tvCalculationAllowance.setText(df.format(totalAllowance * frequency));
        binder.calculations.tvCalculationNetPay.setText(df.format(netIncome * frequency));
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
                expandCalculations();
                break;
            case R.id.imagebutton_add_income:
                Log.d("show", "dialog");
                AddOtherIncomeDialogFragment frag = AddOtherIncomeDialogFragment.newInstance(this);
                frag.setDailyRate(dailyRate);
                frag.show(getFragmentManager(), "ADD INCOME");
                break;

        }
    }

    public void collapseBasic() {
        if (binder.basic.edittextBasicSalary.length() > 0) {
            binder.basic.btnHeaderLabel.setText(R.string.text_basic_details_label);
            binder.basic.tvHeaderTotal.setText(df.format(getViewValue(binder.basic.edittextBasicSalary)));
            binder.basic.tvHeaderTotal.setVisibility(View.VISIBLE);
            basicSalary = getViewValue(binder.basic.edittextBasicSalary);
        }
        collapse(binder.basic.contentBasic);
        imm.hideSoftInputFromWindow(binder.getRoot().getWindowToken(), 0);
    }

    public void expandBasic() {
        binder.basic.btnHeaderLabel.setText(R.string.text_basic_details);
        binder.basic.tvHeaderTotal.setVisibility(View.GONE);
        collapseMisc();
        collapseDeduct();
        collapseAllowance();
        expand(binder.basic.contentBasic);
        binder.calculations.containerCalculations.setVisibility(View.GONE);
    }

    public void collapseMisc() {
        totalMisc = adapter.getOtherIncomeTotal();
        binder.misc1.tvHeaderMiscLabel.setText(R.string.text_miscellaneous_label);
        binder.misc1.tvHeaderMiscTotal.setText(df.format(totalMisc));
        binder.misc1.tvHeaderMiscTotal.setVisibility(View.VISIBLE);
        binder.misc1.imagebuttonAddIncome.setVisibility(View.GONE);
        collapse(binder.misc1.contentMisc);
        imm.hideSoftInputFromWindow(binder.getRoot().getWindowToken(), 0);
    }

    public void expandMisc() {
        expand(binder.misc1.contentMisc);
        binder.misc1.tvHeaderMiscLabel.setText(R.string.text_miscellaneous);
        binder.misc1.tvHeaderMiscTotal.setVisibility(View.GONE);
        binder.misc1.imagebuttonAddIncome.setVisibility(View.VISIBLE);
        collapseAllowance();
        collapseBasic();
        collapseDeduct();
        binder.calculations.containerCalculations.setVisibility(View.GONE);
    }

    public void collapseDeduct() {
        Double absentCount = getViewValue(binder.misc2.edittextAbsent);
        Double absentDeduct = absentCount * dailyRate;
        Double tardinessCount = getViewValue(binder.misc2.edittextTardiness);
        Double tardinessDeduct = (tardinessCount / 60) * (dailyRate / 8);
        Double undertimeCount = getViewValue(binder.misc2.edittextUndertime);
        Double undertimeDeduct = (undertimeCount / 60) * (dailyRate / 8);
        totalWageDeduct = absentDeduct + tardinessDeduct + undertimeDeduct;

        binder.misc2.tvHeaderDeductLabel.setText(R.string.text_deductiuons_label);
        binder.misc2.tvHeaderDeductTotal.setText(df.format(totalWageDeduct));
        binder.misc2.tvHeaderDeductTotal.setVisibility(View.VISIBLE);
        collapse(binder.misc2.contentDeductions);
        imm.hideSoftInputFromWindow(binder.getRoot().getWindowToken(), 0);
    }

    public void expandDeduct() {
        expand(binder.misc2.contentDeductions);
        binder.misc2.tvHeaderDeductLabel.setText(R.string.text_wage_deductions);
        binder.misc2.tvHeaderDeductTotal.setVisibility(View.GONE);
        collapseAllowance();
        collapseBasic();
        collapseMisc();
        binder.calculations.containerCalculations.setVisibility(View.GONE);
    }

    public void collapseAllowance() {
        Double mealAllowance = getViewValue(binder.allowance.edittextMeal);
        Double transpoAllowance = getViewValue(binder.allowance.edittextTransportation);
        Double cola = getViewValue(binder.allowance.edittextCola);
        Double taxShield = getViewValue(binder.allowance.edittextTaxShielded);
        totalAllowance = mealAllowance + transpoAllowance + cola + taxShield;
        binder.allowance.tvHeaderAllowanceLabel.setText(R.string.text_allowance_label);
        binder.allowance.tvHeaderAllowanceTotal.setText(df.format(totalAllowance));
        binder.allowance.tvHeaderAllowanceTotal.setVisibility(View.VISIBLE);
        collapse(binder.allowance.contentAllowance);
        imm.hideSoftInputFromWindow(binder.getRoot().getWindowToken(), 0);

    }

    public void expandAllowance() {
        binder.allowance.tvHeaderAllowanceLabel.setText(R.string.text_allowance);
        binder.allowance.tvHeaderAllowanceTotal.setVisibility(View.GONE);
        collapseMisc();
        expand(binder.allowance.contentAllowance);
        collapseBasic();
        collapseDeduct();
        binder.calculations.containerCalculations.setVisibility(View.GONE);
    }

    public void expandCalculations() {
        binder.calculations.tvHeaderCalculationsLabel.setText(R.string.text_header_calculations);
        collapseAllowance();
        collapseMisc();
        expand(binder.calculations.contentCalculations);
        collapseBasic();
        collapseDeduct();
        collapseBasic();

    }

    public void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

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
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binder.scrollview.scrollTo(0, v.getTop());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void checkValues() {
        if (getViewValue(binder.basic.edittextBasicSalary) < 10000) {
            binder.basic.spinnerCivilStatus.setEnabled(false);
            binder.basic.spinnerEmploymentStatus.setEnabled(false);
            binder.basic.edittextBasicSalary.setError("Please input valid basic salary");
            binder.misc1.headerMisc.setEnabled(false);
            binder.misc2.headerDeductions.setEnabled(false);
            binder.allowance.headerAllowance.setEnabled(false);
            binder.basic.spinnerWorkingDays.setEnabled(false);
            basicSalary = 0.0;
        } else {
            binder.basic.edittextBasicSalary.setError(null);
            binder.basic.spinnerCivilStatus.setEnabled(true);
            binder.basic.spinnerEmploymentStatus.setEnabled(true);
            binder.misc1.headerMisc.setEnabled(true);
            binder.misc2.headerDeductions.setEnabled(true);
            binder.allowance.headerAllowance.setEnabled(true);
            binder.basic.spinnerWorkingDays.setEnabled(true);
        }
    }

    public Double getViewValue(View view) {
        EditText temp = (EditText) view;
        return temp.length() > 0 ? Double.valueOf(temp.getText().toString()) : 0.0;
    }


    public void computeContribution() {
        if (getViewValue(binder.basic.edittextBasicSalary) > 9999) {
            String basic_salary = String.valueOf(binder.basic.edittextBasicSalary.getText());
            List<Sss> sssList = Sss.findWithQuery(Sss.class, "select * from sss where salary_range < ? order by id DESC limit 1", basic_salary);
            List<Philhealth> philhealthList = Philhealth.findWithQuery(Philhealth.class, "select * from philhealth where base_salary <= ? order by id DESC limit 1", basic_salary);
            List<BirSalaryDeductions> birList = BirSalaryDeductions.findWithQuery(BirSalaryDeductions.class, "select * from bir_salary_deductions where salary_ceiling >= ? and salary_floor <= ? and marital_status = ?", basic_salary, basic_salary, selectedCivilStatus);
            if (binder.basic.spinnerWorkingDays.getSelectedItemPosition() == 0) {
                dailyRate = (basicSalary * 12) / 261;
            } else {
                dailyRate = (basicSalary * 12) / 313;
            }
            binder.basic.textviewDailyRate.setText(df.format(dailyRate));
            if (sssList != null && philhealthList != null && birList != null) {
                ee = sssList.get(0).geteE();
                er = sssList.get(0).geteR();
                sssContrib = binder.basic.spinnerEmploymentStatus.getSelectedItemPosition() == 0 ? ee : er;
                phContrib = philhealthList.get(0).getShare();
                birContrib = birList.get(0);
            } else {
                Snackbar.make(binder.getRoot(), "Something went wrong, please try again", Snackbar.LENGTH_LONG).show();
            }
        }
    }


    public void resetFields() {
        binder.basic.edittextBasicSalary.setText("");
        binder.basic.tvHeaderTotal.setText(R.string.default_total_value);
        binder.basic.textviewDailyRate.setText(R.string.default_total_value);

        adapter.clearItem();
        binder.misc1.tvHeaderMiscTotal.setText(R.string.default_total_value);

        binder.misc2.edittextAbsent.setText("");
        binder.misc2.edittextTardiness.setText("");
        binder.misc2.edittextUndertime.setText("");
        binder.misc2.tvHeaderDeductTotal.setText(R.string.default_total_value);

        binder.allowance.edittextMeal.setText("");
        binder.allowance.edittextTransportation.setText("");
        binder.allowance.edittextCola.setText("");
        binder.allowance.edittextTaxShielded.setText("");
        binder.allowance.tvHeaderAllowanceTotal.setText(R.string.default_total_value);

        binder.calculations.spinnerCalculationsFrequency.setSelection(3);
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


    @Override
    public void onSaved(IncomeModel income) {
        adapter.addItem(income);
    }


    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    p.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_sweep_white_24dp);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                if (direction == ItemTouchHelper.LEFT) {
                    adapter.deleteItem(viewHolder.getAdapterPosition());
                }
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(binder.misc1.recyclerviewOtherIncomeList);
    }
}
