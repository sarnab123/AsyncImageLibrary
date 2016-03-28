package com.demo.photogrid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.demo.photogrid.customUI.CustomImageView;

import java.util.ArrayList;

public class PhotoGridAdapter extends ArrayAdapter<NetworkPhotoProvider.PhotoItem> {
    
    private final Context mContext;

    private ArrayList<NetworkPhotoProvider.PhotoItem> listOfPhotos;
    private Bitmap mLoadingBitmap = null;

    int resource;

    public PhotoGridAdapter(Context context, int resource, int resourceID) {
        super(context,resource,resourceID);
        mContext = context;
        this.resource = resource;
        listOfPhotos = new ArrayList();
        mLoadingBitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.thumbnail_loading));
    }

    @Override
    public int getCount() {
        if(listOfPhotos != null) {
            return listOfPhotos.size();
        }
        return 0;
    }

    @Override
    public NetworkPhotoProvider.PhotoItem getItem(int position) {
        if(listOfPhotos != null && listOfPhotos.size() > position) {
            return listOfPhotos.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class PhotoViewHolder {
        CustomImageView photoImage;

        public PhotoViewHolder(View convertView)
        {
            photoImage = (CustomImageView)convertView.findViewById(R.id.id_image);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PhotoViewHolder viewHolder = null;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(resource,parent,false);
            viewHolder = new PhotoViewHolder(convertView);

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (PhotoViewHolder)convertView.getTag();
        }

        viewHolder.photoImage.setImageBitmap(mLoadingBitmap);

        viewHolder.photoImage.setPhotoItem(getItem(position));

        return convertView;

    }



    public void updatePhotoItems(ArrayList<NetworkPhotoProvider.PhotoItem> photoItems)
    {
        this.listOfPhotos = photoItems;
    }


}
