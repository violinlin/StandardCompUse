package com.whl.standardcompuse;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerActivity extends AppCompatActivity implements Runnable, SeekBar.OnSeekBarChangeListener, View.OnTouchListener, MediaPlayer.OnPreparedListener {

    private VideoView videoView;
    private SeekBar seekBar;
    private Thread thread;
    private boolean isrunning;
    private RelativeLayout title_container;
    private LinearLayout bottom_container;
    private boolean isContainerShow = false;
    private Thread hideControlThread;
    TextView textDuration, textCurPosition;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 123) {
                seekBar.setMax(msg.arg1);
                seekBar.setProgress(msg.arg2);
                textCurPosition.setText(timeTOString(msg.arg2));

            } else if (msg.what == 456) {
                if (isContainerShow) {
                    title_container.setVisibility(View.INVISIBLE);
                    bottom_container.setVisibility(View.INVISIBLE);
                    isContainerShow = !isContainerShow;
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        videoView = (VideoView) findViewById(R.id.videoView);
        seekBar = (SeekBar) findViewById(R.id.mc_seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        title_container = (RelativeLayout) findViewById(R.id.player_title_container);
        bottom_container = (LinearLayout) findViewById(R.id.player_bottom_container);
        textCurPosition = (TextView) findViewById(R.id.textCurPosition);
        textDuration = (TextView) findViewById(R.id.textDuration);
//        String url = "http://mp4.68mtv.com:8028/%E9%83%81%E5%8F%AF%E5%94%AF-%E6%97%B6%E9%97%B4%E7%85%AE%E9%9B%A8-%E5%9B%BD%E8%AF%AD%5Bmtvxz.cn%5D.mp4";
//        设置视频的地址，可以说res/raw内部的文件，也可以是本地手机文件还可以是网络地址
        String url = Environment.getExternalStorageDirectory().getAbsolutePath();
        videoView.setVideoPath(url + "/悟空.mp4");

//        videoView.setVideoURI(Uri.parse(url));
//        videoView.setMediaController(new MediaController(this));
//        videoView.setOnPreparedListener(this);
        videoView.setOnTouchListener(this);
        thread = new Thread(this, "seekThread");

        thread.start();
        videoView.setOnPreparedListener(this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isrunning = false;
        thread.interrupt();
        videoView.stopPlayback();
    }

    @Override
    public void run() {
        String curThreadName = Thread.currentThread().getName();
        if (curThreadName != null) {
            if ("seekThread".equals(curThreadName)) {
                isrunning = true;
                while (isrunning) {
                    try {
                        if (videoView != null && videoView.isPlaying()) {
                            int currentPosition = videoView.getCurrentPosition();
                            int duration = videoView.getDuration();
                            Message message = handler.obtainMessage(123);
                            message.arg1 = duration;
                            message.arg2 = currentPosition;
                            handler.sendMessage(message);

                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else if ("controlThread".equals(curThreadName)) {
//                TODO 控制自定义的控制器显示
                try {
                    Thread.sleep(5000);
                    Message message = handler.obtainMessage(456);
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        使用seekBar拖拽处理，注意第三个参数
        if (fromUser) {
            videoView.seekTo(progress);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            if (isContainerShow) {
                title_container.setVisibility(View.INVISIBLE);
                bottom_container.setVisibility(View.INVISIBLE);
            } else {
                title_container.setVisibility(View.VISIBLE);
                bottom_container.setVisibility(View.VISIBLE);
            }
            isContainerShow = !isContainerShow;

            if (isContainerShow) {
                hideControlThread = new Thread(this, "controlThread");
                hideControlThread.start();
            } else {
                if (hideControlThread.isAlive()) {
                    hideControlThread.interrupt();
                }
            }

        }

        return false;
    }


    @Override
    public void onPrepared(MediaPlayer mp) {

        textDuration.setText(timeTOString(mp.getDuration()));
        mp.start();

    }

    public void mediacControl(View view) {
        int position = 0;
        switch (view.getId()) {
            case R.id.media_rew:
                position = videoView.getCurrentPosition() - 10 * 1000;

                if (position < 0) {
                    videoView.seekTo(0);

                }
                videoView.seekTo(position);
                break;
            case R.id.media_play:
                if (isrunning) {
                    videoView.pause();
                } else {
                    videoView.start();
                }
                isrunning = !isrunning;
                if (isrunning) {
                    thread = new Thread(this, "seekThread");

                    thread.start();
                }
                break;
            case R.id.media_ff:
                position = videoView.getCurrentPosition() + 10 * 1000;
                if (position > videoView.getDuration()) {
                    videoView.seekTo(videoView.getDuration());
                }
                videoView.seekTo(position);
                break;

        }
    }

    private String timeTOString(int time) {

//        time = time / 1000;
//        StringBuilder sb = new StringBuilder();
//        String sStr;
//        String mStr;
//
//        int s = time % 60;
//        sStr = s >= 10 ? s + "" : "0" + s;
//        int m = time / 60 % 60;
//        mStr = m >= 10 ? s + "" : "0" + m;
//        sb.append(mStr).append(":").append(sStr);
//        return sb.toString();
        SimpleDateFormat f = new SimpleDateFormat("mm:ss");
        return f.format(new Date(time));

    }

}
