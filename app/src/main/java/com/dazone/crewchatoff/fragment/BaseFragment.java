package com.dazone.crewchatoff.fragment;

import android.content.Context;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}