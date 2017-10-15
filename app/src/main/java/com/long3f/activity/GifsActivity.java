package com.long3f.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duong3f.config.Config;
import com.duong3f.crop.activity.TrimmerActivity;
import com.duong3f.crop.videoTrimmer.utils.FileUtils;
import com.duong3f.mvp.imagetogif.SelectImageFormFolderAcivity;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.group3f.gifmaker.R;
import com.long3f.adapter.GifsPagerAdapter;
import com.long3f.fragment.GIPHYFragment;
import com.long3f.fragment.YourGifsFragment;
import com.long3f.utils.AppCache;
import com.long3f.utils.AppUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static android.os.Environment.getExternalStorageDirectory;
import static com.duong3f.config.Config.EXTRA_VIDEO_PATH;
import static com.duong3f.config.Config.REQUEST_STORAGE_READ_ACCESS_PERMISSION;
import static com.duong3f.config.Config.REQUEST_VIDEO_TRIMMER;
import static com.duong3f.config.Config.VIDEO_CROPPED;
import static com.duong3f.config.Config.VIDEO_TOTAL_DURATION;
import static com.long3f.activity.EditGifActivity.fps;
import static com.long3f.activity.EditGifActivity.listFrame;
import static com.long3f.activity.EditGifActivity.pathImageExtracted;

