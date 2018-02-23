package com.example.administrator.psdprodecuer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;

/**
 * Created by DELL on 2016/5/4.
 */
public class MyView extends View {
    private final String[] context_items = new String[]{"相机拍照", "相册选取"};
    private int mycolor = Color.BLACK;
    private Paint mpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int viewWidth, viewHeight;
    private String text[] = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    private int currentposition = 0;//当前允许处理的地支
    private int[][] PSDStatus = new int[12][6];//十二地支的状态即摩斯密码；
    private float[] currentHeight = new float[6];
    private int linw = 0;//按比例外圈黑线的宽度
    private int midw = 0;//按比例白圈宽度
    private int textSize = 0;//按比例字体大小
    private int centerx = 0;//按比例圆心横坐标
    private int centery = 0;//按比例圆心纵坐标
    private int centerCircle = 0;//按比例中心园的半径
    private int radius = 0;//白圈半径
    private int smallradius = 0;
    private int dotwidth = 0;
    private Context context;
    private AlertDialog method_dialog;
    private String icon_path = "";
    private static final String BASE_SD_PATH = "PSDProducer";
    private static final String MESSAGE_SD_PATH = "Message";
    private static final String ICON_SD_PATH = "icon";
    private static final String ICON_SD_NAME = "center_icon.png";
    private boolean ifdrawBitmap = false;
    private Bitmap centerBitmap = null;

    public MyView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public void setCenterBitmap(Bitmap bitmap) {
        centerBitmap = bitmap;
    }

    public void setPSDStatus(int[][] PSDStatus) {
        this.PSDStatus = PSDStatus;
    }

    public int[][] getPSDStatus() {
        return PSDStatus;
    }


