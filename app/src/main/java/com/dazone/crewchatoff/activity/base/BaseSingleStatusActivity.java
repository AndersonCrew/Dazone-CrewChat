package com.dazone.crewchatoff.activity.base;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.dazone.crewchatoff.R;
import com.dazone.crewchatoff.activity.ChattingActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public abstract class BaseSingleStatusActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        addFragment(savedInstanceState);
    }

    protected FloatingActionButton fab;
    protected TextView toolbar_title, toolbar_status;
    protected ImageView ivMore;
    protected ImageView ivCall;
    protected ImageView ivSearch;
    protected ImageView mSearchView;
    protected RelativeLayout btnGroup;
    protected ImageView ivStatus;

    protected void init() {
        setContentView(R.layout.activity_base_single_status);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ivStatus = findViewById(R.id.ivStatus);
        ivMore = findViewById(R.id.more_menu);
        ivCall = findViewById(R.id.call_menu);
        ivSearch = findViewById(R.id.search_menu);
        mSearchView = findViewById(R.id.searchView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = findViewById(R.id.fab);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_status = findViewById(R.id.toolbar_status);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        btnGroup = findViewById(R.id.btnGroup);
        findViewById(R.id.imgMembers).setOnClickListener(view -> {
            if (ChattingActivity.instance != null) {
                ChattingActivity.instance.menu_list_chat();
            }
        });

        findViewById(R.id.imgBack).setOnClickListener(view -> finish());
        findViewById(R.id.imgBackSearch).setOnClickListener(view -> finish());
        findViewById(R.id.imgCloseSearch).setOnClickListener(view -> {
            findViewById(R.id.llToolBar).setVisibility(View.VISIBLE);
            findViewById(R.id.llSearch).setVisibility(View.GONE);
        });

        mSearchView.setOnClickListener(view -> {
            findViewById(R.id.llToolBar).setVisibility(View.GONE);
            findViewById(R.id.llSearch).setVisibility(View.VISIBLE);
        });

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(ChattingActivity.instance != null) {
                    ChattingActivity.instance.actionSearch(etSearch.getText().toString());
                }
            }
        });
    }
    public static String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public void showIvStt(int id) {
        ivStatus.setVisibility(View.VISIBLE);
        ivStatus.setImageResource(id);
    }

    protected void hideCall() {
        if (ivCall != null) {
            ivCall.setVisibility(View.GONE);
        }
    }

    protected abstract void addFragment(Bundle bundle);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return false;
        }
        return false;
    }

    public void setTitle(String title) {
        toolbar_title.setText(title);
    }

    public void setStatus(String status) {
        toolbar_status.setText(status);
    }
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}