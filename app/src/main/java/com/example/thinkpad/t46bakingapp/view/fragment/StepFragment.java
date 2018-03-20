package com.example.thinkpad.t46bakingapp.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thinkpad.t46bakingapp.R;
import com.example.thinkpad.t46bakingapp.data.Step;
import com.example.thinkpad.t46bakingapp.view.adapter.StepAdapter;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by thinkpad on 2018/1/29.
 */

public class StepFragment extends Fragment{
    public static final String TAG = "StepFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view_in_step_fragment);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        String recipeName = getArguments().getString(getString(R.string.recipe_name));
        List<Step> steps = DataSupport.select("StepTitle", "StepId").where("ForRecipe=?", recipeName).find(Step.class);
        StepAdapter stepAdapter = new StepAdapter(steps, getActivity());
        recyclerView.setAdapter(stepAdapter);
    }
}
