package edu.capstone.scheduler.Fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import edu.capstone.scheduler.Object.Schedule;
import edu.capstone.scheduler.R;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    //들어갈 데이터
    private ArrayList<String> listData = null;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView1;

        ViewHolder(View itemView){
            super(itemView);

            textView1 = itemView.findViewById(R.id.textView1);
        }
    }

    public ListAdapter(ArrayList<String> list){
        listData = list;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.activity_list, parent, false) ;
        ListAdapter.ViewHolder vh = new ListAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        String text = listData.get(position) ;
        holder.textView1.setText(text);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return listData.size() ;
    }
}


