package com.example.thinkpad.t46bakingapp.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import com.example.thinkpad.t46bakingapp.data.Recipe;
import com.squareup.picasso.Picasso;



import com.example.thinkpad.t46bakingapp.R;
import com.example.thinkpad.t46bakingapp.view.StepActivity;

import java.util.List;


/**
 * Created by thinkpad on 2018/1/30.
 */

public class RecipeAdapter  extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private Context mContext;
    private List<Recipe> recipeList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView recipeImageView;
        TextView recipeName;
        TextView recipeServings;
        public ViewHolder(View view){
            super(view);
            cardView=(CardView) view;
            recipeImageView=(ImageView)view.findViewById(R.id.recipe_image_view);
            recipeName=(TextView)view.findViewById(R.id.recipe_name);
            recipeServings=(TextView)view.findViewById(R.id.recipe_servings);
        }
    }

    public RecipeAdapter(List<Recipe> list,View emptyPrompt){
        recipeList=list;
        emptyPrompt.setVisibility(list.size()==0?View.VISIBLE:View.GONE);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        if (mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.recipe_list_item,parent,false);
        final ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击事件
                Intent intent=new Intent(mContext,StepActivity.class);
                String recipeNameforIntent = recipeList.get(viewHolder.getAdapterPosition()).getRecipeName();
                intent.putExtra(mContext.getString(R.string.recipe_name),
                        recipeList.get(viewHolder.getAdapterPosition()).getRecipeName());
                Log.d("RecipeAdapter", ">>>>>>>>the extra is " + recipeNameforIntent);
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Recipe recipe=recipeList.get(position);
        if (TextUtils.isEmpty(recipe.getImageUrl())){
            Picasso.with(mContext).load(R.mipmap.logo_black).into(holder.recipeImageView);
        }else {
            //加载网络图片
            Picasso.with(mContext).load(recipe.getImageUrl()).into(holder.recipeImageView);
        }
        holder.recipeName.setText(recipe.getRecipeName());
        holder.recipeServings.append(recipe.getServings());
    }

    @Override
    public int getItemCount(){return recipeList.size();}
}
