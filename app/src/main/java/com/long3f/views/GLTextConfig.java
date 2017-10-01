package com.long3f.views;

/**
 * Created by Admin on 23/9/2017.
 */

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class GLTextConfig {
    public int[] ints = new int[2];
    public boolean aBoolean = false;
    private int[] ints1;
    private int anInt;
    private int anInt1;

    public int getAnInt() {
        return this.anInt;
    }

    public void init(int[] iArr, int i, int i2) {
        this.ints1 = iArr;
        this.anInt = i;
        this.anInt1 = i2;
        this.aBoolean = false;
    }

    public int getAnInt1() {
        return this.anInt1;
    }

    public void defaultEffect() {
        if (this.ints1 != null) {
            if (this.ints[0] != -1) {
                GLES20.glDeleteTextures(1, new int[]{this.ints[0]}, 0);
            }
            if (this.ints[1] != -1) {
                GLES20.glDeleteTextures(1, new int[]{this.ints[1]}, 0);
            }
            GLES20.glGenTextures(2, this.ints, 0);
            GLES20.glBindTexture(3553, this.ints[0]);
            IntBuffer put = ByteBuffer.allocateDirect(this.ints1.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer().put(this.ints1);
            GLES20.glTexParameterf(3553, 10241, 9729.0f);
            GLES20.glTexParameterf(3553, 10240, 9729.0f);
            GLES20.glTexParameterf(3553, 10242, 33071.0f);
            GLES20.glTexParameterf(3553, 10243, 33071.0f);
            GLES20.glTexImage2D(3553, 0, 6408, this.anInt, this.anInt1, 0, 6408, 5121, put.position(0));
            GLES20.glGenerateMipmap(3553);
            GLES20.glBindTexture(3553, 0);
        }
        this.aBoolean = true;
    }

    public void nullEffect() {
        GLES20.glDeleteTextures(2, this.ints, 0);
        this.ints1 = null;
    }
}

