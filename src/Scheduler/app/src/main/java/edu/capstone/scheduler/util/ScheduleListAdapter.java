package edu.capstone.scheduler.util;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import edu.capstone.scheduler.Activity.AddSchedule;
import edu.capstone.scheduler.Object.Date;
import edu.capstone.scheduler.Object.Schedule;
import edu.capstone.scheduler.R;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.RecyclerViewHolder> {
    List<Schedule> list;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
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

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView textView1, textView2, textView3;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Schedule schedule = list.get(pos);
                        Date date = schedule.getDate();
                        String dateStr = Integer.toString(date.getYear())+String.format("%02d",date.getMonth())+String.format("%02d",date.getDay());
                        ref = database.getReference("Schedule/").child(mUser.getUid()).child(dateStr).child(schedule.getUid());
//                        ref.removeValue();
//                        list.remove(pos);
//                        notifyItemRemoved(pos);
//                        notifyDataSetChanged();

                        final CharSequence[] items = {"수정","삭제"};
                        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                        dialog.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch(i){
                                    case 0: // 수정
                                        Intent intent = new Intent(view.getContext(), AddSchedule.class);
                                        intent.putExtra("schedule", schedule);
                                        view.getContext().startActivity(intent);

                                        break;
                                    case 1: // 삭제
                                        ref.removeValue();
                                        list.remove(pos);
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

