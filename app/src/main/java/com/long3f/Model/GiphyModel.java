package com.long3f.Model;

/**
 * Created by Admin on 6/10/2017.
 */

public class GiphyModel{
    String previewUrl;
    String downloadUrl;
    String id;

    public GiphyModel(String id,String previewUrl, String downloadUrl) {
        this.id = id;
        this.previewUrl = previewUrl;
        this.downloadUrl = downloadUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
