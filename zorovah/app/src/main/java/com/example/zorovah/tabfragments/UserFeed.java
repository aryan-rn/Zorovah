package com.example.zorovah.tabfragments;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zorovah.MainActivity;
import com.example.zorovah.R;
import com.example.zorovah.registration.UserDetailsActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class UserFeed extends AppCompatActivity {
    private Uri result_uri;
    private ImageView imageView;
    private EditText caption;
    private Button upload_btn;
    private FirebaseAuth mAuth;
    private String url,Username1;
    private ProgressBar progressBar;
    String imageName= UUID.randomUUID().toString()+".jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
        //onBackPressed();
        imageView=findViewById(R.id.feed);
        caption=findViewById(R.id.editText);
        upload_btn=findViewById(R.id.upload);
        mAuth= FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);
        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressed();
            }
        };


        Dexter.withContext(UserFeed.this)
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
                            .start(UserFeed.this);
                }
                else{
                    Toast.makeText(UserFeed.this, "Please allow permissions", Toast.LENGTH_SHORT).show();
                }

            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
               upload_btn.setEnabled(false);
                if(result_uri!=null){
                    getData();


                }
            }
        });

    }

    public void upload(){
        final UploadTask uploadTask =  FirebaseStorage.getInstance().getReference().child("posts/"+imageName).putFile(result_uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(UserFeed.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
               // progressBar.setVisibility(View.INVISIBLE);
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
                        return FirebaseStorage.getInstance().getReference().child("posts/"+imageName).getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Date d = new Date();
                            CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());

                            downloadUri[0] = task.getResult();
                            Map<String,String> map=new HashMap<>();
                            map.put("userprofile",url);
                            map.put("user",Username1);
                            map.put("postfeed",downloadUri[0].toString());
                            map.put("postdate",s.toString());
                            map.put("caption",caption.getText().toString());
                            String timestamp=String.valueOf(System.currentTimeMillis());
                         FirebaseFirestore.getInstance().collection("Post").document("-"+timestamp).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       progressBar.setVisibility(View.INVISIBLE);
                                       upload_btn.setEnabled(true);
                                       Toast.makeText(UserFeed.this, "Upload Done", Toast.LENGTH_SHORT).show();
                                       Intent intent=new Intent(UserFeed.this,MainActivity.class);
                                       startActivity(intent);
                                       finish();
                                   }
                                   else{Toast.makeText(UserFeed.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                       progressBar.setVisibility(View.INVISIBLE);
                                       upload_btn.setEnabled(true);
                                   }
                               }
                           });


                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            upload_btn.setEnabled(true);
                            // Handle failures
                            // ...
                        }
                    }
                });



            }
        });
    }
    private void getData(){
        FirebaseFirestore.getInstance().collection("user").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    url= task.getResult().get("profile_url").toString();

                    Username1= task.getResult().get("username").toString();
                    upload();
                }

            }
        });
    }

    public void onBackPressed(){
        Intent back_intent=new Intent(UserFeed.this, MainActivity.class);
        startActivity(back_intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){result_uri=result.getUri();
                Glide.with(this).load(result_uri).centerCrop().into(imageView);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();}

        }
    }
}