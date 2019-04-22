package com.shareefoo.pubgcompanion.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.chrisbanes.photoview.PhotoView;
import com.shareefoo.pubgcompanion.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.uiOptions;

public class FullMapActivity extends AppCompatActivity {

    @BindView(R.id.photo_view)
    PhotoView photoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_map);
        ButterKnife.bind(this);

        String map_name = getIntent().getStringExtra("map_name");
        switch (map_name) {
            case "Erangel":
                photoView.setImageResource(R.drawable.map_erangel_full);
                break;

            case "Miramar":
                photoView.setImageResource(R.drawable.map_erangel_full);
                break;

            case "Sanhok":
                photoView.setImageResource(R.drawable.map_erangel_full);
                break;

            case "Vikendi":
                photoView.setImageResource(R.drawable.map_erangel_full);
                break;
        }

//        fullScreen();
    }


    public void fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled = isImmersiveModeEnabled();
        if (isImmersiveModeEnabled) {
            Log.i("TEST", "Turning immersive mode mode off. ");
        } else {
            Log.i("TEST", "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

    private boolean isImmersiveModeEnabled() {
        return ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
    }

}
