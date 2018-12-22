package com.meta.xuyj.pictureselect;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

public class PictureDetailDialog extends Dialog {
    private PhotoView iv_picture;
    private String picUrl;

    public static PictureDetailDialog create(Context context) {
        return new PictureDetailDialog(context);
    }

    public PictureDetailDialog setPicUrl(String picUrl) {
        this.picUrl = picUrl;

        return this;
    }

    private PictureDetailDialog(@NonNull Context context) {
        super(context);
    }

    private PictureDetailDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    private PictureDetailDialog(@NonNull Context context, boolean cancelable, @Nullable
            OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {


        super.show();

        /**
         * 设置宽度全屏，要设置在show的后面
//         */
//        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//        layoutParams.gravity = Gravity.BOTTOM;
//        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//
//        getWindow().getDecorView().setPadding(0, 0, 0, 0);
//
//        getWindow().setAttributes(layoutParams);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_picture_detail);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //按空白处不能取消动画
     //   setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();

        initEvent();
    }

    public void initView() {
        iv_picture = findViewById(R.id.iv_picture);
        iv_picture.enable();

    }

    public void setPicuture() {
        if (picUrl != null) {
            Glide.with(getContext()).load(picUrl).into(iv_picture);
        }
    }


    public void initEvent() {
        iv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface OnLongClickListener {
        void onLongClick();
    }
}
