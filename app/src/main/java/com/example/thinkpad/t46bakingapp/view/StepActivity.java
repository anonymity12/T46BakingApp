package com.example.thinkpad.t46bakingapp.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.thinkpad.t46bakingapp.R;
import com.example.thinkpad.t46bakingapp.util.Constants;
import com.example.thinkpad.t46bakingapp.view.fragment.DetailFragment;
import com.example.thinkpad.t46bakingapp.view.fragment.StepFragment;
import com.example.thinkpad.t46bakingapp.data.Step;

import org.litepal.crud.DataSupport;

import java.util.List;

public class StepActivity extends AppCompatActivity {
    private static final String TAG = "StepActivity";

    private ActionBar mActionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        Intent intent = getIntent();
        String recipeName = intent.getStringExtra(getString(R.string.recipe_name));
        Log.d(TAG, ">>>>>>>>>.the intent extra is : " + recipeName);

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setTitle(recipeName);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        List<Step> stepList= DataSupport.select("StepTitle","StepId").where("ForRecipe=?",recipeName).find(Step.class);
        if (!Constants.isTwoPane) {
            // 这时是手机界面，仅仅加载StepFragment
            StepFragment stepFragment = new StepFragment();
            Bundle stepFragmentBundle = new Bundle();
            stepFragmentBundle.putString(getString(R.string.recipe_name), recipeName);
            stepFragment.setArguments(stepFragmentBundle);
            //以下使用的step_fragment_container其实可以称为step_and_detail_fragment_container
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.step_fragment_container,stepFragment)
                    .commit();
        }else{
            //这是平板界面
            StepFragment stepFragment = new StepFragment();
            Bundle stepFragmentBundle = new Bundle();
            stepFragmentBundle.putString(getString(R.string.recipe_name), recipeName);
            stepFragment.setArguments(stepFragmentBundle);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.step_fragment,stepFragment)
                    .commit();
            DetailFragment detailFragment=new DetailFragment();
            //使用Bundle携带数据
            Bundle fragmentBundle=new Bundle();
            fragmentBundle.putString(getString(R.string.step_id),stepList.get(0).getStepId());
            detailFragment.setArguments(fragmentBundle);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_fragment,detailFragment)
                    .commit();
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        Log.i(TAG,"onConfigurationChanged");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

}
