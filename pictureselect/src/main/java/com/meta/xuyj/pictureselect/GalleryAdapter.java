package com.meta.xuyj.pictureselect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Set;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<String> datas;
    private Context mContext;
    private String parentPath;
    private Set<String> selectSet;
    private OnItemClickListener listener;
    public GalleryAdapter(List<String> datas, String parentPath,Set<String> set) {

        this.datas = datas;
        this.parentPath = parentPath;
        this.selectSet=set;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_gallery;
        CheckBox cb_gallery;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_gallery = itemView.findViewById(R.id.iv_gallery);
            cb_gallery = itemView.findViewById(R.id.cb_gallery);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_gallery
                    .getLayoutParams();
            int width = (WorkKit.SCREEN_WIDTH - 10 * (PictureSelectActvity.getSpanCount()+1)) /
                    PictureSelectActvity.getSpanCount();
            params.height = width;
            params.width = width;
            params.topMargin = 10;
            iv_gallery.setLayoutParams(params);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gallery, null);
        final ViewHolder holder = new ViewHolder(view);
        holder.iv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    int position=holder.getAdapterPosition();

                    listener.onImageClick(parentPath+'/'+datas.get(position));
                }
            }
        });
        holder.cb_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    int position=holder.getAdapterPosition();
                    listener.onBoxClick(parentPath+'/'+datas.get(position),holder.cb_gallery.isChecked());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String path=parentPath + '/' + datas.get(position);
        if(selectSet.contains(path)){
            holder.cb_gallery.setChecked(true);
        }else{
            holder.cb_gallery.setChecked(false);

        }
        Glide.with(mContext).load(path).into(holder.iv_gallery);

    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
    interface  OnItemClickListener{
        void onImageClick(String picPath);
        void onBoxClick(String picPath,boolean isChecked);
    }
}
