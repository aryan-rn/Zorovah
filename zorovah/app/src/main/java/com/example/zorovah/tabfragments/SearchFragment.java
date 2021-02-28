package com.example.zorovah.tabfragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zorovah.R;
import com.example.zorovah.adapters.searchAdapter;
import com.example.zorovah.models.searchModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
    private RecyclerView searchView;
    private EditText searchText;
    private ImageView search;
    private searchAdapter searchAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView=view.findViewById(R.id.searchView);
        searchText=view.findViewById(R.id.searchText);
        search=view.findViewById(R.id.search);

        final List<searchModel> list=new ArrayList<>();
        LinearLayoutManager searchManager=new LinearLayoutManager(getContext());
        searchManager.setOrientation(RecyclerView.VERTICAL);
        searchView.setLayoutManager(searchManager);
        FirebaseFirestore.getInstance().collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> dataSnapshotList=task.getResult().getDocuments();
                    for(int i=0;i<dataSnapshotList.size();i++)
                    {
                        list.add(new searchModel(dataSnapshotList.get(i).get("profile_url").toString(),dataSnapshotList.get(i).get("username").toString(),dataSnapshotList.get(i).get("email").toString(),dataSnapshotList.get(i).getId()));
                    }
                    searchAdapter =new searchAdapter(list);
                    searchView.setAdapter(searchAdapter);
                }
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               FirebaseFirestore.getInstance().collection("user").orderBy("username").startAt(searchText.getText().toString()).endAt(searchText.getText().toString()+"\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.isSuccessful()){
                           List<DocumentSnapshot> dataSnapshotList=task.getResult().getDocuments();
                           list.clear();
                           for(int i=0;i<dataSnapshotList.size();i++)
                           {
                               list.add(new searchModel(dataSnapshotList.get(i).get("profile_url").toString(),dataSnapshotList.get(i).get("username").toString(),dataSnapshotList.get(i).get("email").toString(),dataSnapshotList.get(i).getId()));
                           }
                           searchAdapter =new searchAdapter(list);
                           searchView.setAdapter(searchAdapter);
                       }
                   }
               });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }



}