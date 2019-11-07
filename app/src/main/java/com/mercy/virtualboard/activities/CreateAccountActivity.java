package com.mercy.virtualboard.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.mercy.virtualboard.R;
import com.mercy.virtualboard.db.User;
import com.mercy.virtualboard.db.Util;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class CreateAccountActivity extends AppCompatActivity {

    private TextInputLayout registrationNumberWrapper;
    private EditText registrationNumber;
    private TextInputLayout emailWrapper;
    private EditText emailAddress;
    private TextInputLayout passwordWrapper;
    private EditText password;
    private TextInputLayout phoneWrapper;
    private EditText phone;
    private AppCompatSpinner spinner;
    private AppCompatButton register;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        realm = Realm.getDefaultInstance();

        registrationNumberWrapper = findViewById(R.id.registration_number_wrapper);
        registrationNumber = findViewById(R.id.registration_number);
        emailWrapper = findViewById(R.id.email_wrapper);
        emailAddress = findViewById(R.id.email);

        passwordWrapper = findViewById(R.id.password_wrapper);
        password = findViewById(R.id.password);
        phoneWrapper = findViewById(R.id.phone_wrapper);
        phone = findViewById(R.id.phone);
        register = findViewById(R.id.register);

        spinner = findViewById(R.id.spinner);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Select type");
        arrayList.add("Admin");
        arrayList.add("Student");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String regisNumber = registrationNumber.getText().toString().trim();
                String email = emailAddress.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String phoneNumber = phone.getText().toString().trim();
                String accountType = spinner.getSelectedItem().toString();

                if (regisNumber.equals("")){
                    registrationNumberWrapper.setError("Registration number is required!");
                    registrationNumberWrapper.setErrorEnabled(true);
                }else if (email.equals("") || !Util.isValidEmail(email)){
                    emailWrapper.setErrorEnabled(true);
                    emailWrapper.setError("A valid email is required!");
                }else if (pass.equals("") || pass.length() < 6){
                    passwordWrapper.setError("Password must be at least 6 characters long");
                    passwordWrapper.setErrorEnabled(true);
                }else if (phoneNumber.equals("") || phoneNumber.length() < 9){
                    phoneWrapper.setErrorEnabled(true);
                    phoneWrapper.setError("Enter a valid phone number (at least 9 digits)");
                }else if (accountType.equals("Select type")){
                    TextView errorText = (TextView)spinner.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Select either Admin or Student");
                }else{
                    store(regisNumber, email, pass, phoneNumber, accountType);
                }

            }
        });
    }

    private void store(String registration, String email, String pass, String phone, String type){

        User user = realm.where(User.class).equalTo("registration_number", registration).findFirst();

        if (user != null){
            registrationNumberWrapper.setErrorEnabled(true);
            registrationNumberWrapper.setError("Your account is already registered. Try to login!");
        }else{
            final RealmResults<User> all = realm.where(User.class).findAll().sort("id", Sort.DESCENDING);

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    all.deleteAllFromRealm();
                }
            });

            final User me = new User();
            me.setId(1);
            me.setEmail_address(email);
            me.setAccount_type(type);
            me.setIs_logged_in(false);
            me.setPassword(pass);
            me.setPhone_number(phone);

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(me);
                }
            });

            startActivity( new Intent( this, LoginActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
