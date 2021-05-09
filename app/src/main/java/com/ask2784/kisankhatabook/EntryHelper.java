package com.ask2784.kisankhatabook;

import com.google.firebase.Timestamp;

public class EntryHelper {

    private String Date, Implement, CropOROther, AreaORTime;
    double Rate, Total, Received, Remain,Times;
    Timestamp AddedTime;

    public EntryHelper() {
    }



    public EntryHelper(String date, String implement, String cropOROther, String areaORTime, double times, double rate, double total, double received, double remain, Timestamp addedTime) {
        Date = date;
        Implement = implement;
        CropOROther = cropOROther;
        AreaORTime = areaORTime;
        Rate = rate;
        Total = total;
        Received = received;
        Remain = remain;
        AddedTime = addedTime;
        Times = times;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getImplement() {
        return Implement;
    }

    public void setImplement(String implement) {
        Implement = implement;
    }

    public String getCropOROther() {
        return CropOROther;
    }

    public void setCropOROther(String cropOROther) {
        CropOROther = cropOROther;
    }

    public String getAreaORTime() {
        return AreaORTime;
    }

    public void setAreaORTime(String areaORTime) {
        AreaORTime = areaORTime;
    }

    public double getTimes() {
        return Times;
    }

    public void setTimes(double times) {
        Times = times;
    }

    public double getRate() {
        return Rate;
    }

    public void setRate(double rate) {
        Rate = rate;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public double getReceived() {
        return Received;
    }

    public void setReceived(double received) {
        Received = received;
    }

    public double getRemain() {
        return Remain;
    }

    public void setRemain(double remain) {
        Remain = remain;
    }

    public Timestamp getAddedTime() {
        return AddedTime;
    }

    public void setAddedTime(Timestamp addedTime) {
        AddedTime = addedTime;
    }

}
