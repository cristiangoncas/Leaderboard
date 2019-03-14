package com.cristiangoncas.looperthread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

public class MyHandlerThread extends HandlerThread {

    Handler handler;

    public MyHandlerThread(String name) {
        super(name);
        Log.d("LooperThread", "Thread " + getThreadId() + " executing.");
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message message) {
                try {
                    Thread.sleep(5000);
                    Log.d("LooperThread", "Thread " + getThreadId() + " executing.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getLooper().quit();
            }
        };
    }
}
