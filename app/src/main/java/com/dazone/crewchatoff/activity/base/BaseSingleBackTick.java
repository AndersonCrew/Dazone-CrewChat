package com.dazone.crewchatoff.activity.base;

import android.os.Bundle;

import com.dazone.crewchatoff.R;
import com.dazone.crewchatoff.interfaces.OnTickCallback;

public abstract class BaseSingleBackTick extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        addFragment(savedInstanceState);

    }

    private OnTickCallback mCallback;

    public void setCallback(OnTickCallback mCallback) {
        this.mCallback = mCallback;
    }

    protected void init() {
        setContentView(R.layout.activity_base_single_back_tick);

        findViewById(R.id.tvDone).setOnClickListener(v -> mCallback.onTick());
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
    }


    protected abstract void addFragment(Bundle bundle);
}