package com.demo.photogrid;

import android.app.Activity;
import android.os.Bundle;

public class PhotoGridActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_grid);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PhotoGridFragment()).commit();
        }
    }

}