public class GifsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST_CODE_RECODER_VIDEO = 1100;
    private static final int VIDEO_PICK_CODE = 1101;
    private static final String TAG = "FFMPEG";
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 1000;
    ViewPager pager;
    TabLayout tabLayout;
    TextView txtCreate, txtSetting;
    private FFmpeg ffmpeg;
    private ArrayList<String> list = new ArrayList<>();
    private ProgressDialog progressDialog;

    private Dialog mBottomSheetDialog;
    private EditText edInputLink;
    private String rootDir = Environment.getExternalStorageDirectory()
            + File.separator + "GifMaker/.data";
    private String fileFromLinkVideo = "fileFromLinkVideo.mp4";
    private String fileFromLinkGif = "fileFromLinkGif.gif";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gifs);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        txtCreate = (TextView) findViewById(R.id.txt_create);
        txtSetting = (TextView) findViewById(R.id.txt_settings);
        txtSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GifsActivity.this, SettingActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
        prequestPermission();
        loadFFMpegBinary();
        tabLayout.setupWithViewPager(pager);
        txtCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetCreateNew();
            }
        });
    }


    private void setupViewPager(ViewPager viewPager) {
        GifsPagerAdapter adapter = new GifsPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new YourGifsFragment(true), getText(R.string.yourgifs).toString());
        adapter.addFrag(new GIPHYFragment(), getText(R.string.gifhy).toString());
        adapter.addFrag(new YourGifsFragment(false), getText(R.string.other_gif).toString());
        viewPager.setAdapter(adapter);
    }


    private void BottomSheetCreateNew() {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_bottomsheet, null);
        LinearLayout lnFromCamera = view.findViewById(R.id.ln_from_camera);
        LinearLayout lnFromVideo = view.findViewById(R.id.ln_from_video);
        LinearLayout lnFromImages = view.findViewById(R.id.ln_from_mutil_image);
        LinearLayout lnFromRecoder = view.findViewById(R.id.ln_from_recorder);
        LinearLayout lnFromGifUrl = view.findViewById(R.id.ln_from_gif_url);
        LinearLayout lnFromVideoUrl = view.findViewById(R.id.ln_from_url_video);

        lnFromVideo.setOnClickListener(this);
        lnFromCamera.setOnClickListener(this);
        lnFromImages.setOnClickListener(this);
        lnFromRecoder.setOnClickListener(this);
        lnFromGifUrl.setOnClickListener(this);
        lnFromVideoUrl.setOnClickListener(this);

        mBottomSheetDialog = new Dialog(GifsActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

    }


    @Override
    public void onClick(View view) {
        mBottomSheetDialog.hide();
        switch (view.getId()) {
            case R.id.ln_from_video:
                PickVideo(GifsActivity.this);
                break;
            case R.id.ln_from_camera:
                recoderVideoo();
                break;
            case R.id.ln_from_url_video:
                linkVideoToGif();
                break;
            case R.id.ln_from_mutil_image:
                startAcitivySelectImage();
                break;
            case R.id.ln_from_gif_url:
                linkURLToGif();
                break;
        }
    }

    private void linkURLToGif() {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_dialog_input_link, null);
        Button btnOk = view.findViewById(R.id.btn_ok);
        edInputLink = view.findViewById(R.id.ed_input_link);
        final Dialog mBottomSheetDialog = new Dialog(GifsActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
        final executeFromLinkGif downloadGif = new executeFromLinkGif();
        edInputLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.getWindow().setGravity(Gravity.FILL_VERTICAL);
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                downloadGif.execute(edInputLink.getText().toString() + "");
            }
        });
    }

    private void startAcitivySelectImage() {
        startActivityForResult(new Intent(this, SelectImageFormFolderAcivity.class), Config.REQUEST_CODE_GET_MULTI_IMAGE);
    }

    private void linkVideoToGif() {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_dialog_input_link, null);
        Button btnOk = view.findViewById(R.id.btn_ok);
        edInputLink = view.findViewById(R.id.ed_input_link);
        final Dialog mBottomSheetDialog = new Dialog(GifsActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
        final exeCuteFromLinkVideo downloadVideo = new exeCuteFromLinkVideo();
        edInputLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.getWindow().setGravity(Gravity.FILL_VERTICAL);
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                downloadVideo.execute(edInputLink.getText().toString() + "");
            }
        });
    }

    private void recoderVideoo() {
//        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takeVideoIntent,
//                    CAMERA_REQUEST_CODE_RECODER_VIDEO);
//        }
        openVideoCapture();
    }

    private void pickFromGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_read_storage_rationale), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setTypeAndNormalize("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), REQUEST_VIDEO_TRIMMER);
        }
    }

    public void PickVideo(Context context) {
        pickFromGallery();
//        CharSequence string = context.getResources().getString(R.string.choose_a_video_to_use);
//        Intent intent = new Intent("android.intent.action.PICK");
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "vnd.android.cursor.dir/image");
//        intent.setType("video/*");
//        try {
//            ((Activity) context).startActivityForResult(Intent.createChooser(intent, string), VIDEO_PICK_CODE);
//        } catch (ActivityNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(this, TrimmerActivity.class);
        intent.putExtra(EXTRA_VIDEO_PATH, FileUtils.getPath(this, uri));
        intent.putExtra(VIDEO_TOTAL_DURATION, getMediaDuration(uri));
        startActivityForResult(intent, VIDEO_CROPPED);
    }

    private int getMediaDuration(Uri uriOfFile) {
        MediaPlayer mp = MediaPlayer.create(this, uriOfFile);
        if(mp!=null){
            return mp.getDuration();
        }
        return 10;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_TRIMMER) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startTrimActivity(selectedUri);
                } else {
                    Toast.makeText(this, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == VIDEO_PICK_CODE) {
            if (requestCode == Config.VIDEO_CROPPED) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    Log.e("onActivityResult: ", "Pick " + selectedUri.getPath());
                    MediaPlayer mp = MediaPlayer.create(this, selectedUri);
                    long duration = mp.getDuration();
                    mp.release();
                    EditGifActivity.fps = getSettingFPS(duration);
                    videoToListImageByPath(selectedUri.getPath(), 0, duration, fps);
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAMERA_REQUEST_CODE_RECODER_VIDEO) {
                final Uri selectedUri = data.getData();
                MediaPlayer mp = MediaPlayer.create(this, selectedUri);
                long duration = mp.getDuration();
                mp.release();
                EditGifActivity.fps = getSettingFPS(duration);
                Log.e("durationVideo", duration + " - fps " + fps);
                videoToListImage(selectedUri, 0, duration, fps);
            }
        }

    }

    private double getSettingFPS(long duration) {
        long maxSettingFrame = AppCache.getInstance(getApplicationContext()).getMaxSettingFrame();
        double fps = maxSettingFrame / (duration / 1000f);
        return fps > 20 ? 20 : fps;
    }

    private void videoToListImage(Uri selectedVideoUri, long startMs, long endMs, double fps) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );
        String filePrefix = "extract_picture";
        String fileExtn = ".png";

        String yourRealPath = getPath(GifsActivity.this, selectedVideoUri);
        File dir = new File(moviesDir, "GifEditor");
        if (dir.exists()) {
            AppUtils.clearFileInfolder(dir.getAbsolutePath());
        } else {
            dir.mkdir();
        }
        File dest = new File(dir, filePrefix + "%03d" + fileExtn);
        String[] complexCommand = {"-y", "-i", yourRealPath, "-an", "-r", String.valueOf(fps), "-ss", "" + startMs / 1000, "-t", "" + (endMs - startMs) / 1000, dest.getAbsolutePath()};
        execFFmpegBinary(complexCommand);
    }

    private void videoToListImageByPath(String path, long startMs, long endMs, double fps) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );
        String filePrefix = "extract_picture";
        String fileExtn = ".png";

