package com.youme.mixers;

import android.content.Context;
import android.opengl.GLES20;
import com.youme.gles.GlUtil;
import java.nio.FloatBuffer;


public class CameraFilterBeauty extends CameraFilter {
    private float[] colorMatrix;

    private int uImagestep;
    private int beautyParam;
    private float beautyLevel = 0.0f;
    FloatBuffer beautyArray = FloatBuffer.allocate(4);
    private final String VertxtShader = "uniform mat4 uMVPMatrix;\n uniform mat4 uTexMatrix;\n attribute vec4 aPosition;\n attribute vec4 aTextureCoord;\n varying vec2 vTextureCoord;\n void main() {\n    gl_Position = uMVPMatrix * aPosition;\n    vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n}\n";

    private final String samplerOES = "\n" +
            "#extension GL_OES_EGL_image_external : require\n" +
    		"precision mediump float; //指定默认精度\n" +
            "uniform samplerExternalOES uTexture;\n";

    private final String sampler = "\n"+
    		"precision mediump float; //指定默认精度\n" +
            "uniform sampler2D uTexture;\n";

    private final String FragmentShader = "\n" +
            "uniform vec2 singleStepOffset;\n" +
            "uniform highp vec4 params;\n" +
            "varying highp vec2 vTextureCoord;\n" +
            "const highp vec3 W = vec3(0.299,0.587,0.114);\n" +
            "const mat3 saturateMatrix = mat3(\n" +
            " 1.1102,-0.0598,-0.061,\n" +
            "-0.0774,1.0826,-0.1186,\n" +
            "-0.0228,-0.0228,1.1772);\n" +
            "float hardlight(float color)\n" +
            "{\n" +
            "if(color <= 0.5)\n" +
            "{\n" +
            "   color = color * color * 2.0;\n" +
            "}\n" +
            "else\n" +
            "{\n" +
            "   color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);\n" +
            " }\n" +
            " return color;\n" +
            "}\n" +
            "void main(){\n" +
            "vec2 blurCoordinates[24];\n" +
            "blurCoordinates[0] = vTextureCoord.xy + singleStepOffset * vec2(0.0, -10.0);\n" +
            "blurCoordinates[1] = vTextureCoord.xy + singleStepOffset * vec2(0.0, 10.0);\n" +
            "blurCoordinates[2] = vTextureCoord.xy + singleStepOffset * vec2(-10.0, 0.0);\n" +
            "blurCoordinates[3] = vTextureCoord.xy + singleStepOffset * vec2(10.0, 0.0);\n" +
            "blurCoordinates[4] = vTextureCoord.xy + singleStepOffset * vec2(5.0, -8.0);\n" +
            "blurCoordinates[5] = vTextureCoord.xy + singleStepOffset * vec2(5.0, 8.0);\n" +
            "blurCoordinates[6] = vTextureCoord.xy + singleStepOffset * vec2(-5.0, 8.0);\n" +
            "blurCoordinates[7] = vTextureCoord.xy + singleStepOffset * vec2(-5.0, -8.0);\n" +
            "blurCoordinates[8] = vTextureCoord.xy + singleStepOffset * vec2(8.0, -5.0);\n" +
            "blurCoordinates[9] = vTextureCoord.xy + singleStepOffset * vec2(8.0, 5.0);\n" +
            "blurCoordinates[10] = vTextureCoord.xy + singleStepOffset * vec2(-8.0, 5.0);\n" +
            "blurCoordinates[11] = vTextureCoord.xy + singleStepOffset * vec2(-8.0, -5.0);\n" +
            "blurCoordinates[12] = vTextureCoord.xy + singleStepOffset * vec2(0.0, -6.0);\n" +
            "blurCoordinates[13] = vTextureCoord.xy + singleStepOffset * vec2(0.0, 6.0);\n" +
            "blurCoordinates[14] = vTextureCoord.xy + singleStepOffset * vec2(6.0, 0.0);\n" +
            "blurCoordinates[15] = vTextureCoord.xy + singleStepOffset * vec2(-6.0, 0.0);\n" +
            "blurCoordinates[16] = vTextureCoord.xy + singleStepOffset * vec2(-4.0, -4.0);\n" +
            "blurCoordinates[17] = vTextureCoord.xy + singleStepOffset * vec2(-4.0, 4.0);\n" +
            "blurCoordinates[18] = vTextureCoord.xy + singleStepOffset * vec2(4.0, -4.0);\n" +
            "blurCoordinates[19] = vTextureCoord.xy + singleStepOffset * vec2(4.0, 4.0);\n" +
            "blurCoordinates[20] = vTextureCoord.xy + singleStepOffset * vec2(-2.0, -2.0);\n" +
            "blurCoordinates[21] = vTextureCoord.xy + singleStepOffset * vec2(-2.0, 2.0);\n" +
            " blurCoordinates[22] = vTextureCoord.xy + singleStepOffset * vec2(2.0, -2.0);\n" +
            "blurCoordinates[23] = vTextureCoord.xy + singleStepOffset * vec2(2.0, 2.0);\n" +
            "float sampleColor = texture2D(uTexture, vTextureCoord).g * 22.0;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[0]).g;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[1]).g;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[2]).g;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[3]).g;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[4]).g;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[5]).g;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[6]).g;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[7]).g;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[8]).g;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[9]).g;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[10]).g;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[11]).g;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[12]).g * 2.0;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[13]).g * 2.0;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[14]).g * 2.0;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[15]).g * 2.0;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[16]).g * 2.0;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[17]).g * 2.0;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[18]).g * 2.0;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[19]).g * 2.0;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[20]).g * 3.0;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[21]).g * 3.0;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[22]).g * 3.0;\n" +
            "sampleColor += texture2D(uTexture, blurCoordinates[23]).g * 3.0;\n" +
            "sampleColor = sampleColor / 62.0;\n" +
            "vec3 centralColor = texture2D(uTexture, vTextureCoord).rgb;\n" +
            "float highpass = centralColor.g - sampleColor + 0.5;\n" +
            "for(int i = 0; i < 5;i++)\n" +
            "{\n" +
            "    highpass = hardlight(highpass);\n" +
            "}\n" +
            "float lumance = dot(centralColor, W);\n" +
            "float alpha = pow(lumance, params.r);\n" +
            "\n" +
            "vec3 smoothColor = centralColor + (centralColor-vec3(highpass))*alpha*0.1;\n" +
            "smoothColor.r = clamp(pow(smoothColor.r, params.g),0.0,1.0);\n" +
            "smoothColor.g = clamp(pow(smoothColor.g, params.g),0.0,1.0);\n" +
            "smoothColor.b = clamp(pow(smoothColor.b, params.g),0.0,1.0);\n" +
            "vec3 lvse = vec3(1.0)-(vec3(1.0)-smoothColor)*(vec3(1.0)-centralColor);\n" +
            "vec3 bianliang = max(smoothColor, centralColor);\n" +
            "vec3 rouguang = 2.0*centralColor*smoothColor + centralColor*centralColor - 2.0*centralColor*centralColor*smoothColor;\n" +
            "gl_FragColor = vec4(mix(centralColor, lvse, alpha), 1.0);\n" +
            "gl_FragColor.rgb = mix(gl_FragColor.rgb, bianliang, alpha);\n" +
            "gl_FragColor.rgb = mix(gl_FragColor.rgb, rouguang, params.b);\n" +
            "vec3 satcolor = gl_FragColor.rgb * saturateMatrix;\n" +
            "gl_FragColor.rgb = mix(gl_FragColor.rgb, satcolor, params.a);}";


