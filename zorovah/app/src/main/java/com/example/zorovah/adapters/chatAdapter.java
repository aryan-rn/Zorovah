package com.example.zorovah.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zorovah.R;
import com.example.zorovah.models.chatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.viewHolder> {
    private List<chatModel> chatList;
    public chatAdapter(List<chatModel> chatList){this.chatList=chatList;}
    private int MSG_TYPE_LEFT=0;
    private int MSG_TYPE_RIGHT=1;
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.right_chat_row,parent,false);
            return new viewHolder(view);
        }
        else {View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.left_chat_row,parent,false);
            return new viewHolder(view);
        }
     }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
       String message=chatList.get(position).getMessage();
       String timeStamp=chatList.get(position).getTimestamp();

       Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
       calendar.setTimeInMillis(Long.parseLong(timeStamp));
       String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

       holder.textView.setText(message);
       holder.date.setText(dateTime);
       if(position==chatList.size()-1){
           if(chatList.get(position).isSeen()){
               holder.seen_stat.setText("Seen");

           }
           else {
               holder.seen_stat.setText("Delivered");
           }
       }
       else {
           holder.seen_stat.setVisibility(View.GONE);
       }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatList.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return MSG_TYPE_RIGHT;
        }
        return MSG_TYPE_LEFT;
    }

    class viewHolder extends RecyclerView.ViewHolder{
          private  TextView textView,date,seen_stat;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.message);
            date=itemView.findViewById(R.id.date);
            seen_stat=itemView.findViewById(R.id.seen_Stat);
        }
    }
}
