package com.example.zorovah.registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zorovah.MainActivity;
import com.example.zorovah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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

    private EditText email_and_phone,login_password;
    private TextView forgot,create_text;
    private Button login_btn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initailize variable
        email_and_phone=view.findViewById(R.id.email_and_phone);
        login_password=view.findViewById(R.id.login_password);
        forgot=view.findViewById(R.id.forgot);
        create_text=view.findViewById(R.id.create_text);
        login_btn=view.findViewById(R.id.login_btn);
        progressBar=view.findViewById(R.id.progressBar);
        mAuth=FirebaseAuth.getInstance();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                email_and_phone.setError(null);
                login_password.setError(null);

                if(email_and_phone.getText().toString().isEmpty()){
                    email_and_phone.setError("Email or Phone Required!");
                    return;
                }
                if(login_password.getText().toString().isEmpty()){
                    login_password.setError("Password Required!");
                    return;
                }
                if(email_and_phone.getText().toString().matches("^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$")){
                           login(email_and_phone.getText().toString());
                }else{
                    if(email_and_phone.getText().toString().matches("\\d{10}")){
                        //find for email and then login
                        FirebaseFirestore.getInstance().collection("user").whereEqualTo("phone",email_and_phone.getText().toString()).get().
                                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            List<DocumentSnapshot> document=task.getResult().getDocuments();
                                            if(document.isEmpty()){
                                                email_and_phone.setError("phone not found");
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                            else{
                                                String email=document.get(0).get("email").toString();
                                                login(email);
                                            }
                                        }
                                        else {
                                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                    }
                    else{
                           email_and_phone.setError("Enter valid phone or email");
                           return;
                    }

                }


            }
        });


        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RegisterActivity)getActivity()).setFragment(new ResetPasswordFragment());
            }
        });
        create_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RegisterActivity)getActivity()).setFragment(new CreateAccountFragment());
            }
        });
    }


    private void login(String email){
            mAuth.signInWithEmailAndPassword(email,login_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                  if(task.isSuccessful()){
                      Intent intent=new Intent(getContext(), MainActivity.class);
                      startActivity(intent);
                      getActivity().finish();
                  }
                  else{
                      Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                      progressBar.setVisibility(View.INVISIBLE);
                  }
                }
            });
    }
}
