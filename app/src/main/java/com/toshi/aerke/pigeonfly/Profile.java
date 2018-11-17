package com.toshi.aerke.pigeonfly;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.toshi.aerke.model.User;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile extends AppCompatActivity {
        private Button save;
        private EditText fullName,nickName,Bio;
        private ImageView imageView;
        FirebaseAuth firebaseAuth;
        private  String userID;
        private ProgressDialog progressDialog;
        DatabaseReference databaseReference;
        private final String EXTRA_STORAGE_REFERENCE_KEY = "StorageReference";
    private Uri filePath;
    private byte[] CompressData;
    private Uri downloadUrl = null;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //initializing user interface control
        InitializeUIControllers();
        //initializing fire base database and fire base authentication
         InitializeFireBaseComponent();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidateInput()){
                    SaveUserInfo();
                }
            }
        });
    }

    private void InitializeUIControllers() {
        fullName = (EditText)findViewById(R.id.txtFullName);
        nickName = (EditText)findViewById(R.id.txtNickName);
        Bio = (EditText)findViewById(R.id.txtBio);
        save = (Button) findViewById(R.id.btnSave);
        imageView = (ImageView) findViewById(R.id.circleImageView);
        progressDialog = new ProgressDialog(this);
    }

    private void InitializeFireBaseComponent() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth =FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
    }

    private void ChooseImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Toast.makeText(this, "inside crop activiety code request", Toast.LENGTH_LONG).show();
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                Toast.makeText(this, "Uri corped image" + filePath, Toast.LENGTH_LONG).show();
                imageView.setImageURI(filePath);
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
               CompressData = baos.toByteArray();
            }
//            }
            progressDialog.setTitle("Uploading image...");

            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            uploadImage updateProfile = new uploadImage(CompressData);
            updateProfile.execute((Void) null);
        }
    }

    protected  class uploadImage extends AsyncTask<Void,Void,Boolean>{
           byte [] CompressedImage;

        public uploadImage(byte[] compressData) {
            this.CompressedImage =compressData;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if(filePath != null)
            {

                final StorageReference ref = storageReference.child("ProfileImages/").child(userID+".jpg");
               UploadTask uploadTask = ref.putBytes(CompressedImage);
                Task<Uri> uriTask =uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        progressDialog.dismiss();
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        downloadUrl = task.getResult();
                        Toast.makeText(getApplicationContext(),"downloading uri: "+downloadUrl,Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Profile.this, "Failed "+downloadUrl, Toast.LENGTH_SHORT).show();
                    }
                });
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
            }
            return true;
        }
    }
    private boolean ValidateInput(){
        String fname =fullName.getText().toString();
        String Nname  =nickName.getText().toString();
        String bio = Bio.getText().toString();
        if(TextUtils.isEmpty(fname)){
            fullName.setError("Provide Full Name.");
            return false;
        }

    return true;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (storageReference != null) {
            outState.putString(EXTRA_STORAGE_REFERENCE_KEY, storageReference.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        final String stringRef = savedInstanceState.getString(EXTRA_STORAGE_REFERENCE_KEY);

        if (stringRef == null) {
            return;
        }

        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);
        List<FileDownloadTask> tasks = storageReference.getActiveDownloadTasks();
        for( FileDownloadTask task : tasks ) {
            task.addOnSuccessListener(this, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("Tuts+", "download successful!");
                }
            });
        }
    }

    private void SaveUserInfo(){
        Map<String,Object> user = new HashMap();
        if(downloadUrl ==null){
            user.put(userID,new User(fullName.getText().toString(),nickName.getText().toString(),Bio.getText().toString()));

        }else{
            user.put(userID,new User(fullName.getText().toString(),nickName.getText().toString(),downloadUrl.toString(),Bio.getText().toString()));
        }
       databaseReference.updateChildren(user)
               .addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
              if(task.isSuccessful()){
                  startActivity(new Intent(Profile.this,Home.class));
              }else {
                  try {
                      throw task.getException();
                  } catch (Exception e) {
                      e.printStackTrace();
                      Snackbar.make(fullName.getRootView(),e.getMessage(),Snackbar.LENGTH_LONG).show();
                  }
              }
           }
       });
    }
}
