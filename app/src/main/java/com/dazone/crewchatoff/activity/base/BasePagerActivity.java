package com.dazone.crewchatoff.activity.base;

import static java.lang.Integer.MAX_VALUE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dazone.crewchatoff.HTTPs.HttpOauthRequest;
import com.dazone.crewchatoff.HTTPs.HttpRequest;
import com.dazone.crewchatoff.R;
import com.dazone.crewchatoff.TestMultiLevelListview.MultilLevelListviewFragment;
import com.dazone.crewchatoff.activity.LoginActivity;
import com.dazone.crewchatoff.activity.MainActivity;
import com.dazone.crewchatoff.adapter.TabPagerAdapter;
import com.dazone.crewchatoff.dto.ErrorDto;
import com.dazone.crewchatoff.fragment.BaseFavoriteFragment;
import com.dazone.crewchatoff.fragment.CompanyFragment;
import com.dazone.crewchatoff.fragment.CurrentChatListFragment;
import com.dazone.crewchatoff.fragment.RecentFavoriteFragment;
import com.dazone.crewchatoff.interfaces.BaseHTTPCallBack;
import com.dazone.crewchatoff.utils.CrewChatApplication;
import com.dazone.crewchatoff.utils.Prefs;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;

public abstract class BasePagerActivity extends BaseActivity {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    protected TabPagerAdapter tabAdapter;
    String TAG = "BasePagerActivity";
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    protected ViewPager mViewPager;
    public TabLayout tabLayout;
    protected FloatingActionButton fab;
    protected ImageView frNewChat;
    private LinearLayout toolbar;

    /**
     * MENU ITEM
     */
    protected MenuItem menuItemSearch;
    protected MenuItem menuItemMore;
    protected SearchView searchView;

    protected ImageView ivSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_pager);
        CrewChatApplication.isAddUser = true;
        toolbar = findViewById(R.id.toolbar);
        setUpSearch();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        tabAdapter = new TabPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);

        mViewPager.setAdapter(tabAdapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        ivSearch = findViewById(R.id.iv_search);
        ivSearch.setOnClickListener(v -> {
            findViewById(R.id.llSearch).setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);

            EditText etSearch = findViewById(R.id.llSearch).findViewById(R.id.etSearch);
            switch (MainActivity.CURRENT_TAB) {
                case 0:
                case 2:
                    etSearch.setHint(R.string.hint_search_company);
                    break;
                case 1:
                    etSearch.setHint(R.string.hint_search_current_chat_list);
                    break;
                default:
                    break;
            }

            if (CompanyFragment.instance != null) CompanyFragment.instance.getListCurrent();
        });

        //fab = findViewById(R.id.fab);
        frNewChat = findViewById(R.id.imgAdd);
        frNewChat.setOnClickListener(view -> {
            if (MainActivity.CURRENT_TAB == 1 || MainActivity.CURRENT_TAB == 0) {
                if (MainActivity.instance != null) {
                    MainActivity.instance.gotoOrganizationChart();
                }
            } else if (MainActivity.CURRENT_TAB == 2) {
                if (BaseFavoriteFragment.CURRENT_TAB == 0) {
                } else {
                    if (MultilLevelListviewFragment.instanceNew != null
                            && MultilLevelListviewFragment.instanceNew.isLoadDB()) {
                        MultilLevelListviewFragment.instanceNew.addFavorite();
                    }
                }


            }
        });
        init();
        inItShare();
    }

    private void setUpSearch() {
        View searchBar = findViewById(R.id.llSearch);
        EditText etSearch = findViewById(R.id.etSearch);
        searchBar.findViewById(R.id.imgCloseSearch).setOnClickListener(view -> {
            searchBar.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                switch (MainActivity.CURRENT_TAB) {
                    case 0:
                        if(CompanyFragment.instance != null){
                            CompanyFragment.instance.updateSearch(etSearch.getText().toString());
                        }

                        break;
                    case 1:
                        if (CurrentChatListFragment.fragment != null) {
                            CurrentChatListFragment.fragment.actionSearch(etSearch.getText().toString());
                        }
                        break;
                    case 2:
                        if (BaseFavoriteFragment.CURRENT_TAB == 1) {
                            MultilLevelListviewFragment.instance.actionSearch(etSearch.getText().toString());
                        } else {
                            RecentFavoriteFragment.instance.actionSearch(etSearch.getText().toString());
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    // Hide topmenubar search icon(default)
    // 탑 메뉴바의 검색 아이콘을 숨김(기본)

    public void hideSearch(boolean isHide) {
        ivSearch.setVisibility(!isHide ? View.VISIBLE : View.GONE);
        if(menuItemSearch != null) {
            menuItemSearch.setVisible(isHide);
        }
    }

    public void changeIcon(boolean hasChange) {
        ImageView imgAdd = findViewById(R.id.imgAdd);
        imgAdd.setImageResource(hasChange ? R.drawable.group_add : R.drawable.chat_add);
    }

    public void hideToolBar(boolean isHide) {
        toolbar.setVisibility(!isHide ? View.VISIBLE : View.GONE);
    }

    public void hideCreateIcon(boolean isHide) {
        frNewChat.setVisibility(!isHide ? View.VISIBLE : View.GONE);
    }

    public void showPAB() {
        if (frNewChat != null) {
            frNewChat.setVisibility(View.VISIBLE);
        }
    }

    public void hidePAB() {
        if (frNewChat != null) {
            frNewChat.setVisibility(View.GONE);
        }
    }

    protected abstract void init();
    protected abstract void inItShare();
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base_pager, menu);
        menuItemSearch = menu.findItem(R.id.action_search);
        menuItemMore = menu.findItem(R.id.action_status);
        searchView = (SearchView) menuItemSearch.getActionView();
        searchView.setMaxWidth(MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (CompanyFragment.instance != null)
                    CompanyFragment.instance.updateSearch(newText);
                Log.d(TAG, "onQueryTextChange");
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            String ids = new Prefs().getGCMregistrationid();
            if (!TextUtils.isEmpty(ids)) {
                HttpRequest.getInstance().DeleteDevice(ids, new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getBaseContext());
                        try {
                            gcm.unregister();
                        } catch (IOException e) {
                            System.out.println("Error Message: " + e.getMessage());
                        }
                        new Prefs().setGCMregistrationid("");
                        HttpOauthRequest.getInstance().logout(new BaseHTTPCallBack() {
                            @Override
                            public void onHTTPSuccess() {
                                Intent intent = new Intent(BasePagerActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }

                            @Override
                            public void onHTTPFail(ErrorDto errorDto) {

                            }
                        });
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {

                    }
                });
            } else {
                HttpOauthRequest.getInstance().logout(new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        Intent intent = new Intent(BasePagerActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {

                    }
                });
            }
            return true;
        } else if (id == R.id.action_search) {
            if (CompanyFragment.instance != null) CompanyFragment.instance.getListCurrent();
            Log.d(TAG, "action_search");
        }

        return super.onOptionsItemSelected(item);
    }

    public void destroyFragment() {
        tabAdapter.destroyItem(mViewPager, 0, tabAdapter.getItem(0));
        tabAdapter.destroyItem(mViewPager, 1, tabAdapter.getItem(1));
        tabAdapter.destroyItem(mViewPager, 2, tabAdapter.getItem(2));
        tabAdapter.destroyItem(mViewPager, 3, tabAdapter.getItem(3));
    }
}
