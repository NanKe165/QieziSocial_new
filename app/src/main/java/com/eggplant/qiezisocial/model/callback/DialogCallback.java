/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eggplant.qiezisocial.model.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Window;

import com.eggplant.qiezisocial.widget.dialog.QzProgressDialog;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.base.Request;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：对于网络请求是否需要弹出进度对话框
 * 修订历史：
 * ================================================
 */
public abstract class DialogCallback<T> extends JsonCallback<T> {

    private QzProgressDialog dialog;
    boolean showProgress = false;
    int fileSize = 1, currentUploadTime = 1;
    int lastSumProgress = 0, lastProgress = 0;
    String msg = "请求网络中...";
    private int sumProgress;

    public DialogCallback() {

    }

    private void initDialog(Activity activity, String msg) {
        this.msg = msg;
        dialog = new QzProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(msg);
    }

    public DialogCallback(Activity activity, String msg) {
        super();
        initDialog(activity, msg);
    }

    public DialogCallback(Activity activity, String msg, boolean showProgress, int fileSize) {
        super();
        initDialog(activity, msg);
        this.showProgress = showProgress;
        this.fileSize = fileSize;
    }

    @Override
    public void uploadProgress(Progress progress) {
        super.uploadProgress(progress);
        if (showProgress) {
            int currPgs = (int) (progress.fraction * 100) / fileSize;
            if (lastProgress > currPgs && currentUploadTime < fileSize) {
                currentUploadTime++;
                lastSumProgress += lastProgress;
            }
            lastProgress = currPgs;
//            Log.e("dialogProgress", "uploadProgress:  sum:" + lastSumProgress + "  currPgs:" + currPgs + "   progress:" + progress.fraction);
            if (lastProgress + currPgs > sumProgress) {
                sumProgress = lastSumProgress + currPgs;
            }
            dialog.setMessage(msg + sumProgress + "%");
        }

    }

    public DialogCallback(Activity activity) {
        super();
        initDialog(activity, null);
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onFinish() {
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
