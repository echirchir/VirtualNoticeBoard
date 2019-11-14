package com.mercy.virtualboard.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.mercy.virtualboard.R;
import com.mercy.virtualboard.db.News;
import com.mercy.virtualboard.db.User;
import com.mercy.virtualboard.db.Util;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AddNewNoticeActivity extends AppCompatActivity {

    private Realm realm;
    private TextInputLayout subjectWrapper;
    private EditText subject;

    private TextInputLayout noticeDetailsWrapper;
    private EditText noticeDetails;

    private EditText selectPhoto;
    private EditText selectTimetable;

    private AppCompatButton save;

    private User user;

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_LOAD_TIMETABLE = 2;
    public static final int SMS_PERMISSIONS = 2;

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

        selectPhoto = findViewById(R.id.select_photo);
        selectTimetable = findViewById(R.id.select_timetable);

        save = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sub = subject.getText().toString().trim();
                String details = noticeDetails.getText().toString().trim();
                String photo_path = selectPhoto.getText().toString().trim();
                String timetable_path = selectTimetable.getText().toString().trim();

                if (sub.equalsIgnoreCase("") || sub.length() < 5){
                    subjectWrapper.setErrorEnabled(true);
                    subjectWrapper.setError("Subject field is required and must be at least 5 characters long");
                }else if (details.equalsIgnoreCase("") || details.length() < 10){
                    noticeDetailsWrapper.setErrorEnabled(true);
                    noticeDetailsWrapper.setError("Notice description cannot be less than 10 characters long");
                }else{

                    String path = "";
                    String timetable = "";

                    if (!timetable_path.equals("")){
                        timetable = timetable_path;
                    }

                    if (!photo_path.equals("")){
                        path = photo_path;
                    }

                    storeAndRedirect(sub, details, path, timetable);

                }
            }
        });

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        selectTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser();
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){

            }else{
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, SMS_PERMISSIONS );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == SMS_PERMISSIONS){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{

            }
            return;
        }
    }

    public void showFileChooser() {

        String[] mimeTypes = {"application/vnd.ms-excel","application/pdf","application/msword"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        try {
            startActivityForResult(intent, RESULT_LOAD_TIMETABLE);
        } catch (android.content.ActivityNotFoundException ex) {

            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == RESULT_LOAD_IMAGE && data != null){

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                selectPhoto.setText(picturePath);

            }else if (requestCode == RESULT_LOAD_TIMETABLE && data != null){

                File file = new File(data.getData().toString());
                selectTimetable.setText(file.getAbsolutePath());

                if (data.getClipData() == null) {

                } else {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {

                    }
                }
            }
        }

    }

    public void sendSmsNotification(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "SMS notification sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void storeAndRedirect(String sub, String details, String path, String timetable){

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
        toAdd.setPhoto_path(path);
        toAdd.setTimetable_path(timetable);
        toAdd.setTitle(sub);
        toAdd.setDescription(details);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(toAdd);
            }
        });

        sendSmsNotification(user.getPhone_number(), sub);

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
