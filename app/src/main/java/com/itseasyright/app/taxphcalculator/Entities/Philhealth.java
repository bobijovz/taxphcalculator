package com.itseasyright.app.taxphcalculator.Entities;

import com.orm.SugarRecord;

/**
 * Created by dione on 7 Sep 2016.
 */
public class Philhealth extends SugarRecord{
    private double baseSalary;
    private double share;

    public Philhealth(){

    }

    public Philhealth(double baseSalary, double share){
        this.baseSalary = baseSalary;
        this.share = share;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public double getShare() {
        return share;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public void setShare(double share) {
        this.share = share;
    }
}
