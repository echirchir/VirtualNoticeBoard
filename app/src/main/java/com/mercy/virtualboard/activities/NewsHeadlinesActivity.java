package com.mercy.virtualboard.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mercy.virtualboard.R;
import com.mercy.virtualboard.db.News;
import com.mercy.virtualboard.db.User;
import com.mercy.virtualboard.ui.DividerItemDecoration;
import com.mercy.virtualboard.ui.NewsItemAdapter;
import com.mercy.virtualboard.ui.NewsUIObject;
import com.mercy.virtualboard.ui.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class NewsHeadlinesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private TextView emptyStateList;

    private LinearLayoutManager mLayoutManager;
    private NewsItemAdapter adapter;
    private List<NewsUIObject> newsUIObjects;
    private Realm realm;

    public static final int PERMISSAO_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_headlines);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        realm = Realm.getDefaultInstance();

        User user = realm.where(User.class).findFirst();

        recyclerView = findViewById(R.id.recycler);
        emptyStateList = findViewById(R.id.generic_empty_view);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_drawable);
        recyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        FloatingActionButton fab = findViewById(R.id.fab);

        if (user != null){
            if (user.getAccount_type().equals("Student")){
                fab.setVisibility(View.INVISIBLE);
            }
        }

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSAO_REQUEST );
            }
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity( new Intent( NewsHeadlinesActivity.this, AddNewNoticeActivity.class));
            }
        });

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(NewsHeadlinesActivity.this, NewsDetailsActivity.class);
                intent.putExtra("id", adapter.getItem(position).getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        loadNotices();
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

    private void loadNotices(){

        newsUIObjects = new ArrayList<>();

        RealmResults<News> newsRealmResults = realm.where(News.class).findAll().sort("id", Sort.DESCENDING);

        if (!newsRealmResults.isEmpty()){

            for (News item : newsRealmResults){

                NewsUIObject object = new NewsUIObject();
                object.setDate(item.getCreated_on());
                object.setTitle(item.getTitle());
                object.setPhoto_path(item.getPhoto_path());
                if (item.getDescription().length() > 100){
                    object.setDescription(item.getDescription().substring(0, 100));
                }else{
                    object.setDescription(item.getDescription());
                }

                object.setId(item.getId());

                newsUIObjects.add(object);
            }
        }

        adapter = new NewsItemAdapter(newsUIObjects, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() > 0) {
            emptyStateList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.global_search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);

        item.expandActionView();

        final SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<NewsUIObject> filter(List<NewsUIObject> models, String query) {

        query = query.toLowerCase();

        final List<NewsUIObject> filteredModelList = new ArrayList<>();

        if(query.equals("")) { return newsUIObjects; }

        for (NewsUIObject model : models) {
            final String text = model.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.equals("")){
            loadNotices();
            return true;
        }else{
            final List<NewsUIObject> filteredModelList = filter(newsUIObjects, newText);

            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            adapter.animateTo(filteredModelList);
            recyclerView.scrollToPosition(0);
            return true;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
