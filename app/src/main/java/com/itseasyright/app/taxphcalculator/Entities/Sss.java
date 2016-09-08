package com.itseasyright.app.taxphcalculator.Entities;

import com.orm.SugarRecord;

/**
 * Created by dione on 8 Sep 2016.
 */
public class Sss extends SugarRecord {
    private double salaryRange;
    private double eE;
    private double eR;
    public Sss(){

    }

    public Sss(double salaryRange, double eE, double eR){
        this.salaryRange = salaryRange;
        this.eE = eE;
        this.eR = eR;
    }

    public void setSalaryRange(double salaryRange) {
        this.salaryRange = salaryRange;
    }

    public void seteE(double eE) {
        this.eE = eE;
    }

    public void seteR(double eR) {
        this.eR = eR;
    }

    public double getSalaryRange() {
        return salaryRange;
    }

    public double geteE() {
        return eE;
    }

    public double geteR() {
        return eR;
    }
}
