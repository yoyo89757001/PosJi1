package com.example.xiaojun.posji.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.xiaojun.posji.MyAppLaction;
import com.example.xiaojun.posji.R;
import com.example.xiaojun.posji.beans.BaoCunBean;
import com.example.xiaojun.posji.beans.BaoCunBeanDao;
import com.example.xiaojun.posji.beans.ChuanSongBean;
import com.example.xiaojun.posji.beans.DaYingXinXiBean;
import com.example.xiaojun.posji.beans.DaYingXinXiBeanDao;
import com.example.xiaojun.posji.beans.FanHuiBean;
import com.example.xiaojun.posji.beans.Photos;
import com.example.xiaojun.posji.beans.ShiBieBean;
import com.example.xiaojun.posji.beans.ShouFangBean;
import com.example.xiaojun.posji.beans.UserInfoBena;
import com.example.xiaojun.posji.beans.YuYueBean;
import com.example.xiaojun.posji.beans.YuYueInterface;
import com.example.xiaojun.posji.dialog.JiaZaiDialog;
import com.example.xiaojun.posji.dialog.TiJIaoDialog;
import com.example.xiaojun.posji.dialog.XuanZeDialog;
import com.example.xiaojun.posji.utils.DateUtils;
import com.example.xiaojun.posji.utils.FileUtil;
import com.example.xiaojun.posji.utils.GsonUtil;
import com.example.xiaojun.posji.utils.ImageUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sdsmdg.tastytoast.TastyToast;

