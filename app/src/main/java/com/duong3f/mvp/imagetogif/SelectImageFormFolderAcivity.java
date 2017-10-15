package com.duong3f.mvp.imagetogif;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Bundle;
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
import com.duong3f.obj.AlbumImage;
import com.duong3f.obj.Image;
import com.group3f.gifmaker.R;
import com.long3f.activity.EditGifActivity;
import com.long3f.adapter.ImageManagerAdapter;
import com.long3f.utils.AppCache;
import com.long3f.utils.AppUtils;

import org.lucasr.twowayview.TwoWayView;

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
        try {
            rcvFolderImage.setAdapter(new AdaptorFolderImageListView(this, android.R.layout.simple_list_item_1, folderImages));
        } catch (Exception e) {
            folderImages = new ArrayList<>();
            rcvFolderImage.setAdapter(new AdaptorFolderImageListView(this, android.R.layout.simple_list_item_1, folderImages));
        }
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
                updateView(imagesInFolder.indexOf(imagesSelect.get(i)));
                imagesSelect.remove(i);
                txtNext.setText(getResources().getString(R.string.next) + " (" + imagesSelect.size() + "/" + Integer.valueOf(AppCache
                        .getInstance(getApplicationContext())
                        .getMaxSettingFrame()) + ")");
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
                isChoose = (boolean) imagesInFolder.get(i).isSelect();
                Image imageSelect = imagesInFolder.get(i);
                if (isChoose) {
                    image_view_imv_is_select.setVisibility(View.GONE);
                    imagesSelect.remove(imageSelect);
                    imagesInFolder.get(i).setSelect(false);
                } else if (imagesSelect.size() < Integer.valueOf(AppCache
                        .getInstance(getApplicationContext())
                        .getMaxSettingFrame())) {
                    imagesSelect.add(imageSelect);
                    image_view_imv_is_select.setVisibility(View.VISIBLE);
                    imagesInFolder.get(i).setSelect(true);
                } else
                    Toast.makeText(SelectImageFormFolderAcivity.this, "Size photo max is " + String.valueOf(AppCache
                            .getInstance(getApplicationContext())
                            .getMaxSettingFrame()), Toast.LENGTH_SHORT).show();
                txtNext.setText(getResources().getString(R.string.next) + " (" + imagesSelect.size() + "/" + Integer.valueOf(AppCache
                        .getInstance(getApplicationContext())
                        .getMaxSettingFrame()) + ")");
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

    private void updateView(int index) {
        View view = rcvImageInFolder.getChildAt(index -
                rcvImageInFolder.getFirstVisiblePosition());
//        DuongLog.e(getClass(), "index " + index);
        if (view == null)
            return;
        ImageView imv_is_select = view.findViewById(R.id.item_image_view_imv_is_select);
        boolean isChoose;
        isChoose = !imagesInFolder.get(index).isSelect();
//        DuongLog.e(getClass(), "isChoose trc" + isChoose);
        if (isChoose) {
            imv_is_select.setVisibility(View.VISIBLE);
            imagesInFolder.get(index).setSelect(true);
        } else {
            imv_is_select.setVisibility(View.GONE);
            imagesInFolder.get(index).setSelect(false);
        }


//        DuongLog.e(getClass(), "isChoose sau" + isChoose);


    }

    @OnClick(R.id.layout_select_picture_from_store_btn_done)
    public void selectDone() {
        int gifSize = AppCache.getInstance(getApplicationContext()).getMaxSettingGifSize();
        if (getIntent().getStringExtra("from_imagemanager") != null && getIntent().getStringExtra("from_imagemanager").equals("from_imagemanager")) {
            for (Image image : imagesSelect) {
                Bitmap bitmap = AppUtils.scalePreserveRatio(BitmapFactory.decodeFile(image.getPath()), gifSize, gifSize, Color.TRANSPARENT);
                EditGifActivity.listFrame.add(bitmap);
                if (EditGifActivity.arrBitmapCropped != null && EditGifActivity.arrBitmapCropped.size() > 0) {
                    EditGifActivity.arrBitmapCropped.add(bitmap);
                }
                ImageManagerAdapter.bitmapArrayList.add(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(image.getPath()), 90, 90));
            }
            ImageManagerAdapter.onListChangeSize.onListChangeSize(EditGifActivity.listFrame.size());
            finish();
            return;
        }
        for (Image image : imagesSelect) {
            Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
            Bitmap resize = AppUtils.scalePreserveRatio(bitmap, gifSize, gifSize, Color.TRANSPARENT);
            EditGifActivity.listFrame.add(resize);
        }
        Intent intent = new Intent(SelectImageFormFolderAcivity.this, EditGifActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.layout_select_picture_from_store_imv_back)
    public void backToMain(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}