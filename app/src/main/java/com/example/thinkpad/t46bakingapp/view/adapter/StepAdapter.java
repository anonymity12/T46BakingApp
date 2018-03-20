package com.example.thinkpad.t46bakingapp.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;


import com.example.thinkpad.t46bakingapp.R;
import com.example.thinkpad.t46bakingapp.data.Step;
import com.example.thinkpad.t46bakingapp.util.Constants;
import com.example.thinkpad.t46bakingapp.view.fragment.DetailFragment;

import java.util.List;



/**
 * Created by thinkpad on 2018/1/30.
 */

public class StepAdapter extends RecyclerView.Adapter <StepAdapter.ViewHolder>{
    private Context mContext;
    private List<Step> mSteps;
    private Activity mActivity;
    public int mSource;
    public int position;
    public StepAdapter(List<Step> list, Activity activity){
        mSteps = list;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.step_list_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.stepName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                position = holder.getAdapterPosition();
                Step step = mSteps.get(position);//测试修改使之无用
                if (Constants.isTwoPane) {
                    //在xml指定的fragment位置进行替换
                    DetailFragment detailFragment = (DetailFragment) mActivity.getFragmentManager().findFragmentById(R.id.detail_fragment);
                    //设置fragment的一些参数
                    detailFragment.setCurrentStepId(Integer.valueOf(step.getStepId()));
                    //刷新fragment，让步骤细节重新显示此step
                    detailFragment.refresh();

                }else{
                    //直接在StepActivity里开始替换当前的fragment
                    DetailFragment detailFragment = new DetailFragment();
                    Bundle detailFragmentBundle = new Bundle();
                    detailFragmentBundle.putString(mContext.getString(R.string.step_id),step.getStepId());
                    detailFragment.setArguments(detailFragmentBundle);
                    mActivity.getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.step_fragment_container,detailFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.stepName.setText(mSteps.get(position).getStepTitle());
    }
    public int getItemCount() {
        return mSteps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout stepLayout;
        TextView stepName;
        public ViewHolder(View view){
            super(view);
            stepLayout=(LinearLayout) view;
            stepName=(TextView)view.findViewById(R.id.step_title);
        }
    }
}