    public MyView(Context context, int width, int height) {
        super(context);
        this.context = context;
        this.viewWidth = width;
        this.viewHeight = height;
        linw = (int) (0.0074 * viewWidth);//按比例外圈黑线的宽度
        midw = (int) (0.06481 * viewWidth);//按比例白圈宽度
        textSize = (int) (0.037 * viewWidth);//按比例字体大小
        centerx = viewWidth / 2;//按比例圆心横坐标
        centery = viewHeight / 2;//按比例圆心纵坐标
        centerCircle = (int) (0.075 * viewWidth);//按比例中心园的半径
        radius = Math.min(viewWidth, viewHeight) / 2 - linw / 2 - midw / 2;//白圈半径
        smallradius = (radius - linw - midw - centerCircle) / 12;
        dotwidth = (int) (0.0074 * viewWidth);
        currentposition = 0;
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 6; j++) {
                PSDStatus[i][j] = 0;
            }
        }
        init();
        intiDialog();
    }

    private void intiDialog() {
        icon_path = getIconPath();
        method_dialog = new AlertDialog.Builder(context).setTitle("请选择方式	")
                .setItems(context_items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent2 = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(new File(icon_path)));
                                ((Activity) context).startActivityForResult(intent2, MainActivity.CODE_CAMERA_START);
                                break;
                            case 1:
                                Intent intent1 = new Intent(Intent.ACTION_PICK,
                                        null);
                                intent1.setDataAndType(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        "image/*");
                                ((Activity) context).startActivityForResult(intent1, MainActivity.CODE_ALBUM_START);
                                break;
                        }
                    }
                }).create();
    }

    public static String getIconPath() {
        File root = getMessageSDPath();
        if (root == null)
            return null;
        File path = new File(root, ICON_SD_PATH);
        try {
            if (!path.exists())
                path.mkdirs();
        } catch (Exception e) {
            return null;
        }
        File icon = new File(path, ICON_SD_NAME);
        try {
            if (icon.exists())
                icon.delete();
        } catch (Exception e) {
            return null;
        }
        String icon_path0 = icon.getAbsolutePath();
        return icon_path0;
    }

    public static File getMessageSDPath() {
        File root = getBaseSDCardPath();
        if (root == null)
            return null;
        File path = new File(root, MESSAGE_SD_PATH);
        try {
            if (!path.exists())
                path.mkdirs();
        } catch (Exception e) {
            return null;
        }
        return path;
    }

    public static File getBaseSDCardPath() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File root_path = new File(
                        Environment.getExternalStorageDirectory(), BASE_SD_PATH);// 存在无卡、无权风险
                return root_path;
            } catch (Exception e) {
                return null;// 禁止权限在此处捕获
            }
        }
        return null;
    }

    public void setCurrentposition(int position) {
        currentposition = position;
    }

    public int getCurrentposition() {
        return currentposition;
    }

    public MyView(Context context, AttributeSet attr) {
        super(context, attr);
        init();

    }

    public MyView(Context context, AttributeSet attr, int defStyleAttr) {
        super(context, attr, defStyleAttr);
        init();
    }

    public void init() {
        mpaint.setColor(mycolor);
    }

    public void setIfdrawBitmap(boolean ifdrawBitmap) {
        this.ifdrawBitmap = ifdrawBitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setStrokeWidth(linw);
        mpaint.setColor(Color.BLACK);
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, radius - linw / 2, mpaint);
        mpaint.setStrokeWidth(midw);
        mpaint.setColor(Color.WHITE);
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, radius - linw - midw / 2, mpaint);
        mpaint.setStrokeWidth(linw);
        mpaint.setColor(Color.BLACK);
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, radius - linw / 2 - midw - linw / 2, mpaint);
        RectF rect = new RectF(centerx - (radius - linw - midw / 2), centerx - (radius - linw - midw / 2),
                centerx + (radius - linw - midw / 2), centerx + (radius - linw - midw / 2));
        mpaint.setColor(Color.BLACK);
        mpaint.setStrokeWidth(midw + 2);
        for (int i = -15; i <= 345; i += 30) {
            canvas.drawArc(rect, i, 1, false, mpaint);
        }
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.GREEN);
        textPaint.setTextSize(textSize);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        // 计算每一个坐标
        float baseX = viewHeight / 2 - textSize / 2;
        float baseY = viewHeight / 2 - (radius - linw / 2 - midw - linw / 2) - textSize;
        float textradius = viewHeight / 2 - baseY;


        mpaint.setColor(Color.BLACK);
        mpaint.setStrokeWidth(2);
        if (ifdrawBitmap) {
            if (centerBitmap == null) {
                Toast.makeText(context, "bitmap is null", Toast.LENGTH_SHORT).show();
            } else {

                int startx = centerx - (int) (0.074 * viewWidth);
                int starty = centery - (int) (0.074 * viewHeight);
                int endx = centerx + (int) (0.074 * viewWidth);
                int endy = centery + (int) (0.074 * viewHeight);
                canvas.drawBitmap(getRoundedCornerBitmap(centerBitmap), null, new Rect(startx, starty, endx, endy), null);
            }
        } else {
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, centerCircle, mpaint);
        }
        int tempx = viewWidth / 2;
        int tempy = viewHeight / 2;
        for (int i = 0; i < 12; i++) {
            float baseXx = (float) (centerx + Math.sin(Math.PI / 6 * i) * textradius - textSize / 2);
            float baseYx = (float) (baseY + textradius * (1 - Math.cos(Math.PI / 6 * i)) + textSize / 3);
            canvas.drawText(text[(i + currentposition) % 12], baseXx, baseYx, textPaint);
            float tempyy = 0, tempxx = 0;
            for (int j = 0; j < 6; j++) {
                if (PSDStatus[(i + currentposition) % 12][j] == 0) {
                    tempxx = (float) (tempx + Math.sin(Math.PI / 6 * i) * (centerCircle + smallradius * (1 + 2 * j)));
                    tempyy = (float) (tempy - Math.cos(Math.PI / 6 * i) * (centerCircle + smallradius * (1 + 2 * j)));
                    canvas.drawCircle(tempxx, tempyy, smallradius, mpaint);
                } else if (PSDStatus[(i + currentposition) % 12][j] == 1) {
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setColor(Color.WHITE);
                    tempxx = (float) (tempx + Math.sin(Math.PI / 6 * i) * (centerCircle + smallradius * (1 + 2 * j)));
                    tempyy = (float) (tempy - Math.cos(Math.PI / 6 * i) * (centerCircle + smallradius * (1 + 2 * j)));
                    canvas.drawCircle(tempxx, tempyy, dotwidth, paint);
                    paint.setColor(Color.BLACK);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    canvas.drawCircle(tempxx, tempyy, smallradius, paint);
                } else if (PSDStatus[(i + currentposition) % 12][j] == 2) {
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    float linex, liney, linexx, lineyy;
                    paint.setStrokeWidth(dotwidth);
                    linex = (float) (viewWidth / 2 + Math.sin(Math.PI / 6 * i) * (centerCircle + smallradius * (2 * j) + dotwidth));
                    liney = (float) (viewHeight / 2 - Math.cos(Math.PI / 6 * i) * (centerCircle + smallradius * (2 * j) + dotwidth));
                    linexx = (float) (viewWidth / 2 + Math.sin(Math.PI / 6 * i) * (centerCircle + smallradius * (2 * (j + 1)) - dotwidth));
                    lineyy = (float) (viewHeight / 2 - Math.cos(Math.PI / 6 * i) * (centerCircle + smallradius * (2 * (j + 1)) - dotwidth));
                    canvas.drawLine(linex, liney, linexx, lineyy, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    paint.setColor(Color.BLACK);
                    tempxx = (float) (tempx + Math.sin(Math.PI / 6 * i) * (centerCircle + smallradius * (1 + 2 * j)));
                    tempyy = (float) (tempy - Math.cos(Math.PI / 6 * i) * (centerCircle + smallradius * (1 + 2 * j)));
                    canvas.drawCircle(tempxx, tempyy, smallradius, paint);

                }
                if (j == 0) {
                    if (PSDStatus[(i + currentposition) % 12][j] == 0) {
                        tempxx = (float) (tempx + Math.sin(Math.PI / 6 * i) * (centerCircle + smallradius * (1 + 2 * j)));
                        tempyy = (float) (tempy - Math.cos(Math.PI / 6 * i) * (centerCircle + smallradius * (1 + 2 * j)));
                        canvas.drawCircle(tempxx, tempyy, smallradius, mpaint);
                    } else if (PSDStatus[(i + currentposition) % 12][j] == 1) {
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.WHITE);
                        tempxx = (float) (tempx + Math.sin(Math.PI / 6 * i) * (centerCircle + smallradius * (1 + 2 * j)));
                        tempyy = (float) (tempy - Math.cos(Math.PI / 6 * i) * (centerCircle + smallradius * (1 + 2 * j)));
                        canvas.drawCircle(tempxx, tempyy, smallradius, paint);

                    } else if (PSDStatus[(i + currentposition) % 12][j] == 2) {
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.BLACK);
                        tempxx = (float) (tempx + Math.sin(Math.PI / 6 * i) * (centerCircle + smallradius * (1 + 2 * j)));
                        tempyy = (float) (tempy - Math.cos(Math.PI / 6 * i) * (centerCircle + smallradius * (1 + 2 * j)));
                        canvas.drawCircle(tempxx, tempyy, smallradius, paint);
                    }
                }
                if (i == 0) {
                    currentHeight[j] = tempyy;
                }
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getRawX() > (viewWidth / 2 - smallradius * 3 / 2) && event.getRawX() < (viewWidth / 2 + smallradius * 3 / 2)) {
                System.out.println("333#$#$#$#$#$#$#$");
                int changePosition = getClickPosition(event.getRawY());
                if (changePosition != 6 && changePosition != 7) {
                    PSDStatus[currentposition % 12][changePosition] = (PSDStatus[currentposition % 12][changePosition] + 1) % 3;
                    invalidate();
                } else if (changePosition == 6) {
                    method_dialog.show();
                }
            }
        }
        return false;
    }

    public String geticon_path() {
        return icon_path;
    }

    public void roate(int currentposition) {
        setCurrentposition(currentposition);
        invalidate();
    }

    public int getClickPosition(float flag) {
        if (flag > (currentHeight[0] + smallradius) && flag < viewWidth / 2 + smallradius) {
            return 0;
        } else if (flag > (currentHeight[1] + smallradius) && flag < (currentHeight[0] + smallradius)) {
            return 1;
        } else if (flag > (currentHeight[2] + smallradius) && flag < (currentHeight[1] + smallradius)) {
            return 2;
        } else if (flag > (currentHeight[3] + smallradius) && flag < (currentHeight[2] + smallradius)) {
            return 3;
        } else if (flag > (currentHeight[4] + smallradius) && flag < (currentHeight[3] + smallradius)) {
            return 4;
        } else if (flag > (currentHeight[5] + smallradius) && flag < (currentHeight[4] + smallradius)) {
            return 5;
        } else if (Math.abs(centery - flag) < centerCircle) {
            return 6;
        } else {
            return 7;
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);


        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2;


        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);


        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
