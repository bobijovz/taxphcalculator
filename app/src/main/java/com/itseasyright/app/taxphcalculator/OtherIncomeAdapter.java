package com.itseasyright.app.taxphcalculator;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itseasyright.app.taxphcalculator.databinding.ItemOtherIncomeLayoutBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by jovijovs on 29/09/2016.
 */

public class OtherIncomeAdapter extends RecyclerView.Adapter<OtherIncomeAdapter.ViewHolder> {
    private Context context;
    private ArrayList<IncomeModel> arrayIncome = new ArrayList<>();
    private String[] some_array;
    private DecimalFormat df;

    public OtherIncomeAdapter(Context context) {
        this.context = context;

    }

    public void addItem(IncomeModel income) {
        this.arrayIncome.add(income);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.arrayIncome.remove(position);
        notifyDataSetChanged();
    }

    public void clearItem() {
        this.arrayIncome.clear();
        notifyDataSetChanged();
    }

    public Double getOtherIncomeTotal() {
        Double total = 0.0;
        for (IncomeModel temp : arrayIncome) {
            total += temp.getIncome();
        }
        return total;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        some_array = context.getResources().getStringArray(R.array.other_income_selection);
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        df = (DecimalFormat) nf;
        df.applyPattern("###,###,###.00");
        return new ViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_other_income_layout, parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getBinding().itemType.setText(some_array[arrayIncome.get(position).getIndex()]);
        holder.getBinding().itemTotal.setText(df.format(arrayIncome.get(position).getIncome()));
    }

    @Override
    public int getItemCount() {
        return arrayIncome.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemOtherIncomeLayoutBinding binder;

        ViewHolder(View itemView) {
            super(itemView);
            binder = DataBindingUtil.bind(itemView);
        }

        public ItemOtherIncomeLayoutBinding getBinding() {
            return binder;
        }
    }
}
