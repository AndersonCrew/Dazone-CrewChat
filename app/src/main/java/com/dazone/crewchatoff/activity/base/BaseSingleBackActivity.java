package com.dazone.crewchatoff.activity.base;

import static android.view.View.*;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.dazone.crewchatoff.R;
import com.google.android.material.appbar.AppBarLayout;

public abstract class BaseSingleBackActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        addFragment(savedInstanceState);
    }

    protected TextView toolbar_title, tvTitle;
    protected ImageView ivBack, imgBack;
    private LinearLayout llToolBar;
    private AppBarLayout appBar;

    protected void init() {
        setContentView(R.layout.activity_base_single_back);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar_title = findViewById(R.id.toolbar_title);
        ivBack = findViewById(R.id.back_imv);
        llToolBar = findViewById(R.id.llToolBar);
        appBar = findViewById(R.id.appBar);
        tvTitle = findViewById(R.id.tvTitle);
        imgBack = findViewById(R.id.imgBack);
        ivBack.setOnClickListener(v -> finish());

        imgBack.setOnClickListener(v -> finish());
    }

    public void setUPToolBar(String title) {
        llToolBar.setVisibility(VISIBLE);
        appBar.setVisibility(GONE);
        tvTitle.setText(title);
    }

    public void setupToolBarSingleTitle(String title, Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(title);
        ImageView ivBack = findViewById(R.id.back_imv);
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected abstract void addFragment(Bundle bundle);

    public void setTitle(String title) {
        toolbar_title.setText(title);
    }
}