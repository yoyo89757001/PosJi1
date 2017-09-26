package com.example.xiaojun.posji.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.xiaojun.posji.MyAppLaction;
import com.example.xiaojun.posji.R;
import com.example.xiaojun.posji.beans.BaoCunBean;
import com.example.xiaojun.posji.beans.BaoCunBeanDao;
import com.example.xiaojun.posji.beans.ChuanSongBean;
import com.example.xiaojun.posji.beans.ShouFangBean;
import com.example.xiaojun.posji.dialog.TiJIaoDialog;
import com.example.xiaojun.posji.dialog.YuYueDialog;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DengJiActivity extends Activity implements View.OnClickListener {
    private ImageView riqi_im,touxiang;
    private TextView riqi_tv,name,bidui_tv;
    private EditText shoufangren,shoufangrenshu,bumen_ET;
    private Button wancheng;
    private SensorInfoReceiver sensorInfoReceiver;
    public static final int TIMEOUT = 1000 * 60;
    private TiJIaoDialog tiJIaoDialog=null;
    private String zhuji=null;
    private YuYueDialog dialog=null;
    private BaoCunBeanDao baoCunBeanDao=null;
    private BaoCunBean baoCunBean=null;
    private ChuanSongBean chuanSongBean=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deng_ji);

        baoCunBeanDao= MyAppLaction.myAppLaction.getDaoSession().getBaoCunBeanDao();
        baoCunBean=baoCunBeanDao.load(123456L);
        if (baoCunBean!=null && baoCunBean.getZhuji()!=null){
            zhuji=baoCunBean.getZhuji();
        }else {
            Toast tastyToast= TastyToast.makeText(DengJiActivity.this,"请先设置主机地址",TastyToast.LENGTH_LONG,TastyToast.ERROR);
            tastyToast.setGravity(Gravity.CENTER,0,0);
            tastyToast.show();
        }


        chuanSongBean= Parcels.unwrap(getIntent().getParcelableExtra("chuansong"));


        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("guanbi2");
        sensorInfoReceiver = new SensorInfoReceiver();
        registerReceiver(sensorInfoReceiver, intentFilter1);

        touxiang= (ImageView) findViewById(R.id.touxiang);
        bidui_tv= (TextView) findViewById(R.id.bidui_tv);
        name= (TextView) findViewById(R.id.editText);
        bumen_ET= (EditText) findViewById(R.id.bumen);
        riqi_tv= (TextView) findViewById(R.id.riqi);
        shoufangren= (EditText) findViewById(R.id.shoufang);
        shoufangrenshu= (EditText) findViewById(R.id.renshu);
        shoufangrenshu.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (zhuji!=null)
                    link_save();
                }
                return false;
            }
        });
        riqi_im= (ImageView) findViewById(R.id.imageView);
        riqi_im.setOnClickListener(this);

        wancheng= (Button) findViewById(R.id.queren2);
        wancheng.setOnClickListener(this);

        if (chuanSongBean.getBeifangshijian().equals("")){
            riqi_tv.setText(DateUtils.timet2(System.currentTimeMillis()+""));
        }else {
            riqi_tv.setText(chuanSongBean.getBeifangshijian());
        }

        shoufangren.setText(chuanSongBean.getBeifangren());
        name.setText(chuanSongBean.getName());
        bidui_tv.setText("比对通过");


        Glide.with(DengJiActivity.this)
                .load(FileUtil.SDPATH + File.separator + FileUtil.PATH + File.separator + "bbbb.jpg")
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(new GlideCircleTransform(DengJiActivity.this,2,Color.parseColor("#ffffffff")))
                .into(touxiang);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView: //日期

                Intent intent = new Intent(DengJiActivity.this, DatePickActivity.class);
                startActivityForResult(intent,2);

                break;
//            case R.id.imageView2: //部门
//                View popupView =getLayoutInflater().inflate(R.layout.popupwindow, null);
//                //  2016/5/17 为了演示效果，简单的设置了一些数据，实际中大家自己设置数据即可，相信大家都会。
//                ListView lsvMore = (ListView) popupView.findViewById(R.id.lsvMore);
//                lsvMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        xuanzhong=position;
//                        window.dismiss();
//
//                    }
//                });
//                lsvMore.setAdapter(myadapter);
//                //  2016/5/17 创建PopupWindow对象，指定宽度和高度
//                 window = new PopupWindow(popupView, 200, 340);
//                //  2016/5/17 设置动画
//               // window.setAnimationStyle(R.style.AnimBottom2);
//                //  2016/5/17 设置背景颜色
//                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#cc4285f4")));
//                //  2016/5/17 设置可以获取焦点
//                window.setFocusable(true);
//                //  2016/5/17 设置可以触摸弹出框以外的区域
//                window.setOutsideTouchable(true);
//                // 更新popupwindow的状态
//                window.update();
//                //  2016/5/17 以下拉的方式显示，并且可以设置显示的位置
//                window.showAsDropDown(bumen_im, -200,0);
//
//
//                break;
            case R.id.queren2:

                if (zhuji!=null)
                link_save();

                break;

        }

    }

