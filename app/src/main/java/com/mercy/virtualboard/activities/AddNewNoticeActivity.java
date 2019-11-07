package com.mercy.virtualboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.mercy.virtualboard.R;
import com.mercy.virtualboard.db.News;
import com.mercy.virtualboard.db.User;
import com.mercy.virtualboard.db.Util;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AddNewNoticeActivity extends AppCompatActivity {

    private Realm realm;
    private TextInputLayout subjectWrapper;
    private EditText subject;

    private TextInputLayout noticeDetailsWrapper;
    private EditText noticeDetails;

    private AppCompatButton save;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_notice);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();

        subject = findViewById(R.id.subject);
        subjectWrapper = findViewById(R.id.subject_wrapper);

        noticeDetails = findViewById(R.id.notice_details);
        noticeDetailsWrapper = findViewById(R.id.notice_details_wrapper);

        save = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sub = subject.getText().toString().trim();
                String details = noticeDetails.getText().toString().trim();

                if (sub.equalsIgnoreCase("") || sub.length() < 5){
                    subjectWrapper.setErrorEnabled(true);
                    subjectWrapper.setError("Subject field is required and must be at least 5 characters long");
                }else if (details.equalsIgnoreCase("") || details.length() < 10){
                    noticeDetailsWrapper.setErrorEnabled(true);
                    noticeDetailsWrapper.setError("Notice description cannot be less than 10 characters long");
                }else{

                    storeAndRedirect(sub, details);
                }
            }
        });
    }

    private void storeAndRedirect(String sub, String details){

        RealmResults<News> all = realm.where(News.class).findAll().sort("id", Sort.ASCENDING);

        long lastId;

        final News toAdd = new News();

        if (all.isEmpty()){
            toAdd.setId(1);
        }else{
            lastId = all.last().getId();
            toAdd.setId( lastId + 1);
        }

        toAdd.setAuthor(user.getRegistration_number());
        toAdd.setCreated_on(Util.getCurrentDate());
        toAdd.setTitle(sub);
        toAdd.setDescription(details);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(toAdd);
            }
        });

        startActivity( new Intent( this, NewsHeadlinesActivity.class));
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
