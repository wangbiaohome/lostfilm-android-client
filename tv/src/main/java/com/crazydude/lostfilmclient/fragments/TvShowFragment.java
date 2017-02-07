package com.crazydude.lostfilmclient.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.crazydude.common.db.DatabaseManager;
import com.crazydude.common.db.models.Episode;
import com.crazydude.common.db.models.Season;
import com.crazydude.common.db.models.TvShow;
import com.crazydude.common.utils.Utils;
import com.crazydude.lostfilmclient.R;
import com.crazydude.lostfilmclient.activity.PlayerActivity;
import com.crazydude.lostfilmclient.activity.TvShowActivity;
import com.crazydude.lostfilmclient.presenters.EpisodePresenter;

import io.realm.RealmChangeListener;

/**
 * Created by Crazy on 10.01.2017.
 */

public class TvShowFragment extends BrowseFragment implements OnItemViewClickedListener, RealmChangeListener<TvShow> {

    private int mTvShowId;
    private DatabaseManager mDatabaseManager;
    private ArrayObjectAdapter mCategoriesAdapter;
    private TvShow mTvShow;
    private BackgroundManager mBackgroundManager;
    private DisplayMetrics mMetrics;


    @Override
    public void onChange(TvShow element) {
        Log.d("TvShow", "Realm updated");
        updateData();
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        Episode episode = (Episode) item;

        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        intent.putExtra(PlayerActivity.EXTRA_EPISODE_ID, episode.getId());
        intent.putExtra(PlayerActivity.EXTRA_SEASON_ID, episode.getSeason().getId());
        intent.putExtra(PlayerActivity.EXTRA_TV_SHOW_ID, episode.getSeason().getTvShow().getId());
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mTvShowId = arguments.getInt(TvShowActivity.EXTRA_TVSHOW_ID);
        mDatabaseManager = new DatabaseManager();
        setupUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseManager.close();
    }

    private void setupUI() {
        setOnItemViewClickedListener(this);
        prepareBackgroundManager();
        loadBackground();
        loadData();
    }

    private void loadData() {
        mTvShow = mDatabaseManager.getTvShow(mTvShowId);
        mTvShow.addChangeListener(this);
        Log.d("TvShow", String.format("Data loaded %d Seasons: %d", mTvShowId, mTvShow.getSeasons().size()));
        updateData();
    }

    private void updateData() {
        mCategoriesAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        for (Season season : mTvShow.getSeasons()) {
            ArrayObjectAdapter episodeAdapter = new ArrayObjectAdapter(new EpisodePresenter());
            episodeAdapter.addAll(0, season.getEpisodes());
            if (season.isHasFullSeasonDownloadUrl()) {
                episodeAdapter.add(new Episode("99", getString(R.string.full_season), season, -1, null));
            }
            mCategoriesAdapter.add(new ListRow(new HeaderItem(season.getName()), episodeAdapter));
        }
        setAdapter(mCategoriesAdapter);
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void loadBackground() {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(this)
                .load(Utils.generatePosterUrl(mTvShowId))
                .asBitmap()
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(width, height) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                            glideAnimation) {
                        mBackgroundManager.setBitmap(resource);
                    }
                });
    }
}
