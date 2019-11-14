package com.mercy.virtualboard.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

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

    private void openExcelFileChooser(){

        String[] mimeTypes = {"application/vnd.ms-excel","application/pdf","application/msword","application/vnd.ms-powerpoint","text/plain"};

        Intent intent = new Intent(Intent.ACTION_PICK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        startActivityForResult(Intent.createChooser(intent,"Choose Timetable"), RESULT_LOAD_TIMETABLE);
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

                Log.d("FILELOCALEXTE", data.getData().toString()) ;

                File file = new File(data.getData().toString());
                selectTimetable.setText(file.getAbsolutePath());

                if (data.getClipData() == null) {

                } else {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Log.d("EXCELDATA", "data.getClipData().getItemAt(i).getUri().toString()" + data.getClipData().getItemAt(i).getUri().toString());
                    }
                }
            }
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
