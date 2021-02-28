package com.example.zorovah.tabfragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zorovah.R;
import com.example.zorovah.adapters.searchAdapter;
import com.example.zorovah.chatActivity;
import com.example.zorovah.models.searchModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private searchAdapter searchAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth= FirebaseAuth.getInstance();

        final String myUid=mAuth.getCurrentUser().getUid();

        recyclerView=view.findViewById(R.id.chatListView);
        final List<searchModel> list=new ArrayList<>();
        LinearLayoutManager searchManager=new LinearLayoutManager(getContext());
        searchManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(searchManager);

        FirebaseFirestore.getInstance().collection("chatList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> list_chat = task.getResult().getDocuments();
                    String uid="";
                    for (int i = 0; i < list_chat.size(); i++) {
                            uid="";
                       if(list_chat.get(i).get("sender").equals(myUid)){
                           uid =list_chat.get(i).get("receiver").toString();
                        list.add(new searchModel(list_chat.get(i).get("r_url").toString(),list_chat.get(i).get("r_name").toString(),"",uid));
                       }

                       if(list_chat.get(i).get("receiver").equals(myUid)){
                           uid =list_chat.get(i).get("sender").toString();
                           list.add(new searchModel(list_chat.get(i).get("s_url").toString(),list_chat.get(i).get("s_name").toString(),"",uid));
                       }
                    }
                    searchAdapter=new searchAdapter(list);
                    recyclerView.setAdapter(searchAdapter);
                }
            }
        });


    }
}