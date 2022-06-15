package com.eggplant.qiezisocial.ui.notify;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.utils.NotifycationUtils;

/**
 * Created by Administrator on 2022/6/15.
 */

public class EmptyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        NotifycationUtils.getInstance(this).cancelNotify(2);
        finish();
    }
}
