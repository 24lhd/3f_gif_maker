package com.long3f.activity;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duong3f.mvp.imagetogif.SelectImageFormFolderAcivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.group3f.gifmaker.R;
import com.long3f.Interface.OnDragedItem;
import com.long3f.Interface.OnListChangeSize;
import com.long3f.adapter.ImageManagerAdapter;
import com.long3f.fragment.EditFragment01;
import com.long3f.fragment.EditFragment02;
import com.long3f.helper.SimpleItemTouchHelperCallback;
import com.long3f.utils.AppCache;
import com.long3f.utils.AppUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONGG on 10/9/2017.
 */

public class EditGifActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int FRAGMENT_EDIT_01 = 0;
    public static final int FRAGMET_EDIT_02 = 1;
    private static final String TAG = "ffmpeg";
    public static Bitmap currentPathFile;
    public static int indexPathFile;
    public static double fps = 10;
    private boolean isPlayingGif = true;
    private int currentPageSelected = 0;
    private ViewPager pager;
    private TabLayout tabLayout;
    public static View imageManager;
    private FFmpeg ffmpeg;
    private ProgressDialog progressDialog;
    String frameTempDir = Environment.getExternalStorageDirectory().getPath() + "/GifMaker/.data";
    String outPutDir = Environment.getExternalStorageDirectory().getPath()
            + "/GifMaker";

    private ViewPagerAdapter viewPagerAdapter;
    private ImageView play_pause, btnBack, btnHideImageManager;
    public int gifSize;

    public static ImageView mGLImageView;
    TextView numberFrames, btnAddImage, btnSaveGif, btnShare;
    RelativeLayout layoutManager;
    RecyclerView recyclerView;
    public static ArrayList<Bitmap> arrPathFile;
    ImageManagerAdapter imageManagerAdapter;
    ItemTouchHelper itemTouchHelper;
    ItemTouchHelper.Callback callback;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    Dialog mBottomSheetDialog;
    public static ArrayList<Bitmap> listFrame = new ArrayList<>();
    public static String pathImageExtracted = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES) + "/GifEditor/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gif);
        getSupportActionBar().hide();
        loadFFMpegBinary();
        initViews();
    }

    public static ArrayList<Bitmap> arrBitmapCropped;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK)
