package com.example.administrator.psdprodecuer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {
    private LinearLayout layout, btn_layout;
    private MyView view;
    private Button btn_left, btn_right, btn_get, btn_roate, btn_clear, btn_bitmap;
    private EditText[] PSD = new EditText[12];//12位密码
    private int[][] PSDStatus = new int[12][6];//十二地支的状态即摩斯密码；
    public static final int CODE_ALBUM_START = 1;
    public static final int CODE_CAMERA_START = 2;
    private static final int CODE_CROP_START = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = (LinearLayout) this.findViewById(R.id.linear);
        btn_left = (Button) this.findViewById(R.id.btn_turnleft);
        btn_right = (Button) this.findViewById(R.id.btn_turnright);
        btn_get = (Button) this.findViewById(R.id.btn_getdata);
        btn_roate = (Button) this.findViewById(R.id.btn_roate);
        btn_clear = (Button) this.findViewById(R.id.btn_clear);
        btn_bitmap = (Button) this.findViewById(R.id.btn_produceimg);
        btn_right.setOnClickListener(this);
        btn_left.setOnClickListener(this);
        btn_roate.setOnClickListener(this);
        btn_get.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_bitmap.setOnClickListener(this);
        PSD[0] = (EditText) this.findViewById(R.id.et_zi);
        PSD[1] = (EditText) this.findViewById(R.id.et_chou);
        PSD[2] = (EditText) this.findViewById(R.id.et_yin);
        PSD[3] = (EditText) this.findViewById(R.id.et_mao);
        PSD[4] = (EditText) this.findViewById(R.id.et_chen);
        PSD[5] = (EditText) this.findViewById(R.id.et_si);
        PSD[6] = (EditText) this.findViewById(R.id.et_wu);
        PSD[7] = (EditText) this.findViewById(R.id.et_wei);
        PSD[8] = (EditText) this.findViewById(R.id.et_shen);
        PSD[9] = (EditText) this.findViewById(R.id.et_you);
        PSD[10] = (EditText) this.findViewById(R.id.et_xu);
        PSD[11] = (EditText) this.findViewById(R.id.et_hai);
        for (int i = 0; i < 12; i++) {
            PSD[i].setOnClickListener(this);
        }
        int width = getWindowManager().getDefaultDisplay().getWidth();
        System.out.println(getWindowManager().getDefaultDisplay().getHeight() + "#$#$#$#$#$#$#$#$#       " + getWindowManager().getDefaultDisplay().getWidth());
        view = new MyView(MainActivity.this, width, width);
        layout.addView(view, 0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_turnleft:
                view.roate((view.getCurrentposition() + 1) % 12);
                break;
            case R.id.btn_turnright:
                if (view.getCurrentposition() == 0) {
                    view.roate(11);
                } else {
                    view.roate(view.getCurrentposition() - 1);
                }
                break;
            case R.id.btn_getdata:
                //摩斯密码圆盘生成十二位字母数字密码
                PSDStatus = view.getPSDStatus();
                for (int i = 0; i < 12; i++) {
                    String everyPSDStatus = "";
                    for (int j = 0; j < 6; j++) {
                        everyPSDStatus += PSDStatus[i][j];
                    }
                    if(!everyPSDStatus.equals("000000")){
                        if (("" + PsdTools.getChar(everyPSDStatus)).equals("!")) {
                            int index = i+1;
                            Toast.makeText(MainActivity.this, "第" + index+ "位莫尔斯密码有错误,请重新输入！", Toast.LENGTH_SHORT).show();
                        } else {
                            PSD[i].setText("" + PsdTools.getChar(everyPSDStatus));
                        }
                    }
                }
                //十二位字母数字密码生成摩斯密码圆盘
                for (int i = 0; i < 12; i++) {
                    String everyPSDStatus = "";
                       everyPSDStatus = ""+PSD[i].getText().toString();
                    if(everyPSDStatus.equals("无")){
                        for (int j = 0; j < 6; j++) {
                            PSDStatus[i][j]=0;
                        }
                    }else{
                        if(PsdTools.getMoPSD(everyPSDStatus)[0]==4){
                            for (int j = 0; j < 6; j++) {
                                PSDStatus[i][j]=0;
                            }
                        }else{
                            PSDStatus[i] = PsdTools.getMoPSD(everyPSDStatus);
                        }
                    }
                }
                view.setPSDStatus(PSDStatus);
                view.invalidate();
                break;
            case R.id.btn_roate:
                int random = (int) (Math.random() * 20) + 5;
                Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
                LinearInterpolator lin = new LinearInterpolator();

                operatingAnim.setInterpolator(lin);

                Animation operatingAnim2 = AnimationUtils.loadAnimation(btn_roate.getContext(), R.anim.btn_rotate);
                LinearInterpolator lin2 = new LinearInterpolator();
                operatingAnim2.setInterpolator(lin2);
                view.startAnimation(operatingAnim);
                btn_roate.startAnimation(operatingAnim2);
                view.roate((view.getCurrentposition() + random) % 12);
                break;
            case R.id.btn_clear:
                int[][] PSDStatus = new int[12][6];
                for (int i = 0; i < 12; i++) {
                    for (int j = 0; j < 6; j++) {
                        PSDStatus[i][j] = 0;
                        PSD[i].setText(null);
                    }
                }
                view.setCurrentposition(0);
                view.setPSDStatus(PSDStatus);
                view.invalidate();

                break;
            case R.id.btn_produceimg:
                Long filename = System.currentTimeMillis();
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;
                view.setBackgroundResource(R.drawable.green_background);
                String pp = MediaStore.Images.Media.insertImage(getContentResolver(), getViewBitmap(view), "", "");
                //saveBitmap(getViewBitmap(view), "jpg", path);
                Uri localUri = Uri.fromFile(new File(pp));
                Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
                view.setBackgroundColor(Color.parseColor("#00ffffff"));
                sendBroadcast(localIntent);
                Toast.makeText(MainActivity.this, "图片已保存到手机", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_ALBUM_START:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }
                break;
            case CODE_CAMERA_START:
                if (resultCode == RESULT_OK) {
                    File temp = new File(view.geticon_path());
                    cropPhoto(Uri.fromFile(temp));// 裁剪图片
                }
                break;
            case CODE_CROP_START:
                if (data == null)
                    return;
                Bundle extras = data.getExtras();
                Bitmap bitmap = extras.getParcelable("data");
                if (bitmap != null) {
                    view.setCenterBitmap(bitmap);
                    view.setIfdrawBitmap(true);
                    view.invalidate();
                }
                break;
        }
    }

    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 宽高的比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);// 裁剪图片宽高
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CODE_CROP_START);
    }
}
