package com.long3f.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duong3f.config.Config;
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
import com.long3f.Interface.OnGifPlayListener;
import com.long3f.adapter.DetailGifViewAdapter;
import com.long3f.adapter.GiphyViewAdapter;
import com.long3f.fragment.GIPHYFragment;
import com.long3f.utils.AppCache;
import com.long3f.utils.AppUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import pl.droidsonroids.gif.GifDrawable;

import static android.os.Environment.getExternalStorageDirectory;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.long3f.activity.EditGifActivity.fps;
import static com.long3f.activity.EditGifActivity.pathImageExtracted;

public class GifDetailActivity extends Activity implements View.OnClickListener {
    private
    DetailGifViewAdapter adapter;
    GiphyViewAdapter giphyViewAdapter;
    private ViewPager viewPager;
    private TextView btnCapture, btnDetail;
    private View includeDetail;
    private TextView txtDetailName, txtDetailSize, txtDetailResolution, txtDetailNumberFrame, txtDetailPath, txtDetailDate;
    private LinearLayout btnDelete, btnEdit, btnShare;
    private ProgressDialog progressDialog;
    private FFmpeg ffmpeg;
    private ArrayList<String> arrYourGif;
    private Dialog mBottomSheetDialog;
    private String urlShare = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_detail);
        viewPager = findViewById(R.id.pager);
        btnCapture = findViewById(R.id.capture_image);
        btnDetail = findViewById(R.id.btn_details_gif);
        btnDelete = findViewById(R.id.ln_delete_gif);
        btnEdit = findViewById(R.id.ln_edit_gif);
        btnShare = findViewById(R.id.ln_share_gif);
        loadFFMpegBinary();

        Intent intent = getIntent();
        String flag = intent.getStringExtra("flag") + "";
        if (flag.equals("yourgif")) {
            initYourGifView();
        } else {
            initGiphyView();
        }
    }

    private void initGiphyView() {
        btnDetail.setVisibility(View.INVISIBLE);
        btnDelete.setVisibility(View.GONE);
        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);
        giphyViewAdapter = new GiphyViewAdapter(this, GIPHYFragment.listUrl);
        viewPager.setAdapter(giphyViewAdapter);
        viewPager.setCurrentItem(position);
        urlShare = GIPHYFragment.listUrl.get(position).getDownloadUrl();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                createDialogShare(GifDetailActivity.this);
            }
        });

        giphyViewAdapter.setOnPlayListener(new OnGifPlayListener() {
            @Override
            public void onGifState(final GifDrawable gifDrawable, String gifPath) {
                if (gifDrawable != null) {
                    if (gifDrawable.isPlaying()) {
                        btnCapture.setVisibility(View.GONE);
                    } else {
                        btnCapture.setVisibility(View.VISIBLE);
                    }

                    btnCapture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bitmap bitmap = gifDrawable.getCurrentFrame();
                            captureCurrentFrame(bitmap);
                        }
                    });
                }
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                btnCapture.setVisibility(View.GONE);
                urlShare = GIPHYFragment.listUrl.get(viewPager.getCurrentItem()).getDownloadUrl();
                giphyViewAdapter.setOnPlayListener(new OnGifPlayListener() {
                    @Override
                    public void onGifState(final GifDrawable gifDrawable, String gifPath) {
                        if (gifDrawable != null) {
                            final Bitmap bitmap = gifDrawable.getCurrentFrame();
                            if (gifDrawable.isPlaying()) {
                                btnCapture.setVisibility(View.GONE);
                            } else {
                                btnCapture.setVisibility(View.VISIBLE);
                            }
                            btnCapture.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Bitmap bitmap = gifDrawable.getCurrentFrame();
                                    captureCurrentFrame(bitmap);
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rootDir = Environment.getExternalStorageDirectory()
                        + File.separator + "GifMaker/.data";
                String gifpath = rootDir + "/" + GIPHYFragment.listUrl.get(viewPager.getCurrentItem()).getId() + ".gif";
                File file = new File(gifpath);
                if (file.exists()) {
                    extractImagesFromGif(gifpath);
                } else {
                    new executeFromLinkGif(rootDir
                            , GIPHYFragment.listUrl.get(viewPager.getCurrentItem()).getId() + ".gif")
                            .execute(GIPHYFragment.listUrl.get(viewPager.getCurrentItem()).getDownloadUrl());
                }
            }
        });

    }

    private void initYourGifView() {
        includeDetail = findViewById(R.id.layout_detail);
        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);
        arrYourGif = new ArrayList<>();
        arrYourGif = i.getStringArrayListExtra("list");
        detailView(arrYourGif);
        adapter = new DetailGifViewAdapter(GifDetailActivity.this, arrYourGif);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        adapter.setOnPlayListener(new OnGifPlayListener() {
            @Override
            public void onGifState(final GifDrawable gifDrawable, String gifPath) {
                super.onGifState(gifDrawable, gifPath);
                if (gifDrawable.isPlaying()) {
                    btnCapture.setVisibility(View.GONE);
                } else {
                    btnCapture.setVisibility(View.VISIBLE);
                }

                btnCapture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bitmap bitmap = gifDrawable.getCurrentFrame();
                        captureCurrentFrame(bitmap);
                    }
                });

            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                btnCapture.setVisibility(View.GONE);
                adapter.setOnPlayListener(new OnGifPlayListener() {
                    @Override
                    public void onGifState(final GifDrawable gifDrawable, String gifPath) {
                        super.onGifState(gifDrawable, gifPath);
                        if (gifDrawable.isPlaying()) {
                            btnCapture.setVisibility(View.GONE);
                        } else {
                            btnCapture.setVisibility(View.VISIBLE);
                        }
                        btnCapture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bitmap bitmap = gifDrawable.getCurrentFrame();
                                captureCurrentFrame(bitmap);
                            }
                        });
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractImagesFromGif(arrYourGif.get(viewPager.getCurrentItem()));
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteGif();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.shareImageByFile(new File(arrYourGif.get(viewPager.getCurrentItem())), GifDetailActivity.this);
//                createDialogShare(GifDetailActivity.this);
            }
        });
    }

    private void extractImagesFromGif(String inputPath) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );
        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(inputPath);
            fps = gifDrawable.getNumberOfFrames() / (gifDrawable.getDuration() / 1000f);
            Log.e("fps_DetailGif", (gifDrawable.getDuration() / 1000f) + " " + gifDrawable.getNumberOfFrames() + " " + fps);
        } catch (IOException e) {
            e.printStackTrace();
        }


        String outFile = "out_%03d.png";
        File dir = new File(moviesDir, "GifEditor");
        if (dir.exists()) {
            AppUtils.clearFileInfolder(dir.getAbsolutePath());
        } else {
            dir.mkdirs();
        }

        File dest = new File(dir, outFile);
        String[] complexCommand = {
                "-i", inputPath,
                "-vsync", "0",
                dest.getAbsolutePath()};
        execFFmpegBinary(complexCommand, dir.getAbsolutePath());
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

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(GifDetailActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Not Supported")
                .setMessage("Device Not Supported")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GifDetailActivity.this.finish();
                    }
                })
                .create()
                .show();

    }

    private void execFFmpegBinary(final String[] command, final String filePath) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.d(TAG, "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "SUCCESS with output : " + s);
                    cropAndAddBitmapFromFolderToListFrame();
                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("progress : " + s);
                    Log.d(TAG, "progress : " + s);
                }

                @Override
                public void onStart() {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog = new ProgressDialog(GifDetailActivity.this);
                    progressDialog.setTitle(null);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command.toString());

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    private void cropAndAddBitmapFromFolderToListFrame() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                int gifSize = AppCache.getInstance(getApplicationContext()).getMaxSettingGifSize();
                EditGifActivity.listFrame = AppUtils.cropListImageFromFolder(new File(pathImageExtracted), gifSize, gifSize, Color.TRANSPARENT);

                return null;
            }

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Waiting...");
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                startActivity(new Intent(GifDetailActivity.this, EditGifActivity.class));
                progressDialog.dismiss();
                finish();
            }

            @Override
            protected void onProgressUpdate(Void... values) {

            }
        }.execute();
    }

    private void detailView(final ArrayList<String> arr) {
        txtDetailName = includeDetail.findViewById(R.id.gif_detail_name);
        txtDetailSize = includeDetail.findViewById(R.id.gif_detail_size);
        txtDetailResolution = includeDetail.findViewById(R.id.gif_detail_resolution);
        txtDetailNumberFrame = includeDetail.findViewById(R.id.gif_detail_number_frame);
        txtDetailDate = includeDetail.findViewById(R.id.gif_detail_date);
        txtDetailPath = includeDetail.findViewById(R.id.gif_detail_path);


        ImageView imgBack = includeDetail.findViewById(R.id.back_detail);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                includeDetail.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.layout_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                includeDetail.setVisibility(View.GONE);
            }
        });

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                includeDetail.setVisibility(View.VISIBLE);
                File file = new File(arr.get(viewPager.getCurrentItem()));
                GifDrawable gifDrawable = null;
                try {
                    gifDrawable = new GifDrawable(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (gifDrawable != null) {
                    txtDetailName.setText(getText(R.string.title).toString() + " " + file.getName());
                    txtDetailSize.setText(getText(R.string.size).toString() + " " + String.valueOf(new DecimalFormat("#.##").format(file.length() / 1024f / 1024f)) + " MB");
                    txtDetailResolution.setText(getText(R.string.resolution).toString() + " " + gifDrawable.getIntrinsicWidth() + "x" + gifDrawable.getIntrinsicHeight());
                    txtDetailNumberFrame.setText(getText(R.string.number_of_frame).toString() + " " + gifDrawable.getNumberOfFrames());
                    txtDetailDate.setText(getText(R.string.date_dot).toString() + " " + (new Date(file.lastModified()).toString()));
                    txtDetailPath.setText(getText(R.string.path).toString() + " " + file.getParent());
                }

            }
        });
    }

    private void captureCurrentFrame(Bitmap bitmap) {
        final String dirPath = getExternalStorageDirectory().getAbsolutePath() + "/GifMaker";
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            Toast.makeText(this, getText(R.string.save_success) + " " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getText(R.string.error_null_cursor), Toast.LENGTH_LONG).show();

        }
    }

    void deleteGif() {
        final int position = viewPager.getCurrentItem();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getText(R.string.are_you_sure_delete_this_item))
                .setCancelable(false)
                .setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if ((position < 0) || (position >= adapter._imagePaths.size()) || (adapter._imagePaths.size() <= 1)) {
                            DeleteAndScanFile(getApplicationContext(), adapter._imagePaths.get(position));
                            adapter.removeView(position);
                            onBackPressed();
//                        } else if (position == 0 && adapter.giphyModels.size() >= 1) {
//                            DeleteAndScanFile(getApplicationContext(), adapter.giphyModels.get(position));
//                            adapter.removeView(position);
//                            viewPager.setCurrentItem(position);
                        } else {
                            DeleteAndScanFile(getApplicationContext(), adapter._imagePaths.get(position));
                            adapter.removeView(position);
                            viewPager.setCurrentItem(position);
                        }
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

    private void DeleteAndScanFile(final Context context, String path) {
        final File fi = new File(path);
        fi.delete();
        AppUtils.deleteImageToGallery(path, context);
        Log.e("file Deleted :", fi.getPath());
    }


    public void createDialogShare(Context context) {
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

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    public void onClick(View view) {
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
                            .setContentUrl(Uri.parse(urlShare))
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private class executeFromLinkGif extends AsyncTask<String, String, String> {
        ProgressDialog PD;
        String pathDir;
        String fileFromLinkGif;

        public executeFromLinkGif(String dir, String filename) {
            pathDir = dir;
            fileFromLinkGif = filename;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PD = new ProgressDialog(GifDetailActivity.this);
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

                File rootFile = new File(pathDir);
                rootFile.mkdir();

                URL url = new URL(param[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                // Output stream to write file
                OutputStream output = new FileOutputStream(new File(pathDir, fileFromLinkGif));
                Log.e("Download file", pathDir + "/" + fileFromLinkGif);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
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
            return pathDir + "/" + fileFromLinkGif;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            if (Integer.parseInt(progress[0]) == -1) {
                PD.dismiss();
                Toast.makeText(GifDetailActivity.this, getText(R.string.decode_fail), Toast.LENGTH_SHORT).show();
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
