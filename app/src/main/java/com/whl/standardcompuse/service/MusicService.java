package com.whl.standardcompuse.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private static final int STATE_STOP = 5;
    /**
     * 确保mediaPlayer是唯一的
     */
    private MediaPlayer mediaPlayer;
    public static final int STATE_CREATE = 0;
    public static final int STATE_DESTORY = 1;
    private static final int STATE_PREPARED = 2;
    public static final int STATE_START = 3;
    public static final int STATE_PAUSE = 4;
    //    是否继续下一首
    private boolean ConfigAutoNext;
    //播放列表
    private Vector<String> playList;
    private int playerState;
    private int currentPosition = 0;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playList = new Vector<String>();


//        初始化MediaPlayer
//        if (mediaPlayer)
        mediaPlayer = new MediaPlayer();
//       当prepareAsync()准备资源完成时调用
        mediaPlayer.setOnPreparedListener(this);
//        当一首歌播放完成调用
        mediaPlayer.setOnCompletionListener(this);
        playerState = STATE_CREATE;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int action = intent.getIntExtra("action", -1);
            switch (action) {
                case 0://播放自定网址的声音，通常不需要继续播放
//                    http://10.10.60.18:8080/nobody.mp3
                    playMusic(intent);
                    break;
                case 1:
                    pauseMusic(intent);
                    break;
                case 2:
                    continueMusic(intent);
                    break;
                case 3://重新设置播放列表，可以报哈自动播放的列表

                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 暂停播放，
     *
     * @param intent
     */
    private void pauseMusic(Intent intent) {


        try {
            if (playerState == STATE_START && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playerState = STATE_PAUSE;
            }

        } catch (Exception e) {

        }

    }

    //
    private void continueMusic(Intent intent) {
        if (!mediaPlayer.isPlaying() && playerState == STATE_PAUSE) {
            mediaPlayer.start();
            playerState = STATE_START;
        }
    }

    /**
     * 播放intent中的网址
     *
     * @param intent
     */
    private void playMusic(Intent intent) {
        if (intent != null) {
            String url = intent.getStringExtra("url");
            playMusic(url);
        }
    }

    private void playMusic(String url) {
        if (url != null) {
            resetPlayer();
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();
                playerState = STATE_PREPARED;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 如果正在播放那么停止，重置MediaPlayer
     * 准备下次播放
     */
    private void resetPlayer() {
        try {
            mediaPlayer.stop();

        } catch (Exception e) {

        }


        mediaPlayer.reset();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {

            playerState = STATE_DESTORY;
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();

            }
            mediaPlayer.release();

        } catch (Exception e) {
//            因为player的操作可能出现状态错误，所以捕获异常

        }
        mediaPlayer = null;


        playList.clear();
        playList = null;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        playerState = STATE_START;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playerState = STATE_STOP;
        if (playList != null && ConfigAutoNext) {
            int size = playList.size();
            if (currentPosition + 1 >= size) {
                // TODO 最后一首不在播放下一首
            } else {
                //下一首
                String url = playList.get(currentPosition + 1);
                playMusic(url);
                currentPosition++;
            }
        }

    }
}
