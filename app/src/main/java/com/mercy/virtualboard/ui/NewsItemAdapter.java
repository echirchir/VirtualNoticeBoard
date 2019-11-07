package com.mercy.virtualboard.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mercy.virtualboard.R;

import java.util.ArrayList;
import java.util.List;

public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemViewHolder>{

    private List<NewsUIObject> items;

    public NewsItemAdapter(List<NewsUIObject> notices) {
        items = new ArrayList<>(notices);
    }

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_layout, parent, false);

        return new NewsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsItemViewHolder holder, int position) {

        holder.noticeTitle.setText("Notice - " + items.get(position).getDate());
        holder.newsTitle.setText(items.get(position).getTitle());
        holder.newsDescription.setText(items.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<NewsUIObject> list) {
        items.addAll(list);
        notifyDataSetChanged();
    }

    public void setModels(List<NewsUIObject> customers){
        items = new ArrayList<>(customers);
    }

    public void animateTo(List<NewsUIObject> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<NewsUIObject> newModels) {
        for (int i = items.size() - 1; i >= 0; i--) {
            final NewsUIObject model = items.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<NewsUIObject> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final NewsUIObject model = newModels.get(i);
            if (!items.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<NewsUIObject> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final NewsUIObject model = newModels.get(toPosition);
            final int fromPosition = items.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private NewsUIObject removeItem(int position) {
        final NewsUIObject model = items.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, NewsUIObject model) {
        items.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final NewsUIObject model = items.remove(fromPosition);
        items.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public NewsUIObject getItem(int position){
        return items.get(position);
    }
}