//            arrBitmapCropped = data.getParcelableArrayListExtra(Config.ARRAY_BITMAP_CROPPED);
    }

    private void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }

                @Override
                public void onSuccess() {
                    Log.d("ffmpeg", "correct Loaded");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        } catch (Exception e) {
        }
    }


    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(EditGifActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Not Supported")
                .setMessage("Device Not Supported")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditGifActivity.this.finish();
                    }
                })
                .create()
                .show();

    }

    private void videoToListImage(String selectedVideoPath, long startMs, long endMs, double fps) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );
        String filePrefix = "extract_picture";
        String fileExtn = ".jpg";
        File dir = new File(moviesDir, "GifEditor");
        if (dir.exists()) {
            AppUtils.clearFileInfolder(dir.getAbsolutePath());
        } else {
            dir.mkdir();
        }
        File dest = new File(dir, filePrefix + "%03d" + fileExtn);
        Log.d(TAG, "startTrim: src: " + selectedVideoPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
        String[] complexCommand = {"-y", "-i", selectedVideoPath, "-an", "-r", String.valueOf(fps), "-ss", "" + startMs / 1000, "-t", "" + (endMs - startMs) / 1000, pathImageExtracted};
        execFFmpegBinary(complexCommand, null);

    }

    void listImageToGif(final int width, final int height, int bgColor, final float fps, final String outPut, final boolean isRepeat) {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                if (arrBitmapCropped != null && arrBitmapCropped.size() > 0) {
                    AppUtils.saveImageToFolderFromListBitmap(arrBitmapCropped, frameTempDir, isRepeat);
                } else {
                    AppUtils.saveImageToFolderFromListBitmap(listFrame, frameTempDir, isRepeat);
                }
                Bitmap bg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bg);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bg, 0, 0, null);
                AppUtils.saveBitmap(bg, frameTempDir + "/bg.png");
                return null;
            }

            @Override
            protected void onPreExecute() {
                AppUtils.clearFileInfolder(frameTempDir);
                progressDialog = new ProgressDialog(EditGifActivity.this);
                progressDialog.setMessage("Processing...");
                progressDialog.show();
                progressDialog.setCancelable(false);

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                String arr[] = {"-y", "-framerate", String.valueOf(fps),
//                "-i", frameTempDir + "/bg.png",
                        "-i", frameTempDir + "/out_%03d.png",
//                "-filter_complex" ,"overlay", "-shortest",
//                "-frames:v",
                        outPut};
                execFFmpegBinary(arr, outPut);
            }

            @Override
            protected void onProgressUpdate(Void... values) {

            }
        }.execute();


    }

    private void execFFmpegBinary(final String[] command, final String filePath) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.d(TAG, "FAILED with output : " + s);
                    progressDialog.dismiss();

                }

                @Override
                public void onSuccess(String s) {
                    Log.d("SUCCESS", " with output " + s);
                    AppUtils.addImageToGallery(filePath, getApplicationContext());
                    Toast.makeText(EditGifActivity.this, getText(R.string.save_success) + " " + filePath, Toast.LENGTH_SHORT).show();
                    finish();

                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setTitle(null);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("progress : " + s);
                    Log.d(TAG, "progress : " + s);
                }

                @Override
                public void onStart() {
                    Log.d(TAG, "Started command : ffmpeg " + command);

                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command.toString());
                    progressDialog.dismiss();
                    EditFragment01.runSeekBar.stop();
                    AppUtils.clearFileInfolder(frameTempDir);
                    AppUtils.clearFileInfolder(pathImageExtracted);
                    listFrame.clear();
                    if (arrBitmapCropped != null) {
                        arrBitmapCropped.clear();
                    }
                    ImageManagerAdapter.bitmapArrayList.clear();

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    private void initViews() {
        gifSize = AppCache.getInstance(getApplicationContext()).getMaxSettingGifSize();
        imageManager = findViewById(R.id.image_manager);
        this.pager = (ViewPager) findViewById(R.id.viewpager_edit_gif);
        this.viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        tabLayout = (TabLayout) findViewById(R.id.tablayout_edit_gif);
        btnSaveGif = (TextView) findViewById(R.id.txt_save);
        btnShare = (TextView) findViewById(R.id.txt_share);
        btnShare.setOnClickListener(this);
        btnSaveGif.setOnClickListener(this);
        mGLImageView = (ImageView) findViewById(R.id.img_gif_view);
        mGLImageView.setOnClickListener(this);
        play_pause = (ImageView) findViewById(R.id.img_play_pause);
        play_pause.setOnClickListener(this);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        this.viewPagerAdapter.addFrag(new EditFragment01());

        double miniTimeDelay = ((1 / fps) * 1000);

        this.viewPagerAdapter.addFrag(new EditFragment02((int) miniTimeDelay));
        Log.e("miniDelay_DetailGif", "fps " + fps + " " + miniTimeDelay);
        this.pager.setAdapter(this.viewPagerAdapter);
        this.pager.setCurrentItem(currentPageSelected);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(pager);
        tabLayout.getTabAt(0).setIcon(R.drawable.bg_tab_play);
        tabLayout.getTabAt(1).setIcon(R.drawable.bg_tab_speed);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == FRAGMENT_EDIT_01) {

                } else if (position == FRAGMET_EDIT_02) {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Imagemanagaer
        View imagemanager = findViewById(R.id.image_manager);
        ArrayList<Bitmap> listAdapter = AppUtils.getListThumb(listFrame);
        layoutManager = imagemanager.findViewById(R.id.layout_imagemanager);
        numberFrames = imagemanager.findViewById(R.id.number_frames);
        btnAddImage = imagemanager.findViewById(R.id.txt_imagemanager_addimage);
        btnAddImage.setOnClickListener(this);
        layoutManager.setOnClickListener(this);
        numberFrames.setText(listFrame.size() + " " + getText(R.string.image));
        recyclerView = imagemanager.findViewById(R.id.recyclerview_imagemanager);
        imageManagerAdapter = new ImageManagerAdapter(getApplicationContext(), listAdapter, new OnDragedItem() {
            @Override
            public void onItemDragged(int from, int to) {
                Log.e("draged", "" + from + " to " + to);
            }
        }, new OnListChangeSize() {
            @Override
            public void onListChangeSize(int size) {
                numberFrames.setText(size + " " + getText(R.string.image));
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        recyclerView.setAdapter(imageManagerAdapter);
        recyclerView.setHasFixedSize(true);
        //drag item
        callback = new SimpleItemTouchHelperCallback(imageManagerAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        btnHideImageManager = imagemanager.findViewById(R.id.btn_hide_imagemanager);
        btnHideImageManager.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.txt_imagemanager_addimage:
                Intent intent = new Intent(EditGifActivity.this, SelectImageFormFolderAcivity.class);
                intent.putExtra("from_imagemanager", "from_imagemanager");
                startActivity(intent);
                break;
            case R.id.img_gif_view:
                if (play_pause.getVisibility() == View.VISIBLE) {
                    play_pause.setVisibility(View.GONE);
                } else {
                    play_pause.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.img_play_pause:
                if (isPlayingGif) {
                    isPlayingGif = false;
                    play_pause.setImageResource(R.drawable.ic_btn_play);
                    EditFragment01.runSeekBar.stop();
                } else {
                    isPlayingGif = true;
                    play_pause.setImageResource(R.drawable.ic_btn_pause);
                    EditFragment01.runSeekBar.start();

                }
                break;
            case R.id.btn_hide_imagemanager:
                hideImageManager();
                break;
            case R.id.txt_save:
                saveGif();
                break;
            case R.id.txt_share:
//                Config.shareImageByFile(new File(),this);
                shareDialog(EditGifActivity.this);
                break;
            case R.id.btn_back:
                onBackPressed();
                break;

        }


        if (view instanceof LinearLayout) {
            //layout share
            mBottomSheetDialog.hide();
            switch (view.getId()) {
                case R.id.share_to_facebook:
                    callbackManager = CallbackManager.Factory.create();
                    shareDialog = new ShareDialog(this);
                    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                        @Override
                        public void onSuccess(Sharer.Result result) {

                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FacebookException error) {

                        }
                    });

                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse("http://giphy.com/gifs/3ov9k8ZnTUpHpEmpMc"))
                                .build();
                        shareDialog.show(linkContent);
                    }
                    break;

                case R.id.share_to_twitter:

                    break;

                case R.id.share_to_more:

                    break;
                case R.id.share_to_instagram:

                    break;
                case R.id.share_to_imgur:

                    break;
            }
        }

    }

    private void hideImageManager() {
        Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_dow);
        EditGifActivity.imageManager.startAnimation(slide_down);
        slide_down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                EditGifActivity.imageManager.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void saveGif() {
        File dir = new File(outPutDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        float value = EditFragment02.seekBar.getMax() - (EditFragment02.seekBar.getProgress() - 5); //get position seekbar;
        float delay = value / 100f; //delay beetween frames
        float fps = 1 / delay;
        String outPutFile = outPutDir + "/videotogif_" + System.currentTimeMillis() + ".gif";
        listImageToGif(gifSize, gifSize, Color.TRANSPARENT, fps, outPutFile, EditFragment01.runSeekBar.isRepeat());
    }

    public void shareDialog(Context context) {
        View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.custom_bottomsheet_share, null);
        LinearLayout lnShareToFaceBook = view.findViewById(R.id.share_to_facebook);
        LinearLayout lnShareToTwitter = view.findViewById(R.id.share_to_twitter);
        LinearLayout lnShareToInstagram = view.findViewById(R.id.share_to_instagram);
        LinearLayout lnShareToGiphy = view.findViewById(R.id.share_to_giphy);
        LinearLayout lnShareToImgur = view.findViewById(R.id.share_to_imgur);
        LinearLayout lnShareToMore = view.findViewById(R.id.share_to_more);

        lnShareToTwitter.setOnClickListener(this);
        lnShareToFaceBook.setOnClickListener(this);
        lnShareToInstagram.setOnClickListener(this);
        lnShareToGiphy.setOnClickListener(this);
        lnShareToImgur.setOnClickListener(this);
        lnShareToMore.setOnClickListener(this);

        mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (EditGifActivity.imageManager.getVisibility() == View.VISIBLE) {
            hideImageManager();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getText(R.string.save_change_before_back))
                .setPositiveButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNeutralButton(getText(R.string.back), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AppUtils.clearFileInfolder(frameTempDir);
                        finish();
                    }
                })
                .setNegativeButton(getText(R.string.save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveGif();
                    }
                }).setCancelable(true);

        AlertDialog alert = builder.create();
        alert.show();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
