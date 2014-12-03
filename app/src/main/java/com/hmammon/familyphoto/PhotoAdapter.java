package com.hmammon.familyphoto;

import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by icyfox on 2014/12/3.
 */
public class PhotoAdapter extends BaseAdapter {

    Cursor c;
    Activity activity;
    ImageLoader loader;

    public PhotoAdapter(Cursor c, Activity activity) {
        this.c = c;
        this.activity = activity;
        loader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return c.getCount();
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
            view.setTag(vh);
        }else {
            vh = (ViewHolder) view.getTag();
        }

        if (!c.move(i)) return view;

        String path = "file:///" + c.getString(0);

        loader.displayImage(path, vh.iv);

        return view;
    }

    static class ViewHolder{
        ImageView iv;
    }
}
