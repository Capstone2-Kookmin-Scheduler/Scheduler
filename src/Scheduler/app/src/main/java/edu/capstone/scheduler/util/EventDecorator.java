package edu.capstone.scheduler.util;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

import edu.capstone.scheduler.R;

import static edu.capstone.scheduler.R.*;

/**
 * Decorate several days with a dot
 */
public class EventDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private int color;
    private HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates,Activity context) {
        drawable = context.getResources().getDrawable(R.drawable.more,null);
        this.color = color;
        this.dates = new HashSet<>(dates);
        Log.e("이벤트","는"+dates.size());
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        Log.e("EventDecorate","decorate");
        //view.setSelectionDrawable(drawable); // 테두리
        view.addSpan(new DotSpan(5, color)); // 날자밑에 점
    }
}
