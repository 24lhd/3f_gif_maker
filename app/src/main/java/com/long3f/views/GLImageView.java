package com.long3f.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
/**
 * Created by Admin on 22/9/2017.
 */
public class GLImageView extends GLSurfaceView implements Renderer {
    public float[] alphas = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};
    public int aLocation;
    private int a_Position;
    private int a_TextureCoordinate;
    private int u_Sampler;
    private FloatBuffer floatBuffer;
    private FloatBuffer floatBuffer1;
    private int f10691h;
    private int f10692i;
    private RectF rectF;
    private GLTextConfig listEfect;
    private Context context;
    private EffectContext effectContext;
    private Effect effect;
    private int selectEfect;

    public GLImageView(Context context) {
        super(context);
        init();
        this.context = context;
    }

    public GLImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
        this.context = context;
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(0);
        this.selectEfect = 1056964608;
        this.floatBuffer = ByteBuffer.allocateDirect(this.alphas.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(this.alphas);
        this.floatBuffer.position(0);
    }

    private void m16984d() {
        this.aLocation = GLShaderSource.m16996a("attribute vec2 a_Position;\nattribute vec2 a_TextureCoordinate;\nvarying vec2 v_TextureCoordinate;\nvoid main() {\ngl_Position = vec4(a_Position,0,1);\nv_TextureCoordinate = a_TextureCoordinate;\n }", "precision mediump float;\nvarying vec2 v_TextureCoordinate;\nvec4 tempColor;\nuniform sampler2D u_Sampler;\nvoid main() { \ngl_FragColor = texture2D(u_Sampler, v_TextureCoordinate).bgra;\n}");
        this.a_Position = GLES20.glGetAttribLocation(this.aLocation, "a_Position");
        this.a_TextureCoordinate = GLES20.glGetAttribLocation(this.aLocation, "a_TextureCoordinate");
        this.u_Sampler = GLES20.glGetUniformLocation(this.aLocation, "u_Sampler");
    }

    private void m16985e() {
        m16988a();
        float[] a = GLShaderSource.m16997a(this.rectF.left, this.rectF.top, this.rectF.width(), this.rectF.height(), this.f10691h, this.f10692i);
        this.floatBuffer1 = ByteBuffer.allocateDirect(a.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(a);
        this.floatBuffer1.position(0);
    }

    private void chooseEffect() {
        EffectFactory factory = this.effectContext.getFactory();
        if (this.effect != null) {
            this.effect.release();
            this.effect = null;
        }
        switch (this.selectEfect) {
            case 1056964609:
                this.effect = factory.createEffect("android.media.effect.effects.BrightnessEffect");
                this.effect.setParameter("brightness", Float.valueOf(2.0f));
                break;
            case 1056964610:
                this.effect = factory.createEffect("android.media.effect.effects.ContrastEffect");
                this.effect.setParameter("contrast", Float.valueOf(1.4f));
                break;
            case 1056964611:
                this.effect = factory.createEffect("android.media.effect.effects.FisheyeEffect");
                this.effect.setParameter("scale", Float.valueOf(0.45f));
                break;
            case 1056964613:
                this.effect = factory.createEffect("android.media.effect.effects.AutoFixEffect");
                this.effect.setParameter("scale", Float.valueOf(0.5f));
                break;
            case 1056964614:
                this.effect = factory.createEffect("android.media.effect.effects.BlackWhiteEffect");
                this.effect.setParameter("black", Float.valueOf(0.1f));
                this.effect.setParameter("white", Float.valueOf(0.7f));
                break;
            case 1056964616:
                this.effect = factory.createEffect("android.media.effect.effects.CrossProcessEffect");
                break;
            case 1056964617:
                this.effect = factory.createEffect("android.media.effect.effects.DocumentaryEffect");
                break;
            case 1056964619:
                this.effect = factory.createEffect("android.media.effect.effects.DuotoneEffect");
                this.effect.setParameter("first_color", Integer.valueOf(-256));
                this.effect.setParameter("second_color", Integer.valueOf(-12303292));
                break;
            case 1056964620:
                this.effect = factory.createEffect("android.media.effect.effects.FillLightEffect");
                this.effect.setParameter("strength", Float.valueOf(0.8f));
                break;
            case 1056964622:
                this.effect = factory.createEffect("android.media.effect.effects.GrainEffect");
                this.effect.setParameter("strength", Float.valueOf(1.0f));
                break;
            case 1056964623:
                this.effect = factory.createEffect("android.media.effect.effects.GrayscaleEffect");
                break;
            case 1056964624:
                this.effect = factory.createEffect("android.media.effect.effects.LomoishEffect");
                break;
            case 1056964625:
                this.effect = factory.createEffect("android.media.effect.effects.NegativeEffect");
                break;
            case 1056964626:
                this.effect = factory.createEffect("android.media.effect.effects.PosterizeEffect");
                break;
            case 1056964629:
                this.effect = factory.createEffect("android.media.effect.effects.SaturateEffect");
                this.effect.setParameter("scale", Float.valueOf(0.5f));
                break;
            case 1056964630:
                this.effect = factory.createEffect("android.media.effect.effects.SepiaEffect");
                break;
            case 1056964631:
                this.effect = factory.createEffect("android.media.effect.effects.SharpenEffect");
                break;
            case 1056964633:
                this.effect = factory.createEffect("android.media.effect.effects.ColorTemperatureEffect");
                this.effect.setParameter("scale", Float.valueOf(0.9f));
                break;
            case 1056964634:
                this.effect = factory.createEffect("android.media.effect.effects.TintEffect");
                this.effect.setParameter("tint", Integer.valueOf(-65281));
                break;
            case 1056964635:
                this.effect = factory.createEffect("android.media.effect.effects.VignetteEffect");
                this.effect.setParameter("scale", Float.valueOf(0.5f));
                break;
        }
        if (this.effect != null && this.selectEfect != 1056964608) {
            this.effect.apply(this.listEfect.ints[0], this.listEfect.getAnInt(), this.listEfect.getAnInt1(), this.listEfect.ints[1]);
        }
    }

    private synchronized void frameDraw() {
        if (this.listEfect != null) {
            if (!this.listEfect.aBoolean) {
                this.listEfect.defaultEffect();
            }
            try {
                chooseEffect();
                GLES20.glUseProgram(this.aLocation);
                GLES20.glViewport(0, 0, this.f10691h, this.f10692i);
                GLES20.glBlendFunc(770, 771);
                GLES20.glEnable(3042);
                GLES20.glEnableVertexAttribArray(this.a_Position);
                GLES20.glEnableVertexAttribArray(this.a_TextureCoordinate);
                GLES20.glUniform1i(this.u_Sampler, 0);
                GLES20.glActiveTexture(33984);
                if (this.selectEfect != 1056964608) {
                    GLES20.glBindTexture(3553, this.listEfect.ints[1]);
                } else {
                    GLES20.glBindTexture(3553, this.listEfect.ints[0]);
                }
                GLES20.glVertexAttribPointer(this.a_Position, 2, 5126, false, 0, this.floatBuffer1.position(0));
                GLES20.glVertexAttribPointer(this.a_TextureCoordinate, 2, 5126, false, 0, this.floatBuffer.position(0));
                GLES20.glDrawArrays(5, 0, 4);
                GLES20.glBindTexture(3553, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void m16988a() {
        int i = this.f10691h;
        int i2 = this.f10692i;
        float max = Math.max(((float) this.listEfect.getAnInt()) / ((float) (i - 0)), ((float) this.listEfect.getAnInt1()) / ((float) (i2 - 0)));
        int round = Math.round(((float) this.listEfect.getAnInt()) / max);
        int round2 = Math.round(((float) this.listEfect.getAnInt1()) / max);
        i = (((i - 0) - round) / 2) + 0;
        i2 = (((i2 - 0) - round2) / 2) + 0;
        this.rectF = new RectF((float) i, (float) i2, (float) (i + round), (float) (i2 + round2));
    }

    public void m16989a(int[] iArr, int i, int i2) {
        Object obj = 1;
        if (this.listEfect == null) {
            this.listEfect = new GLTextConfig();
        } else if (i == this.listEfect.getAnInt() && i2 == this.listEfect.getAnInt1() && this.floatBuffer1 != null) {
            obj = null;
        }
        this.listEfect.init(iArr, i, i2);
        if (obj != null) {
            m16985e();
        }
        requestRender();
    }

    public void m16990b() {
        if (this.listEfect != null) {
            this.listEfect.nullEffect();
        }
    }

    public int getEffect() {
        return this.selectEfect;
    }

    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(16384);
        frameDraw();
    }

    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        GLES20.glViewport(0, 0, i, i2);
        GLES20.glClearColor(0.92f, 0.92f, 0.92f, 1.0f);
        this.f10691h = i;
        this.f10692i = i2;
        if (this.listEfect != null) {
            m16985e();
            requestRender();
        }
        GLMessage.m16587a("onSurfaceChanged mScreenWidth = " + this.f10691h + ", mScreenHeight  = " + this.f10692i);
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        GLES20.glClearColor(0.92f, 0.92f, 0.92f, 1.0f);
        GLES20.glEnable(3042);
        GLES20.glBlendFunc(770, 771);
        GLES20.glEnable(3089);
        m16984d();
        this.effectContext = EffectContext.createWithCurrentGlContext();
    }

    public void setEffect(int i) {
        this.selectEfect = i;
        requestRender();
        GLListEffect.m16558a().m16569i(this.selectEfect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
