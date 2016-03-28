package com.demo.photogrid;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

public class PhotoGridFragment extends Fragment {
    
    PhotoGridAdapter adapter = null;
    GridView gridView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_grid, null);
        gridView = (GridView) v.findViewById(R.id.grid);
        adapter = new PhotoGridAdapter(getActivity(),R.layout.gridview_item,R.id.id_image);
        gridView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadContent();
    }

    private void loadContent() {
        new PhotoDownloader().execute();
    }

    class PhotoDownloader extends AsyncTask<Void,Void,ArrayList<NetworkPhotoProvider.PhotoItem>>
    {
        ProgressDialog dialog = null;

        @Override
        protected ArrayList<NetworkPhotoProvider.PhotoItem> doInBackground(Void... params) {

            ArrayList<NetworkPhotoProvider.PhotoItem> listOfPhotos = new ArrayList();
            NetworkPhotoProvider provider = NetworkPhotoProvider.getInstance(getActivity());
            listOfPhotos.addAll(provider.getAllPhotos());

            return listOfPhotos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(),"loADING","Images");
        }

        @Override
        protected void onPostExecute(ArrayList<NetworkPhotoProvider.PhotoItem> photoItems) {
            super.onPostExecute(photoItems);
            dialog.dismiss();
            adapter.updatePhotoItems(photoItems);
            adapter.notifyDataSetChanged();
        }
    }
}
