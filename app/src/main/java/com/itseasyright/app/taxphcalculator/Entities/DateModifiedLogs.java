package com.itseasyright.app.taxphcalculator.Entities;

import com.orm.SugarRecord;

/**
 * Created by dione on 7 Sep 2016.
 */
public class DateModifiedLogs extends SugarRecord {
    private String dateModified;

    public DateModifiedLogs() {

    }

    public DateModifiedLogs(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }
}
