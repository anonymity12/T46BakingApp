package com.example.thinkpad.t46bakingapp.util.network;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.example.thinkpad.t46bakingapp.R;
import com.example.thinkpad.t46bakingapp.data.RecipeModel;
import com.example.thinkpad.t46bakingapp.data.StubProvider;
import com.example.thinkpad.t46bakingapp.data.Recipe;
import com.example.thinkpad.t46bakingapp.data.Step;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.ACCOUNT_SERVICE;
/**
 * Created by thinkpad on 2018/1/29.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    private static final String TAG = "SyncAdapter";
    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.example.thinkpad.t46bakingapp";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "t46bakingapp.thinkpad.example.com";
    // The account name
    public static final String ACCOUNT ="T46BakingApp";



    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(
            final Account account,
            final Bundle extras,
            final String authority,
            final ContentProviderClient provider,
            final SyncResult syncResult) {
    /*
     * Put the data transfer code here.
     */
        Log.i(TAG,"onPerformSync is run");
        final String urlstr=getContext().getString(R.string.api_url);
        requestForRecipe(urlstr);
    }
    private void requestForRecipe(final String string){
        //使用okhttp
        Request request=new Request.Builder().url(string).build();
        OkHttpClient client=new OkHttpClient();
        try {
            Response response=client.newCall(request).execute();
            final String responseData=response.body().string();
            if (!TextUtils.isEmpty(responseData)){
                //连接正常
                Log.i("netWork","OK!we have data!");
                analysisJson(responseData);
            }else {
                //服务器停机
                //setBadNews(NETWORK_STATUS_SERVER_DOWN);
            }
        }catch (IOException e){
            Log.i(TAG,"we have a Error:"+e);
            if(e.getClass().getName().equals(SocketTimeoutException.class.getName())){
                //请求超时
                Log.i(TAG,"请求超时");
                //setBadNews(NETWORK_STATUS_SERVER_TIMEOUT);//
            }
            if(e.getClass().getName().equals(ConnectException.class.getName())||e.getClass().getName().equals(UnknownHostException.class.getName())){
                //没有网络
                // setBadNews(NETWORK_STATUS_NO_NETWORK);
            }
        }
    }

    //使用Gson
    private void analysisJson(String JsonStr){
        Gson gson=new Gson();
        Type type=new TypeToken<List<RecipeModel>>(){}.getType();
        List<RecipeModel> list=gson.fromJson(JsonStr,type);
        int stepId=0;
        for(RecipeModel OneRecipe:list){
            //保存一个菜谱
            Recipe recipe=new Recipe();
            recipe.setRecipeId(OneRecipe.id);
            recipe.setRecipeName(OneRecipe.name);
            recipe.setServings(OneRecipe.servings);
            recipe.setImageUrl(OneRecipe.image);
            recipe.save();

            //将配料表保存为一个步骤
            List<RecipeModel.IngredientsModel> IngredientsList=OneRecipe.ingredients;
            String DescriptionStr="";
            for (RecipeModel.IngredientsModel OneIngredients:IngredientsList){
                DescriptionStr=DescriptionStr
                        +getContext().getString(R.string.n)+getContext().getString(R.string.quantity)+OneIngredients.quantity
                        +getContext().getString(R.string.n)+getContext().getString(R.string.measure)+OneIngredients.measure
                        +getContext().getString(R.string.n)+getContext().getString(R.string.ingredient)+OneIngredients.ingredient+getContext().getString(R.string.n);
            }
            Step IngredientsStep =new Step();
            IngredientsStep.setForRecipe(OneRecipe.name);
            IngredientsStep.setStepId(String.valueOf(stepId++));
            IngredientsStep.setStepTitle(getContext().getString(R.string.ingredients_title));
            IngredientsStep.setDescription(DescriptionStr);
            IngredientsStep.save();
            //保存所有步骤
            List<RecipeModel.StepsModel> StepsList=OneRecipe.steps;
            for(RecipeModel.StepsModel OneSteps:StepsList){
                Step step=new Step();
                step.setForRecipe(OneRecipe.name);
                step.setStepId(String.valueOf(stepId++));
                step.setStepTitle(OneSteps.shortDescription);
                step.setDescription(OneSteps.description);
                if(!TextUtils.isEmpty(OneSteps.videoURL)){
                    step.setVideoUrl(OneSteps.videoURL);
                }else {
                    step.setVideoUrl(OneSteps.thumbnailURL);
                }
                step.save();
            }
        }
        //通知,数据库发生变化
        noticeSQLChange();
        Log.i(TAG,"Data input SQL");
    }

    //通知,数据库发生变化
    public void noticeSQLChange(){
        ContentValues[] cvArray = new ContentValues[1];
        getContext().getContentResolver().bulkInsert(StubProvider.bakingUri,cvArray);
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Log.i(TAG,"CreateSyncAccount is run");
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        if ( null == accountManager.getPassword(newAccount) ) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

        }

        onAccountCreated(newAccount, context);
        return newAccount;
    }

    //创建账号后开始同步
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        //设置周期同步

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        //网络可用时自动同步

        /*
         * Finally, let's do a sync to get things started
         */
        //手动同步
        syncImmediately(newAccount,context);
    }

    public static void syncImmediately(Account newAccount,Context context) {
        Log.i(TAG,"syncImmediately is run");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(newAccount, AUTHORITY, bundle);
    }

/*    private void setBadNews(@NetWorkStatus int netWorkStatus){
        if (badNews!=null){
            badNews.weHaveABadNews(netWorkStatus);
        }
    }

    public interface BadNews{
        void weHaveABadNews(@NetWorkStatus int netWorkStatus);
    }
    public static void setBadNewsCallBack(BadNews bn){badNews=bn;}*/
}
