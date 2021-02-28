package com.example.zorovah.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zorovah.R;
import com.example.zorovah.chatActivity;
import com.example.zorovah.models.searchModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.viewHolder> {
   private  List<searchModel> search_list;
   public searchAdapter(List<searchModel> search_list){this.search_list=search_list;}
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.searchxml,parent,false);
       return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, final int position) {
        Glide.with(holder.itemView.getContext()).load(search_list.get(position).getImage()).centerCrop().placeholder(R.drawable.profile).into(holder.profile_image);
        holder.email.setText(search_list.get(position).getEmail());
        holder.userName.setText(search_list.get(position).getName());
        FirebaseAuth mauth=FirebaseAuth.getInstance();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), chatActivity.class);
                intent.putExtra("hisUid",search_list.get(position).getHisUid());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return search_list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{
       private CircleImageView profile_image;
       private TextView userName,email;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image=itemView.findViewById(R.id.profile_image);
            userName=itemView.findViewById(R.id.userName);
            email=itemView.findViewById(R.id.email);
        }
    }
}
