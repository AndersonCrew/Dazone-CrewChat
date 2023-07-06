package com.dazone.crewchatoff.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dazone.crewchatoff.R;
import com.dazone.crewchatoff.TestMultiLevelListview.MultilLevelListviewFragment;
import com.dazone.crewchatoff.activity.MainActivity;
import com.dazone.crewchatoff.constant.Statics;
import com.google.android.material.tabs.TabLayout;

public class BaseFavoriteFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    private View rootView;
    private ViewPager pager;
    private TabLayout tabLayout;
    private boolean isCreated = false;
    private String TAG = "BaseFavoriteFragment";
    public static BaseFavoriteFragment instance = null;

    public void setContext(Activity context) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_base_favorite, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        instance = this;
        CURRENT_TAB = 0;
        pager = rootView.findViewById(R.id.pager);
        tabLayout = rootView.findViewById(R.id.tabLayout);

        initChildPage();

        // Hide search icon for tab favorite chat room
        if (pager.getCurrentItem() == 0) {
            hideIcon();
        } else {
            showIcon();
            showSearchFavorite();
        }

        return rootView;
    }

    private void initChildPage() {
        if (!isCreated) {
            isCreated = true;

            FragmentManager manager = getChildFragmentManager();
            PagerAdapter adapter = new PagerAdapter(manager);
            pager.setAdapter(adapter);
            pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            pager.setCurrentItem(0);

            tabLayout.setOnTabSelectedListener(this);
        }
    }

    private boolean isShowSearchIcon = false;




    public void Favorite_Right() {
        Log.d(TAG, "showSearchFavorite");
        if (!isShowSearchIcon) {
            Intent intent = new Intent(Statics.ACTION_SHOW_SEARCH_FAVORITE_INPUT);
            getActivity().sendBroadcast(intent);
            isShowSearchIcon = true;

        } else {
            isShowSearchIcon = false;
            Intent intent = new Intent(Statics.ACTION_HIDE_SEARCH_FAVORITE_INPUT);
            getActivity().sendBroadcast(intent);

        }
    }

    private void showSearchFavorite() {
    }


    boolean isShowIcon = false;

    public void Favorite_left() {
        if (!isShowIcon) {
            Intent intent = new Intent(Statics.ACTION_SHOW_SEARCH_INPUT);
            getActivity().sendBroadcast(intent);
            isShowIcon = true;
        } else {
            Intent intent = new Intent(Statics.ACTION_HIDE_SEARCH_INPUT);
            getActivity().sendBroadcast(intent);
            isShowIcon = false;
        }
    }

    private void hideIcon() {
        if (MainActivity.instance != null) {
            MainActivity.instance.hidePAB();
        }
    }

    private void showIcon() {
        if (MainActivity.instance != null) {
            MainActivity.instance.showPAB();
        }
    }

    public static int CURRENT_TAB = 0;

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        pager.setCurrentItem(tab.getPosition());
        CURRENT_TAB = tab.getPosition();
        if (tab.getPosition() == 0) {
            hideIcon();
            ImageView icon = tab.getCustomView().findViewById(R.id.iv_icon_left);
            icon.setImageResource(R.drawable.favorite_chat_defalt_64x64);
        } else {
            showIcon();
            ImageView icon = tab.getCustomView().findViewById(R.id.iv_icon_right);
            icon.setImageResource(R.drawable.favorite_user_defalt_64x64);
            if (getActivity() != null) {
                showSearchFavorite();
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            ImageView icon = tab.getCustomView().findViewById(R.id.iv_icon_left);
            icon.setImageResource(R.drawable.favorite_chat_active_64x64);
        } else {
            ImageView icon = tab.getCustomView().findViewById(R.id.iv_icon_right);
            icon.setImageResource(R.drawable.favorite_user_active_64x64);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 1:
                    fragment = MultilLevelListviewFragment.newInstance();
                    break;
                default:
                    fragment = new RecentFavoriteFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}