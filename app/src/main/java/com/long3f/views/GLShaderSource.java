package com.long3f.views;

/**
 * Created by Admin on 23/9/2017.
 */

import android.opengl.GLES20;

public class GLShaderSource {
    public static int m16996a(String str, String str2) {
        int glCreateShader = GLES20.glCreateShader(35633);
        int glCreateShader2 = GLES20.glCreateShader(35632);
        int[] iArr = new int[1];
        GLES20.glShaderSource(glCreateShader, str);
        GLES20.glShaderSource(glCreateShader2, str2);
        GLES20.glCompileShader(glCreateShader);
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
        if (iArr[0] == 0) {
            GLMessage.sendMessage("phi.hd", "vertex shader compilation failed....");
            GLMessage.sendMessage("phi.hd", "log = " + GLES20.glGetShaderInfoLog(glCreateShader));
            return 0;
        }
        GLES20.glCompileShader(glCreateShader2);
        GLES20.glGetShaderiv(glCreateShader2, 35713, iArr, 0);
        if (iArr[0] == 0) {
            GLMessage.sendMessage("phi.hd", "fragment shader compilation failed....");
            GLMessage.sendMessage("phi.hd", GLES20.glGetShaderInfoLog(glCreateShader2));
            return 0;
        }
        int glCreateProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(glCreateProgram, glCreateShader);
        GLES20.glAttachShader(glCreateProgram, glCreateShader2);
        GLES20.glLinkProgram(glCreateProgram);
        GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);
        if (iArr[0] != 0) {
            return glCreateProgram;
        }
        GLMessage.sendMessage("phi.hd", "program link error...");
        GLMessage.sendMessage("phi.hd", GLES20.glGetProgramInfoLog(glCreateProgram));
        return 0;
    }

    public static float[] m16997a(float f, float f2, float f3, float f4, int i, int i2) {
        return new float[]{((f / ((float) i)) * 2.0f) - 1.0f, 1.0f - ((2.0f * f2) / ((float) i2)), ((f / ((float) i)) * 2.0f) - 1.0f, 1.0f - (((f2 + f4) * 2.0f) / ((float) i2)), (((f + f3) * 2.0f) / ((float) i)) - 1.0f, 1.0f - ((2.0f * f2) / ((float) i2)), (((f + f3) * 2.0f) / ((float) i)) - 1.0f, 1.0f - (((f2 + f4) * 2.0f) / ((float) i2))};
    }
}
