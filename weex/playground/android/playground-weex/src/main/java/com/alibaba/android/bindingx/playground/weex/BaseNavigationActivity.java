/**
 * Copyright 2018 Alibaba Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.android.bindingx.playground.weex;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import com.taobao.weex.utils.WXLogUtils;

public abstract class BaseNavigationActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    protected void setupToolbar(@Nullable Toolbar toolbar) {
        if(toolbar == null) {
            return;
        }
        this.mToolbar = toolbar;

        toolbar.setBackgroundColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setSubtitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() == null) {
            return;
        }

        setActionBarBackArrowEnabled(true);
        setActionBarBackArrowColor(Color.BLACK);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public boolean setActionBarBackgroundColor(@ColorInt int color) {
        if(getSupportActionBar() == null) {
            return false;
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        return true;
    }

    public boolean setActionBarColor(@ColorInt int color) {
        if(getToolbar() == null) {
            return false;
        }
        getToolbar().setTitleTextColor(color);
        getToolbar().setSubtitleTextColor(color);
        return true;
    }

    public void setActionBarBackArrowEnabled(boolean shown) {
        if(getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(shown);
        getSupportActionBar().setDisplayShowHomeEnabled(shown);
    }

    public void setActionBarBackArrowColor(@ColorInt int color) {
        if(getSupportActionBar() != null) {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.back_arrow);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(drawable);
        }
    }

    public boolean setActionBarLeftIconDrawable(@Nullable Drawable drawable) {
        if(getSupportActionBar() == null) {
            return false;
        }
        getSupportActionBar().setHomeAsUpIndicator(drawable);
        return true;
    }

    public boolean setActionBarLeftIconDrawable(@DrawableRes int id) {
        if(getSupportActionBar() == null) {
            return false;
        }
        getSupportActionBar().setHomeAsUpIndicator(id);
        return true;
    }

    public boolean setActionBarLogo(@Nullable Drawable drawable) {
        if(getSupportActionBar() == null) {
            return false;
        }
        getSupportActionBar().setLogo(drawable);
        return true;
    }

    public boolean showActionBar() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().show();
            return true;
        }
        return false;
    }

    public boolean hideActionBar() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
            return true;
        }
        return false;
    }

    public boolean setActionBarTitle(@Nullable String title) {
        if(TextUtils.isEmpty(title)) {
            return false;
        }
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            return true;
        }
        return false;
    }

    public boolean setActionBarSubTitle(@Nullable String title) {
        if(TextUtils.isEmpty(title)) {
            return false;
        }
        if(getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(title);
            return true;
        }
        return false;
    }

    public boolean setActionBarMenuItemClickListener(Toolbar.OnMenuItemClickListener listener) {
        if(getToolbar() == null) {
            return false;
        }
        getToolbar().setOnMenuItemClickListener(listener);
        return true;
    }

    public boolean setNavigationIconClickListener(final View.OnClickListener listener) {
        if(getToolbar() == null) {
            return false;
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WXLogUtils.d("navigationBar left item clicked.");
                if(listener != null) {
                    listener.onClick(v);
                }
            }
        });
        return true;
    }

    public void invalidateMenu() {
        invalidateOptionsMenu();
    }


    public Toolbar getToolbar() {
        return mToolbar;
    }
}
