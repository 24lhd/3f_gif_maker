package com.long3f.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duong3f.module.DuongLog;
import com.duong3f.mvp.imagetogif.SelectImageFormFolderAcivity;
import com.group3f.gifmaker.R;
import com.long3f.Interface.OnDragedItem;
import com.long3f.Interface.OnListChangeSize;
import com.long3f.adapter.ImageManagerAdapter;
import com.long3f.fragment.EditFragment01;
import com.long3f.fragment.EditFragment02;
import com.long3f.helper.SimpleItemTouchHelperCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by LONGG on 10/9/2017.
 */

public class EditGifActivity extends AppCompatActivity {
    public static final int FRAGMENT_EDIT_01 = 0;
    public static final int FRAGMET_EDIT_02 = 1;
    public static String currentPathFile;
    public static ArrayList<String> currentPathFiles;
    public static int indexPathFile;
    private boolean isPlayingGif = true;
    private int currentPageSelected = 0;
    private ViewPager pager;
    private TabLayout tabLayout;
    public static View imageManager;

    private ViewPagerAdapter viewPagerAdapter;
    private ImageView play_pause, btnBack, btnHideImageManager;
    private TextView txtShare, txtSave;


    public static ImageView mGLImageView;
    TextView numberFrames, btnAddImage;
    RelativeLayout layoutManager;
    RecyclerView recyclerView;
    ImageManagerAdapter imageManagerAdapter;
    ItemTouchHelper itemTouchHelper;
    ItemTouchHelper.Callback callback;


    public static ArrayList<String> listFrame = new ArrayList<>();
    String path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES) + "/GifEditor/";
//            Environment.DIRECTORY_PICTURES) + "/GifEditorFlag/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gif);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        initViews();
    }

    private void initViews() {
        imageManager = findViewById(R.id.image_manager);
        this.pager = (ViewPager) findViewById(R.id.viewpager_edit_gif);
        this.viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        tabLayout = (TabLayout) findViewById(R.id.tablayout_edit_gif);
        mGLImageView = (ImageView) findViewById(R.id.img_gif_view);
        mGLImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (play_pause.getVisibility() == View.VISIBLE) {
                    play_pause.setVisibility(View.GONE);
                } else {
                    play_pause.setVisibility(View.VISIBLE);
                }
            }
        });
        play_pause = (ImageView) findViewById(R.id.img_play_pause);
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlayingGif) {
                    isPlayingGif = false;
                    play_pause.setImageResource(R.drawable.ic_btn_play);
                    EditFragment01.runSeekBar.stop();
                } else {
                    isPlayingGif = true;
                    play_pause.setImageResource(R.drawable.ic_btn_pause);
                    EditFragment01.runSeekBar.start();
                }
            }
        });

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path + "out_001.jpg", options);
        mGLImageView.setImageBitmap(bitmap);
        //mGLImageView.setBackgroundResource(R.drawable.ic_logo_facebook);
        this.viewPagerAdapter.addFrag(new EditFragment01());
        this.viewPagerAdapter.addFrag(new EditFragment02());
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
        ArrayList<Bitmap> listAdapter = listFilesForFolder(new File(path));
        layoutManager = imagemanager.findViewById(R.id.layout_imagemanager);
        numberFrames = imagemanager.findViewById(R.id.number_frames);
        btnAddImage = imagemanager.findViewById(R.id.txt_imagemanager_addimage);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditGifActivity.this, SelectImageFormFolderAcivity.class);
                intent.putExtra("from_imagemanager", "from_imagemanager");
                startActivity(intent);

            }
        });

        layoutManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //nothing
                //disable parent click
            }
        });

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
        btnHideImageManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

    }

    private ArrayList<Bitmap> listFilesForFolder(final File folder) {
        ArrayList<Bitmap> arr = new ArrayList<>();
        DuongLog.e(getClass(), folder.getParent());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileEntry.getAbsolutePath()), 90, 90);
                arr.add(resized);
                listFrame.add(fileEntry.getAbsolutePath());
                Log.d("ListFrame", fileEntry.getName());
            }
        }
        return arr;
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
