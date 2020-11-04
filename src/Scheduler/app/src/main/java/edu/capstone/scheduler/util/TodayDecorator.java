package edu.capstone.scheduler.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;

import edu.capstone.scheduler.R;


public class TodayDecorator implements DayViewDecorator {

    private CalendarDay date;

    public TodayDecorator(){
        date = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day){
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view){
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.4f));
        view.addSpan(new ForegroundColorSpan(Color.BLACK));
    }

    public void setDate(Date date){
        this.date = CalendarDay.from(date);
    }


}
