package com.example.educar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private Button register, forgotP, signIn;
    private TextView email, password;
    private ProgressBar progressBar;
    private String[] warnings, messages;
    private CustomAdapter warningsAdapter;
    private CustomAdapter messagesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Resources res = getResources();
        mAuth = FirebaseAuth.getInstance();
        register = findViewById(R.id.Register);
        forgotP = findViewById(R.id.forgotPassword);
        signIn = findViewById(R.id.sign_in);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressBar);
        messages = getResources().getStringArray(R.array.messages);
        messagesAdapter = new CustomAdapter(messages);
        warnings = getResources().getStringArray(R.array.warnings);
        warningsAdapter = new CustomAdapter(warnings);


        register.setOnClickListener(this);
        signIn.setOnClickListener(this);
        forgotP.setOnClickListener(this);

        //terminationAfterSignout();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Register:
               moveToRegisterActivity();
                break;
            case R.id.sign_in:
                userLogin();
                break;
            case R.id.forgotPassword:
                resetPassword();
                break;



        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkSession();
    }

    private void checkSession(){
        SessionManagement sessionManagement = new SessionManagement(Login.this);
        if(sessionManagement.returnSession() != "") {
            startActivity(new Intent(Login.this, MainActivity.class));
        }else {
            //Do nothing.
        }
    }

    private void userLogin() {
        final String email = this.email.getText().toString().trim();
        final String password = this.password.getText().toString().trim();


        if (email.isEmpty()){
            this.email.setError(warningsAdapter.getItem(1).toString());
            this.email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            this.email.setError(warningsAdapter.getItem(2).toString());
            this.email.requestFocus();
            return;
        }
        if (password.isEmpty()){
            this.password.setError(warningsAdapter.getItem(3).toString());
            this.password.requestFocus();
            return;
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
        logIn(email, password);

    }

    private void logIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar.setVisibility(View.GONE);
                    FirebaseUser user_verify = FirebaseAuth.getInstance().getCurrentUser();
                    if(user_verify.isEmailVerified()) {
                        createSession();
                        moveToMainActivity();

                    }else {
                        notifyUserToVerifyEmail();
                    }
                }else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar.setVisibility(View.GONE);
                    try
                    {
                        throw task.getException();
                    }
                    // if user enters wrong email.
                    catch (FirebaseAuthInvalidUserException invalidEmail)
                    {
                        notifyUserInvalidEmail();

                    }
                    // if user enters wrong password.
                    catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                    {
                        notifyUserWrongPassword();

                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        });

    }

    private void moveToMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    private void moveToRegisterActivity() {
        Intent intent = new Intent(Login.this, Register.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void resetPassword() {
        String email = this.email.getText().toString().trim();
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            notifyUserResetPassword();

                        }
                    }
                });
    }

    private void notifyUserResetPassword() {
        new MaterialAlertDialogBuilder(Login.this)
                .setTitle("Sign in" + "...")
                .setMessage(messagesAdapter.getItem(1).toString())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    private void notifyUserWrongPassword() {
        new MaterialAlertDialogBuilder(Login.this)
                .setTitle("Sign in" + "...")
                .setMessage(warningsAdapter.getItem(6).toString())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    private void notifyUserInvalidEmail() {

        new MaterialAlertDialogBuilder(Login.this)
                .setTitle("Sign in" + "...")
                .setMessage(warningsAdapter.getItem(5).toString())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void notifyUserToVerifyEmail() {
        new MaterialAlertDialogBuilder(Login.this)
                .setTitle("Sign in" + "...")
                .setMessage(messagesAdapter.getItem(0).toString())
                .setPositiveButton(messagesAdapter.getItem(6).toString(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendVarificationEmail();
                    }
                })
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }


    private void createSession() {
        SessionManagement sessionManagement = new SessionManagement(Login.this);
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
    private void sendVarificationEmail() {
        FirebaseUser user_verify = FirebaseAuth.getInstance().getCurrentUser();
        user_verify.sendEmailVerification();
    }
    }


