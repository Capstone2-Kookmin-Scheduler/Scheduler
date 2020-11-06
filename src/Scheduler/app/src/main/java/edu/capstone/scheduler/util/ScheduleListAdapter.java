package edu.capstone.scheduler.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.capstone.scheduler.Object.Schedule;
import edu.capstone.scheduler.R;

public class ScheduleListAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    List<Schedule> list;

    public ScheduleListAdapter(List<Schedule> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.textView1.setText(list.get(position).getName());
        holder.textView2.setText(Integer.toString(list.get(position).getTotal_time()));
        holder.textView3.setText(list.get(position).getArrival_location());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }



}  // end of class MainAdapter

class RecyclerViewHolder extends RecyclerView.ViewHolder{
    TextView textView1, textView2, textView3;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        textView1 = itemView.findViewById(R.id.textView1);
        textView2 = itemView.findViewById(R.id.textView2);
        textView3 = itemView.findViewById(R.id.textView3);
    }
}