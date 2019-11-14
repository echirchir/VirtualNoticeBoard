package com.mercy.virtualboard.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mercy.virtualboard.R;
import com.mercy.virtualboard.db.News;

import io.realm.Realm;

public class NewsDetailsActivity extends AppCompatActivity {

    private Realm realm;
    private TextView title;
    private TextView description;
    private ImageView headerImage;
    private AppCompatButton timetable;

    public static final int PERMISSAO_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        realm = Realm.getDefaultInstance();

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        headerImage = findViewById(R.id.header_image);
        timetable = findViewById(R.id.timetable);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSAO_REQUEST );
            }
        }

        Intent intent = getIntent();

        long id = intent.getExtras().getLong("id");

        News news = realm.where(News.class).equalTo("id", id).findFirst();

        if (news != null){

            title.setText(news.getTitle());
            description.setText(news.getDescription());
            if (!news.getPhoto_path().equals("")){
                headerImage.setImageBitmap(BitmapFactory.decodeFile(news.getPhoto_path()));
            }

            if (!news.getTimetable_path().equals("")){
                timetable.setVisibility(View.VISIBLE);
            }

        }

        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewFiles();
            }
        });

    }

    public void viewFiles() {

        String[] mimeTypes = {"application/vnd.ms-excel","application/pdf","application/msword"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {

            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSAO_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{

            }
            return;
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
