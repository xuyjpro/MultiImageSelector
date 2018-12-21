package com.meta.xuyj.pictureselect;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PictureSelectActvity extends Activity {

    private List<String> mDirPaths;
    private List<ImageFloder> mImageFloders;
    private Set<String> mSelectedSet;
    private int mPicsSize;
    private File mImgDir;
    private List<String> mImgs;

    private GalleryAdapter mAdapter;
    private RecyclerView rv_gallery;

    private int realCount;

    private TextView tv_gallery;
    private TextView tv_selected;

    private TextView tv_clean_all;
    private ListImageDirPopupWindow mListImageDirPopupWindow;

    private static int spanCount;

    public static int getSpanCount() {
        return spanCount;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
    public void initView(){
        mDirPaths=new ArrayList<>();
        mImageFloders=new ArrayList<>();
        mSelectedSet=new HashSet<>();

        mImgs=new ArrayList<>(0);
        rv_gallery=findViewById(R.id.rv_gallery);
        tv_gallery=findViewById(R.id.tv_gallery);
        tv_selected=findViewById(R.id.tv_selected);
        tv_clean_all=findViewById(R.id.tv_clean_all);

        DisplayMetrics dm=getResources().getDisplayMetrics();
        WorkKit.SCREEN_WIDTH=dm.widthPixels;
        WorkKit.SCREEN_HEIGHT=dm.heightPixels;

        tv_clean_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedSet.clear();
                mAdapter.notifyDataSetChanged();
                tv_selected.setText("完成");
                realCount=0;
            }
        });
        tv_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                MutiImageSelector.setmSelectedSet(mSelectedSet);
                finish();
            }
        });
        findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        spanCount=getIntent().getIntExtra("span-count",4);
        getImages();
    }

    //设置适配器数据
    private void setAdapterData() {
        if (mImgDir == null) {
            Toast.makeText(this,"没有查询到图片",Toast.LENGTH_SHORT).show();
            return;
        }
       // tv_pop_gallery.setText(mImgDir.getName());
        try {
            mImgs = Arrays.asList(mImgDir.list(getFileterImage()));//获取文件夹下的图片集合
        }catch (Exception e){
            e.printStackTrace();
        }
        //查询出来的图片是正序的，为了让图片按照时间倒序显示，对其倒序操作
        Collections.sort(mImgs, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return -1;
            }
        });

        mAdapter=new GalleryAdapter(mImgs,mImgDir.getAbsolutePath(),mSelectedSet);
        mAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onImageClick(String picPath) {

            }

            @Override
            public void onBoxClick(String picPath, boolean isChecked) {
                if(isChecked){
                    if(!mSelectedSet.contains(picPath)){
                        mSelectedSet.add(picPath);
                        realCount++;
                    }
                }else{
                    mSelectedSet.remove(picPath);
                    realCount--;
                }
                if(realCount!=0){
                    tv_selected.setText("完成 ("+realCount+')');

                }else{
                    tv_selected.setText("完成");
                }
            }
        });
        rv_gallery.setLayoutManager(new GridLayoutManager(this, spanCount));
      //  rv_gallery.addItemDecoration(new DividerGridItemDecoration(this));
        rv_gallery.setAdapter(mAdapter);
    }


    //获取图片的路径和父路径 及 图片size
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "检测到没有内存卡", Toast.LENGTH_SHORT).show();
            return;
        }
       // showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                ContentResolver mContentResolver = getContentResolver();

                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "+
                                MediaStore.Images.Media.MIME_TYPE + "=? or "+
                                MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png","image/jpg"},
                        MediaStore.Images.Media.DATE_TAKEN +" DESC");//获取图片的cursor，按照时间倒序（发现没卵用)

                while (mCursor.moveToNext()) {
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));// 1.获取图片的路径
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;//不获取sd卡根目录下的图片
                    String parentPath = parentFile.getAbsolutePath();//2.获取图片的文件夹信息
                    String parentName = parentFile.getName();
                    ImageFloder imageFloder ;//自定义一个model，来保存图片的信息

                    //这个操作，可以提高查询的效率，将查询的每一个图片的文件夹的路径保存到集合中，
                    //如果存在，就直接查询下一个，避免对每一个文件夹进行查询操作
                    if (mDirPaths.contains(parentPath)) {
                        continue;
                    } else {
                        mDirPaths.add(parentPath);//将父路径添加到集合中
                        imageFloder = new ImageFloder();
                        imageFloder.setFirstImagePath(path);
                        imageFloder.setDir(parentPath);
                        imageFloder.setName(parentName);
                    }
                    List<String> strings = null;

                    try {
                        strings =  Arrays.asList(parentFile.list(getFileterImage()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int  picSize = strings.size();//获取每个文件夹下的图片个数
                    imageFloder.setCount(picSize);//传入每个相册的图片个数
                    mImageFloders.add(imageFloder);//添加每一个相册
                    //获取图片最多的文件夹信息（父目录对象和个数，使得刚开始显示的是最多图片的相册
                    if (picSize > mPicsSize) {
                        mPicsSize = picSize;
                        mImgDir = parentFile;
                    }
                }
                mCursor.close();
                mDirPaths = null;
                mHandler.sendEmptyMessage(1);
            }
        }).start();
    }


    //图片筛选器，过滤无效图片
    private FilenameFilter getFileterImage(){
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg")
                        || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        };
        return filenameFilter;
    }
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setAdapterData();//设置图片的显示
            initListDirPopupWindw();//初始化小相册的popupWindow

            tv_gallery.setText(mImgDir.getName()+'('+mPicsSize+')');
            tv_gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 0.5f; //0.0-1.0
                    getWindow().setAttributes(lp);
                    mListImageDirPopupWindow.showAsDropDown(tv_gallery);

                    //中间
                  //  mListImageDirPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity
                        //    .CENTER, 0, 0);

                }
            });
        }
    };

    //设置相册PopupWindow
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(this, mImageFloders);
        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f; //0.0-1.0
                getWindow().setAttributes(lp);
               // setToRightDrawable(R.drawable.arrow_bottom);
            }
        });

        mListImageDirPopupWindow.setOnImageDirSelected(new ListImageDirPopupWindow.OnImageDirSelected() {
            //点击item之后的回调
            @Override
            public void selected(ImageFloder floder) {
                mListImageDirPopupWindow.showAtDropDownCenter(tv_gallery);
             //   setToRightDrawable(R.drawable.arrow_bottom);
          //      realCount = 0;
                tv_gallery.setText(floder.getName()+" ("+floder.getCount()+')');

                File file = new File(floder.getDir());
                List<String> picFileList = null;

                try {
                    picFileList =   Arrays.asList(file.list(getFileterImage()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Collections.sort(picFileList, new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        return -1;
                    }
                });
                //重新设置数据和checkBox初始化
                mImgDir = file;
                mImgs = picFileList;
                mAdapter = new GalleryAdapter(picFileList,mImgDir.getAbsolutePath(),mSelectedSet);
                mAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
                    @Override
                    public void onImageClick(String picPath) {

                    }

                    @Override
                    public void onBoxClick(String picPath, boolean isChecked) {
                        if(isChecked){
                            if(!mSelectedSet.contains(picPath)){
                                mSelectedSet.add(picPath);
                                realCount++;
                            }
                        }else{
                            mSelectedSet.remove(picPath);
                            realCount--;
                        }
                        if(realCount!=0){
                            tv_selected.setText("完成 ("+realCount+')');
                        }else{
                            tv_selected.setText("完成");
                        }
                    }
                });
                rv_gallery.setAdapter(mAdapter);
            }
        });
    }

}
