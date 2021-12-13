package com.example.homespace;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    FragmentActivity context;
    ArrayList<String> directories;

    public ImageAdapter(FragmentActivity localcontext) {
        context = localcontext;

        directories = new ArrayList<>();
        directories = getAllShownImagesPath(context);
    }

    @Override
    public int getCount() {
        return directories.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ImageView picturesView;

        if (convertView == null)
        {
            picturesView = new ImageView(context);
            picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            picturesView.setLayoutParams(new GridView.LayoutParams(270,270));
        }
        else
        {
            picturesView = (ImageView) convertView;
        }

        Glide.with(context)
                .load(directories.get(position))
                .placeholder(R.drawable.ic_launcher_background)
                .into(picturesView);

        return picturesView;
    }


    private ArrayList<String> getAllShownImagesPath(FragmentActivity activity)
    {

        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        //order data by date
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;


        String[] projection =
                {
                        MediaStore.MediaColumns.DATA,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                };

        cursor = activity.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");


        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while (cursor.moveToNext())
        {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }

        return listOfAllImages;
    }

    public ArrayList<String> getDirectories()
    {
        return directories;
    }
}
