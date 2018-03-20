package com.example.thinkpad.t46bakingapp.data;

import org.litepal.crud.DataSupport;

/**
 * Created by thinkpad on 2018/1/29.
 */

public class Recipe extends DataSupport {
    private String RecipeId;
    private String RecipeName;
    private String Servings;
    private String ImageUrl;

    public String getRecipeId() {
        return RecipeId;
    }

    public void setRecipeId(String recipeId) {
        RecipeId = recipeId;
    }

    public String getRecipeName() {
        return RecipeName;
    }

    public void setRecipeName(String recipeName) {
        RecipeName = recipeName;
    }

    public String getServings() {
        return Servings;
    }

    public void setServings(String servings) {
        Servings = servings;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
