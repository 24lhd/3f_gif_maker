package com.long3f.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.group3f.gifmaker.R;
import com.long3f.Interface.OnGifPlayListener;
import com.long3f.adapter.DetailGifViewAdapter;
import com.long3f.utils.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import pl.droidsonroids.gif.GifDrawable;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class GifDetailActivity extends Activity {
    private
    DetailGifViewAdapter adapter;
    private ViewPager viewPager;
    private TextView btnCapture, btnDetail;
    private View includeDetail;
    private TextView txtDetailName, txtDetailSize, txtDetailResolution, txtDetailNumberFrame, txtDetailPath, txtDetailDate;
    private LinearLayout btnDelete, btnEdit, btnShare;
    private ProgressDialog progressDialog;
    private FFmpeg ffmpeg;
    private ArrayList<String> arr;
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
        includeDetail = findViewById(R.id.layout_detail);
        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);
        arr = new ArrayList<>();
        arr = i.getStringArrayListExtra("list");
        detailView(arr);
        adapter = new DetailGifViewAdapter(GifDetailActivity.this, arr, GifDetailActivity.this);
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
                final Bitmap bitmap = gifDrawable.getCurrentFrame();
                if (gifDrawable.isPlaying()) {
                    btnCapture.setVisibility(View.GONE);
                } else {
                    btnCapture.setVisibility(View.VISIBLE);
                }

                btnCapture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                adapter.setOnPlayListener(new OnGifPlayListener() {
                    @Override
                    public void onGifState(GifDrawable gifDrawable, String gifPath) {
                        super.onGifState(gifDrawable, gifPath);
                        final Bitmap bitmap = gifDrawable.getCurrentFrame();
                        if (gifDrawable.isPlaying()) {
                            btnCapture.setVisibility(View.GONE);
                        } else {
                            btnCapture.setVisibility(View.VISIBLE);
                        }
                        btnCapture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
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
                extractImagesFromGif(arr.get(viewPager.getCurrentItem()));
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

            }
        });
    }

        private void extractImagesFromGif(String inputPath) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );

        String outFile = "out_%03d.jpg";
        File dir = new File(moviesDir, "GifEditor");
        if (!dir.exists()){
            dir.mkdir();
        }else {
            dir.delete();
            dir.mkdir();
        }

        File dest = new File(dir, outFile);
        String[] complexCommand = {
                "-i", inputPath,
                "-vsync", "0",
                dest.getAbsolutePath()};
  /*   Remove -r 1 if you want to extract all video frames as images from the specified time duration.*/
        execFFmpegBinary(complexCommand,dir.getAbsolutePath());
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
                        Intent intent = new Intent(GifDetailActivity.this, EditGifActivity.class);
                        intent.putExtra("filepath", filePath);
                        startActivity(intent);
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
                    progressDialog.dismiss();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
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
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GifMaker";
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
//                        } else if (position == 0 && adapter._imagePaths.size() >= 1) {
//                            DeleteAndScanFile(getApplicationContext(), adapter._imagePaths.get(position));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
