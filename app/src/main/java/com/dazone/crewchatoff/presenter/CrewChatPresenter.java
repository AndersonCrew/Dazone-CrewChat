package com.dazone.crewchatoff.presenter;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dazone.crewchatoff.R;
import com.dazone.crewchatoff.constant.Statics;
import com.dazone.crewchatoff.utils.Constant;
import com.dazone.crewchatoff.utils.CrewChatApplication;
import com.dazone.crewchatoff.utils.Prefs;

public class CrewChatPresenter implements CompoundButton.OnCheckedChangeListener {
    private Context context;
    private Dialog mDialog;
    private Prefs mPrefs;
    private RadioButton rd_automatic;
    private RadioButton rd_portrait;
    private RadioButton rd_landscape;
    private RotationInterface mRotationInterface;

    public CrewChatPresenter(Context context, RotationInterface mRotationInterface) {
        this.mRotationInterface = mRotationInterface;
        this.context = context;
        mPrefs = CrewChatApplication.getInstance().getPrefs();
    }

    public void showDialog(String msg) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setContentView(R.layout.dialog_group_radiobutton);
        rd_automatic = mDialog.findViewById(R.id.rd_automatic);
        rd_portrait = mDialog.findViewById(R.id.rd_portrait);
        rd_landscape = mDialog.findViewById(R.id.rd_landscape);
        rd_automatic.setOnCheckedChangeListener(this);
        rd_portrait.setOnCheckedChangeListener(this);
        rd_landscape.setOnCheckedChangeListener(this);
        setRotation();
        mDialog.show();

    }


    private void setRotation() {
        int rotation = mPrefs.getIntValue(Statics.SCREEN_ROTATION, Constant.PORTRAIT);
        switch (rotation) {
            case Constant.AUTOMATIC:
                rd_automatic.setChecked(true);
                break;
            case Constant.PORTRAIT:
                rd_portrait.setChecked(true);
                break;
            case Constant.LANSCAPE:
                rd_landscape.setChecked(true);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            switch (buttonView.getId()) {
                case R.id.rd_automatic:
                    mPrefs.putIntValue(Statics.SCREEN_ROTATION, Constant.AUTOMATIC);
                    mDialog.dismiss();
                    break;
                case R.id.rd_portrait:
                    mPrefs.putIntValue(Statics.SCREEN_ROTATION, Constant.PORTRAIT);
                    mDialog.dismiss();
                    break;
                case R.id.rd_landscape:
                    mPrefs.putIntValue(Statics.SCREEN_ROTATION, Constant.LANSCAPE);
                    mDialog.dismiss();
                    break;
            }

            mRotationInterface.rotationScreen();
        }



    }

    /**
     * set value to Textview Rotation
     *
     * @param tv
     */
    public void rotationSettingValue(TextView tv) {
        int rotation = mPrefs.getIntValue(Statics.SCREEN_ROTATION, Constant.PORTRAIT);

        switch (rotation) {
            case Constant.AUTOMATIC:
                tv.setText(context.getString(R.string.automatic));
                break;
            case Constant.LANSCAPE:
                tv.setText(context.getString(R.string.landscape));
                break;
            case Constant.PORTRAIT:
            default:
                tv.setText(context.getString(R.string.portrait));
        }
    }

    public interface RotationInterface {
        void rotationScreen();

    }
}
