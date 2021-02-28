package com.example.zorovah.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zorovah.R;
import com.example.zorovah.models.storyModel;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class storiesAdapter extends RecyclerView.Adapter<storiesAdapter.storyViewHolder> {

    private List<storyModel> list;
    public storiesAdapter(List<storyModel> list){
        this.list=list;
    }



    @NonNull
    @Override
    public storyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.storiesview,parent,false);
        return new storyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull storyViewHolder holder, int position) {
          holder.storyText.setText(list.get(position).getName());
        Glide.with(holder.itemView.getContext()).load(list.get(position).getImages().get(0))
                .centerCrop().placeholder(R.drawable.profile)
                .into(holder.storythumbnail);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class storyViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView storythumbnail;
        private TextView storyText;
        public storyViewHolder(@NonNull View itemView) {
            super(itemView);
            storythumbnail=itemView.findViewById(R.id.storythumbnail);
            storyText=itemView.findViewById(R.id.storyusername);
        }
    }
}
