package com.example.pmuchatproject.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pmuchatproject.R;
import com.example.pmuchatproject.commons.NodeNames;
import com.example.pmuchatproject.login.LoginActivity;
import com.example.pmuchatproject.signup.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etName;

    private String email, name;

    private ImageView ivProfile;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference fileStorage;
    private Uri localFileUri, serverFileUri;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        ivProfile = findViewById(R.id.ivProfile);

        fileStorage = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null)
        {
            etName.setText(firebaseUser.getDisplayName());
            etEmail.setText(firebaseUser.getEmail());
            serverFileUri = firebaseUser.getPhotoUrl();

            if (serverFileUri!= null)
            {
                Glide.with(this).load(serverFileUri).placeholder(R.drawable.profil_users).error(R.drawable.profil_users).into(ivProfile);
            }
        }
    }


    private void updateNameAndPhoto()
    {
        String strFileName = firebaseUser.getUid() + ".jpg";

        final StorageReference fileRef = fileStorage.child("images/" + strFileName);

        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            serverFileUri = uri;

                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etName.getText().toString().trim())
                                    .setPhotoUri(serverFileUri)
                                    .build();

                            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        String userId = firebaseUser.getUid();
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put(NodeNames.NAME, etName.getText().toString().trim());
                                        hashMap.put(NodeNames.PHOTO, serverFileUri.getPath());

                                        databaseReference.child(userId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                finish();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        Toast.makeText(ProfileActivity.this,
                                                getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void removePicture()
    {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString().trim())
                .setPhotoUri(null)
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    String userId = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(NodeNames.NAME, etName.getText().toString().trim());
                    hashMap.put(NodeNames.PHOTO, null);

                    databaseReference.child(userId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ProfileActivity.this, R.string.photo_removed_successfully, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(ProfileActivity.this,
                            getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateOnlyName()
    {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString().trim()).build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    String userId = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(NodeNames.NAME, etName.getText().toString().trim());

                    databaseReference.child(userId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                        }
                    });
                }
                else
                {
                    Toast.makeText(ProfileActivity.this,
                            getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void changeImage(View v)
    {
        if (serverFileUri == null)
        {
            pickImage();
        }
        else
        {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_picture, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.mmuChangePic)
                    {
                        pickImage();
                    }
                    else if (id == R.id.mmuRemovePic)
                    {
                        removePicture();
                    }

                    return false;
                }
            });

            popupMenu.show();
        }
    }

    public void btnLogoutClick(View v)
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
    }

    public void btnSaveClick(View v)
    {
        if (etName.getText().toString().trim().equals(""))
        {
            etName.setError(getString(R.string.enter_name));
        }
        else
        {
            if (localFileUri != null)
            {
                updateNameAndPhoto();
            }
            else
            {
                updateOnlyName();
            }
        }
    }

    public void pickImage()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==101)
        {
            if (resultCode == RESULT_OK)
            {
                localFileUri = data.getData();
                ivProfile.setImageURI(localFileUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            }
            else{
                Toast.makeText(this, R.string.permission_request, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
