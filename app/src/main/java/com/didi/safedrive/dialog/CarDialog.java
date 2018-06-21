package com.didi.safedrive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.didi.safedrive.R;
import com.didi.safedrive.helper.IDialogListener;

public class CarDialog extends Dialog {
    private IDialogListener mListener;
    private CountDownTimer mTimer;
    private int mTime = 15;
    private String mTips;
    private TextView mTitleTV;
    private TextView mContentTV;
    private TextView mTipsTV;
    private ImageView mCloseBTN;

    public CarDialog listener(IDialogListener listener) {
        this.mListener = listener;
        return this;
    }

    public CarDialog content(String content) {
        mContentTV.setText(content);
        return this;
    }

    public CarDialog tips(String tips) {
        mTips = tips;
        return this;
    }

    public CarDialog(@NonNull Context context) {
        super(context, R.style.TipFloatDialog);
        init();
    }

    public CarDialog title(String title) {
        mTitleTV.setText(title);
        return this;
    }

    protected void init() {
        setContentView(R.layout.dialog_countdown);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //防止点击返回时关闭
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        mTitleTV = findViewById(R.id.dialog_title);
        mContentTV = findViewById(R.id.dialog_content);
        mTipsTV = findViewById(R.id.dialog_tips);
        mCloseBTN = findViewById(R.id.dialog_close);
    }

    private void updateView() {
        mCloseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarDialog.this.dismiss();
            }
        });
        mTimer = new CountDownTimer(mTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTipsTV.setText(millisUntilFinished / 1000 + mTips);
            }

            @Override
            public void onFinish() {
                CarDialog.this.dismiss();
            }
        };
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mListener != null) {
            mListener.onDismiss();
        }
        mTimer.cancel();
    }

    @Override
    public void show() {
        super.show();
        if (mListener != null) {
            mListener.onShow();
        }
        updateView();
        mTimer.start();
    }

    public void show(int time) {
        mTime = time;
        this.show();
    }
}
