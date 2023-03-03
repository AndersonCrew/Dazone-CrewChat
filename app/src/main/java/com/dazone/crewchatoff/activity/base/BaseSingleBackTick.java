package com.dazone.crewchatoff.activity.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.dazone.crewchatoff.R;
import com.dazone.crewchatoff.interfaces.OnTickCallback;

public abstract class BaseSingleBackTick extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        addFragment(savedInstanceState);
    }

    protected TextView toolbar_title;
    protected ImageView ivBack, ivTick;

    private OnTickCallback mCallback;

    public void setCallback(OnTickCallback mCallback) {
        this.mCallback = mCallback;
    }

    protected void init() {
        setContentView(R.layout.activity_base_single_back_tick);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        toolbar_title = findViewById(R.id.toolbar_title);
        ivBack = findViewById(R.id.back_imv);
        ivTick = findViewById(R.id.iv_tick);
        ivTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onTick();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setUPToolBar(String title) {
        if (TextUtils.isEmpty(title)) {
            toolbar_title.setText(mContext.getResources().getString(R.string.unknown));
            toolbar_title.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
        } else {
            toolbar_title.setText(title);
            toolbar_title.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
    }

    protected abstract void addFragment(Bundle bundle);

    public void setTitle(String title) {
        toolbar_title.setText(title);
    }
}