package com.example.thinkpad.t46bakingapp.view.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;

import com.example.thinkpad.t46bakingapp.R;
import com.example.thinkpad.t46bakingapp.data.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by thinkpad on 2018/1/29.
 */

public class DetailFragment extends Fragment implements ExoPlayer.EventListener{
    private View view;
    private SimpleExoPlayer mExoPlayer;
    private static MediaSession mMediaSession;
    private PlaybackState.Builder mStateBuilder;
    private static final String TAG = "DetailFragment";
    @BindView(R.id.step_title_in_detail) TextView stepTitle;
    @BindView(R.id.textViewDescription) TextView stepDescription;
    @BindView(R.id.button) Button nextButton;
    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    private Context mContext;
    private String currentStepId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detail_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentStepId = getArguments().getString(getString(R.string.step_id));
        initializeMediaSession();
        initializePlayer(null);
        refresh();
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
    }
    /*
        * refresh all the views
        * */
    public void refresh(){
        List<Step> stepList = DataSupport.select("StepTitle", "Description", "VideoUrl").where("StepId=?", currentStepId).find(Step.class);
        if (stepList.size() > 0) {
            Step currentStep = stepList.get(0);
            refreshAllViews(currentStep);
        }else{
            Log.d(TAG, ">>>>>>>>>>>>stepList.size()<=0");
        }
    }
    private void refreshAllViews(Step currentStep){
        stepTitle.setText(currentStep.getStepTitle());
        stepDescription.setText(currentStep.getDescription());
        mPlayerView.setVisibility(View.INVISIBLE);
        if (mExoPlayer != null) {
            releasePlayer();
        }
        //completed: refresh the exo player
        if (currentStep.getVideoUrl() != null) {
            mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(currentStep.getVideoUrl()));
        }


    }

    /*
    * 点击next按钮跳转到下一个Step的DetailFragment
    * */
    @OnClick(R.id.button) void showNextStep(){
        //变换一个id，然后refresh
        this.currentStepId = String.valueOf(Integer.valueOf(currentStepId)+1);
        refresh();
    }
    /*
    * set this fragment's corresponding step. useless
    * */
    public void setCurrentStepId(int stepId){
        this.currentStepId = String.valueOf(stepId);
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackState.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackState.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this.getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);
        }
        if (mediaUri != null) {
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this.getActivity(), "MyApplication");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this.getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSession(this.getActivity(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackState.Builder()
                .setActions(
                        PlaybackState.ACTION_PLAY |
                                PlaybackState.ACTION_PAUSE |
                                PlaybackState.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackState.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }
    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MySessionCallback extends MediaSession.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
