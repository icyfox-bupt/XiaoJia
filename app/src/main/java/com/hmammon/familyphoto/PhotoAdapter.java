package com.hmammon.familyphoto;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hmammon.familyphoto.utils.ImageHelper;
import com.hmammon.familyphoto.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by icyfox on 2014/12/3.
 */
public class PhotoAdapter extends BaseAdapter {

    List<Photo> photos;
    Activity activity;
    ImageLoader loader;
    private int checked = 0;

    public PhotoAdapter(List<Photo> path, Activity activity) {
        this.photos = path;
        this.activity = activity;
        loader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;

        if (view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.item_preview, null);
            vh = new ViewHolder();
            vh.iv = (ImageView) view.findViewById(R.id.iv_pre);
            vh.card = (CardView) view.findViewById(R.id.card);
            view.setTag(vh);
        }else {
            vh = (ViewHolder) view.getTag();
        }

        String thumb = "file://" + photos.get(i).thumb;
        loader.displayImage(thumb, vh.iv, ImageHelper.smallOptions);

        //动态改变图片大小
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) vh.card.getLayoutParams();
        int normalSize = Tools.dp2px(activity, 100);
        int bigSize = Tools.dp2px(activity, 120);
        if (i == checked){
            lp.width = bigSize;
            lp.height = bigSize;
        }else{
            lp.width = normalSize;
            lp.height = normalSize;
        }

        view.measure(-2, -2);

        return view;
    }

    static class ViewHolder{
        ImageView iv;
        CardView card;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
}