    public CameraFilterBeauty(int type, boolean useOES) {
        super(type, useOES);
    }

    @Override
    public void setTextureSize(int width, int height) {
        super.setTextureSize(width, height);
    }

    @Override
    protected int createProgram(Context applicationContext) {
        String shader;
        if (mUseOES){
            shader = samplerOES + FragmentShader;
        }
        else{
            shader = sampler + FragmentShader;
        }
        return GlUtil.createProgram(VertxtShader, shader);
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();
        uImagestep = GLES20.glGetUniformLocation(mProgramHandle, "singleStepOffset");
        beautyParam = GLES20.glGetUniformLocation(mProgramHandle, "params");
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex,
                                  int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        super.bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix,
                texBuffer, texStride);

        GLES20.glUniform2f(uImagestep, (2.0f / mTextureWidth), (2.0f / mTextureHeight));
        GLES20.glUniform4fv(beautyParam, 1, beautyArray);
    }

    @Override
    public void setBeautyLevel(float level){
        beautyLevel = level;
        beautyArray.position(0);
        float[] array = beautyArray.array();
        array[0] = -beautyLevel * 0.8f + 1.0f;
        array[1] = -beautyLevel * 0.5f + 1.0f;
        array[2] = 0.3f * beautyLevel + 0.15f;
        array[3] = 0.25f * beautyLevel;
    }

    @Override
    public void makeMatrix(float[] samplingMatrix, int rotation, int mirror) {
      super.makeMatrix(samplingMatrix, rotation, mirror);
    }
}