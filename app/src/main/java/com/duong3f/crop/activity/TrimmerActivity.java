package com.duong3f.crop.activity;

import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.duong3f.crop.videoTrimmer.HgLVideoTrimmer;
import com.duong3f.crop.videoTrimmer.interfaces.OnHgLVideoListener;
import com.duong3f.crop.videoTrimmer.interfaces.OnTrimVideoListener;
import com.duong3f.module.MyEditCrop;
import com.duong3f.module.MyEditableImage;
import com.group3f.gifmaker.R;

import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;
import me.littlecheesecake.croplayout.model.ScalableBox;

public class TrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnHgLVideoListener {

    private HgLVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;
    MyEditCrop myEditCrop;
    private MyEditableImage editableImage;
    private ScalableBox scalableBox;

    public void setImageToCropViewFromPath(int w, int h) {
        Bitmap bmp = Bitmap.createBitmap(myEditCrop.getWidth(), myEditCrop.getHeight(), Bitmap.Config.ARGB_8888);
        editableImage = new MyEditableImage(bmp);
        scalableBox = new ScalableBox(0, 0, w, h);
        editableImage.setBox(scalableBox);
        myEditCrop.initView(this, editableImage);
        myEditCrop.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);
        myEditCrop = (MyEditCrop) findViewById(R.id.layout_crop_edit_cropvideo);
        Intent extraIntent = getIntent();
        String path = "";
        int maxDuration = 10;
        if (extraIntent != null) {
            path = extraIntent.getStringExtra(MainActivity.EXTRA_VIDEO_PATH);
            maxDuration = extraIntent.getIntExtra(MainActivity.VIDEO_TOTAL_DURATION, 10);
        }
        getSupportActionBar().hide();
        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));
        mVideoTrimmer = ((HgLVideoTrimmer) findViewById(R.id.timeLine));
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(String.valueOf(Uri.parse(path)));
        String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
//        layout.width = Integer.parseInt(width);
//        layout.height = Integer.parseInt(height);
//        setImageToCropViewFromPath(Integer.parseInt(width), Integer.parseInt(height));
        if (mVideoTrimmer != null) {
            //mVideoTrimmer.setMaxDuration(maxDuration);
            mVideoTrimmer.setMaxDuration(maxDuration);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setOnHgLVideoListener(this);
            //mVideoTrimmer.setDestinationPath("/storage/emulated/0/DCIM/CameraCustom/");
            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

    @Override
    public void onTrimStarted() {
        mProgressDialog.show();
    }

    @Override
    public void getResult(final Uri contentUri) {
        mProgressDialog.cancel();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(TrimmerActivity.this, getString(R.string.video_saved_at, contentUri.getPath()), Toast.LENGTH_SHORT).show();

            }
        });
//        try {
//            String path = contentUri.getPath();
//            File file = new File(path);
//            DuongLog.e(getClass(), "ket qua");
//            Log.e("tg", " path1 = " + path + " uri1 = " + Uri.fromFile(file));
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
//            intent.setData(contentUri)
//            intent.setDataAndType(Uri.fromFile(file), "video/*");
//            startActivity(intent);

//            finish();
        Intent intent = new Intent();
        intent.setData(contentUri);
        setResult(RESULT_OK, intent);
        finish();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(TrimmerActivity.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    private void playUriOnVLC(Uri uri) {
        int vlcRequestCode = 42;
        Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
        vlcIntent.setPackage("org.videolan.vlc");
        vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
        vlcIntent.putExtra("title", "Kung Fury");
        vlcIntent.putExtra("from_start", false);
        vlcIntent.putExtra("position", 90000l);
        startActivityForResult(vlcIntent, vlcRequestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("tg", "resultCode = " + resultCode + " data " + data);
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(final String message) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(TrimmerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
