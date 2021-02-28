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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OTPFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OTPFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OTPFragment(String email, String phone, String password) {
        // Required empty public constructor
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OTPFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OTPFragment newInstance(String param1, String param2) {
        OTPFragment fragment = new OTPFragment(email, phone, password);
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


    private EditText otp;
    private ProgressBar progressBar;
    private Button verify_btn,resend_btn;
    private TextView phone_text;
    private static String email,phone,password;
    private Timer timer;
    private int count=60;
    private FirebaseAuth mAuth;
    private  String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallsbacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_o_t_p, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth=FirebaseAuth.getInstance();
        otp=view.findViewById(R.id.otp);
        progressBar=view.findViewById(R.id.progressBar);
        verify_btn=view.findViewById(R.id.verify_btn);
        resend_btn=view.findViewById(R.id.resend_btn);
        phone_text=view.findViewById(R.id.phone_text);

        phone_text.setText("OTP has been send to                    +91" +phone);
        sendOTP();

        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(count==0){
                            resend_btn.setText("Resend");
                            resend_btn.setEnabled(true);
                            resend_btn.setAlpha(1f);
                        }
                        else {
                            resend_btn.setText(Integer.toString(count));
                            count--;
                        }
                    }
                });
            }
        },0,1000);

        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOTP();
                resend_btn.setEnabled(false);
                resend_btn.setAlpha(0.5f);
                count=60;
            }
        });

        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              progressBar.setVisibility(View.VISIBLE);

                otp.setError(null);
                if(otp.getText().toString().isEmpty()){
                    otp.setError("Please enter OTP!");
                    return;
                }
                String code=otp.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }
    private void sendOTP(){
        mCallsbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                //Log.d(TAG, "onVerificationCompleted:" + credential);

                // signInWithPhoneAuthCredential(credential);
            }



            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                //Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    otp.setError(e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    otp.setError(e.getMessage());
                }

                // Show a message and update the UI
                // ...
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallsbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
    }
    private void resendOTP(){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallsbacks,mResendToken);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           // Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                            user.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull final Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        Map<String,String> user=new HashMap<>();
                                        user.put("email",email);
                                        user.put("phone",phone);
                                        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                                        firebaseFirestore.collection("user").document(mAuth.getCurrentUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){

                                                    Intent userintent=new Intent(getContext(), UserDetailsActivity.class);
                                                    startActivity(userintent);
                                                    getActivity().finish();
                                                }
                                                else{
                                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        });





                                    }
                                    else{
                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }

                                }
                            });
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                               otp.setError("Invalid OTP!");
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}