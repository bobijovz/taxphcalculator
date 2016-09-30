package com.itseasyright.app.taxphcalculator;

/**
 * Created by jovijovs on 29/09/2016.
 */

public class IncomeModel {
    private Integer index;
    private Double income;

    public  IncomeModel(){

    }

    public IncomeModel(Integer i, Double income){
        this.index = i;
        this.income = income;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }
}
