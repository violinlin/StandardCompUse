package com.whl.standardcompuse.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.whl.standardcompuse.Constants;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 * 数据加载服务
 */
public class DataFetchService extends Service implements Runnable {

    public static final String TAG = "DataFetchService";
    private Queue<String> urls;
    /**
     * 用于下载，操作urls队列的内容
     */
    private Thread downLoadThread;
    /**
     *
     */
    private boolean running;

    public DataFetchService() {
        Log.d(TAG, "构造");

    }
//   服务的onCreat()只执行一次，用于初始化数据与对象，通常是启动线程，初始化数据
//    在主线程执行

    @Override
    public void onCreate() {
        super.onCreate();
        urls = new LinkedList<String>();
        downLoadThread = new Thread(this);
    }
    /**
     * @param intent  第一个参数intent用与传递参数
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand" + flags);
//        检测Intent
        if (intent != null) {
            int action = intent.getIntExtra("action", -1);
            Log.d(TAG, "onStartCommand  action" + action);
            switch (action) {
                case 0://0 添加网址开始下载

                    processDownLoad(intent);
                    break;
                case 1://1 获取下载进度，
                    getProgress(intent);
                    break;
                case 2://2 删除下载任务
                    removeProgress(intent);
                    break;
            }
        }
//        返回值如果调用super()那么启动的服务是粘性的，进程终止，服务重新启动
//        但是intent为null,
//        return super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        urls.clear();
        urls = null;
        running = false;
        downLoadThread.interrupt();
    }
    public void run() {
        running = true;
        try {
            while (running) {
                String url = urls.poll();
                if (url != null) {
//                模拟网络下载
                    Thread.sleep(2000);
//                    TODO 跟新UI
                    sendUpdataBroadCast();
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除下载的任务
     *
     * @param intent
     */
    private void removeProgress(Intent intent) {
        if (!urls.isEmpty()) {
//            删除第一个
            urls.poll();
        }
    }
    /**
     * 获取下载的进度
     *
     * @param intent
     */
    private void getProgress(Intent intent) {
        //因为要获取进度，服务在没有绑定的情况下需要广播来完成进度的更新
        sendUpdataBroadCast();
    }
    private void sendUpdataBroadCast() {
        Intent broadcast = new Intent(Constants.RECEIVER_ACTION);
        String strs[] = new String[urls.size()];
        urls.toArray();
        broadcast.putExtra("urls", strs);
        sendBroadcast(broadcast);
    }
    /**
     * 处理地址下载的操作
     *
     * @param intent
     */
    private void processDownLoad(Intent intent) {
        if (intent != null) {
            String url = intent.getStringExtra("url");
            if (url != null) {
                urls.offer(url);

            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
