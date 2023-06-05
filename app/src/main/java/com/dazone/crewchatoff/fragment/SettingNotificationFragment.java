package com.dazone.crewchatoff.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dazone.crewchatoff.HTTPs.HttpRequest;
import com.dazone.crewchatoff.R;
import com.dazone.crewchatoff.constant.Statics;
import com.dazone.crewchatoff.dto.ErrorDto;
import com.dazone.crewchatoff.interfaces.OnSetNotification;
import com.dazone.crewchatoff.interfaces.Urls;
import com.dazone.crewchatoff.utils.CrewChatApplication;
import com.dazone.crewchatoff.utils.Prefs;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.HashMap;
import java.util.Map;

public class SettingNotificationFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private View mView;
    private CheckBox swEnableSound, swEnableVibrate, swEnableNotificationTime, swEnableNotificationWhenUsingPCVersion, swEnableNotification;
    private Prefs prefs;
    private TextView tvStartTime, tvEndTime,tv_notify_setting_time;
    /*
    * All date time var
    * */
    int hourStart, minuteStart, hourEnd, minuteEnd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_notification_setting, container, false);

        prefs = CrewChatApplication.getInstance().getPrefs();
        initView();

        return mView;
    }

    private void initView() {
        swEnableNotification = mView.findViewById(R.id.sw_enable_notification);
        swEnableSound = mView.findViewById(R.id.sw_enable_sound);
        swEnableVibrate = mView.findViewById(R.id.sw_enable_vibrate);
        swEnableNotificationTime = mView.findViewById(R.id.sw_enable_notification_time);
        swEnableNotificationWhenUsingPCVersion = mView.findViewById(R.id.sw_enable_chat_by_pc_version);

        tvStartTime = mView.findViewById(R.id.tv_start_time);
        tvEndTime = mView.findViewById(R.id.tv_end_time);
        tv_notify_setting_time = mView.findViewById(R.id.tv_notify_setting_time);
        tv_notify_setting_time.setText(Html.fromHtml("<font color=#000>"
                + getResources().getString(R.string.settings_notification_time_value) + "<br />" + "<small>" + getResources().getString(R.string.settings_notification_time_2)
                + "</small>"));

        boolean isEnableN = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION, true);
        boolean isEnableSound = prefs.getBooleanValue(Statics.ENABLE_SOUND, true);
        boolean isEnableVibrate = prefs.getBooleanValue(Statics.ENABLE_VIBRATE, true);
        boolean isEnableTime = prefs.getBooleanValue(Statics.ENABLE_TIME, false);

        boolean isEnableNotificationWhenUsingPcVersion = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION, true);


        hourStart = prefs.getIntValue(Statics.TIME_HOUR_START_NOTIFICATION, 8);
        minuteStart = prefs.getIntValue(Statics.TIME_MINUTE_START_NOTIFICATION, 0);

        hourEnd = prefs.getIntValue(Statics.TIME_HOUR_END_NOTIFICATION, 18);
        minuteEnd = prefs.getIntValue(Statics.TIME_MINUTE_END_NOTIFICATION, 0);
        String strHourStart = hourStart < 10 ? "0" + hourStart : hourStart + "";
        String strMinuteStart = minuteStart < 10 ? "0" + minuteStart : minuteStart + "";

        String strHourEnd = hourEnd < 10 ? "0" + hourEnd : hourEnd + "";
        String strMinuteEnd = minuteEnd < 10 ? "0" + minuteEnd : minuteEnd + "";
        tvStartTime.setText(strHourStart + ":" + strMinuteStart);
        tvEndTime.setText(strHourEnd + ":" + strMinuteEnd);


        if (isEnableN) {
            swEnableNotification.setChecked(true);

            swEnableSound.setEnabled(true);
            swEnableVibrate.setEnabled(true);
            swEnableNotificationTime.setEnabled(true);
            swEnableNotificationWhenUsingPCVersion.setEnabled(true);
        } else {

            swEnableNotification.setChecked(false);

            swEnableSound.setEnabled(false);
            swEnableVibrate.setEnabled(false);
            swEnableNotificationTime.setEnabled(false);
            swEnableNotificationWhenUsingPCVersion.setEnabled(false);
        }

        swEnableSound.setChecked(isEnableSound);
        swEnableVibrate.setChecked(isEnableVibrate);
        swEnableNotificationTime.setChecked(isEnableTime);
        swEnableNotificationWhenUsingPCVersion.setChecked(isEnableNotificationWhenUsingPcVersion);

        // Checked time to enable button time select
        if (isEnableTime) {
            tvStartTime.setEnabled(true);
            tvEndTime.setEnabled(true);
        } else {
            tvStartTime.setEnabled(false);
            tvEndTime.setEnabled(false);
        }


        // Set event when toggle button change state
        swEnableNotification.setOnCheckedChangeListener(this);
        swEnableSound.setOnCheckedChangeListener(this);
        swEnableVibrate.setOnCheckedChangeListener(this);
        swEnableNotificationTime.setOnCheckedChangeListener(this);
        swEnableNotificationWhenUsingPCVersion.setOnCheckedChangeListener(this);
        // set event listener for set time text view
        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
    }

    private void setNotification() {

        boolean isEnableN = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION, true);
        boolean isEnableSound = prefs.getBooleanValue(Statics.ENABLE_SOUND, true);
        boolean isEnableVibrate = prefs.getBooleanValue(Statics.ENABLE_VIBRATE, true);
        boolean isEnableTime = prefs.getBooleanValue(Statics.ENABLE_TIME, false);
        boolean isEnableNotificationWhenUsingPcVersion = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION, true);


        String strHourStart = hourStart < 10 ? "0" + hourStart : hourStart + "";
        String strMinuteStart = minuteStart < 10 ? "0" + minuteStart : minuteStart + "";

        String strHourEnd = hourEnd < 10 ? "0" + hourEnd : hourEnd + "";
        String strMinuteEnd = minuteEnd < 10 ? "0" + minuteEnd : minuteEnd + "";


        Map<String, Object> params = new HashMap<>();
        params.put("enabled", isEnableN);
        params.put("sound", isEnableSound);
        params.put("vibrate", isEnableVibrate);
        params.put("notitime", isEnableTime);
        params.put("starttime", strHourStart + ":" + strMinuteStart );
        params.put("endtime", strHourEnd + ":" + strMinuteEnd);
        params.put("confirmonline", isEnableNotificationWhenUsingPcVersion);


        HttpRequest.getInstance().setNotification(Urls.URL_INSERT_DEVICE,
                prefs.getGCMregistrationid(),
                params,
                new OnSetNotification() {
                    @Override
                    public void OnSuccess() {
                    }

                    @Override
                    public void OnFail(ErrorDto errorDto) {
                    }
                }
        );
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_enable_notification:
                if (isChecked) {
                    prefs.putBooleanValue(Statics.ENABLE_NOTIFICATION, true);

                    swEnableSound.setEnabled(true);
                    swEnableVibrate.setEnabled(true);
                    swEnableNotificationTime.setEnabled(true);
                    swEnableNotificationWhenUsingPCVersion.setEnabled(true);

                } else {
                    prefs.putBooleanValue(Statics.ENABLE_NOTIFICATION, false);

                    swEnableSound.setEnabled(false);
                    swEnableVibrate.setEnabled(false);
                    swEnableNotificationTime.setEnabled(false);
                    swEnableNotificationWhenUsingPCVersion.setEnabled(false);
                }
                break;
            case R.id.sw_enable_sound:
                if (isChecked) {
                    prefs.putBooleanValue(Statics.ENABLE_SOUND, true);
                } else {
                    prefs.putBooleanValue(Statics.ENABLE_SOUND, false);
                }
                break;
            case R.id.sw_enable_vibrate:
                if (isChecked) {
                    prefs.putBooleanValue(Statics.ENABLE_VIBRATE, true);
                } else {
                    prefs.putBooleanValue(Statics.ENABLE_VIBRATE, false);
                }
                break;
            case R.id.sw_enable_notification_time:
                if (isChecked) {
                    prefs.putBooleanValue(Statics.ENABLE_TIME, true);

                    // Enable time select
                    tvStartTime.setEnabled(true);
                    tvEndTime.setEnabled(true);

                } else {
                    prefs.putBooleanValue(Statics.ENABLE_TIME, false);

                    // Disable time select
                    tvStartTime.setEnabled(false);
                    tvEndTime.setEnabled(false);
                }
                break;
            case R.id.sw_enable_chat_by_pc_version:
                if (isChecked) {
                    prefs.putBooleanValue(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION, true);
                } else {
                    prefs.putBooleanValue(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION, false);
                }
                break;
        }
        // Update to server
        setNotification();
    }

    private TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            // set value and tag for start time textview
            hourStart = hourOfDay;
            minuteStart = minute;
            String strHourStart = hourStart < 10 ? "0" + hourStart : hourStart + "";
            String strMinuteStart = minuteStart < 10 ? "0" + minuteStart : minuteStart + "";

            tvStartTime.setText(strHourStart + ":" + strMinuteStart);
            if(hourStart > hourEnd) {
                hourEnd = hourOfDay;
                minuteEnd = minute;
                String strHourEnd = hourEnd < 10 ? "0" + hourEnd : hourEnd + "";
                String strMinuteEnd = minuteEnd < 10 ? "0" + minuteEnd : minuteEnd + "";
                tvEndTime.setText(strHourEnd + ":" + strMinuteEnd);
            }

            if(swEnableNotificationTime.isChecked()) {
                setNotification();
                prefs.putIntValue(Statics.TIME_HOUR_START_NOTIFICATION, hourStart);
                prefs.putIntValue(Statics.TIME_MINUTE_START_NOTIFICATION, minuteStart);
                prefs.putIntValue(Statics.TIME_HOUR_END_NOTIFICATION, hourEnd);
                prefs.putIntValue(Statics.TIME_MINUTE_END_NOTIFICATION, minuteEnd);
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            if (hourOfDay < hourStart || hourOfDay == hourStart && minute < minuteStart) {
                Toast.makeText(getActivity(), "Please choose end time greater than start time", Toast.LENGTH_LONG).show();
            } else {
                hourEnd = hourOfDay;
                minuteEnd = minute;

                String strHourEnd = hourEnd < 10 ? "0" + hourEnd : hourEnd + "";
                String strMinuteEnd = minuteEnd < 10 ? "0" + minuteEnd : minuteEnd + "";
                tvEndTime.setText(strHourEnd + ":" + strMinuteEnd);

                if (swEnableNotificationTime.isChecked()) {
                    setNotification();
                    prefs.putIntValue(Statics.TIME_HOUR_END_NOTIFICATION, hourEnd);
                    prefs.putIntValue(Statics.TIME_MINUTE_END_NOTIFICATION, minuteEnd);
                }
            }
        }
    };

    private void showTimePicker(int type) {
        TimePickerDialog tpd = null;

        if (type == 0) {
            tpd = TimePickerDialog.newInstance(startTimeListener, hourStart, minuteStart, true);
        } else if (type == 1) {
            tpd = TimePickerDialog.newInstance(endTimeListener, hourEnd, minuteEnd, true);
        }

        if (tpd != null) {
            int accentColor = getResources().getColor(R.color.actionbar_background);
            tpd.setAccentColor(accentColor);
            tpd.show(getFragmentManager(), "Timepickerdialog");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start_time:
                showTimePicker(0);
                break;

            case R.id.tv_end_time:
                showTimePicker(1);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}