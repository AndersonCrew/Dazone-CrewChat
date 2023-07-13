package com.dazone.crewchatoff.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.dazone.crewchatoff.BuildConfig;
import com.dazone.crewchatoff.HTTPs.HttpOauthRequest;
import com.dazone.crewchatoff.HTTPs.HttpRequest;
import com.dazone.crewchatoff.R;
import com.dazone.crewchatoff.activity.ChangePasswordActivity;
import com.dazone.crewchatoff.activity.CrewChatSettingActivity;
import com.dazone.crewchatoff.activity.LoginActivity;
import com.dazone.crewchatoff.activity.MainActivity;
import com.dazone.crewchatoff.activity.NotificationSettingActivity;
import com.dazone.crewchatoff.activity.ProfileUserActivity;
import com.dazone.crewchatoff.activity.base.BaseActivity;
import com.dazone.crewchatoff.constant.Statics;
import com.dazone.crewchatoff.database.AllUserDBHelper;
import com.dazone.crewchatoff.database.BelongsToDBHelper;
import com.dazone.crewchatoff.database.ChatMessageDBHelper;
import com.dazone.crewchatoff.database.ChatRoomDBHelper;
import com.dazone.crewchatoff.database.DepartmentDBHelper;
import com.dazone.crewchatoff.database.FavoriteGroupDBHelper;
import com.dazone.crewchatoff.database.FavoriteUserDBHelper;
import com.dazone.crewchatoff.database.UserDBHelper;
import com.dazone.crewchatoff.dto.BelongDepartmentDTO;
import com.dazone.crewchatoff.dto.ErrorDto;
import com.dazone.crewchatoff.dto.ProfileUserDTO;
import com.dazone.crewchatoff.dto.UserDto;
import com.dazone.crewchatoff.interfaces.BaseHTTPCallBack;
import com.dazone.crewchatoff.interfaces.OnGetUserCallBack;
import com.dazone.crewchatoff.utils.Constant;
import com.dazone.crewchatoff.utils.CrewChatApplication;
import com.dazone.crewchatoff.utils.DialogUtils;
import com.dazone.crewchatoff.utils.ImageUtils;
import com.dazone.crewchatoff.utils.Prefs;
import com.dazone.crewchatoff.utils.Utils;

import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;

