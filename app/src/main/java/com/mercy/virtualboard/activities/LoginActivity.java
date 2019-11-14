package com.mercy.virtualboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.mercy.virtualboard.R;
import com.mercy.virtualboard.db.User;

import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private TextInputLayout userNameWrapper;
    private EditText password;
    private TextInputLayout passwordWrapper;
    private AppCompatButton login;
    private AppCompatButton signUp;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        User current = realm.where(User.class).findFirst();

        if (current != null){
            if (current.isIs_logged_in()){
                startActivity( new Intent( this, NewsHeadlinesActivity.class));
            }
        }

        username = findViewById(R.id.user_name);
        userNameWrapper = findViewById(R.id.user_name_wrapper);
        password = findViewById(R.id.password);
        passwordWrapper = findViewById(R.id.password_wrapper);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username_value = username.getText().toString().trim();
                String password_value = password.getText().toString().trim();

                if (username_value.equalsIgnoreCase("")){
                    userNameWrapper.setErrorEnabled(true);
                    userNameWrapper.setError("Username field is required");
                }else if (password_value.equalsIgnoreCase("")){
                    passwordWrapper.setErrorEnabled(true);
                    passwordWrapper.setError("Password is required.");
                }else{
                    doLogin(username_value, password_value);
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( LoginActivity.this, CreateAccountActivity.class));
            }
        });
    }

    private void doLogin(String username_val, String password_val){

        User existing = realm.where(User.class).equalTo("email_address", username_val).findFirst();

        if (existing == null){
            userNameWrapper.setErrorEnabled(true);
            userNameWrapper.setError("This email does not exist. Try a different one?");
        }else{

            userNameWrapper.setErrorEnabled(false);

            final User toLoggIn = realm.where(User.class).equalTo("email_address", username_val).equalTo("password", password_val).findFirst();

            if (toLoggIn == null){
                Toast.makeText(this, "Check your email and password then try again!", Toast.LENGTH_LONG).show();
            }else{
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        toLoggIn.setIs_logged_in(true);
                        realm.copyToRealmOrUpdate(toLoggIn);
                    }
                });

                startActivity( new Intent( this, NewsHeadlinesActivity.class));
            }
        }
    }
}
