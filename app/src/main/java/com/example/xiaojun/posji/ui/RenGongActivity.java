package com.example.xiaojun.posji.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.xiaojun.posji.MyAppLaction;
import com.example.xiaojun.posji.R;
import com.example.xiaojun.posji.beans.BaoCunBean;
import com.example.xiaojun.posji.beans.BaoCunBeanDao;
import com.example.xiaojun.posji.beans.BuMenBeans;
import com.example.xiaojun.posji.beans.ChuanSongBean;
import com.example.xiaojun.posji.beans.Photos;
import com.example.xiaojun.posji.beans.PopupWindowAdapter;
import com.example.xiaojun.posji.beans.PopupWindowAdapter2;
import com.example.xiaojun.posji.beans.ShouFangBean;
import com.example.xiaojun.posji.dialog.JiaZaiDialog;
import com.example.xiaojun.posji.dialog.TiJIaoDialog;
import com.example.xiaojun.posji.utils.DateUtils;
import com.example.xiaojun.posji.utils.FileUtil;
import com.example.xiaojun.posji.utils.GsonUtil;
import com.example.xiaojun.posji.view.GlideCircleTransform;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sdsmdg.tastytoast.TastyToast;
import org.parceler.Parcels;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

public class RenGongActivity extends Activity {
    private final int REQUEST_TAKE_PHOTO=33;
    private File mSavePhotoFile;
    private ImageView paizhao;
    private EditText name,brdianhua,lfrdianhua,beifangren;
    private TextView riqi,beizhu,renshu;
    private Button baocun;
    public static final int TIMEOUT = 1000 * 60;
    private BaoCunBeanDao baoCunBeanDao=null;
    private BaoCunBean baoCunBean=null;
    private String zhuji=null;
    private JiaZaiDialog jiaZaiDialog=null;
    private Photos photos=null;
    private TiJIaoDialog tiJIaoDialog=null;
    private SensorInfoReceiver sensorInfoReceiver;
    private boolean isTiJiao=false;
    private List<String> stringList=new ArrayList<>();
    private List<String> stringList2=new ArrayList<>();
    private PopupWindow popupWindow=null;
    private PopupWindowAdapter adapterss;
    private PopupWindowAdapter2 adapterss2;
    private int p3=-1,p4=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ren_gong);

        String fn = "bbbb.jpg";
        FileUtil.isExists(FileUtil.PATH, fn);
        mSavePhotoFile=new File( FileUtil.SDPATH + File.separator + FileUtil.PATH + File.separator + fn);
        baoCunBeanDao= MyAppLaction.myAppLaction.getDaoSession().getBaoCunBeanDao();
        baoCunBean=baoCunBeanDao.load(123456L);
        if (baoCunBean!=null && baoCunBean.getZhuji()!=null){
            zhuji=baoCunBean.getZhuji();
        }else {
            Toast tastyToast= TastyToast.makeText(RenGongActivity.this,"请先设置主机地址",TastyToast.LENGTH_LONG,TastyToast.ERROR);
            tastyToast.setGravity(Gravity.CENTER,0,0);
            tastyToast.show();
            return;
        }

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("guanbi2");
        sensorInfoReceiver = new SensorInfoReceiver();
        registerReceiver(sensorInfoReceiver, intentFilter1);
        stringList2.add("业务");
        stringList2.add("合作");
        stringList2.add("面试");
        stringList2.add("其它");
        initView();
        link_bumen();

    }

    private class SensorInfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals("guanbi2")){
                finish();
            }
        }
    }

    private void initView() {
        paizhao= (ImageView) findViewById(R.id.paizhao);
        paizhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });
        baocun= (Button) findViewById(R.id.wancheng);
        baocun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存
                if (photos==null){
                    Toast tastyToast= TastyToast.makeText(RenGongActivity.this,"请点击顶部圆形图标拍照",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    tastyToast.setGravity(Gravity.CENTER,0,0);
                    tastyToast.show();
                }else if (name.getText().toString().trim().equals("")){
                    Toast tastyToast= TastyToast.makeText(RenGongActivity.this,"请填写姓名",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    tastyToast.setGravity(Gravity.CENTER,0,0);
                    tastyToast.show();

                }else {

                    try {
                        if (isTiJiao){

                            link_save();

                        }else {
                            Toast tastyToast= TastyToast.makeText(RenGongActivity.this,"人脸质量太低，请重新拍摄",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                            tastyToast.setGravity(Gravity.CENTER,0,0);
                            tastyToast.show();
                        }

                    }catch (Exception e){
                        Log.d("RenGongActivity", e.getMessage()+"");
                    }

                }

            }
        });
        riqi= (TextView) findViewById(R.id.qixian);
        riqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RenGongActivity.this, DatePickActivity.class);
                startActivityForResult(intent,2);
            }
        });
        riqi.setText(DateUtils.timet2(System.currentTimeMillis()+""));
        name= (EditText) findViewById(R.id.name);
        brdianhua= (EditText) findViewById(R.id.dianhua);
        lfrdianhua= (EditText) findViewById(R.id.chepai);
        beifangren= (EditText) findViewById(R.id.dizhi);
        renshu= (TextView) findViewById(R.id.renshu);
        renshu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View contentView = LayoutInflater.from(RenGongActivity.this).inflate(R.layout.xiangmu_po_item, null);
                popupWindow=new PopupWindow(contentView,400, 560);
                ListView listView= (ListView) contentView.findViewById(R.id.dddddd);
                adapterss=new PopupWindowAdapter(RenGongActivity.this,stringList);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        renshu.setText(stringList.get(position));
                        popupWindow.dismiss();
                    }
                });
                listView.setAdapter(adapterss);

                popupWindow.setFocusable(true);//获取焦点
                popupWindow.setOutsideTouchable(true);//获取外部触摸事件
                popupWindow.setTouchable(true);//能够响应触摸事件
                popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));//设置背景
                popupWindow.showAsDropDown(renshu,renshu.getLeft()-150,0);
            }
        });
        beizhu= (TextView) findViewById(R.id.mudi);
        beizhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View contentView = LayoutInflater.from(RenGongActivity.this).inflate(R.layout.xiangmu_po_item, null);
                popupWindow=new PopupWindow(contentView,400, 390);
                ListView listView= (ListView) contentView.findViewById(R.id.dddddd);
                adapterss2=new PopupWindowAdapter2(RenGongActivity.this,stringList2);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        beizhu.setText(stringList2.get(position));
                        popupWindow.dismiss();
                    }
                });
                listView.setAdapter(adapterss2);

                popupWindow.setFocusable(true);//获取焦点
                popupWindow.setOutsideTouchable(true);//获取外部触摸事件
                popupWindow.setTouchable(true);//能够响应触摸事件
                popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));//设置背景
                popupWindow.showAsDropDown(beizhu,beizhu.getLeft()-150,0);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:  //拍照
                    //注意，如果拍照的时候设置了MediaStore.EXTRA_OUTPUT，data.getData=null
                   // paizhao.setImageURI(Uri.fromFile(mSavePhotoFile));
                    try {
                        link_P2();
                    }catch (Exception e){
                        Log.d("RenGongActivity", e.getMessage()+"");
                    }


                    break;

            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            // 选择预约时间的页面被关闭
            String date = data.getStringExtra("date");
            riqi.setText(date);
        }
    }

    /**
     * 启动拍照
     * @param
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

    private void link_P2() {

        if (jiaZaiDialog==null){
            jiaZaiDialog=new JiaZaiDialog(RenGongActivity.this);
            jiaZaiDialog.setText("上传图片中...");
            jiaZaiDialog.show();
        }
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
                            jiaZaiDialog=null;
                        }
                        Toast tastyToast= TastyToast.makeText(RenGongActivity.this,"上传图片出错，请重试！",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        tastyToast.setGravity(Gravity.CENTER,0,0);
                        tastyToast.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求识别成功"+call.request().toString());
                //删掉文件
                if (jiaZaiDialog!=null && jiaZaiDialog.isShowing()){
                    jiaZaiDialog.dismiss();
                    jiaZaiDialog=null;
                }
                //获得返回体
                try {

                    ResponseBody body = response.body();
                    String ss=body.string();
                     Log.d("AllConnects", "aa  "+ss);

                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson=new Gson();
                    photos= gson.fromJson(jsonObject,Photos.class);



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            link_zhiliang();
                            Glide.with(RenGongActivity.this)
                                    .load(mSavePhotoFile)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .transform(new GlideCircleTransform(RenGongActivity.this,1, Color.parseColor("#ffffffff")))
                                    .into(paizhao);
                        }
                    });

                }catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (jiaZaiDialog!=null && jiaZaiDialog.isShowing()){
                                jiaZaiDialog.dismiss();
                                jiaZaiDialog=null;
                            }
                            Toast tastyToast= TastyToast.makeText(RenGongActivity.this,"上传图片出错，请重试！",TastyToast.LENGTH_LONG,TastyToast.ERROR);
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
        if (sensorInfoReceiver!=null)
        unregisterReceiver(sensorInfoReceiver);
    }

    private void link_save() {
        if (tiJIaoDialog==null){
            tiJIaoDialog=new TiJIaoDialog(RenGongActivity.this);
            tiJIaoDialog.show();
        }

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
                .add("name",name.getText().toString().trim()+"")
                .add("phone",brdianhua.getText().toString().trim()+"")
                .add("visitDate2",riqi.getText().toString().trim()+"")
                .add("visitPerson",beifangren.getText().toString().trim()+"")
                .add("visitDepartment",renshu.getText().toString().trim()+"")
                .add("visitNum","1")
                .add("homeNumber",lfrdianhua.getText().toString().trim())
                .add("accountId",baoCunBean.getZhangHuID())
                .add("scanPhoto",photos.getExDesc())
                .add("visitIncident",beizhu.getText().toString().trim()+"")
                .add("cardNumber","rt"+System.currentTimeMillis())
                .add("source","1")
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                // .header("Content-Type", "application/json")
                .post(body)
                .url(zhuji + "/saveCompareVisit.do");

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
                    Log.d("DengJiActivity", ss);

                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson=new Gson();
                    ShouFangBean zhaoPianBean=gson.fromJson(jsonObject,ShouFangBean.class);

                    if (zhaoPianBean.getDtoResult()==0){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast tastyToast= TastyToast.makeText(RenGongActivity.this,"提交成功!",TastyToast.LENGTH_LONG,TastyToast.INFO);
                                tastyToast.setGravity(Gravity.CENTER,0,0);
                                tastyToast.show();

                            }
                        });
                        //   Log.d("DengJiActivity", "dddd");

//                        ChuanSongBean bean=new ChuanSongBean(name.getText().toString(),3,zhaoPianBean.getSid(),lfrdianhua.getText().toString().trim()
//                                ,beifangren.getText().toString().trim(),riqi.getText().toString().trim(),"");
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelable("chuansong", Parcels.wrap(bean));
//                        startActivity(new Intent(RenGongActivity.this,ShiYouActivity.class).putExtras(bundle));



//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                final YuYueDialog dialog=new YuYueDialog(RenGongActivity.this,"你已成功预约,感谢你的来访!");
//                                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
//                                dialog.setOnPositiveListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        dialog.dismiss();
//                                        finish();
//                                    }
//                                });
//                                dialog.show();
//                            }
//                        });


                    }else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast tastyToast= TastyToast.makeText(RenGongActivity.this,"提交失败",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                                tastyToast.setGravity(Gravity.CENTER,0,0);
                                tastyToast.show();

                            }
                        });


                    }

                }catch (Exception e){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tiJIaoDialog!=null){
                                tiJIaoDialog.dismiss();
                                tiJIaoDialog=null;
                            }
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast tastyToast= TastyToast.makeText(RenGongActivity.this,"提交失败,请检查网络",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                            tastyToast.setGravity(Gravity.CENTER,0,0);
                            tastyToast.show();

                        }
                    });
                    Log.d("WebsocketPushMsg", e.getMessage());
                }
            }
        });


    }

    private void link_zhiliang() {
        if (tiJIaoDialog==null){
            tiJIaoDialog=new TiJIaoDialog(RenGongActivity.this);
            tiJIaoDialog.show();
        }

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
                .add("scanPhoto",photos.getExDesc())
                .add("accountId",baoCunBean.getZhangHuID())
                .build();


        Request.Builder requestBuilder = new Request.Builder()
                // .header("Content-Type", "application/json")
                .post(body)
                .url(zhuji + "/faceQuality.do");

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
                    Log.d("DengJiActivity", ss);

                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson=new Gson();
                    ShouFangBean zhaoPianBean=gson.fromJson(jsonObject,ShouFangBean.class);

                    if (zhaoPianBean.getDtoResult()!=0){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast tastyToast= TastyToast.makeText(RenGongActivity.this,"照片质量不符合入库要求,请拍正面照!",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                                tastyToast.setGravity(Gravity.CENTER,0,0);
                                tastyToast.show();

                            }
                        });

                    }else {
                        isTiJiao=true;
                    }

                }catch (Exception e){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tiJIaoDialog!=null){
                                tiJIaoDialog.dismiss();
                                tiJIaoDialog=null;
                            }
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast tastyToast= TastyToast.makeText(RenGongActivity.this,"提交失败,请检查网络",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                            tastyToast.setGravity(Gravity.CENTER,0,0);
                            tastyToast.show();

                        }
                    });
                    Log.d("WebsocketPushMsg", e.getMessage());
                }
            }
        });


    }

    private void link_bumen() {

        //final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        //http://192.168.2.4:8080/sign?cmd=getUnSignList&subjectId=jfgsdf
        OkHttpClient okHttpClient= new OkHttpClient.Builder()
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();

        if (null!=baoCunBean.getZhuji()) {

            //    /* form的分割线,自己定义 */
            //        String boundary = "xx--------------------------------------------------------------xx";
            RequestBody body = new FormBody.Builder()
                    .add("status","1")
                    .add("token",System.currentTimeMillis()+"")
                    .add("accountId",baoCunBean.getZhangHuID())
                    .build();

            Request.Builder requestBuilder = new Request.Builder()
                    // .header("Content-Type", "application/json")
                    .post(body)
                    .url(baoCunBean.getZhuji() + "/queryAllDept.do");

            // step 3：创建 Call 对象
            Call call = okHttpClient.newCall(requestBuilder.build());

            //step 4: 开始异步请求
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("AllConnects", "请求识别失败" + e.getMessage());


                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String ss=null;
                    Log.d("AllConnects", "请求识别成功" + call.request().toString());
                    //获得返回体
                    try {

                        ResponseBody body = response.body();
                        ss = body.string().trim();
                        Log.d("DengJiActivity", ss);

                        JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                        Gson gson = new Gson();
                        BuMenBeans zhaoPianBean = gson.fromJson(jsonObject, BuMenBeans.class);
                        int size=zhaoPianBean.getObjects().size();
                        if (stringList.size()>0){
                            stringList.clear();
                        }
                        for (int i=0;i<size;i++){
                            stringList.add(zhaoPianBean.getObjects().get(i).getDeptName());
                        }
                        Collections.reverse(stringList); // 倒序排列


                    } catch (Exception e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (tiJIaoDialog != null && !RenGongActivity.this.isFinishing()) {
                                    tiJIaoDialog.dismiss();
                                    tiJIaoDialog = null;
                                }
                            }
                        });

                        Log.d("WebsocketPushMsg", e.getMessage());
                    }
                }
            });
        }else {
            Toast tastyToast = TastyToast.makeText(RenGongActivity.this, "账户ID为空!,请设置帐户ID", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            tastyToast.setGravity(Gravity.CENTER, 0, 0);
            tastyToast.show();
        }
    }
}
