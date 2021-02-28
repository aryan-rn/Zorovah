package com.example.zorovah;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zorovah.registration.UserDetailsActivity;
import com.example.zorovah.tabfragments.UserFeed;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class profileChange extends AppCompatActivity {

    private ImageView imageView;
    private Button button;
    private ProgressBar progressBar;
    private Uri result_uri;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);
        mAuth= FirebaseAuth.getInstance();
        imageView=findViewById(R.id.profile_imagee);
        progressBar=findViewById(R.id.progressBar3);
        button=findViewById(R.id.update_buttonn);

        Dexter.withContext(profileChange.this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    // selectImage();
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setActivityMenuIconColor(getResources().getColor(R.color.colorAccent))
                            .setActivityTitle("Profile Picture")
                            .setFixAspectRatio(true)
                            .setAspectRatio(1,1)
                            .start(profileChange.this);
                }
                else{
                    Toast.makeText(profileChange.this, "Please allow permissions", Toast.LENGTH_SHORT).show();
                }

            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                button.setEnabled(false);
                if(result_uri!=null){
                    //no need to delete directly update
                    //update new profile
                   // getData();

                   image();


                }
                else{
                    Intent intent=new Intent(profileChange.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void image(){

        final UploadTask uploadTask =  FirebaseStorage.getInstance().getReference().child("profiles/"+mAuth.getCurrentUser().getUid()).putFile(result_uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(profileChange.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                //final StorageReference ref =FirebaseStorage.getInstance().getReference().child("images/"+imageName);
                //uploadTask = ref.putFile(file);
                final Uri[] downloadUri = new Uri[1];
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return FirebaseStorage.getInstance().getReference().child("profiles/"+mAuth.getCurrentUser().getUid()).getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadUri[0] = task.getResult();
                            Map<String,Object> user=new HashMap<>();
                            user.put("profile_url",downloadUri[0].toString());
                            FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                            firebaseFirestore.collection("user").document(mAuth.getCurrentUser().getUid()).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Intent intent=new Intent(profileChange.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(profileChange.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            // Handle failures
                            // ...
                        }
                    }
                });



            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                result_uri = result.getUri();
                Glide.with(this).load(result_uri).centerCrop().into(imageView);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }
}