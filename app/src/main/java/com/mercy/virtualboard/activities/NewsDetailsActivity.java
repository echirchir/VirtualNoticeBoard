package com.mercy.virtualboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mercy.virtualboard.R;
import com.mercy.virtualboard.db.News;

import io.realm.Realm;

public class NewsDetailsActivity extends AppCompatActivity {

    private Realm realm;
    private TextView title;
    private TextView description;

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

        Intent intent = getIntent();

        long id = intent.getExtras().getLong("id");

        News news = realm.where(News.class).equalTo("id", id).findFirst();

        if (news != null){

            title.setText(news.getTitle());
            description.setText(news.getDescription());
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
