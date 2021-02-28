package com.example.zorovah;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.zorovah.registration.CreateAccountFragment;
import com.example.zorovah.registration.UserDetailsActivity;
import com.example.zorovah.tabfragments.HomeFragment;
import com.example.zorovah.tabfragments.NotificationFragment;
import com.example.zorovah.tabfragments.SearchFragment;
import com.example.zorovah.tabfragments.UserFeed;
import com.example.zorovah.tabfragments.UserFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
 private FirebaseAuth mAuth;
 public TabLayout tabLayout;
 private FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout=findViewById(R.id.tabLayout);
        frameLayout=findViewById(R.id.framelayout);
        mAuth= FirebaseAuth.getInstance();
        checkDetails();
        setFragment(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()!=2){
                setFragment(tab.getPosition());}
                if(tab.getPosition()==2){Intent intent=new Intent(MainActivity.this, UserFeed.class);startActivity(intent);
                finish();}

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }


    private void checkDetails(){
        FirebaseFirestore.getInstance().collection("user").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                        if(task.getResult().exists()){
                               if(!task.getResult().contains("username")){
                                   Intent intent=new Intent(MainActivity.this, UserDetailsActivity.class);
                                   startActivity(intent);
                                   finish();
                               }

                        }
                        else{
                            Intent intent=new Intent(MainActivity.this, CreateAccountFragment.class);
                            startActivity(intent);
                            finish();
                        }
                }
                else{
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    public void setFragment(int pos){
        Fragment fragment= new HomeFragment();
        if(pos==0){fragment= new HomeFragment();}
        if(pos==1){fragment= new SearchFragment();}
        if(pos==3){fragment= new NotificationFragment();}
        if(pos==4){fragment= new UserFragment();
        }
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }


}