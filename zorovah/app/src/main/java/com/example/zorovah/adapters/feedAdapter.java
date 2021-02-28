package com.example.zorovah.adapters;

import android.icu.text.Transliterator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zorovah.R;
import com.example.zorovah.models.feedModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class feedAdapter extends RecyclerView.Adapter<feedAdapter.feedViewHolder> {
    private List<feedModel> list;
    public feedAdapter(List<feedModel> list){this.list=list;}
    @NonNull
    @Override
    public feedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post,parent,false);
        return new feedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull feedViewHolder holder, int position) {
   //you have to work here
        Glide.with(holder.itemView.getContext()).load(list.get(position).getUserprofile())
                .centerCrop().placeholder(R.drawable.profile)
                .into(holder.userprofile);
        Glide.with(holder.itemView.getContext()).load(list.get(position).getPostfeed())
                .into(holder.postfeed);
        holder.user.setText(list.get(position).getUser());
        holder.likescnt.setText("");
        holder.postdate.setText(list.get(position).getPostdate());
        holder.caption.setText(list.get(position).getCaption());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class feedViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView userprofile;
        private TextView user,likescnt,postdate,caption,commentTV;
        private ImageView postfeed,likebtn,commentbtn;


        public feedViewHolder(@NonNull View itemView) {
            super(itemView);
            userprofile=itemView.findViewById(R.id.userprofile);
            user=itemView.findViewById(R.id.user);
            likescnt=itemView.findViewById(R.id.likescnt);
            postdate=itemView.findViewById(R.id.postdate);
            caption=itemView.findViewById(R.id.caption);
            postfeed=itemView.findViewById(R.id.postfeed);
            postdate=itemView.findViewById(R.id.postdate);
            likebtn=itemView.findViewById(R.id.likebtn);
            commentbtn=itemView.findViewById(R.id.comment);
            commentTV=itemView.findViewById(R.id.commentTV);
            final int[] i = {0};
            likebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(i[0] ==0)
                    {likebtn.setImageResource(R.drawable.heartfilled);
                        i[0] =1;}
                    else{likebtn.setImageResource(R.drawable.heart);i[0]=0;}
                }
            });

        }
    }
}
