package com.example.thinkpad.t46bakingapp.data;

import java.util.List;

/**
 * Created by thinkpad on 2018/1/31.
 */

public class RecipeModel {
    public String id;
    public String name;
    public List<IngredientsModel> ingredients;
    public List<StepsModel> steps;
    public String servings;
    public String image;

    public static class IngredientsModel {
        public String quantity;
        public String measure;
        public String ingredient;
    }
    public static class StepsModel {
        public int id;
        public String shortDescription;
        public String description;
        public String videoURL;
        public String thumbnailURL;
    }
}
