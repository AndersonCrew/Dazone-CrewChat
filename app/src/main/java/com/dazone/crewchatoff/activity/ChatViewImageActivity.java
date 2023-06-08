package com.dazone.crewchatoff.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.dazone.crewchatoff.R;
import com.dazone.crewchatoff.activity.base.BaseActivity;
import com.dazone.crewchatoff.adapter.ViewImageAdapter;
import com.dazone.crewchatoff.constant.Statics;
import com.dazone.crewchatoff.dto.ChattingDto;
import com.dazone.crewchatoff.dto.TreeUserDTOTemp;
import com.dazone.crewchatoff.fragment.CompanyFragment;
import com.dazone.crewchatoff.interfaces.OnClickViewCallback;
import com.dazone.crewchatoff.utils.Constant;
import com.dazone.crewchatoff.utils.CrewChatApplication;
import com.dazone.crewchatoff.utils.ImageUtils;
import com.dazone.crewchatoff.utils.Prefs;
import com.dazone.crewchatoff.utils.TimeUtils;
import com.dazone.crewchatoff.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatViewImageActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private ArrayList<ChattingDto> listData = new ArrayList<>();
    private int position = 0;
    private RelativeLayout rlHeader;
    private LinearLayout lnFooter;
    private boolean showFull = false;
    private boolean get_user_name_from_db = false;

    String TAG = "ChatViewImageActivity";
    private ViewPager viewPager;
    private ImageView imgAvatar;
    private TextView tvUserName;
    private TextView tvDate;
    private boolean isHide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view_image);
        Log.d(TAG, "onCreate");

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Object object = getIntent().getExtras().get(Intent.EXTRA_STREAM);
            if (object != null) {
                if (!TextUtils.isEmpty(object.toString())) {
                    callActivity(OrganizationActivity.class);
                    finish();
                }
            } else {
                initView();
                initData();
                loadData();
            }
        }
    }

    private void initView() {
        viewPager = findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(this);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        ImageView btnDownload = findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(this);

        ImageView btnShare = findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);

        ImageView btnDelete = findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);

        ImageView btnInfo = findViewById(R.id.btn_infor);
        btnInfo.setOnClickListener(this);

        imgAvatar = findViewById(R.id.img_avatar);
        tvUserName = findViewById(R.id.tv_username);
        tvDate = findViewById(R.id.tv_date);

        rlHeader = findViewById(R.id.rl_header);
        lnFooter = findViewById(R.id.ln_footer);
    }

    List<TreeUserDTOTemp> allUser;

    private void initData() {
        Prefs prefs = CrewChatApplication.getInstance().getPrefs();
        String response = prefs.getIMAGE_LIST();
        Type listType = new TypeToken<ArrayList<ChattingDto>>() {
        }.getType();
        listData = new Gson().fromJson(response, listType);

        if (listData == null) {
            listData = new ArrayList<>();
        }

        Intent i = getIntent();
        position = i.getIntExtra(Statics.CHATTING_DTO_GALLERY_POSITION, 0);
        showFull = i.getBooleanExtra(Statics.CHATTING_DTO_GALLERY_SHOW_FULL, false);
        get_user_name_from_db = i.getBooleanExtra(Statics.get_user_name_from_db, false);

        if (CompanyFragment.instance != null) allUser = CompanyFragment.instance.getUser();
        if (allUser == null) allUser = new ArrayList<>();


        if (showFull) {
            rlHeader.setVisibility(View.GONE);
            lnFooter.setVisibility(View.GONE);
        } else {
            rlHeader.setVisibility(View.VISIBLE);
            lnFooter.setVisibility(View.VISIBLE
            );
        }
    }

    OnClickViewCallback mCallback = () -> toggleMenu();

    private void toggleMenu() {
        rlHeader.setTag("Header");
        lnFooter.setTag("Footer");
        if (this.isHide) {
            slideToBottom(rlHeader);
            slideToTop(lnFooter);
            this.isHide = false;
        } else {
            slideToTop(rlHeader);
            slideToBottom(lnFooter);
            this.isHide = true;
        }
    }

    // To animate view slide out from top to bottom
    public void slideToBottom(View view) {
        TranslateAnimation animate;
        if (view.getTag().equals("Header")) {
            animate = new TranslateAnimation(0, 0, 0, 0);
        } else {
            animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        }

        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from bottom to top
    public void slideToTop(View view) {
        TranslateAnimation animate;
        if (view.getTag().equals("Header")) {
            animate = new TranslateAnimation(0, 0, 0, -view.getHeight());
        } else {
            animate = new TranslateAnimation(0, 0, 0, 0);
        }

        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    private void loadData() {
        ViewImageAdapter adapter = new ViewImageAdapter(this, listData, showFull, mCallback);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        try {

            ChattingDto chattingDto = listData.get(position);
            String avatarUrl = new Prefs().getServerSite();

            if (chattingDto != null && chattingDto.getUser() != null) {
                avatarUrl += chattingDto.getUser().avatar;
            }

            ImageUtils.showCycleImageFromLink(avatarUrl, imgAvatar, R.dimen.common_avatar);

            if (chattingDto != null && chattingDto.getUser() != null) {
                tvUserName.setText(chattingDto.getUser().FullName);
            } else {
                String userName = getResources().getString(R.string.unknown);

                if (get_user_name_from_db) {
                    try {
                        userName = Constant.getUserName(allUser, listData.get(position).getUserNo());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                tvUserName.setText(userName);
            }

            tvDate.setText(TimeUtils.displayTimeWithoutOffset(this, chattingDto.getRegDate(), 0, TimeUtils.KEY_FROM_SERVER));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        ChattingDto dto = listData.get(viewPager.getCurrentItem());
        String url = String.format("/UI/CrewChat/MobileAttachDownload.aspx?session=%s&no=%s",
                new Prefs().getaccesstoken(), dto.getAttachNo());
        String urlDownload1 = new Prefs().getServerSite() + url;
        String path = "";
        if (listData.get(viewPager.getCurrentItem()).getAttachInfo() != null) {
            path = Environment.getExternalStorageDirectory() + Constant.pathDownload + "/" + listData.get(viewPager.getCurrentItem()).getAttachInfo().getFileName();
        }

        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_share:
                ChattingDto chattingDto1 = listData.get(viewPager.getCurrentItem());
                if (chattingDto1 != null) {
                    Bitmap bitmap = getBitmapFromURL(urlDownload1);
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, getImageUri(this, bitmap));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/png");
                    startActivity(intent);
                }

                break;
            case R.id.btn_download:
                ChattingDto chattingDto = listData.get(viewPager.getCurrentItem());
                if (chattingDto != null) {
                    Utils.displayDownloadFileDialog(this, urlDownload1, chattingDto.getAttachInfo().getFileName());
                }
                break;

            case R.id.btn_delete:
                break;
            case R.id.btn_infor:
                getInfor(dto, urlDownload1);
                break;

        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



    interface getBitmap {
        void onSuccess(Bitmap result);
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        String avatarUrl;
        getBitmap calback;

        public DownloadImageTask(String avatarUrl, getBitmap calback) {
            this.avatarUrl = avatarUrl;
            this.calback = calback;

        }

        Bitmap bitmapOrg = null;

        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(avatarUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmapOrg = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmapOrg;
        }

        protected void onPostExecute(final Bitmap result) {
            calback.onSuccess(result);
        }
    }

    void getInfor(ChattingDto dto, String url) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_infor_layout);

        TextView tv_type = dialog.findViewById(R.id.tv_type);
        final TextView tv_size = dialog.findViewById(R.id.tv_size);
        final TextView tv_dimen = dialog.findViewById(R.id.tv_dimen);
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        String[] str = dto.getAttachInfo().getFileName().split("[.]");
        int size = dto.getAttachFileSize() / 1024;
        String strSize = size + " KB";

        String type;
        try {
            type = str[str.length - 1];
        } catch (Exception e) {
            type = "error";
            e.printStackTrace();
        }
        tv_type.setText(type);
        new DownloadImageTask(url, new getBitmap() {
            @Override
            public void onSuccess(Bitmap result) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                    String dimensions = result.getWidth() + "X" + result.getHeight();
                    tv_dimen.setText(dimensions);

                    int size = result.getRowBytes() * result.getHeight();
                    tv_size.setText(size + " KB");
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.d(TAG, "type:" + type);
        Log.d(TAG, "size:" + strSize);

        dialog.show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        ChattingDto chattingDto = listData.get(position);
        String avatarUrl = new Prefs().getServerSite();
        if (chattingDto != null && chattingDto.getUser() != null) {
            avatarUrl += chattingDto.getUser().avatar;
        }

        ImageLoader.getInstance().displayImage(avatarUrl, imgAvatar, Statics.options2);

        if (chattingDto != null && chattingDto.getUser() != null) {
            tvUserName.setText(chattingDto.getUser().FullName);
        } else {
            String unknownUser = getResources().getString(R.string.unknown);

            if (get_user_name_from_db) {
                try {
                    unknownUser = Constant.getUserName(allUser, chattingDto.getUserNo());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            tvUserName.setText(unknownUser);
        }

        tvDate.setText(TimeUtils.displayTimeWithoutOffset(this, chattingDto.getRegDate(), 0, TimeUtils.KEY_FROM_SERVER));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}