package com.example.xiaojun.posji.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.xiaojun.posji.MyAppLaction;
import com.example.xiaojun.posji.R;
import com.example.xiaojun.posji.beans.JiuDianBean;
import com.example.xiaojun.posji.beans.LogoBean;
import com.example.xiaojun.posji.beans.ShiBieBean;
import com.example.xiaojun.posji.utils.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sdsmdg.tastytoast.TastyToast;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.UsbThermalPrinter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class DaYingActivity extends Activity {
    private LinearLayout baocun;
    private Button dayin;
    private TextView t1,t2,t3,t4,t5;
    private EditText beizhu;
    private ImageView erweima,logo;
    private String Result;
    private ImageView iii;
    private int printGray=5; //灰度
    private Boolean nopaper = false;
    MyHandler handler;
    private ProgressDialog progressDialog;
    private final int NOPAPER = 3;
    private final int LOWBATTERY = 4;
    private final int PRINTVERSION = 5;
    private final int PRINTBARCODE = 6;
    private final int PRINTPAPERWALK = 8;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private final int MAKER = 13;
    private final int PRINTPICTURE = 14;
    private final int NOBLACKBLOCK = 15;
    private UsbThermalPrinter mUsbThermalPrinter = new UsbThermalPrinter(DaYingActivity.this);
    private Bitmap bitmap=null;
    private String name=null,shoufangren=null,riqi=null;
    private String id=null;
    public static final int TIMEOUT = 1000 * 60;
    private String zhuji=null;
    private JiuDianBean jiudianbean=null;

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOPAPER:
                    noPaperDlg();
                    break;
                case LOWBATTERY:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(DaYingActivity.this);
                    alertDialog.setTitle(R.string.operation_result);
                    alertDialog.setMessage(getString(R.string.LowBattery));
                    alertDialog.setPositiveButton(getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alertDialog.show();
                    break;
                case NOBLACKBLOCK:
                    Toast.makeText(DaYingActivity.this, R.string.maker_not_find, Toast.LENGTH_SHORT).show();
                    break;
//                case PRINTVERSION:
//
//                    if (msg.obj.equals("1")) {
//                        textPrintVersion.setText(printVersion);
//                    } else {
//                        Toast.makeText(UsbPrinterActivity.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
//                    }
//                    break;
//                case PRINTBARCODE:
//                    new barcodePrintThread().start();
//                    break;
//                case PRINTQRCODE:
//                    new qrcodePrintThread().start();
//                    break;
//                case PRINTPAPERWALK:
//                    new paperWalkPrintThread().start();
//                    break;
//                case PRINTCONTENT:
//                    new contentPrintThread().start();
//                    break;
//                case MAKER:
//                    new MakerThread().start();
//                    break;
                case PRINTPICTURE:
                    new printPicture().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !DaYingActivity.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(DaYingActivity.this);
                    overHeatDialog.setTitle(R.string.operation_result);
                    overHeatDialog.setMessage(getString(R.string.overTemp));
                    overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    overHeatDialog.show();
                    break;
                default:
                    Toast.makeText(DaYingActivity.this, "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daying);

        jiudianbean= MyAppLaction.jiuDianBean;
        Type resultType2 = new TypeToken<String>() {
        }.getType();
        Reservoir.getAsync("zhuji", resultType2, new ReservoirGetCallback<String>() {
            @Override
            public void onSuccess(final String i) {
                zhuji=i;
            link_tianqi3();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("InFoActivity", "获取本地异常ip:"+e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast tastyToast= TastyToast.makeText(DaYingActivity.this,"获取地址失败",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        tastyToast.setGravity(Gravity.CENTER,0,0);
                        tastyToast.show();
                    }
                });


            }

        });


        name=getIntent().getStringExtra("name");
        shoufangren=getIntent().getStringExtra("shoufangren");
        riqi=getIntent().getStringExtra("time");
        id=getIntent().getStringExtra("idid");

        logo=(ImageView) findViewById(R.id.logo);
        t1= (TextView) findViewById(R.id.name);
        t2= (TextView) findViewById(R.id.danwei);
        t3= (TextView) findViewById(R.id.riqi);
        t4= (TextView) findViewById(R.id.beifangren);
        t5= (TextView) findViewById(R.id.bianha);
        beizhu= (EditText) findViewById(R.id.beizhu);
        erweima= (ImageView) findViewById(R.id.erweima);
        Bitmap bitmap2 = null;
        try {
            bitmap2 = CreateCode(id, BarcodeFormat.CODE_128, 316, 316);
            erweima.setImageBitmap(bitmap2);

        } catch (WriterException e) {
            Log.d("DaYingActivity", e.getMessage());

        }

        if (name!=null){
            t1.setText(name);
        }
        if (riqi!=null){
            t3.setText(riqi);
        }
        if (shoufangren!=null){
            t4.setText(shoufangren);
        }


        handler = new MyHandler();

        baocun= (LinearLayout) findViewById(R.id.baocun);
        dayin= (Button) findViewById(R.id.dayin);
        dayin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              bitmap = createViewBitmap(baocun);

                new printPicture().start();

            }
        });
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mUsbThermalPrinter.start(0);
                    mUsbThermalPrinter.reset();

                } catch (TelpoException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        Button guanbi= (Button) findViewById(R.id.guanbi);
        guanbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("guanbi2");
                sendBroadcast(intent);
                finish();
            }
        });

    }

    private Bitmap createViewBitmap(View view){

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private void noPaperDlg() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(DaYingActivity.this);
        dlg.setTitle(getString(R.string.noPaper));
        dlg.setMessage(getString(R.string.noPaperNotice));
        dlg.setCancelable(false);
        dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dlg.show();
    }

    private class printPicture extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setGray(printGray);
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);

                if (bitmap!=null) {
                    mUsbThermalPrinter.printLogo(bitmap,false);
                    mUsbThermalPrinter.walkPaper(20);
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(DaYingActivity.this, getString(R.string.not_find_picture), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper){
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }

    public Bitmap CreateCode(String str, com.google.zxing.BarcodeFormat type, int bmpWidth, int bmpHeight) throws WriterException {
        Hashtable<EncodeHintType,String> mHashtable = new Hashtable<EncodeHintType,String>();
        mHashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 生成二维矩阵,编码时要指定大小,不要生成了图片以后再进行缩放,以防模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight, mHashtable);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组（一直横着排）
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private void link_tianqi3() {
        //final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        //http://192.168.2.4:8080/sign?cmd=getUnSignList&subjectId=jfgsdf
        OkHttpClient okHttpClient= new OkHttpClient.Builder()
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();


//    /* form的分割线,自己定义 */
//        String boundary = "xx--------------------------------------------------------------xx";
        RequestBody body = new FormBody.Builder()
                .add("accountId",jiudianbean.getId())
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                // .header("Content-Type", "application/json")
                .post(body)
                .url(zhuji + "/getTemplate.do");


        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast tastyToast= TastyToast.makeText(DaYingActivity.this,"上传图片出错，请返回后重试！",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        tastyToast.setGravity(Gravity.CENTER,0,0);
                        tastyToast.show();
                    }
                });
                Log.d("AllConnects", "请求识别失败"+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.d("AllConnects", "请求识别成功"+call.request().toString());
                //获得返回体
                try {

                    ResponseBody body = response.body();
                    // Log.d("AllConnects", "识别结果返回"+response.body().string());
                    String ss=body.string().trim();
                    Log.d("InFoActivity", ss);
                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson=new Gson();
                    final LogoBean logoBean=gson.fromJson(jsonObject,LogoBean.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(DaYingActivity.this).load(zhuji+"/upload/logo/"+logoBean.getLogo()).asBitmap().into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    logo.setImageBitmap(resource);
                                }
                            });
                        }
                    });


                }catch (Exception e){

                    Log.d("WebsocketPushMsg", e.getMessage());
                }
            }
        });

    }
}
