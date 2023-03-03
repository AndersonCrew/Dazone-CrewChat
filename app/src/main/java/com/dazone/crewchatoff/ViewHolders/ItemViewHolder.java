package com.dazone.crewchatoff.ViewHolders;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.dazone.crewchatoff.utils.CrewChatApplication;

public abstract class ItemViewHolder<T> extends RecyclerView.ViewHolder {
    protected final String mRootLink;

    public ItemViewHolder(View v) {
        super(v);
        mRootLink = CrewChatApplication.getInstance().getPrefs().getServerSite();
        setup(v);
    }

    protected abstract void setup(View v);
    public abstract void bindData(T t);

    @Override
    public String toString() {
        return super.toString();
    }
}