import org.parceler.Parcels;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class YuYueActivity extends Activity implements YuYueInterface {
    private EditText name,shouji,biduijieguo,xiangsidu;
    private ImageView p1,p2;
    private Button chaxun,xiayibu;
    public static final int TIMEOUT = 1000 * 60;
    private BaoCunBeanDao baoCunBeanDao=null;
    private BaoCunBean baoCunBean=null;
    private String zhuji=null;
    private TiJIaoDialog tiJIaoDialog=null;
    private List<YuYueBean.ObjectsBean> objectsBeanList=null;
    private String shengfenzhengPath=null;
    private final int REQUEST_TAKE_PHOTO=33;
    private File mSavePhotoFile;
    private JiaZaiDialog  jiaZaiDialog=null;
    private UserInfoBena userInfoBena=null;
    private XuanZeDialog xuanZeDialog=null;
    private boolean isPai=false;
    private YuYueBean.ObjectsBean objectsBean=null;
    private SensorInfoReceiver sensorInfoReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yu_yue);
        baoCunBeanDao= MyAppLaction.myAppLaction.getDaoSession().getBaoCunBeanDao();
        baoCunBean=baoCunBeanDao.load(123456L);

        if (baoCunBean!=null && baoCunBean.getZhuji()!=null){
            zhuji=baoCunBean.getZhuji();
        }else {
            Toast tastyToast= TastyToast.makeText(YuYueActivity.this,"请先设置主机地址",TastyToast.LENGTH_LONG,TastyToast.ERROR);
            tastyToast.setGravity(Gravity.CENTER,0,0);
            tastyToast.show();
        }
        userInfoBena=new UserInfoBena();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("guanbi2");
        sensorInfoReceiver = new SensorInfoReceiver();
        registerReceiver(sensorInfoReceiver, intentFilter1);

        String fn = "bbbb.jpg";
        FileUtil.isExists(FileUtil.PATH, fn);
        mSavePhotoFile=new File( FileUtil.SDPATH + File.separator + FileUtil.PATH + File.separator + fn);

        initView();
    }

    private class SensorInfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals("guanbi2")) {
                finish();

            }
        }}

    private void initView() {
        name= (EditText) findViewById(R.id.name);
        shouji= (EditText) findViewById(R.id.shouji);
        biduijieguo= (EditText) findViewById(R.id.jieguo);
        xiangsidu= (EditText) findViewById(R.id.xiangsidu);
        p1= (ImageView) findViewById(R.id.zhengjian);
        p2= (ImageView) findViewById(R.id.paizhao);
        p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });
        chaxun= (Button) findViewById(R.id.wancheng);
        chaxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().equals("")&& shouji.getText().toString().trim().equals("")){
                    Toast tastyToast = TastyToast.makeText(YuYueActivity.this, "请填写姓名和手机号", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    tastyToast.setGravity(Gravity.CENTER, 0, 0);
                    tastyToast.show();
                }else {
                    link_chaxun();
                }

            }
        });
        xiayibu= (Button) findViewById(R.id.xiayibu);
        xiayibu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( shengfenzhengPath!=null && isPai){
                    link_P1(shengfenzhengPath);
                }else {

                    Toast tastyToast = TastyToast.makeText(YuYueActivity.this, "请先点击拍照", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    tastyToast.setGravity(Gravity.CENTER, 0, 0);
                    tastyToast.show();
                }

            }
        });

    }

    private void link_chaxun() {
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
        RequestBody body=null;
        try {

            body = new FormBody.Builder()
                    .add("name",name.getText().toString().trim())
                    .add("audit","0")
                    .add("compareTimeStart", DateUtils.timeData(System.currentTimeMillis()+"")+" 00:00")
                    .add("compareTimeEnd",DateUtils.timeData(System.currentTimeMillis()+"")+" 23:59")
                    .add("pageNum","1")
                    .add("pageSize","10")
                    .add("accountId",baoCunBean.getZhangHuID())
                    .add("visitDepartment",shouji.getText().toString().trim())
                    .build();


            Request.Builder requestBuilder = new Request.Builder()
                    // .header("Content-Type", "application/json")
                    .post(body)
                    .url(zhuji + "/iqueryCompares.do");

            if (tiJIaoDialog==null){
                tiJIaoDialog=new TiJIaoDialog(YuYueActivity.this);
                tiJIaoDialog.show();
            }

            // step 3：创建 Call 对象
            Call call = okHttpClient.newCall(requestBuilder.build());

            //step 4: 开始异步请求
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("AllConnects", "请求识别失败"+e.getMessage());
                    if (tiJIaoDialog!=null){
                        tiJIaoDialog.dismiss();
                        tiJIaoDialog=null;
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    Log.d("AllConnects", "请求识别成功"+call.request().toString());
                    //获得返回体
                    try {

                        ResponseBody body = response.body();
                        String ss=body.string().trim();
                        Log.d("InFoActivity", ss);
                        JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                        Gson gson=new Gson();
                        YuYueBean yuYueBean=gson.fromJson(jsonObject,YuYueBean.class);
                        objectsBeanList=yuYueBean.getObjects();
                        int size=objectsBeanList.size();
                        if (size==1){
                            objectsBean=objectsBeanList.get(0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Glide.with(YuYueActivity.this).load(zhuji+"/upload/compare/"+ objectsBeanList.get(0).getScanPhoto()).asBitmap().into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {


                                            final String fn="yuyuezhao.jpg";
                                            FileUtil.isExists(FileUtil.PATH,fn);
                                            saveBitmap2File(resource.copy(Bitmap.Config.ARGB_8888,false), FileUtil.SDPATH+ File.separator+FileUtil.PATH+File.separator+fn,100);

                                        }
                                    });
                                }
                            });

                        }else if (size>1){

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                     xuanZeDialog=new XuanZeDialog(YuYueActivity.this,objectsBeanList,zhuji,YuYueActivity.this);
                                    xuanZeDialog.setCanceledOnTouchOutside(false);
                                     xuanZeDialog.show();
                                }
                            });


                        }else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (tiJIaoDialog!=null){
                                        tiJIaoDialog.dismiss();
                                        tiJIaoDialog=null;
                                    }

                                    Toast tastyToast = TastyToast.makeText(YuYueActivity.this, "没有查询到数据", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                                    tastyToast.setGravity(Gravity.CENTER, 0, 0);
                                    tastyToast.show();

                                }
                            });
                        }


                    }catch (Exception e){

                        if (tiJIaoDialog!=null){
                            tiJIaoDialog.dismiss();
                            tiJIaoDialog=null;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast tastyToast = TastyToast.makeText(YuYueActivity.this, "查询失败...", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                                tastyToast.show();

                            }
                        });
                        Log.d("WebsocketPushMsg", e.getMessage());
                    }
                }
            });

        }catch (NullPointerException e){
            Log.d("InFoActivity2", e.getMessage()+"");
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:  //拍照
                    //注意，如果拍照的时候设置了MediaStore.EXTRA_OUTPUT，data.getData=null
                   // p2.setImageURI(Uri.fromFile(mSavePhotoFile));
                    isPai=true;
                    Glide.with(YuYueActivity.this)
                            .load(mSavePhotoFile)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                           // .transform(new GlideCircleTransform(RenGongFuWuActivity.this,1, Color.parseColor("#ffffffff")))
                            .into(p2);
                    break;

            }
        }
    }

    /**
     * 启动拍照
     */
    private void startCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Continue only if the File was successfully created
            if (mSavePhotoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(mSavePhotoFile));//设置文件保存的URI
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    /***
     *保存bitmap对象到文件中
     */
    public  void saveBitmap2File(Bitmap bm, final String path, int quality) {
        if (null == bm || bm.isRecycled()) {
            Log.d("InFoActivity", "回收|空");
            return ;
        }
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
            shengfenzhengPath=path;
            p1.setImageBitmap(ImageUtil.decodeSampledBitmapFromFilePath(shengfenzhengPath,260,260));
            if (tiJIaoDialog!=null){
                tiJIaoDialog.dismiss();
                tiJIaoDialog=null;
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            if (!bm.isRecycled()) {
                bm.recycle();
            }
            bm = null;
        }
    }

    private void link_P1(String filename1) {
        jiaZaiDialog=new JiaZaiDialog(YuYueActivity.this);
        jiaZaiDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        jiaZaiDialog.setText("上传图片中...");
        jiaZaiDialog.show();

        //final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        //http://192.168.2.4:8080/sign?cmd=getUnSignList&subjectId=jfgsdf
        OkHttpClient okHttpClient= new OkHttpClient.Builder()
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();

         /* 第一个要上传的file */
        File file1 = new File(filename1);
        RequestBody fileBody1 = RequestBody.create(MediaType.parse("application/octet-stream") , file1);
        final String file1Name = System.currentTimeMillis()+"testFile1.jpg";

//    /* 第二个要上传的文件,*/
//        File file2 = new File(fileName2);
//        RequestBody fileBody2 = RequestBody.create(MediaType.parse("application/octet-stream") , file2);
//        String file2Name =System.currentTimeMillis()+"testFile2.jpg";


//    /* form的分割线,自己定义 */
//        String boundary = "xx--------------------------------------------------------------xx";

        MultipartBody mBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
            /* 底下是上传了两个文件 */
                .addFormDataPart("voiceFile" , file1Name , fileBody1)
                  /* 上传一个普通的String参数 */
                //  .addFormDataPart("subject_id" , subject_id+"")
                //  .addFormDataPart("image_2" , file2Name , fileBody2)
                .build();
        Request.Builder requestBuilder = new Request.Builder()
                // .header("Content-Type", "application/json")
                .post(mBody)
                .url(zhuji + "/AppFileUploadServlet?FilePathPath=cardFilePath&AllowFileType=.jpg,.gif,.jpeg,.bmp,.png&MaxFileSize=10");

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (jiaZaiDialog!=null && jiaZaiDialog.isShowing()){
                            jiaZaiDialog.dismiss();
                        }
                        Toast tastyToast= TastyToast.makeText(YuYueActivity.this,"上传图片出错，请返回后重试！",TastyToast.LENGTH_LONG,TastyToast.ERROR);
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
                    String ss=body.string();

                    Log.d("AllConnects", "aa   "+ss);

                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson=new Gson();
                    Photos zhaoPianBean=gson.fromJson(jsonObject,Photos.class);
                    userInfoBena.setCardPhoto(zhaoPianBean.getExDesc());
                    link_P2();


                }catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (jiaZaiDialog!=null && jiaZaiDialog.isShowing()){
                                jiaZaiDialog.dismiss();
                            }
                            Toast tastyToast= TastyToast.makeText(YuYueActivity.this,"上传图片出错，请返回后重试！",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                            tastyToast.setGravity(Gravity.CENTER,0,0);
                            tastyToast.show();
                        }
                    });
                    Log.d("WebsocketPushMsg", e.getMessage());
                }
            }
        });

    }

    private void link_P2() {
        //final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        //http://192.168.2.4:8080/sign?cmd=getUnSignList&subjectId=jfgsdf
        OkHttpClient okHttpClient= new OkHttpClient.Builder()
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();

//         /* 第一个要上传的file */
//        File file1 = new File(filename1);
//        RequestBody fileBody1 = RequestBody.create(MediaType.parse("application/octet-stream") , file1);
//        final String file1Name = System.currentTimeMillis()+"testFile1.jpg";

    /* 第二个要上传的文件,*/
        //file2 = new File(fileName2);

        RequestBody fileBody2 = RequestBody.create(MediaType.parse("application/octet-stream") , mSavePhotoFile);
        String file2Name =System.currentTimeMillis()+"testFile2.jpg";


//    /* form的分割线,自己定义 */
//        String boundary = "xx--------------------------------------------------------------xx";

        MultipartBody mBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
            /* 底下是上传了两个文件 */
                //  .addFormDataPart("image_1" , file1Name , fileBody1)
                  /* 上传一个普通的String参数 */
                //  .addFormDataPart("subject_id" , subject_id+"")
                .addFormDataPart("voiceFile" , file2Name , fileBody2)
                .build();
        Request.Builder requestBuilder = new Request.Builder()
                // .header("Content-Type", "application/json")
                .post(mBody)
                .url(zhuji + "/AppFileUploadServlet?FilePathPath=compareFilePath&AllowFileType=.jpg,.gif,.jpeg,.bmp,.png&MaxFileSize=10");


        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求识别失败"+e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (jiaZaiDialog!=null && jiaZaiDialog.isShowing()){
                            jiaZaiDialog.dismiss();
                        }
                        Toast tastyToast= TastyToast.makeText(YuYueActivity.this,"上传图片出错，请返回后重试！",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        tastyToast.setGravity(Gravity.CENTER,0,0);
                        tastyToast.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求识别成功"+call.request().toString());
                //删掉文件

                //获得返回体
                try {

                    ResponseBody body = response.body();
                    // Log.d("AllConnects", "aa   "+response.body().string());

                    JsonObject jsonObject= GsonUtil.parse(body.string()).getAsJsonObject();
                    Gson gson=new Gson();
                    Photos zhaoPianBean=gson.fromJson(jsonObject,Photos.class);
                    userInfoBena.setScanPhoto(zhaoPianBean.getExDesc());

                    link_tianqi3();


                }catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (jiaZaiDialog!=null && jiaZaiDialog.isShowing()){
                                jiaZaiDialog.dismiss();
                            }
                            Toast tastyToast= TastyToast.makeText(YuYueActivity.this,"上传图片出错，请返回后重试！",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                            tastyToast.setGravity(Gravity.CENTER,0,0);
                            tastyToast.show();
                        }
                    });
                    Log.d("WebsocketPushMsg", e.getMessage());
                }
            }
        });

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
                .add("cardPhoto",userInfoBena.getCardPhoto())
                .add("scanPhoto",userInfoBena.getScanPhoto())
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                // .header("Content-Type", "application/json")
                .post(body)
                .url(zhuji + "/compare.do");


        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (jiaZaiDialog!=null && jiaZaiDialog.isShowing()){
                            jiaZaiDialog.dismiss();
                        }
                        Toast tastyToast= TastyToast.makeText(YuYueActivity.this,"上传图片出错，请返回后重试！",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        tastyToast.setGravity(Gravity.CENTER,0,0);
                        tastyToast.show();
                    }
                });
                Log.d("AllConnects", "请求识别失败"+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (jiaZaiDialog!=null && jiaZaiDialog.isShowing()){
                            jiaZaiDialog.dismiss();
                        }
                    }
                });
                Log.d("AllConnects", "请求识别成功"+call.request().toString());
                //获得返回体
                try {

                    ResponseBody body = response.body();
                    // Log.d("AllConnects", "识别结果返回"+response.body().string());
                    String ss=body.string();
                   // Log.d("InFoActivity", ss);
                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson=new Gson();
                    final ShiBieBean zhaoPianBean=gson.fromJson(jsonObject,ShiBieBean.class);
                    Log.d("YuYueActivity", ":" + zhaoPianBean.getFace_info_1().getBrightness());

                    if (zhaoPianBean.getScore()>=65.0) {
                        //比对成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                xiayibu.setEnabled(false);
                                xiangsidu.setText((zhaoPianBean.getScore()+"").substring(0,5));
                                biduijieguo.setText("比对成功");
                                link_shenghe();

                            }
                        });


                    }else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                xiangsidu.setText((zhaoPianBean.getScore()+"").substring(0,5));
                                biduijieguo.setText("比对失败");

                                Toast tastyToast= TastyToast.makeText(YuYueActivity.this,"比对失败了,请重试！",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                                tastyToast.setGravity(Gravity.CENTER,0,0);
                                tastyToast.show();
                            }
                        });

                    }

                }catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (jiaZaiDialog!=null && jiaZaiDialog.isShowing()){
                                jiaZaiDialog.dismiss();
                            }
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            biduijieguo.setText("人脸质量差");
                            xiangsidu.setText("");
                            Toast tastyToast= TastyToast.makeText(YuYueActivity.this,"比对失败了,人脸质量差！",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                            tastyToast.setGravity(Gravity.CENTER,0,0);
                            tastyToast.show();
                        }
                    });

                    Log.d("WebsocketPushMsg", e.getMessage());
                }
            }
        });

    }

    @Override
    public void setP( int position) {

        objectsBean=objectsBeanList.get(position);

        Glide.with(YuYueActivity.this).load(zhuji+"/upload/compare/"+ objectsBeanList.get(position).getScanPhoto()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                xuanZeDialog.dismiss();
                xuanZeDialog=null;

                String fn="yuyuezhao.jpg";
                FileUtil.isExists(FileUtil.PATH,fn);
                saveBitmap2File(resource.copy(Bitmap.Config.ARGB_8888,false), FileUtil.SDPATH+ File.separator+FileUtil.PATH+File.separator+fn,100);


            }
        });
    }


    private void link_shenghe() {

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
                .add("audit","1")
                .add("accountId",baoCunBean.getZhangHuID())
                .add("reason","默认POS机审核")
                .add("id",objectsBean.getId()+"")
                .build();


        Request.Builder requestBuilder = new Request.Builder()
                // .header("Content-Type", "application/json")
                .post(body)
                .url(zhuji + "/iauditVisitor.do");

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求识别失败"+e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        xiayibu.setEnabled(true);
                        if (tiJIaoDialog!=null){
                            tiJIaoDialog.dismiss();
                            tiJIaoDialog=null;
                        }
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        xiayibu.setEnabled(true);
                        if (tiJIaoDialog!=null){
                            tiJIaoDialog.dismiss();
                            tiJIaoDialog=null;
                        }
                    }
                });
                Log.d("AllConnects", "请求识别成功"+call.request().toString());
                //获得返回体
                try {

                    ResponseBody body = response.body();
                    String ss=body.string().trim();
                  //  Log.d("DengJiActivity", ss);

                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson=new Gson();
                    FanHuiBean fanHuiBean=gson.fromJson(jsonObject,FanHuiBean.class);

                    if (fanHuiBean.getDtoResult()==0){
                        ChuanSongBean bean=new ChuanSongBean(name.getText().toString(),2,objectsBean.getId(),objectsBean.getPhone()
                                ,objectsBean.getVisitPerson(),DateUtils.timet2(objectsBean.getVisitDate()+""),"");
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("chuansong", Parcels.wrap(bean));
                        startActivity(new Intent(YuYueActivity.this,ShiYouActivity.class).putExtras(bundle));

                    }else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast tastyToast= TastyToast.makeText(YuYueActivity.this,"审核失败",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                                tastyToast.setGravity(Gravity.CENTER,0,0);
                                tastyToast.show();

                            }
                        });

                    }

                }catch (Exception e){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            xiayibu.setEnabled(true);
                            if (tiJIaoDialog!=null){
                                tiJIaoDialog.dismiss();
                                tiJIaoDialog=null;
                            }
                            Toast tastyToast= TastyToast.makeText(YuYueActivity.this,"提交失败,请检查网络",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                            tastyToast.setGravity(Gravity.CENTER,0,0);
                            tastyToast.show();
                        }
                    });
                    Log.d("WebsocketPushMsg", e.getMessage());
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sensorInfoReceiver);
    }
}
