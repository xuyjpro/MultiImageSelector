package com.meta.xuyj.pictureselect;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ListImageDirPopupWindow extends PopupWindow {
    RecyclerView recycl_camera_list;

    public ListImageDirPopupWindow(Context context, List<ImageFloder> mImageFloders) {
        View conentView = LayoutInflater.from(context).inflate(R.layout.layout_popup_view, null);
        recycl_camera_list = (RecyclerView) conentView.findViewById(R.id.rv_popup_view);
        setContentView(conentView);

        ListAdapter listAdapter = new ListAdapter( mImageFloders);
        recycl_camera_list.setLayoutManager(new LinearLayoutManager(context));//设置垂直
        recycl_camera_list.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));
        recycl_camera_list.setAdapter(listAdapter);
        listAdapter.setListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position,ImageFloder imageFloder) {

                onImageDirSelected.selected(imageFloder);
            }

        });

       setAnimationStyle(R.style.popuw_anim_style);
        setFocusable(true);
        setTouchable(true);

        setOutsideTouchable(true);
        ColorDrawable background = new ColorDrawable(0xffffff);
        setBackgroundDrawable(background);

        setWidth((WorkKit.SCREEN_WIDTH));
        setHeight(WorkKit.SCREEN_HEIGHT / 3*2);

    }


    static class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        private List<ImageFloder> datas;
        private Context mContext;
        private OnItemClickListener listener;

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public ListAdapter(List<ImageFloder> datas) {
            this.datas = datas;

        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView iv_first_image;
            private TextView tv_gallery_name;

            public ViewHolder(View itemView) {
                super(itemView);
                iv_first_image = itemView.findViewById(R.id.iv_first_image);
                tv_gallery_name = itemView.findViewById(R.id.tv_gallery_name);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popup_view,
                    null);
            final ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int positon = viewHolder.getAdapterPosition();
                        listener.onItemClick(positon,datas.get(positon));
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ImageFloder imageFloder = datas.get(position);
            Glide.with(mContext).load(imageFloder.getFirstImagePath()).into(holder.iv_first_image);

            holder.tv_gallery_name.setText(imageFloder.getName() + '(' + imageFloder.getCount() +
                    ')');


        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        interface OnItemClickListener {
            void onItemClick(int position,ImageFloder imageFloder);
        }

    }

    public void showAtDropDownCenter(View parent) {
        if (!isShowing()) {
            //   setAnimationStyle(R.style.popup_camera);
            int[] location = new int[2];
            parent.getLocationOnScreen(location);//获取以屏幕为原点的位置
            showAtLocation(parent, Gravity.TOP | Gravity.LEFT, 0, location[1] - getHeight());
//            showAtLocation(parent,Gravity.TOP,(location[0]-getWidth())/2, location[1]-getHeight
// ());
//            showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1]-getHeight());
// 三种方式都可以 原理是一样的
        } else {
            dismiss();
        }
    }

    //点击之后的接口回调
    private OnImageDirSelected onImageDirSelected;

    public void setOnImageDirSelected(OnImageDirSelected onImageDirSelected) {
        this.onImageDirSelected = onImageDirSelected;
    }

    public interface OnImageDirSelected {
        void selected(ImageFloder floder);
    }
}

