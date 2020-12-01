package com.example.educar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicBoolean;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference reference;
    private Button goToSignIn, goToPersonalInfo;
    private Spinner occupationSpinner;
    private EditText fullName, email, password, username;
    private ProgressBar progressBar;
    private String[] warnings , messages;
    private CustomAdapter messagesAdapter, warningsAdapter;
    private static final String TAG = "REGISTER";
    private AtomicBoolean usernameNotTaken ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        Resources res = getResources();
        goToSignIn = findViewById(R.id.goToSignIn);
        occupationSpinner = findViewById(R.id.ocupationSpiner);
        fullName = findViewById(R.id.fullName);
        username = findViewById(R.id.editTextUsername);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressBar);
        goToPersonalInfo = findViewById(R.id.goToPersonalInfo);
        messages = getResources().getStringArray(R.array.messages);
        messagesAdapter = new CustomAdapter(messages);
        usernameNotTaken = new AtomicBoolean(false);



        fullName.setOnClickListener(this);
        email.setOnClickListener(this);
        password.setOnClickListener(this);
        progressBar.setOnClickListener(this);
        goToSignIn.setOnClickListener(this);
        goToPersonalInfo.setOnClickListener(this);
        username.addTextChangedListener(watcher);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(25);
        username.setFilters(filterArray);
        fullName.setFilters(filterArray);

        finishAfterRegistrationCompletion();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goToSignIn:
                startActivity(new Intent(this, Login.class));

                break;
            case R.id.goToPersonalInfo:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        final String fullName = this.fullName.getText().toString().trim();
        final String email = this.email.getText().toString().trim();
        final String password = this.password.getText().toString().trim();
        final String username = this.username.getText().toString().trim();
        final String occupation = occupationSpinner.getSelectedItem().toString();
        warnings = getResources().getStringArray(R.array.warnings);
        warningsAdapter = new CustomAdapter(warnings);


        if (fullName.isEmpty()) {
            this.fullName.setError(warningsAdapter.getItem(0).toString());
            this.fullName.requestFocus();
            return;
        }
        if (username.isEmpty()) {
            this.username.setError(warningsAdapter.getItem(7).toString());
            this.username.requestFocus();
            return;
        }



        if (email.isEmpty()) {
            this.email.setError(warningsAdapter.getItem(1).toString());
            this.email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError(warningsAdapter.getItem(2).toString());
            this.email.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            this.password.setError(warningsAdapter.getItem(3).toString());
            this.password.requestFocus();
            return;
        }
        if (password.length() < 6) {
            this.password.setError(warningsAdapter.getItem(4).toString());
            this.password.requestFocus();
            return;

        }
        while (!this.usernameNotTaken.get()){
            return;
        }
       if (this.usernameNotTaken.get()){
           getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                   WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);
            checkIfValid(email, password, fullName, username, occupation);
        }
      



    }

    private void checkIfValid(final String email, final String password,
                              final String fullName, final String username, final String occupation) {
        reference = FirebaseDatabase.getInstance().getReference();
        Query userQuery = reference.child("Users").orderByChild("email").equalTo(email);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    notifyUserEmailTaken();
                } else {
                    progressBar.setVisibility(View.GONE);
                    passInfoToNextActivity(email, password, fullName, username, occupation);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void passInfoToNextActivity(String email, String password, String fullname, String username, String occupation) {
        Intent passInfo = new Intent(Register.this,PersonalInfo.class);
        passInfo.putExtra("email", email);
        passInfo.putExtra("password", password);
        passInfo.putExtra("fullname", fullname);
        passInfo.putExtra("username", username);
        passInfo.putExtra("occupation", occupation);
        startActivity(passInfo);

    }

    private void notifyUserEmailTaken() {
        new MaterialAlertDialogBuilder(Register.this)
                .setTitle("Sign up" + "...")
                .setMessage(messagesAdapter.getItem(2).toString())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }



    TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            checkIfUsernameTaken();

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void afterTextChanged(Editable s) {
            checkIfUsernameTaken();

        }

    };



    private void checkIfUsernameTaken() {
        final String username = '@' + this.username.getText().toString().trim();
        Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(username);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    usernameNotTaken.set(false);
                    notifyUsernameTaken();
                    usernameNotTaken.set(false);

                }else{
                    usernameNotTaken.set(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void notifyUsernameTaken() {
        this.username.setError(messagesAdapter.getItem(4).toString());
        this.username.requestFocus();

    }

    private void finishAfterRegistrationCompletion() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals("finish_activity")){
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));
    }


}