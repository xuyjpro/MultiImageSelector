package com.meta.xuyj.pictureselect;

import android.app.Activity;
import android.content.Intent;

import java.util.Set;

public class MutiImageSelector {
    public static final int PICTURTE_SELECT=10001;
    private static Set<String> mSelectedSet;

    public static Builder create(Activity activity){
        Builder builder=new Builder(activity);
        return builder;
    }

    public static void setmSelectedSet(Set<String> mSelectedSet) {
        MutiImageSelector.mSelectedSet = mSelectedSet;
    }

    public static Set<String> getSelectedImageSet() {
        return mSelectedSet;
    }

    public static  class Builder{
        private int spanCount=4;
        private Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setSpancount(int spanCount) {
            this.spanCount = spanCount;
            return this;
        }
        public void init(){
            MutiImageSelector.setmSelectedSet(null);
           // Intent intent=new Intent("com.xuyj.picture-select");
            Intent intent=new Intent(activity,PictureSelectActvity.class);

            intent.putExtra("span-count",spanCount);

            activity.startActivityForResult(intent,PICTURTE_SELECT);
        }
    }
}
