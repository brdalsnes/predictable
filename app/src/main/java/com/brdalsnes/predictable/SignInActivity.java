package com.brdalsnes.predictable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Bjornar on 02/08/2015.
 */
public class SignInActivity extends Activity {

    private String username, email, password, passwordCheck, id;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @InjectView(R.id.usernameEdit) EditText usernameEdit;
    @InjectView(R.id.passwordEdit) EditText passwordEdit;
    @InjectView(R.id.passwordCheckEdit) EditText passwordCheckEdit;
    @InjectView(R.id.emailEdit) EditText emailEdit;
    @InjectView(R.id.signupBtn) Button signupBtn;
    @InjectView(R.id.loginTextBtn) TextView loginTextBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        

        database = FirebaseDatabase.getInstance().getReference();

        //Respond to changes in sign-up state
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.i("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                    id = user.getUid();
                } else {
                    // User is signed out
                    Log.i("TAG", "onAuthStateChanged:signed_out");
                }
            }
        };

        signupBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                username = usernameEdit.getText().toString();
                email = emailEdit.getText().toString();
                password = passwordEdit.getText().toString();
                passwordCheck = passwordCheckEdit.getText().toString();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordCheck.isEmpty()) {
                    //Failed
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                    builder.setTitle("Try again!");
                    builder.setMessage("Fill all fields");
                    builder.setPositiveButton("OK", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (!password.equals(passwordCheck)) {
                    //Failed
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                    builder.setTitle("Try again!");
                    builder.setMessage("Passwords must match");
                    builder.setPositiveButton("OK", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    //Success
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Toasts
                            if(task.isSuccessful()){
                                Toast.makeText(SignInActivity.this, "Account created",
                                        Toast.LENGTH_SHORT).show();

                                //Add user to database
                                User user = new User(username, email, 1000);
                                database.child("users").child(id).setValue(user);


                                //Go to main
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(SignInActivity.this, "Registration failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        loginTextBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
