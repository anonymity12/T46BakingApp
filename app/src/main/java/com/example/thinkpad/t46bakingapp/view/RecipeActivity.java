package com.example.thinkpad.t46bakingapp.view;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.thinkpad.t46bakingapp.R;
import com.example.thinkpad.t46bakingapp.data.Recipe;
import com.example.thinkpad.t46bakingapp.data.RecipeIdlingResource;
import com.example.thinkpad.t46bakingapp.util.Constants;
import com.example.thinkpad.t46bakingapp.util.Util;
import com.example.thinkpad.t46bakingapp.util.network.SyncAdapter;
import com.example.thinkpad.t46bakingapp.view.adapter.RecipeAdapter;
import com.example.thinkpad.t46bakingapp.data.StubProvider;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "RecipeActivity";

    private List<Recipe> recipeList;
    private RecipeAdapter recipeAdapter;
    private RecipeIdlingResource idlingResource;

    @BindView(R.id.recycler_view_recipe)
    RecyclerView recipeRecyclerView;
    TextView emptyPrompt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        GridLayoutManager layoutManager;
        //判断是否是平板
        if (findViewById(R.id.empty_data_text_view_in_600dp) != null) {
            Constants.isTwoPane = true;
            Log.i(TAG, ">>>>>>>>>>>>>>>isTwoPane = " + Constants.isTwoPane);
            emptyPrompt = (TextView) findViewById(R.id.empty_data_text_view_in_600dp);
            layoutManager = new GridLayoutManager(this, 3);
        }else{
            Constants.isTwoPane = false;
            emptyPrompt = (TextView) findViewById(R.id.empty_data_text_view);
            layoutManager = new GridLayoutManager(this, 1);
        }
        recipeRecyclerView.setLayoutManager(layoutManager);
        recipeList = DataSupport.findAll(Recipe.class);
        if (recipeList.size() <= 0) {
            syncData();


        }else{
            recipeAdapter=new RecipeAdapter(recipeList,emptyPrompt);
            recipeRecyclerView.setAdapter(recipeAdapter);
        }
        getLoaderManager().initLoader(0, null,this);


    }
    /*
    * 同步一下网络数据
    * */
    private void syncData(){
        if (Util.isNetworkConnected(this)) {
            emptyPrompt.setText("Refreshing");
            emptyPrompt.setOnClickListener(null);
            SyncAdapter.CreateSyncAccount(RecipeActivity.this);
        }else{
            emptyPrompt.setText("Network not available");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG,"onCreateLoader");
        return new CursorLoader(RecipeActivity.this, StubProvider.bakingUri,null,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG,"onLoadFinished(Loader<Cursor> loader, Cursor cursor)");
        recipeList= DataSupport.findAll(Recipe.class);
        recipeAdapter=new RecipeAdapter(recipeList,emptyPrompt);
        recipeAdapter.notifyDataSetChanged();
        recipeRecyclerView.setAdapter(recipeAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new RecipeIdlingResource();
        }
        return idlingResource;
    }
}
