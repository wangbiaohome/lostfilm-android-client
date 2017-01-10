package com.crazydude.lostfilmclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Crazy on 10.01.2017.
 */

public class TvShowActivity extends Activity {

    public static final String EXTRA_TVSHOW_ID = "extra_tvshow_id";
    private int mTvShowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tv_show);

        Intent intent = getIntent();
        mTvShowId = intent.getIntExtra(EXTRA_TVSHOW_ID, -1);
        if (mTvShowId == -1) {
            finish();
        }
        TvShowFragment tvShowFragment = (TvShowFragment) getFragmentManager().findFragmentById(R.id.fragment_tv_show);
        tvShowFragment.setTvShowId(mTvShowId);
    }
}
