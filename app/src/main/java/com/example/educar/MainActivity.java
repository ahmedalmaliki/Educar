package com.example.educar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button signOut;
    private FirebaseUser user;
    private DatabaseReference reference;
    private Toolbar toolbar;
    private TextInputEditText postField;
    private String  userID, fullname, gender;
    private ImageView profileImage;
    private FirebaseMethods firebaseMethods;
    FirebaseStorage storage = FirebaseStorage.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.headBar);
        toolbar.setTitleTextAppearance(this, R.style.TitleAppearance);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        signOut = findViewById(R.id.signOut);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        postField = findViewById(R.id.postField);
        profileImage = findViewById(R.id.profile_image);
        firebaseMethods = new FirebaseMethods();

        signOut.setOnClickListener(this);
        postField.setOnClickListener(this);
        populateProfileImage();
        terminationAfterSignout();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signOut:
                signOut();
                break;
            case R.id.postField:
                MoveToPostWritingActivity();
                break;
        }

    }

    private void populateProfileImage() {
        firebaseMethods.determineGender(profileImage);
    }

    private void MoveToPostWritingActivity() {
        Intent passInfo = new Intent(MainActivity.this, PostWriting.class);
        passInfo.putExtra("fullname", fullname);
        passInfo.putExtra("userID", userID);
        startActivity(passInfo);
    }


    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
        sessionManagement.removeSession();
        startActivity(new Intent(MainActivity.this, Login.class));
        sendBroadcastToPreventAccessToAllActivities();
    }

    private void sendBroadcastToPreventAccessToAllActivities() {
        Intent brodcastIntent = new Intent();
        brodcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(brodcastIntent);
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



}