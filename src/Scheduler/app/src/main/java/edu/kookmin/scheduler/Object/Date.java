package edu.kookmin.scheduler.Object;

import java.io.Serializable;

/**
 * Date 객체
 * @author - 구윤모, 이주형
 * @start - 2020.10.10
 * @finish - 2020.11.08
 */
public class Date implements Serializable {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public Date(){}
    public Date(int year, int month, int day, int hour, int minute) {
        this.year = year; this.month = month; this.day = day; this.hour = hour; this.minute = minute;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}