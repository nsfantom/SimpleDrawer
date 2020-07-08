package tm.fantom.simpledrawer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;

import java.io.InputStream;
import java.util.List;

import tm.fantom.simpledrawer.databinding.ActivityMainBinding;
import tm.fantom.simpledrawer.net.Funcs;
import tm.fantom.simpledrawer.net.NetMenuItem;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding layout;
    MainViewModel mainViewModel;
    private AppCompatImageView iv;
    private AppCompatTextView tv;
    private WebView wv;
    private FrameLayout.LayoutParams lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(layout.getRoot());

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        if (savedInstanceState == null) {
            setUiOptions();
            setTheme(R.style.AppThemeLight);
        }
        layout.navView.setNavigationItemSelectedListener(
                item -> mainViewModel.onClickMenu(item.getItemId())
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribe();
    }

    private void setUiOptions() {
        View decorView = getWindow().getDecorView();
        int uiOptions = getUIOption();
        decorView.setSystemUiVisibility(uiOptions);
    }

    private int getUIOption() {
        if (Build.VERSION.SDK_INT == 22) {
            return View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        } else {
            return View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        }
    }

    private void subscribe() {
        mainViewModel.getNetMenuItems().observe(this, this::populateMenu);
        mainViewModel.getSelected().observe(this, this::showContent);

    }

    protected void populateMenu(List<NetMenuItem> list) {
        layout.navView.getMenu().clear();
        SubMenu sm = layout.navView.getMenu().addSubMenu("Network Menu");
        NetMenuItem mni;
        MenuItem mi;
        for (int i = 0; i < list.size(); i++) {
            mni = list.get(i);
            mi = sm.add(Menu.NONE, i, Menu.NONE, mni.getName());
            switch (mni.getFunction()) {
                default:
                case Funcs.TEXT:
                    mi.setIcon(R.drawable.ic_subtitles_black_24dp);
                    break;
                case Funcs.IMAGE:
                    mi.setIcon(R.drawable.ic_photo_size_select_actual_black_24dp);
                    break;
                case Funcs.URL:
                    mi.setIcon(R.drawable.ic_web_black_24dp);
                    break;
            }
        }
        layout.navView.invalidate();
    }

    protected void showContent(NetMenuItem netMenuItem) {
        final View v;
        switch (netMenuItem.getFunction()) {
            default:
            case Funcs.TEXT:
                v = getTextView(netMenuItem.getParam());
                break;
            case Funcs.IMAGE:
                v = getImageView(netMenuItem.getParam());
                break;
            case Funcs.URL:
                v = getWebView(netMenuItem.getParam());
                break;
        }
        layout.container.removeAllViews();
        layout.container.addView(v, getLp());
        layout.drawerLayout.closeDrawers();
    }

    private FrameLayout.LayoutParams getLp() {
        if (lp == null) {
            lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.CENTER;
        }
        return lp;
    }

    private WebView getWebView(String url) {
        if (wv == null) wv = new WebView(this);
        wv.loadUrl(url);
        return wv;
    }

    private AppCompatTextView getTextView(String txt) {
        if (tv == null) {
            tv = new AppCompatTextView(this);
            tv.setGravity(Gravity.CENTER);
        }
        tv.setText(txt);
        return tv;
    }

    private AppCompatImageView getImageView(String url) {
        if (iv == null) iv = new AppCompatImageView(this);
        new DownloadImageTask(iv).execute(url);
        return iv;
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @SuppressLint("StaticFieldLeak")
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage(), e);
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (bmImage != null)
                bmImage.setImageBitmap(result);
        }
    }
}
