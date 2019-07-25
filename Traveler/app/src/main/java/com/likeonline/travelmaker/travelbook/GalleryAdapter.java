package com.likeonline.travelmaker.travelbook;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.likeonline.travelmaker.R;

import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter {

    private Context ctx ;
    private int pos ;
    private LayoutInflater inflater ;
    private ImageView ivGallery ;
    ArrayList<String> mArrayUri ;
    public GalleryAdapter (Context  ctx ,ArrayList < String >mArrayUri )   {

        this.ctx = ctx ;
        this.mArrayUri=mArrayUri ;
    }

    @ Override
    public int getCount ( )   {
        return mArrayUri.size ( ) ;
    }

    @ Override
    public Object getItem (int position) {
        return mArrayUri.get(position) ;
    }

    @ Override
    public long getItemId (int position )   {
        return 0 ;
    }

    @ Override
    public View getView (int position , View convertView , ViewGroup parent )   {

        pos = position;
        inflater=(LayoutInflater)   ctx
                .getSystemService ( Context.LAYOUT_INFLATER_SERVICE ) ;

        View itemView=inflater . inflate ( R.layout .item_bookalbum ,  parent ,  false ) ;

        ivGallery = (ImageView)itemView .findViewById (R.id.album_imagae) ;

        //ivGallery.setImageURI ( mArrayUri.get(position)) ;
        Glide.with(ctx).load(mArrayUri.get(position)).into(ivGallery);

        return itemView ;
    }
}
