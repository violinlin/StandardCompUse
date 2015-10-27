package com.whl.standardcompuse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.whl.standardcompuse.service.MusicService;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String[] urls = intent.getStringArrayExtra("urls");
//                TODO 跟新UI
                StringBuilder builder = new StringBuilder();
                if (urls != null) {
                    for (int i = 0; i < urls.length; i++) {
                        builder.append(urls[i]).append('\n');
                    }
                }
                textInfo.setText(builder.toString());


            }

        }
    };
    private TextView textInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textInfo = (TextView) findViewById(R.id.progress_info);


    }

    @Override
    protected void onResume() {
        super.onResume();
//       对receiver实行动态注册
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.RECEIVER_ACTION);
        filter.addAction(Constants.ACTION_DATA_FETCH);
        registerReceiver(progressReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(progressReceiver);
    }

    public void btnAddRequest(View view) {
//        添加请求
//        Intent intent=new Intent(Constants.ACTION_DATA_FETCH);
        Intent intent = new Intent("com.whl.standardcompuse.action.DATA_FETCH_SERVICE");
        intent.putExtra("action", 0);//0 代表添加请求
        intent.putExtra("url", "www.baidu.com");
        startService(intent);


    }

    public void btnGetProgress(View view) {
//        获取进度
        Intent intent = new Intent("com.whl.standardcompuse.action.DATA_FETCH_SERVICE");
        intent.putExtra("action", 1);//0 代表添加请求
        intent.putExtra("url", "www.baidu.com");
        startService(intent);


    }

    public void btnRemoveRequest(View view) {


    }

    /**
     * 启动服务播放音乐
     *
     * @param view
     */
    public void btnPlayMusic(View view) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("action", 0);
        intent.putExtra("url", "http://10.10.60.18:8080/nobody.mp3");
        startService(intent);

    }

    public void btnPauseMusic(View view) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("action", 1);
        intent.putExtra("url", "http://10.10.60.18:8080/nobody.mp3");
        startService(intent);

    }
}
