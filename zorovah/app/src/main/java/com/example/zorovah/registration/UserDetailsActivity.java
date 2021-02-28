package com.example.zorovah.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zorovah.MainActivity;
import com.example.zorovah.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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

public class UserDetailsActivity extends AppCompatActivity {

    private ImageView profile_image;
    private TextView userName;
    private Button remove_button,update_button;
    private ProgressBar progressBar;
    private Uri resultUri;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        mAuth= FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar2);
        profile_image=findViewById(R.id.profile_image);
        userName=findViewById(R.id.userName);
        remove_button=findViewById(R.id.remove_button);
        update_button=findViewById(R.id.update_button);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(UserDetailsActivity.this)
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            selectImage();
                        }
                        else{
                            Toast.makeText(UserDetailsActivity.this, "Please allow permissions", Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();

            }
        });
       remove_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               resultUri=null;
               profile_image.setImageResource(R.drawable.profile);
           }
       });

       update_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               progressBar.setVisibility(View.VISIBLE);
               userName.setError(null);
               if(userName.getText().toString().isEmpty()){
                   userName.setError("Invalid username");
                   return;
               }
              /* if(!userName.getText().toString().matches("/^[a-zA-Z0-9][a-zA-Z0-9_\\s\\-]*[a-zA-Z0-9](?<![_\\s\\-]{2,}.)$/")){
                   userName.setError("Please enter valid username");
                   return;
               }*/

               FirebaseFirestore.getInstance().collection("user").whereEqualTo("username",userName.getText().toString()).get().
                       addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                               if(task.isSuccessful()){
                                   List<DocumentSnapshot> document=task.getResult().getDocuments();
                                   if(document.isEmpty()){
                                     uploadData();
                                   }
                                   else{
                                       userName.setError("Already exist");
                                       progressBar.setVisibility(View.INVISIBLE);
                                   }
                               }
                               else {
                                   Toast.makeText(UserDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                   progressBar.setVisibility(View.INVISIBLE);
                               }
                           }
                       });

           }
       });

    }

    private void uploadData(){
        if(resultUri==null){
            //upload username only
          updateCloud();
        }
        else{
            //upload both username and image
                updateCloud();
                image();
        }
    }
    private void image(){

        final UploadTask uploadTask =  FirebaseStorage.getInstance().getReference().child("profiles/"+mAuth.getCurrentUser().getUid()).putFile(resultUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(UserDetailsActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
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
                                        Intent intent=new Intent(UserDetailsActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(UserDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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



    private void updateCloud(){
        Map<String,Object> user=new HashMap<>();
        user.put("username",userName.getText().toString());
        user.put("profile_url","");
        user.put("onlineStatus","online");
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("user").document(mAuth.getCurrentUser().getUid()).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                      Intent intent=new Intent(UserDetailsActivity.this, MainActivity.class);
                      startActivity(intent);
                      finish();
                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(UserDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectImage(){
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityMenuIconColor(getResources().getColor(R.color.colorAccent))
                .setActivityTitle("Profile Picture")
                .setFixAspectRatio(true)
                .setAspectRatio(1,1)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                Glide
                        .with(this)
                        .load(resultUri)
                        .centerCrop()
                        .placeholder(R.drawable.profile)
                        .into(profile_image);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}