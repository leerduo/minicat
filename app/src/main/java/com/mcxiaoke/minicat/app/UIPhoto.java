package com.mcxiaoke.minicat.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.controller.EmptyViewController;
import com.mcxiaoke.minicat.util.IOHelper;
import com.mcxiaoke.minicat.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import uk.co.senab.photoview.PhotoView;

import java.io.File;

/**
 * @author mcxiaoke
 * @version 5.0 2012.03.27
 */
public class UIPhoto extends AppCompatActivity implements OnClickListener {

    private static final String TAG = UIPhoto.class.getSimpleName();
    private String url;

    private PhotoView mImageView;

    private View vEmpty;
    private EmptyViewController emptyViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0x66333333));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("查看照片");
        initialize();
        setLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_save:
                doSave();
                break;
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_enter_2, R.anim.zoom_exit_2);
    }

    protected void initialize() {
        parseIntent(getIntent());
    }

    protected void setLayout() {
        setContentView(R.layout.ui_photo);
        findViews();

        if (AppContext.DEBUG) {
            Log.d(TAG, "mPhotoPath=" + url);
        }

        displayImage();

    }

    private void findViews() {
        mImageView = (PhotoView) findViewById(R.id.photo);
//        mImageView.setOnClickListener(this);
        vEmpty = findViewById(android.R.id.empty);
        emptyViewController = new EmptyViewController(vEmpty);
    }

    private void toggleActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar.isShowing()) {
            actionBar.hide();
        } else {
            actionBar.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.photo) {
            toggleActionBar();
        }
    }

    private void showProgress() {
        mImageView.setVisibility(View.GONE);
        emptyViewController.showProgress();
    }

    private void showEmptyText(String text) {
        mImageView.setVisibility(View.GONE);
        emptyViewController.showEmpty(text);
    }

    private void showContent(Bitmap bitmap) {
        emptyViewController.hideProgress();
        mImageView.setVisibility(View.VISIBLE);
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        }
    }

    private void displayImage() {
        final String imageUrl = url;
        final ImageLoadingListener listener = new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                showProgress();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                showEmptyText(failReason.getType() + ":" + failReason.getCause());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                showContent(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                showEmptyText("Cancelled");
            }
        };
        ImageLoader.getInstance().loadImage(imageUrl, getDisplayImageOptions(), listener);
    }

    private DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(true).cacheOnDisc(true);
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        builder.showImageOnFail(R.drawable.photo_error);
        builder.showImageOnLoading(R.drawable.photo_loading);
        builder.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
        return builder.build();
    }

    private void parseIntent(Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            url = intent.getStringExtra("url");
        } else if (action.equals(Intent.ACTION_VIEW)) {
            Uri uri = intent.getData();
            if (uri.getScheme().equals("content")) {
                url = IOHelper.getRealPathFromURI(this, uri);
            } else {
                url = uri.getPath();
            }
        }

    }

    private void doSave() {
        File file = ImageLoader.getInstance().getDiscCache().get(url);
        final String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        File dest = new File(IOHelper.getPictureDir(this), fileName);
        if (dest.exists() || IOHelper.copyFile(file, dest)) {
            Utils.notify(this, "照片已保存到 " + dest.getAbsolutePath());
        }
    }

}