//        String yourRealPath = getPath(GifsActivity.this, path);
//        DuongLog.e(getClass(), " yourRealPath" + yourRealPath);
        File dir = new File(moviesDir, "GifEditor");
        if (dir.exists()) {
            AppUtils.clearFileInfolder(dir.getAbsolutePath());
        } else {
            dir.mkdir();
        }
        File dest = new File(dir, filePrefix + "%03d" + fileExtn);
        String[] complexCommand = {"-y", "-i", path, "-an", "-r", String.valueOf(fps), "-ss", "" + startMs / 1000, "-t", "" + (endMs - startMs) / 1000, dest.getAbsolutePath()};
        execFFmpegBinary(complexCommand);
    }

    private void extractImagesFromGif(String inputPath) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );

        String outFile = "extract_picture_%03d.png";
        File dir = new File(moviesDir, "GifEditor");
        if (dir.exists()) {
            AppUtils.clearFileInfolder(dir.getAbsolutePath());
        } else {
            dir.mkdir();
        }

        File dest = new File(dir, outFile);
        String[] complexCommand = {
                "-i", inputPath,
                "-vsync", "0",
                dest.getAbsolutePath()};
        execFFmpegBinary(complexCommand);
    }

    private void executeGifImageCommand(Uri selectedVideoUri, long timeStart, long timeFinish, double fps) {
        String savePath = AppCache.getInstance(getApplicationContext()).getSavePath();
        String dirGallery = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath() + "/VideoToGifa";
        if (savePath == null || savePath.equals("")) {
            AppCache.getInstance(getApplicationContext()).setSavePath(dirGallery);
        }
        File saveDir = new File(AppCache.getInstance(getApplicationContext()).getSavePath());
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        File saveGif = new File(saveDir, "videotogif_" + System.currentTimeMillis() + ".gif");
        String str = getPath(this, selectedVideoUri);
        String filePath = saveGif.getAbsolutePath();
        execFFmpegBinary(new String[]{"-ss", "" + timeStart / 1000, "-t", "" + (timeFinish - timeStart) / 1000, "-y", "-i", str, "-vf", "scale=500:-1", "-r", String.valueOf(fps), filePath});
    }

    private String getPath(final Context context, final Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();

        }
        return null;
    }

    private String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
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
                    Log.d(TAG, "ffmpeg : correct Loaded");

                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        } catch (Exception e) {
        }
    }
    private void openVideoCapture() {
        Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(videoCapture, REQUEST_VIDEO_TRIMMER);
    }
    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(GifsActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Not Supported")
                .setMessage("Device Not Supported")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GifsActivity.this.finish();
                    }
                })
                .create()
                .show();

    }

    private void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.d(TAG, "FAILED with output : " + s);
                    progressDialog.dismiss();
                    (new File(rootDir, fileFromLinkVideo)).delete();
                    (new File(rootDir, fileFromLinkGif)).delete();
                }

                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "SUCCESS with output : " + s);
                    cropAndAddBitmapFromFolderToListFrame();
                    (new File(rootDir, fileFromLinkVideo)).delete();
                    (new File(rootDir, fileFromLinkGif)).delete();
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
                    progressDialog = new ProgressDialog(GifsActivity.this);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command.toString());
                    (new File(rootDir, fileFromLinkVideo)).delete();
                    (new File(rootDir, fileFromLinkGif)).delete();

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getText(R.string.do_you_want_exit))
                .setCancelable(false)
                .setPositiveButton(getText(R.string.exit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).setCancelable(true);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void cropAndAddBitmapFromFolderToListFrame() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                int gifSize = AppCache.getInstance(getApplicationContext()).getMaxSettingGifSize();
                listFrame = AppUtils.cropListImageFromFolder(new File(pathImageExtracted), gifSize, gifSize, Color.TRANSPARENT);
                return null;
            }

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Waiting...");
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                startActivity(new Intent(GifsActivity.this, EditGifActivity.class));
                progressDialog.dismiss();

            }

            @Override
            protected void onProgressUpdate(Void... values) {

            }
        }.execute();
    }

    private void prequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (ActivityCompat.checkSelfPermission(GifsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        getString(R.string.no_permission),
                        REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
            } else {
                setupViewPager(pager);
            }

        } else {
            setupViewPager(pager);
        }

    }

    protected void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(GifsActivity.this, permission)) {
            ActivityCompat.requestPermissions(GifsActivity.this, new String[]{permission}, requestCode);
        } else {
            showAlertDialog(
                    getString(R.string.error_null_cursor)
                    , rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(GifsActivity.this,
                                    new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(GifsActivity.this,
                                    new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.cancel)
            );

        }
    }


    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GifsActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        Dialog dialog = builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GifsActivity.REQUEST_STORAGE_WRITE_ACCESS_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupViewPager(pager);
                    break;
                } else {
                    finish();
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.clearFileInfolder(Environment.getExternalStorageDirectory()
                + File.separator + "GifMaker/.data");
    }

    private class exeCuteFromLinkVideo extends AsyncTask<String, String, String> {
        ProgressDialog PD;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PD = new ProgressDialog(GifsActivity.this);
            PD.setMessage(getText(R.string.downloading));
            PD.setIndeterminate(true);
            PD.setMax(100);
            PD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            PD.setCancelable(false);
            PD.show();
        }

        @Override
        protected String doInBackground(String... param) {
            int count;
            try {

                File rootFile = new File(rootDir);
                if (rootFile.exists()) {
                    AppUtils.clearFileInfolder(rootFile.getAbsolutePath());
                } else {
                    rootFile.mkdir();
                }

                URL url = new URL(param[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                // Output stream to write file
//                String split[] = param[0].split(".");
//                String ex = split[split.length - 1];
                OutputStream output = new FileOutputStream(new File(rootDir, fileFromLinkVideo));
                Log.e("Download file", rootDir + "/" + fileFromLinkGif);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    // writing data to file
                    output.write(data, 0, count);
                }
                // flushing output
                output.flush();
                // closing streams
                output.close();
                input.close();


            } catch (InterruptedIOException e) {

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                publishProgress("" + -1);
                return null;

            }
            return rootDir + "/" + fileFromLinkVideo;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            if (Integer.parseInt(progress[0]) == -1) {
                PD.dismiss();
                Toast.makeText(GifsActivity.this, getText(R.string.not_support_this_video), Toast.LENGTH_SHORT).show();
                return;
            }
            PD.setProgress(Integer.parseInt(progress[0]));
            PD.setIndeterminate(false);
        }

        @Override
        protected void onPostExecute(String filePath) {
            // dismiss the dialog after the file was downloaded
            PD.dismiss();
            if (filePath != null) {
                Uri videoUri = Uri.fromFile(new File(filePath));
                MediaPlayer mp = MediaPlayer.create(GifsActivity.this, videoUri);
                long duration = mp.getDuration();
                mp.release();
                EditGifActivity.fps = getSettingFPS(duration);
                Log.e("durationVideo", duration + " - fps " + fps);
                videoToListImage(videoUri, 0, duration, fps);
            }
        }


    }

    private class executeFromLinkGif extends AsyncTask<String, String, String> {
        ProgressDialog PD;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PD = new ProgressDialog(GifsActivity.this);
            PD.setMessage(getText(R.string.downloading));
            PD.setIndeterminate(true);
            PD.setMax(100);
            PD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            PD.setCancelable(false);
            PD.show();
        }

        @Override
        protected String doInBackground(String... param) {
            int count;

            try {

                File rootFile = new File(rootDir);
                rootFile.mkdir();

                URL url = new URL(param[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                // Output stream to write file
                OutputStream output = new FileOutputStream(new File(rootDir, fileFromLinkGif));
                Log.e("Download file", rootDir + "/" + fileFromLinkGif);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    // writing data to file
                    output.write(data, 0, count);
                }
                // flushing output
                output.flush();
                // closing streams
                output.close();
                input.close();


            } catch (InterruptedIOException e) {

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                publishProgress("" + -1);
                return null;

            }
            return rootDir + "/" + fileFromLinkGif;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            if (Integer.parseInt(progress[0]) == -1) {
                PD.dismiss();
                Toast.makeText(GifsActivity.this, getText(R.string.decode_fail), Toast.LENGTH_SHORT).show();
                return;
            }
            PD.setProgress(Integer.parseInt(progress[0]));
            PD.setIndeterminate(false);
        }

        @Override
        protected void onPostExecute(String filePath) {
            // dismiss the dialog after the file was downloaded
            PD.dismiss();
            if (filePath != null) {
                extractImagesFromGif(filePath);
            }
        }


    }
}
