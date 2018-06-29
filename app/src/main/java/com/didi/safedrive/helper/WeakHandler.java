package com.didi.safedrive.helper;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class WeakHandler extends Handler {

    public WeakReference<IHandler> mListener;

    public WeakHandler(IHandler handler) {
        mListener = new WeakReference<>(handler);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        IHandler handler = mListener.get();
        if (handler != null) {
            handler.handleMsg(msg);
        }
    }

    public interface IHandler {
        void handleMsg(Message msg);
    }
}
