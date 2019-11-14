package com.mercy.virtualboard.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mercy.virtualboard.R;

public class NewsItemViewHolder extends RecyclerView.ViewHolder {

    public TextView noticeTitle;
    public TextView newsTitle;
    public TextView newsDescription;
    public ImageView photo;

    public NewsItemViewHolder(@NonNull View itemView) {

        super(itemView);

        noticeTitle = itemView.findViewById(R.id.title);
        newsTitle = itemView.findViewById(R.id.newsTitle);
        newsDescription = itemView.findViewById(R.id.newsInfo);
        photo = itemView.findViewById(R.id.thumbnail);
    }
}
