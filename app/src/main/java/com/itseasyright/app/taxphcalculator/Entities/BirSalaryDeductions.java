package com.itseasyright.app.taxphcalculator.Entities;

import com.orm.SugarRecord;

/**
 * Created by dione on 8 Sep 2016.
 */
public class BirSalaryDeductions extends SugarRecord {
    private double salaryFloor;
    private double salaryCeiling;
    private String maritalStatus; // zero_exemption, zero_dependents, one_dependents, two_dependents, three_dependents
    private double percentage;
    private double exemption;

    public BirSalaryDeductions(){

    }

    public BirSalaryDeductions(double salaryFloor, double salaryCeiling, String maritalStatus, double percentage, double exemption){
        this.salaryFloor = salaryFloor;
        this.salaryCeiling = salaryCeiling;
        this.maritalStatus = maritalStatus;
        this.percentage = percentage;
        this.exemption = exemption;
    }

    public double getExemption() {
        return exemption;
    }

    public void setExemption(double exemption) {
        this.exemption = exemption;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getSalaryCeiling() {
        return salaryCeiling;
    }

    public void setSalaryCeiling(double salaryCeiling) {
        this.salaryCeiling = salaryCeiling;
    }

    public double getSalaryFloor() {
        return salaryFloor;
    }

    public void setSalaryFloor(double salaryFloor) {
        this.salaryFloor = salaryFloor;
    }
}
