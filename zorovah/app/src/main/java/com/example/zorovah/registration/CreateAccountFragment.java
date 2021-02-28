package com.example.zorovah.registration;

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

import com.example.zorovah.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateAccountFragment newInstance(String param1, String param2) {
        CreateAccountFragment fragment = new CreateAccountFragment();
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

    private FirebaseAuth mAuth;

    private EditText phone,email,password,confirm_password;
    private ProgressBar progressBar;
    private Button create_account_btn;
    private TextView login_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        //initailize variable
        phone=view.findViewById(R.id.phone);
        email=view.findViewById(R.id.email);
        password=view.findViewById(R.id.password);
        confirm_password=view.findViewById(R.id.confirm_password);
        progressBar=view.findViewById(R.id.progressBar);
        login_text=view.findViewById(R.id.login_text);
        create_account_btn=view.findViewById(R.id.create_account_btn);


        //function to call new fragmant when login text is clicked
        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ((RegisterActivity)getActivity()).setFragment(new LoginFragment());
            }
        });

        create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setError(null);
                phone.setError(null);
                password.setError(null);
                confirm_password.setError(null);
                if(email.getText().toString().isEmpty()){
                    email.setError("Required");
                    return;
                }
                if(phone.getText().toString().isEmpty()){
                    phone.setError("Required");
                    return;
                }
                if(password.getText().toString().isEmpty()){
                    password.setError("Required");
                    return;
                }
                if(confirm_password.getText().toString().isEmpty()){
                    confirm_password.setError("Required");
                    return;
                }
                if(phone.getText().toString().length()!=10){
                    phone.setError("Invalid Number");
                    return;
                }
                if(!password.getText().toString().equals(confirm_password.getText().toString())){
                    confirm_password.setError("Passwords do not match");
                    return;
                }
                createAccount();
            }
        });
    }

    private void createAccount(){
        progressBar.setVisibility(View.VISIBLE);
        mAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getSignInMethods().isEmpty()){
                        ((RegisterActivity)getActivity()).setFragment(new OTPFragment(email.getText().toString(), phone.getText().toString(), password.getText().toString()));
                    }
                    else{
                        email.setError("Email alredy exist");
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                }
                else{
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });



    }
}