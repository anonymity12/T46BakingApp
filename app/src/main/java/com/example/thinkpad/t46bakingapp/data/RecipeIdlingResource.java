package com.example.thinkpad.t46bakingapp.data;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * Created by thinkpad on 2018/3/20.
 */

public class RecipeIdlingResource implements IdlingResource {

    private volatile ResourceCallback callback;
    private AtomicBoolean isIdleNow = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

    public void setIdleState(boolean idleState) {
        isIdleNow.set(idleState);
        if (idleState && callback != null) {
            callback.onTransitionToIdle();
        }
    }
}
