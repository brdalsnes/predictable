package com.brdalsnes.predictable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Bjornar on 02/08/2015.
 */
public class CreateUserActivity extends Activity {

    private String username, email, password, passwordCheck;

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

        signupBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                username = usernameEdit.getText().toString();
                email = emailEdit.getText().toString();
                password = passwordEdit.getText().toString();
                passwordCheck = passwordCheckEdit.getText().toString();

                if(username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordCheck.isEmpty()){
                    //Failed
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateUserActivity.this);
                    builder.setTitle("Try again!");
                    builder.setMessage("Fill all fields");
                    builder.setPositiveButton("OK", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if(!password.equals(passwordCheck)){
                    //Failed
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateUserActivity.this);
                    builder.setTitle("Try again!");
                    builder.setMessage("Passwords must match");
                    builder.setPositiveButton("OK", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    Log.i("else", "here");
                    Firebase ref = new Firebase("https://predictable-88ea7.firebaseio.com/");
                    ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            Log.i("SUCCESS", "Successfully created user account with uid: " + result.get("uid"));
                        }
                        @Override
                        public void onError(FirebaseError firebaseError) {
                            // there was an error
                            Log.i("Error", firebaseError.getDetails());
                        }
                    });

                }
            }
        });

        loginTextBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(CreateUserActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

}