public class SettingFragment extends BaseFragment implements View.OnClickListener {
    private String TAG = "SettingFragment";
    private View mView;
    private UserDto userDBHelper;
    private Context mContext;
    public Prefs prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDBHelper = UserDBHelper.getUser();
        prefs = CrewChatApplication.getInstance().getPrefs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.chat_setting, container, false);
        initSettingGroup();
        return mView;
    }

    private TextView tvUserName, tvPosition, tvEmail, tvPhone, tvPhoneCompany;
    private TextView tvNotificationSettings;
    private ImageView mAvatar;
    private Button btnLogout;
    private LinearLayout llCellPhone, llCompanyPhone, llNotificationSetting, llChangePass, llInfo, llCrewChatSetting;

    private void initSettingGroup() {
        btnLogout = mView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        tvNotificationSettings = mView.findViewById(R.id.tv_notification_settings);
        tvNotificationSettings.setOnClickListener(this);
        tvUserName = mView.findViewById(R.id.tv_username);
        tvUserName.setOnClickListener(this);
        mAvatar = mView.findViewById(R.id.iv_avatar);
        tvPosition = mView.findViewById(R.id.tvPosition);
        tvEmail = mView.findViewById(R.id.tvEmail);
        tvPhone = mView.findViewById(R.id.tvPhone);
        llCellPhone = mView.findViewById(R.id.llCellPhone);
        llCompanyPhone = mView.findViewById(R.id.llCompanyPhone);
        tvPhoneCompany = mView.findViewById(R.id.tvPhoneCompany);
        llNotificationSetting = mView.findViewById(R.id.llNotificationSetting);
        llChangePass = mView.findViewById(R.id.llChangePass);
        llInfo = mView.findViewById(R.id.llInfo);
        llCrewChatSetting = mView.findViewById(R.id.llCrewChatSetting);
        mAvatar.setOnClickListener(this);
        llNotificationSetting.setOnClickListener(this);
        llChangePass.setOnClickListener(this);
        llInfo.setOnClickListener(this);
        llCrewChatSetting.setOnClickListener(this);


        String url = prefs.getServerSite() + prefs.getAvatarUrl();
        Log.d(TAG, "url:" + url);
        ImageUtils.loadImageNormal(url, mAvatar);


        getDataFromServer();
    }

    private void getDataFromServer() {

        int userNo = 0;
        if (userDBHelper.getId() == 0) {
            userNo = CrewChatApplication.currentId;
        } else {
            userNo = userDBHelper.getId();
        }
        HttpRequest.getInstance().GetUser(userNo, new OnGetUserCallBack() {
            @Override
            public void onHTTPSuccess(ProfileUserDTO profileUserDTO) {
                fillData(profileUserDTO);
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {
            }
        });
    }

    private void fillData(ProfileUserDTO profileUserDTO) {

        tvUserName.setText(profileUserDTO.getName());
        String strPositionName = "";
        String belongToDepartment = "";
        ArrayList<BelongDepartmentDTO> listBelong = profileUserDTO.getBelongs();

        for (BelongDepartmentDTO belongDepartmentDTOs : listBelong) {
            belongToDepartment += listBelong.indexOf(belongDepartmentDTOs) == listBelong.size() - 1 ?
                    belongDepartmentDTOs.getDepartName() + " / " + belongDepartmentDTOs.getPositionName() + " / " + belongDepartmentDTOs.getDutyName() :
                    belongDepartmentDTOs.getDepartName() + " / " + belongDepartmentDTOs.getPositionName() + " / " + belongDepartmentDTOs.getDutyName() + "<br>";
            if (belongDepartmentDTOs.isDefault()) {
                strPositionName = belongDepartmentDTOs.getDepartName() + " / " + belongDepartmentDTOs.getPositionName() + " / " + belongDepartmentDTOs.getDutyName();
            }
        }

        tvPosition.setText(strPositionName);
        tvEmail.setText(profileUserDTO.getMailAddress());

        String cellPhone = profileUserDTO.getCellPhone();
        Log.d(TAG, "cellPhone:" + cellPhone);
        if (TextUtils.isEmpty(cellPhone)) {
            llCellPhone.setVisibility(View.GONE);
        } else {
            tvPhone.setText(cellPhone);

        }


        String exPhone = profileUserDTO.getCompanyPhone();
        Log.d(TAG, "exPhone:" + exPhone);
        if (TextUtils.isEmpty(exPhone)) {
            llCompanyPhone.setVisibility(View.GONE);
        } else {
            tvPhoneCompany.setText(exPhone);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llNotificationSetting:
                Intent intent = new Intent(mContext, NotificationSettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.btnLogout:
                logoutV2();
                break;

            case R.id.llChangePass:
                 startActivity(new Intent(getContext(), ChangePasswordActivity.class));
                break;

                case R.id.llInfo:
                    showInfoV2();
                break;
            case R.id.llCrewChatSetting:
                CrewChatSettingActivity.toActivity(mContext);
                break;
        }
    }

    private void showInfoV2() {
        String versionName = BuildConfig.VERSION_NAME;
        String user_version = getResources().getString(R.string.user_version) + " " + versionName;
        Utils.oneButtonAlertDialog(getActivity(), getResources().getString(R.string.about_crewchat), user_version, getResources().getString(R.string.confirm));
    }

    private void goProfile() {
        try {
            Intent intent = new Intent(mContext, ProfileUserActivity.class);
            int userNo = 0;
            if (userDBHelper.getId() == 0) {
                userNo = CrewChatApplication.currentId;
            } else {
                userNo = userDBHelper.getId();
            }
            intent.putExtra(Constant.KEY_INTENT_USER_NO, userNo);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generalSetting() {
    }

    private void logoutV2() {
        Utils.customAlertDialog(getActivity(), getResources().getString(R.string.app_name),Utils.getString(R.string.logout_confirm), Utils.getString(R.string.yes), Utils.getString(R.string.no), new DialogUtils.OnAlertDialogViewClickEvent() {
            @Override
            public void onOkClick(DialogInterface alertDialog) {
                doLogout();
            }

            @Override
            public void onCancelClick() {

            }
        });
    }

    final int LOGOUT_COMPLETE = 100;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LOGOUT_COMPLETE) {
                Prefs prefs1 = CrewChatApplication.getInstance().getPrefs();
                final String userId = prefs1.getUserID();
                final String dm = prefs1.getDDSServer();
                final String pw = prefs1.getPass();
                prefs1.clear();
                prefs1.putUserID(userId);
                prefs1.setDDSServer(dm);
                prefs1.setPass(pw);
                prefs1.putBooleanValue(Statics.FIRST_LOGIN, false);
                prefs1.set_login_install_app(false);
                prefs1.setDataComplete(false);
                ((MainActivity) getActivity()).destroyFragment();
                getActivity().finish();

                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    private void doLogout() {
        new Prefs().putIntValue("PAGE", 0);
        String regId = new Prefs().getGCMregistrationid();

        if(!regId.isEmpty()) {
            BaseActivity.Instance.showProgressDialog();
            HttpRequest.getInstance().DeleteDevice(regId, new BaseHTTPCallBack() {
                @Override
                public void onHTTPSuccess() {

                    HttpOauthRequest.getInstance().logout(new BaseHTTPCallBack() {
                        @Override
                        public void onHTTPSuccess() {
                            BaseActivity.Instance.dismissProgressDialog();
                            // New thread to clear all cache
                            CrewChatApplication.isAddUser = false;
                            new Thread(() -> {
                                BelongsToDBHelper.clearBelong();
                                AllUserDBHelper.clearUser();
                                ChatRoomDBHelper.clearChatRooms();
                                ChatMessageDBHelper.clearMessages();
                                DepartmentDBHelper.clearDepartment();
                                UserDBHelper.clearUser();
                                FavoriteGroupDBHelper.clearGroups();
                                FavoriteUserDBHelper.clearFavorites();
                                CrewChatApplication.resetValue();
                                CrewChatApplication.isLoggedIn = false;
                                CrewChatApplication.getInstance().getPrefs().clearLogin();
                                handler.obtainMessage(LOGOUT_COMPLETE).sendToTarget();
                                ShortcutBadger.removeCount(getContext()); //for 1.1.4
                            }).start();
                        }

                        @Override
                        public void onHTTPFail(ErrorDto errorDto) {
                            Log.d(TAG, "onHTTPFail 1");
                            BaseActivity.Instance.dismissProgressDialog();
                            Toast.makeText(mContext, "Logout failed !", Toast.LENGTH_LONG).show();
                        }
                    });

                }

                @Override
                public void onHTTPFail(ErrorDto errorDto) {
                    Log.d(TAG, "onHTTPFail 2");
                    BaseActivity.Instance.dismissProgressDialog();
                    Toast.makeText(mContext, "Logout failed !", Toast.LENGTH_LONG).show();
                }
            });
        }



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
