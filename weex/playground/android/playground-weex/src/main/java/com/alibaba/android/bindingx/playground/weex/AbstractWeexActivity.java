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

import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractWeexActivity extends BaseNavigationActivity implements IWXRenderListener {
  private static final String TAG = "weex";

  private ViewGroup mContainer;
  private WXSDKInstance mInstance;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    createWeexInstance();
    mInstance.onActivityCreate();

    getWindow().setFormat(PixelFormat.TRANSLUCENT);
  }

  protected final void setContainer(ViewGroup container){
    mContainer = container;
  }

  protected final ViewGroup getContainer(){
    return mContainer;
  }

  protected void destroyInstance(){
    if(mInstance != null){
      mInstance.registerRenderListener(null);
      mInstance.destroy();
      mInstance = null;
    }
  }

  protected void createWeexInstance(){
    destroyInstance();

    Rect outRect = new Rect();
    getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);

    mInstance = new WXSDKInstance(this);
    mInstance.registerRenderListener(this);
  }

  protected void renderPage(String template, String source){
    renderPage(template,source,null);
  }

  protected void renderPage(String template, String source, String jsonInitData){
    Map<String, Object> options = new HashMap<>();
    options.put(WXSDKInstance.BUNDLE_URL, source);
    mInstance.setTrackComponent(true);
    mInstance.render(getPageName(),template,options,jsonInitData,WXRenderStrategy.APPEND_ASYNC);
  }

  protected void renderPageByURL(String url){
    renderPageByURL(url,null);
  }

  protected void renderPageByURL(String url, String jsonInitData){
    Map<String, Object> options = new HashMap<>();
    options.put(WXSDKInstance.BUNDLE_URL, url);
    mInstance.setTrackComponent(true);
    mInstance.renderByUrl(getPageName(),url,options,jsonInitData,WXRenderStrategy.APPEND_ASYNC);
  }

  protected String getPageName(){
    return TAG;
  }

  @Override
  public void onStart() {
    super.onStart();
    if(mInstance!=null){
      mInstance.onActivityStart();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if(mInstance!=null){
      mInstance.onActivityResume();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if(mInstance!=null){
      mInstance.onActivityPause();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if(mInstance!=null){
      mInstance.onActivityStop();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if(mInstance!=null){
      mInstance.onActivityDestroy();
    }
  }

  @Override
  public void onViewCreated(WXSDKInstance wxsdkInstance, View view) {
    if (mContainer != null) {
      mContainer.removeAllViews();
      mContainer.addView(view);
    }
  }



  @Override
  public void onRefreshSuccess(WXSDKInstance wxsdkInstance, int i, int i1) {

  }

  @Override
  @CallSuper
  public void onRenderSuccess(WXSDKInstance instance, int width, int height) {

  }

  @Override
  @CallSuper
  public void onException(WXSDKInstance instance, String errCode, String msg) {
    Toast.makeText(this,
            String.format(Locale.getDefault(),"render failed(%s,%s)",errCode,msg),
            Toast.LENGTH_SHORT).
            show();
  }


}
