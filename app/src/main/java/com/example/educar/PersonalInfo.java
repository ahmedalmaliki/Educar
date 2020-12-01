package com.example.educar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import butterknife.ButterKnife;

public class PersonalInfo extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText schoolName, bio;
    private TextView bDate , profile_fullname, profile_username;
    private Button register, back;
    private ProgressBar progressBar;
    private DatePickerDialog.OnDateSetListener bDateSetListener;
    private String[] warnings ;
    private CustomAdapter warningsAdapter;
    private ImageView profileImage;
    private Bitmap profileBitmap;
    private Spinner gendersSpinner;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    public static final int REQUEST_IMAGE = 100;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        terminationAfterSignout();
        ButterKnife.bind(this);
        ImagePickerActivity.clearCache(this);
        mAuth = FirebaseAuth.getInstance();
        schoolName = findViewById(R.id.schoolName);
        bio = findViewById(R.id.bio);
        bDate = findViewById(R.id.bDate);
        register = findViewById(R.id.register);
        back = findViewById(R.id.goBack);
        progressBar = findViewById(R.id.progressBar);
        profileImage = findViewById(R.id.profile_image);
        profile_fullname = findViewById(R.id.profile_fullname);
        profile_username = findViewById(R.id.profile_username);
        gendersSpinner = findViewById(R.id.genderSpiner);


        profileImage.setOnClickListener(this);
        bDate.setOnClickListener(this);
        register.setOnClickListener(this);
        back.setOnClickListener(this);
        progressBar.setOnClickListener(this);
        bDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth +"/" + year;
                bDate.setText(date);

            }
        };
        InputFilter[] filterArraySchoolName = new InputFilter[1];
        filterArraySchoolName[0] = new InputFilter.LengthFilter(25);
        schoolName.setFilters(filterArraySchoolName);
        InputFilter[] filterArrayBio = new InputFilter[1];
        filterArrayBio[0] = new InputFilter.LengthFilter(250);
        bio.setFilters(filterArrayBio);
        terminationAfterSignout();
        setProfileFullnameAndUsername();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bDate:
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(PersonalInfo.this,
                        android.R.style.Widget_Holo_DatePicker,
                        bDateSetListener,
                        year,month,day);

                dialog.show();
                break;
            case R.id.register:
                registerUser();
                break;
            case R.id.goBack:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.profile_image:
                onProfileImageClick();
                break;


        }
    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        GlideApp.with(this).load(url)
                .into(profileImage);
        profileImage.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }


    private void onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(PersonalInfo.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(PersonalInfo.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");

                try {
                    // You can update this bitmap to your server
                    if(uri != null) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        profileBitmap = bitmap;
                        // loading profile image from local cache
                        loadProfile(uri.toString());
                    }
                    else {
                        profileBitmap = null;
                        Log.d("PHOTO_NULLED", "NULL");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfo.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }





    private void registerUser() {
        final String schoolName = this.schoolName.getText().toString().trim();
        String bio = this.bio.getText().toString().trim();
        final String bDate = this.bDate.getText().toString().trim();
        warnings = getResources().getStringArray(R.array.warnings);
        warningsAdapter = new CustomAdapter(warnings);

        if(schoolName.isEmpty()){
            this.schoolName.setError(warningsAdapter.getItem(8).toString());
            this.schoolName.requestFocus();
            return;
        }
        if(bDate.isEmpty()){
            this.bDate.setError(warningsAdapter.getItem(0).toString());
            this.bDate.requestFocus();
            return;
        }
        if(bio.isEmpty()){
            bio = "/";
        }


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
        sendUserInfo(schoolName, bio, bDate);

    }


      private void sendUserInfo(final String schoolName,final String bio,
                              final String bDate) {
      final String email = getIntent().getStringExtra("email");
      final String password = getIntent().getStringExtra("password");
      final String fullName = getIntent().getStringExtra("fullname");
      final String username = getIntent().getStringExtra("username");
      final String occupation = getIntent().getStringExtra("occupation");
      final String gender = gendersSpinner.getSelectedItem().toString();

          mAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(fullName, email, occupation, username, schoolName, bDate, bio, gender);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().
                                    getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        uploadProfileImage();
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(PersonalInfo.this, R.string.register_success, Toast.LENGTH_LONG).show();
                                        sendVarificationEmail();
                                        createSession();
                                        moveToMainActivity();
                                    } else {
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(PersonalInfo.this, R.string.register_fail, Toast.LENGTH_LONG).show();
                                    }
                                }

                            });



                        }

                  }
                });



    }

    private void uploadProfileImage() {
        StorageReference storageRef = storage.getReference();
        StorageReference profilesRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+ "/profilePicture/" + "profile.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (profileBitmap == null) {
            sendDefaultProfileImage(baos, profilesRef);

        }
        else {
            sendSelectedProfileImage(profilesRef, baos);
        }




    }

    private void sendSelectedProfileImage(StorageReference profilesRef, ByteArrayOutputStream baos) {
        profileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] data = baos.toByteArray();
        UploadTask uploadTask = profilesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    private void sendDefaultProfileImage( ByteArrayOutputStream baos,  StorageReference profilesRef) {
        switch (gendersSpinner.getSelectedItemPosition()) {
            //Male
            case (0):
                Uri default_male_path = Uri.parse("android.resource://com.example.educar/" + R.drawable.default_male);
                Bitmap default_male = null;
                try {
                    default_male = MediaStore.Images.Media.getBitmap(this.getContentResolver(), default_male_path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profileBitmap = default_male;
                break;
            //Female
            case (1):
                Uri default_female_path = Uri.parse("android.resource://com.example.educar/" + R.drawable.default_female);
                Bitmap default_female = null;
                try {
                    default_female = MediaStore.Images.Media.getBitmap(this.getContentResolver(), default_female_path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profileBitmap = default_female;
                break;
            //nonBinary
            case (2):
                Uri default_nonBinary_path = Uri.parse("android.resource://com.example.educar/" + R.drawable.default_nonbinary);
                Bitmap default_nonBinary = null;
                try {
                    default_nonBinary = MediaStore.Images.Media.getBitmap(this.getContentResolver(), default_nonBinary_path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profileBitmap = default_nonBinary;
                break;
        }
        profileBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        byte[] data = baos.toByteArray();
        UploadTask uploadTask = profilesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

    }


    private void sendVarificationEmail() {
        FirebaseUser user_verify = FirebaseAuth.getInstance().getCurrentUser();
        user_verify.sendEmailVerification();
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(PersonalInfo.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finishPreviousActivity();
        finish();


    }

    private void finishPreviousActivity() {
        Intent intent = new Intent("finish_activity");
        sendBroadcast(intent);
    }


    private void createSession() {
        SessionManagement sessionManagement = new SessionManagement(PersonalInfo.this);
        sessionManagement.saveSession(FirebaseAuth.getInstance().getCurrentUser().getUid());

    }
    private void terminationAfterSignout() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        }, intentFilter);
    }

    private void setProfileFullnameAndUsername() {
        String fullName = getIntent().getStringExtra("fullname");
        String username = "@" + getIntent().getStringExtra("username");

        profile_fullname.setText(fullName);
        profile_username.setText(username);
    }

}

