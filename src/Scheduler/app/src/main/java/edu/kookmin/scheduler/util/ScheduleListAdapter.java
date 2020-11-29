package edu.kookmin.scheduler.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import edu.kookmin.scheduler.Activity.AddSchedule;
import edu.kookmin.scheduler.Activity.ShowMapActivity;
import edu.kookmin.scheduler.Object.Date;
import edu.kookmin.scheduler.Object.Schedule;
import edu.kookmin.scheduler.R;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.RecyclerViewHolder> {
    List<Schedule> list;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    public ScheduleListAdapter(List<Schedule> list) {
        this.list = list;
    }
    private Context mContext;
    private int lateCount;

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule, parent, false);
        mContext = parent.getContext();
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.textView1.setText(list.get(position).getName());
        holder.textView2.setText(list.get(position).getArrival_location());
        int hour = list.get(position).getDate().getHour();
        int minute = list.get(position).getDate().getMinute();
        int time = list.get(position).getTotal_time();
        holder.textView3.setText(util.calculateDepartureTime(hour,minute,time));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView textView1, textView2, textView3;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    GpsTracker gpsTracker = new GpsTracker(view.getContext());
                    Double lat = gpsTracker.getLat();
                    Double lng = gpsTracker.getLng();
                    Intent intent = new Intent(view.getContext(), ShowMapActivity.class);
                    intent.putExtra("lat",lat);
                    intent.putExtra("lng",lng);
                    intent.putExtra("arrival_lat",list.get(pos).getArrival_lat());
                    intent.putExtra("arrival_lng",list.get(pos).getArrival_lng());
                    intent.putExtra("isNoti",false);
                    view.getContext().startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Schedule schedule = list.get(pos);
                        Date date = schedule.getDate();
                        String dateStr = Integer.toString(date.getYear())+String.format("%02d",date.getMonth())+String.format("%02d",date.getDay());
                        ref = database.getReference("Schedule/").child(mUser.getUid()).child(dateStr).child(schedule.getUid());

                        final CharSequence[] items = {"수정","삭제"};
                        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                        dialog.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch(i){
                                    case 0: // 수정
                                        Intent intent = new Intent(view.getContext(), AddSchedule.class);
                                        intent.putExtra("schedule", schedule);
//                                        intent.putExtra("lateCount",lateCount);
                                        view.getContext().startActivity(intent);

                                        break;
                                    case 1: // 삭제
                                        ref.removeValue();
                                        list.remove(pos);
                                        int id = schedule.getDate().getHour()*60 + schedule.getDate().getMinute();
                                        util.removeAlarm(mContext, id);
                                        notifyItemRemoved(pos);
                                        notifyDataSetChanged();
                                        break;
                                }
                            }
                        });
                        dialog.create().show();
                    }
                    return false;
                }
            });
        }
    }

}  // end of class MainAdapter

