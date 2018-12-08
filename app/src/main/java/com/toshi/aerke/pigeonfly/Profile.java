package com.toshi.aerke.pigeonfly;

import android.app.ActionBar;
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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.toshi.aerke.Utilitis.OfflineStorage;
import com.toshi.aerke.Utilitis.UserState;
import com.toshi.aerke.model.User;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
        private Button save;
        private ImageButton addImage;
        private EditText fullName,nickName,Bio;
        private CircleImageView imageView;
        FirebaseAuth firebaseAuth;
        private  String userID;
        Toolbar toolbar;
        private ProgressDialog progressDialog;
        DatabaseReference databaseReference;
        private final String EXTRA_STORAGE_REFERENCE_KEY = "StorageReference";
    private Uri filePath;
    private byte[] CompressData;
    private Uri downloadUrl = null;
    StorageReference storageReference;
    private String _fullName,_nickName,_bio,_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //initializing user interface control
        InitializeUIControllers();
        //initializing fire base database and fire base authentication
         InitializeFireBaseComponent();

        addImage.setOnClickListener(new View.OnClickListener() {
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
        toolbar =(Toolbar)findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
         actionBar.setDisplayShowTitleEnabled(false);
        imageView = (CircleImageView) findViewById(R.id.circleImageView);
        addImage =(ImageButton)findViewById(R.id.btnAddImage);
        progressDialog = new ProgressDialog(this);
    }

    private void InitializeFireBaseComponent() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        databaseReference.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth =FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
    }

    private void ChooseImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1).start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();


                imageView.setImageURI(filePath);
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                CompressData = baos.toByteArray();
                Toast.makeText(this, "Compressdate" + CompressData.length, Toast.LENGTH_LONG).show();
            }

            uploadImage(CompressData);

        }
    }

    private  void uploadImage ( byte [] CompressedImage){



            if(CompressData!=null)
            {

//                progressDialog.setTitle("Uploading image...");
//
//                progressDialog.show();
//                progressDialog.setCanceledOnTouchOutside(false);
                final StorageReference ref = storageReference.child("ProfileImages/").child(userID+".jpg");
               UploadTask uploadTask = (UploadTask) ref.putBytes(CompressedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                       Toast.makeText(getApplicationContext(),"image loaded ",Toast.LENGTH_LONG).show();
                   }
               });
//                       .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                   @Override
//                   public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                       double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
//                               .getTotalByteCount());
//                       progressDialog.setMessage("Uploaded "+(int)progress+"%");
//                   }
//               });

                Task<Uri> uriTask =uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        downloadUrl = task.getResult();
                        Log.i("output", "onComplete uploading: downloading url"+downloadUrl);
//                        progressDialog.dismiss();

                          if(downloadUrl!=null){

                              if(databaseReference.child(userID).child("image").setValue(downloadUrl.toString()).isSuccessful())
                                  Toast.makeText(getApplicationContext(),"image upload",Toast.LENGTH_LONG).show();

                          }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
                        Toast.makeText(Profile.this, "Failed "+downloadUrl, Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }

    private boolean ValidateInput(){
        String fname =fullName.getText().toString();
        String Nname  =nickName.getText().toString();
        String bio = Bio.getText().toString();
        if(TextUtils.isEmpty(fname)){
            fullName.setError("Provide Full Name.");
            return false;
        }if(bio.trim().length()>70){
            Bio.setError("Biography shouldn't be more than 70 char ");
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
           user.put("bio",Bio.getText().toString().isEmpty()?"not provide":Bio.getText().toString());
           user.put("fullName",fullName.getText().toString());
           user.put("nickName",nickName.getText().toString());
           user.put("uid",userID);

       databaseReference.child(userID).updateChildren(user)
               .addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
              if(task.isSuccessful()){
                  Toast.makeText(Profile.this,"Profile saved successfully",Toast.LENGTH_LONG).show();
                  UserState.getInstance(userID).setUserState(true);
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

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               User user = dataSnapshot.getValue(User.class);
               if(user!=null){
                  _fullName =  user.getFullName();
                   _nickName =user.getNickName();
                   _bio = user.getBio();
                   if(dataSnapshot.child("image").exists())
                   _image = user.getImage();
                   Log.i("output", "onDataChange: "+_image);

               }else{
                   Snackbar.make(fullName.getRootView(),"User object is empty",Snackbar.LENGTH_LONG).show();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fullName.setText(_fullName);
        nickName.setText(_nickName);
        Bio.setText(_bio);
        Picasso.get().load(_image).networkPolicy(NetworkPolicy.OFFLINE).into(imageView
                ,        new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(_image).placeholder(R.drawable.avatar).into(imageView);
                    }
                });
    }
}
