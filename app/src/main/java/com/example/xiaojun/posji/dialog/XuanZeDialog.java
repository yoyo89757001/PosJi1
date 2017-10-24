package com.example.xiaojun.posji.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.xiaojun.posji.R;
import com.example.xiaojun.posji.beans.FanHuiBean;
import com.example.xiaojun.posji.beans.YuYueBean;
import com.example.xiaojun.posji.beans.YuYueInterface;
import com.example.xiaojun.posji.utils.DateUtils;
import com.example.xiaojun.posji.view.DividerItemDecoration;
import com.example.xiaojun.posji.view.GlideCircleTransform;
import com.example.xiaojun.posji.view.MyEditText;
import com.example.xiaojun.posji.view.WrapContentLinearLayoutManager;

import java.util.List;


/**
 * @Function: 自定义对话框
 * @Date: 2013-10-28
 * @Time: 下午12:37:43
 * @author Tom.Cai
 */
public class XuanZeDialog extends Dialog {
    private RecyclerView recyclerView;
    private MyAdapter adapter=null;
    private WrapContentLinearLayoutManager manager;
    private List<YuYueBean.ObjectsBean> objectsBeanList=null;
    private Context context;
    private String zhuji=null;
    private YuYueInterface yuYueInterface;


    public XuanZeDialog(Context context,List<YuYueBean.ObjectsBean> objectsBeen,String zhuji,YuYueInterface yuYueInterface) {
        super(context, R.style.dialog_style2);
        this.context=context;
        objectsBeanList=objectsBeen;
        this.zhuji=zhuji;
        this.yuYueInterface=yuYueInterface;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(R.layout.xuanzedialog, null);

        recyclerView= (RecyclerView) mView.findViewById(R.id.recylerview);
        manager = new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new MyAdapter(R.layout.yuyue_item,objectsBeanList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                yuYueInterface.setP(position);
            }
        });
        super.setContentView(mView);
    }




    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }



    public  class MyAdapter extends BaseQuickAdapter<YuYueBean.ObjectsBean,BaseViewHolder> {
       ;
        private MyAdapter(int layoutResId, List<YuYueBean.ObjectsBean> data) {
            super(layoutResId, data);

        }

        @Override
        protected void convert(final BaseViewHolder helper, YuYueBean.ObjectsBean item) {
            //Log.d(TAG, "动画执行");


//			ViewAnimator
//					.animate(helper.itemView)
//				//	.scale(0,1)
//					.alpha(0,1)
//					.duration(1000)
//					.start();

            ImageView touxiang=helper.getView(R.id.touxiang3);
            TextView time= helper.getView(R.id.time);
            TextView shouji= helper.getView(R.id.shouji);
            TextView name= helper.getView(R.id.name);
            time.setText(DateUtils.timet2(item.getVisitDate()+""));
            if (item.getPhone().equals("")){
                shouji.setText("没有填写");
            }else {
                shouji.setText(item.getPhone());
            }
            name.setText(item.getName());


                Glide.with(context)
                        .load(zhuji+"/upload/compare/"+item.getScanPhoto())
                        .transform(new GlideCircleTransform(context,1,Color.parseColor("#ffffffff")))
                        .thumbnail(0.1f)
                      //  .transform(new GlideRoundTransform(MyApplication.getAppContext(), 6))
                        .into(touxiang);

        }


    }


}
