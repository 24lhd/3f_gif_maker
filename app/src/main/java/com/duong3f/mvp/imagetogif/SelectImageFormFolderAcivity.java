package com.duong3f.mvp.imagetogif;

import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duong3f.adaptor.AdaptorFolderImageListView;
import com.duong3f.adaptor.AdaptorImageSelected;
import com.duong3f.adaptor.AdaptorItemImageInFolder;
import com.duong3f.config.Config;
import com.duong3f.obj.AlbumImage;
import com.duong3f.obj.Image;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.group3f.gifmaker.R;
import com.long3f.activity.EditGifActivity;
import com.long3f.adapter.ImageManagerAdapter;
import com.long3f.utils.AppUtils;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by d on 9/21/2017.
 */

public class SelectImageFormFolderAcivity extends AppCompatActivity implements SelectImageFormFolderFace {
    @Bind(R.id.layout_select_picture_from_store_rcv_fodler_image)
    ListView rcvFolderImage;
    @Bind(R.id.layout_select_picture_from_store_rcv_image_in_folder)
    GridView rcvImageInFolder;
    @Bind(R.id.layout_select_picture_from_store_rcv_image_select)
    TwoWayView rcvImageSelected;
    @Bind(R.id.layout_select_picture_from_store_btn_done)
    TextView txtNext;
    //    ListView rcvImageSelected;
    //    @Bind(R.id.layout_select_picture_from_store_imv_back)
//    ImageView backToMain;
    SelectImageFormFolderHandling selectImageFormFolderPresenter;
    ArrayList<AlbumImage> folderImages;
    ArrayList<Image> imagesInFolder;
    ArrayList<Image> imagesSelect;
    private AdaptorImageSelected adaptorImageSelected;
    private AdaptorItemImageInFolder adaptorItemImageInFolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_picture_from_store);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        imagesSelect = new ArrayList<>();
        selectImageFormFolderPresenter = new SelectImageFormFolderHandlingImpl(this);

    }

    @Override
    public void hideButtomActionBar() {
        txtNext.setVisibility(View.GONE);
    }

    @Override
    public void showButtomActionBar() {
        txtNext.setVisibility(View.VISIBLE);
    }

    @Override
    public void setListFolderImage(ArrayList<AlbumImage> imageAlba) {
        this.folderImages = imageAlba;
    }

    @Override
    public void showListFolderImage() {
        rcvFolderImage.setAdapter(new AdaptorFolderImageListView(this, android.R.layout.simple_list_item_1, folderImages));
        rcvFolderImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlbumImage albumImageCick = folderImages.get(i);
                selectImageFormFolderPresenter.setListImageInFolder(albumImageCick);
            }
        });
    }

    @Override
    public void showListImageSelect() {
        adaptorImageSelected = new AdaptorImageSelected(this, android.R.layout.simple_list_item_1, imagesSelect);
        rcvImageSelected.setAdapter(adaptorImageSelected);
        rcvImageSelected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                imagesSelect.remove(i);
                view.setTag(false);
//                ImageView item_image_view_imv_is_select = view.findViewById(R.id.item_image_view_imv_is_select);
//                item_image_view_imv_is_select.setVisibility(View.GONE);
                adaptorImageSelected.notifyDataSetChanged();
                adaptorItemImageInFolder.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void setListImageFromFolder(final ArrayList<Image> images) {
        this.imagesInFolder = images;
        adaptorItemImageInFolder = new AdaptorItemImageInFolder(this, android.R.layout.simple_list_item_1, images);
        rcvImageInFolder.setAdapter(adaptorItemImageInFolder);
        rcvImageInFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                AlbumImage albumImageCick = folderImages.get(i);
//                selectImageFormFolderPresenter.setListImageInFolder(albumImageCick);
                ImageView image_view_imv_is_select = view.findViewById(R.id.item_image_view_imv_is_select);
                boolean isChoose;
                try {
                    isChoose = (boolean) view.getTag();
                } catch (Exception e) {
                    view.setTag(false);
                    isChoose = (boolean) view.getTag();
                }
                Image imageSelect = imagesInFolder.get(i);
                if (isChoose) {
                    image_view_imv_is_select.setVisibility(View.GONE);
                    imagesSelect.remove(imageSelect);
                    view.setTag(false);
                } else {
                    imagesSelect.add(imageSelect);
                    image_view_imv_is_select.setVisibility(View.VISIBLE);
                    view.setTag(true);
                }
                if (imagesSelect.size() > 0) showButtomActionBar();
                else hideButtomActionBar();
                adaptorImageSelected.notifyDataSetChanged();
                rcvImageSelected.setSelection(adaptorImageSelected.getCount() - 1);
                setTextTitleActionBar("");

            }
        });

    }

    @Override
    public void setListImageSelcet() {

    }

    @Override
    public void setTextTitleActionBar(String str) {

    }

    @Override
    public void addListImageSelcet(Image image) {

    }

    @OnClick(R.id.layout_select_picture_from_store_btn_done)
    public void selectDone() {
        if(getIntent().getStringExtra("from_imagemanager")!= null && getIntent().getStringExtra("from_imagemanager").equals("from_imagemanager")){
            for (Image image : imagesSelect) {
                    EditGifActivity.listFrame.add(image.getPath());
                    ImageManagerAdapter.bitmapArrayList.add(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(image.getPath()), 90, 90));
            }
            ImageManagerAdapter.onListChangeSize.onListChangeSize(EditGifActivity.listFrame.size());
            finish();
            return;
        }

        FFmpeg fFmpeg = Config.initFFmpeg(this);
        new Thread() {
            @Override
            public void run() {
                for (Image image : imagesSelect) {
//            DuongLog.loge(getClass(), image.toString());
                    try {
                        File newFile = new File(Environment.getExternalStorageDirectory().getPath() +
                                "/GifFlag/out_" + String.format("%03d", (imagesSelect.indexOf(image) + 1)) + ".jpg");
                        if (!newFile.exists()) {
                            newFile.getParentFile().mkdirs();
                            newFile.createNewFile();
                        }
                        Config.copy(new File(image.getPath()), newFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                String arr[] = {"-y","-framerate", "1",
                        "-i", Environment.getExternalStorageDirectory().getPath() + "/GifFlag/" + "out_%03d.jpg",
                        "-vf","scale=500:-1",
                        Environment.getExternalStorageDirectory().getPath() + "/GifFlag/" + "gifout.gif"};

                //-framerate là fps , a bị bug ko mở đc là do ảnh định dạng gốc là jpg, a đổi tên thaafnh png nên lỗi
                Config.runFFmpegCommand(arr,SelectImageFormFolderAcivity.this);
                AppUtils.addImageToGallery(Environment.getExternalStorageDirectory().getPath() + "/GifFlag/" + "gifout.gif",SelectImageFormFolderAcivity.this);

            }
        }.start();
        Toast.makeText(this, "Đã tạo thành công : "+Environment.getExternalStorageDirectory().getPath() + "/GifFlag/" + "gifout.gif", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @OnClick(R.id.layout_select_picture_from_store_imv_back)
    public void backToMain(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}