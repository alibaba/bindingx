package com.alibaba.android.bindingx.plugin.weex;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class PlatformManager {

    private IDeviceResolutionTranslator mResolutionTranslator;
    private IViewFinder mViewFinder;
    private IViewUpdater mViewUpdater;

    private PlatformManager() {
    }

    @NonNull
    public IDeviceResolutionTranslator getResolutionTranslator() {
        return mResolutionTranslator;
    }

    @NonNull
    public IViewFinder getViewFinder() {
        return mViewFinder;
    }

    @NonNull
    public IViewUpdater getViewUpdater() {
        return mViewUpdater;
    }

    public interface IDeviceResolutionTranslator {
        double webToNative(double rawSize, Object... extension);
        double nativeToWeb(double rawSize, Object... extension);
    }

    public interface IViewFinder {

        @Nullable
        View findViewBy(String ref, Object... extension);
    }

    public interface IViewUpdater {

        /**
         * @param targetView target view that will be updated
         * @param propertyName the property that will be changed by property value
         * @param propertyValue the property value that will changed to
         * @param extension extension params
         * */
        void synchronouslyUpdateViewOnUIThread(@NonNull View targetView,
                                               @NonNull String propertyName,
                                               @NonNull Object propertyValue,
                                               Object... extension);
    }



    /**A helper class to create {@link PlatformManager} */
    public static class Builder {

        private IDeviceResolutionTranslator deviceResolutionTranslator;
        private IViewFinder viewFinder;
        private IViewUpdater viewUpdater;

        public Builder() {}

        public PlatformManager build() {
            PlatformManager factory = new PlatformManager();
            factory.mViewFinder = viewFinder;
            factory.mResolutionTranslator = deviceResolutionTranslator;
            factory.mViewUpdater = viewUpdater;
            return factory;
        }

        public Builder withDeviceResolutionTranslator(@NonNull IDeviceResolutionTranslator translator) {
            this.deviceResolutionTranslator = translator;
            return this;
        }

        public Builder withViewFinder(@NonNull IViewFinder finder) {
            this.viewFinder =  finder;
            return this;
        }

        public Builder withViewUpdater(@NonNull IViewUpdater viewUpdater) {
            this.viewUpdater = viewUpdater;
            return this;
        }
    }


}
