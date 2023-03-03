package com.dazone.crewchatoff.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.dazone.crewchatoff.R;
import com.dazone.crewchatoff.activity.base.BaseSingleBackActivity;
import com.dazone.crewchatoff.constant.Statics;
import com.dazone.crewchatoff.eventbus.NotifyAdapterOgr;
import com.dazone.crewchatoff.eventbus.RotationAction;
import com.dazone.crewchatoff.presenter.CrewChatPresenter;
import com.dazone.crewchatoff.utils.CrewChatApplication;
import com.dazone.crewchatoff.utils.Prefs;

import org.greenrobot.eventbus.EventBus;


public class CrewChatSettingActivity extends BaseSingleBackActivity implements CrewChatPresenter.RotationInterface {
    private Toolbar mToolBar;
    private SwitchCompat swEnter;
    private SwitchCompat swEnterVDuty;
    private TextView mTvScreenRotation;
    private Prefs mPrefs;

    private CrewChatPresenter mPresenter;

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, CrewChatSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_setting);

        mToolBar = findViewById(R.id.toolbar);
        swEnter = findViewById(R.id.sw_enter_auto);
        swEnterVDuty = findViewById(R.id.sw_enter_v_duty);
        mTvScreenRotation = findViewById(R.id.tv_screen_rotation);

        setupToolBarSingleTitle(getString(R.string.settings_crew_chat), mToolBar);
        mPresenter = new CrewChatPresenter(this, this);
        mPrefs = CrewChatApplication.getInstance().getPrefs();
        initView();
        initControls();
    }

    private void initControls() {
        swEnter.setOnClickListener(view -> mPrefs.putBooleanValue(Statics.IS_ENABLE_ENTER_KEY, swEnter.isChecked()));
        swEnterVDuty.setOnClickListener(view -> {
            Log.d("CrewChatSettingActivity", swEnterVDuty.isChecked() + "");
            setEnterVDuty(swEnterVDuty);
        });

        mTvScreenRotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.showDialog("");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void addFragment(Bundle bundle) {

    }

    private void initView() {
        isEnterAuto();
        inItEnterVDuty();
        mPresenter.rotationSettingValue(mTvScreenRotation);
    }

    private boolean isEnterAuto() {
        boolean isEnable = false;
        isEnable = mPrefs.getBooleanValue(Statics.IS_ENABLE_ENTER_KEY, isEnable);
        swEnter.setChecked(isEnable);
        return isEnable;
    }

    private boolean inItEnterVDuty() {
        boolean isEnable = false;
        isEnable = mPrefs.getBooleanValue(Statics.IS_ENABLE_ENTER_VIEW_DUTY_KEY, isEnable);
        swEnterVDuty.setChecked(isEnable);
        return isEnable;
    }

    private void setEnterVDuty(SwitchCompat swEnterVDuty) {
        boolean isEnable = swEnterVDuty.isChecked();
        mPrefs.putBooleanValue(Statics.IS_ENABLE_ENTER_VIEW_DUTY_KEY, isEnable);
        EventBus.getDefault().post(new NotifyAdapterOgr());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void rotationScreen() {
        mPresenter.rotationSettingValue(mTvScreenRotation);
        EventBus.getDefault().post(new RotationAction());
    }
}