//    private class Myadapter extends BaseAdapter{
//        Context mContext;
//
//        LayoutInflater mInflater;
//
//        List<String> mDataList;
//
//        /**
//         * @param context
//         * @param data
//         */
//        public Myadapter(Context context, List<String> data) {
//            mContext = context;
//            mInflater = LayoutInflater.from(context);
//            mDataList = data;
//        }
//
//        @Override
//        public int getCount() {
//            return mDataList.size();
//        }
//
//        @Override
//        public String getItem(int position) {
//
//            return mDataList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder = null;
//            if (convertView == null) {
//                convertView = mInflater.inflate(R.layout.tachuangdialog, null, false);
//                viewHolder = new ViewHolder();
//                viewHolder.mTextView = (TextView) convertView.findViewById(R.id.fffff);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//
//
//            viewHolder.mTextView.setText(getItem(position));
//            return convertView;
//        }
//
//        /**
//         * ViewHolder
//         *
//         * @author mrsimple
//         */
//        private class ViewHolder {
//
//            TextView mTextView;
//        }
//
//    }

    private class SensorInfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals("guanbi2")) {
                finish();

            }
        }}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            // 选择预约时间的页面被关闭
            String date = data.getStringExtra("date");
            riqi_tv.setText(date);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sensorInfoReceiver);
        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }

    }


    private void link_save() {
        //final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        //http://192.168.2.4:8080/sign?cmd=getUnSignList&subjectId=jfgsdf
        OkHttpClient okHttpClient= new OkHttpClient.Builder()
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();
      //  Log.d("DengJiActivity", "chuanSongBean.getId():" + chuanSongBean.getId());

//    /* form的分割线,自己定义 */
//        String boundary = "xx--------------------------------------------------------------xx";
        RequestBody body = new FormBody.Builder()
                .add("id",chuanSongBean.getId()+"")
                .add("visitIncident",chuanSongBean.getShiyou())
                .add("accountId",baoCunBean.getZhangHuID())
                .add("visitDate2",riqi_tv.getText().toString().trim())
                .add("visitDepartment",bumen_ET.getText().toString().trim()+"")
                .add("visitPerson",shoufangren.getText().toString().trim()+"")
                .add("visitNum",shoufangrenshu.getText().toString().trim()+"")
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                // .header("Content-Type", "application/json")
                .post(body)
                .url(zhuji + "/saveCompareVisit.do");

        if (tiJIaoDialog==null){
            tiJIaoDialog=new TiJIaoDialog(DengJiActivity.this);
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
                if (tiJIaoDialog != null) {
                    tiJIaoDialog.dismiss();
                    tiJIaoDialog = null;
                }
            try {

                //获得返回体
                ResponseBody body = response.body();
                String ss = body.string().trim();
                Log.d("DengJiActivity", "请求识别成功" + ss);

                JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                Gson gson = new Gson();
                ShouFangBean zhaoPianBean = gson.fromJson(jsonObject, ShouFangBean.class);

                if (zhaoPianBean.getDtoResult() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog = new YuYueDialog(DengJiActivity.this, "你已成功预约,感谢你的来访!");
                            dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                            dialog.setOnPositiveListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    Intent intent = new Intent("guanbi2");
                                    sendBroadcast(intent);
                                }
                            });
                            dialog.setOnQuXiaoListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    ChuanSongBean bean=new ChuanSongBean(name.getText().toString(),chuanSongBean.getType(),chuanSongBean.getId(),
                                            chuanSongBean.getDianhua()
                                            ,shoufangren.getText().toString().trim(),riqi_tv.getText().toString().trim(),chuanSongBean.getShiyou());
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("chuansong", Parcels.wrap(bean));
                                    startActivity(new Intent(DengJiActivity.this,DaYingActivity.class).putExtras(bundle));

                                }
                            });

                            dialog.show();
                        }
                    });


                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast tastyToast = TastyToast.makeText(DengJiActivity.this, "提交失败,请检查网络", TastyToast.LENGTH_LONG, TastyToast.ERROR);
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
                    Log.d("WebsocketPushMsg", e.getMessage());
                }
            }
        });
    }


}
