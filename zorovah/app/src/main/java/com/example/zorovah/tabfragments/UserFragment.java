package com.example.zorovah.tabfragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zorovah.MainActivity;
import com.example.zorovah.R;
import com.example.zorovah.SplashActivity;
import com.example.zorovah.profileChange;
import com.example.zorovah.registration.CreateAccountFragment;
import com.example.zorovah.registration.UserDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public ImageView imageView;
    public Button logout_btn;
    private FirebaseAuth mAuth;
    String url,mobileNo,Username1,email1;
    TextView username;
    TextView mobile;
    TextView emailId;

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

        return inflater.inflate(R.layout.fragment_user, container, false);

    }

private ImageView change;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        change=view.findViewById(R.id.change);
        imageView=view.findViewById(R.id.profile_image);
        mAuth= FirebaseAuth.getInstance();
        username=(TextView) view.findViewById(R.id.username);
        mobile=(TextView) view.findViewById(R.id.mobile);
        emailId=(TextView) view.findViewById(R.id.emailId);
        logout_btn=(Button) view.findViewById(R.id.logout_btn);

        FirebaseFirestore.getInstance().collection("user").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                url= task.getResult().get("profile_url").toString();
                mobileNo= task.getResult().get("phone").toString();
                email1= task.getResult().get("email").toString();
                Username1= task.getResult().get("username").toString();
                    emailId.setText(email1);
                    mobile.setText(mobileNo);
                    username.setText(Username1);
                    if(url.length()>4){
                    Glide
                            .with(getContext())
                            .load(url)
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(imageView);}
                }

            }
        });
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Logging Out...", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            Intent intent=new Intent(getContext(), SplashActivity.class);
            startActivity(intent);


            }
        });
       change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getContext(), profileChange.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



    }
}