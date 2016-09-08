package com.itseasyright.app.taxphcalculator.Entities;

/**
 * Created by dione on 8 Sep 2016.
 */
public class BirSalaryDeductions {
    private String salaryRange;
    private String maritalStatus; // zero_exemption, zero_dependents, one_dependents, two_dependents, three_dependents
    private double percentage;
    private double exemption;
    public BirSalaryDeductions(){

    }
}
