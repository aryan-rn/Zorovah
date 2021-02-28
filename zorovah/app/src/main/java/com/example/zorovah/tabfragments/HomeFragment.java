package com.example.zorovah.tabfragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.zorovah.R;
import com.example.zorovah.adapters.feedAdapter;
import com.example.zorovah.adapters.storiesAdapter;
import com.example.zorovah.chatActivity;
import com.example.zorovah.models.feedModel;
import com.example.zorovah.models.storyModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
      private RecyclerView feeds,stories;
    private storiesAdapter storiesAdapter;
    private feedAdapter feedAdapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        feeds=view.findViewById(R.id.feeds);
        stories=view.findViewById(R.id.stories);

        LinearLayoutManager storiesManager=new LinearLayoutManager(getContext());
            storiesManager.setOrientation(RecyclerView.HORIZONTAL);
        stories.setLayoutManager(storiesManager);

        LinearLayoutManager feedsManager=new LinearLayoutManager(getContext());
        feedsManager.setOrientation(RecyclerView.VERTICAL);
        feeds.setLayoutManager(feedsManager);
        final List<storyModel> list=new ArrayList<>();
        final List<String> images=new ArrayList<>();
        FirebaseFirestore.getInstance().collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    images.add(task.getResult().get("profile_url").toString());
                    list.add(new storyModel(images,task.getResult().get("username").toString()));
                    storiesAdapter=new storiesAdapter(list);
                    stories.setAdapter(storiesAdapter);
                }
            }
        });





        final List<feedModel> list1=new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Post").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> postlist=task.getResult().getDocuments();
                    for(int i=postlist.size()-1;i>=0;i--){

                        list1.add(new feedModel( postlist.get(i).get("userprofile").toString(),postlist.get(i).get("user").toString(),postlist.get(i).get("postfeed").toString(),"10 likes",postlist.get(i).get("postdate").toString(),postlist.get(i).get("caption").toString()));
                    }
                    feedAdapter=new feedAdapter(list1);
                    feeds.setAdapter(feedAdapter);
                }
            }
        });








    }
}