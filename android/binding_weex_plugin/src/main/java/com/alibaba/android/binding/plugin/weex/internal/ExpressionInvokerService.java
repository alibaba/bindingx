package com.alibaba.android.binding.plugin.weex.internal;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.binding.plugin.weex.LogProxy;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXScroller;
import com.taobao.weex.ui.component.WXText;
import com.taobao.weex.ui.view.WXTextView;
import com.taobao.weex.utils.WXUtils;
import com.taobao.weex.utils.WXViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

final class ExpressionInvokerService {
    private static final Map<String,IExpressionInvoker> sExpressionInvokerMap;
    private static final NOpInvoker EMPTY_INVOKER = new NOpInvoker();

    private static final String PERSPECTIVE = "perspective";
    private static final String TRANSFORM_ORIGIN = "transformOrigin";

    static {
        sExpressionInvokerMap = new HashMap<>();
        sExpressionInvokerMap.put("opacity",new OpacityInvoker());
        sExpressionInvokerMap.put("transform.translate",new TranslateInvoker());
        sExpressionInvokerMap.put("transform.translateX",new TranslateXInvoker());
        sExpressionInvokerMap.put("transform.translateY",new TranslateYInvoker());

        sExpressionInvokerMap.put("transform.scale",new ScaleInvoker());
        sExpressionInvokerMap.put("transform.scaleX",new ScaleXInvoker());
        sExpressionInvokerMap.put("transform.scaleY",new ScaleYInvoker());

        sExpressionInvokerMap.put("transform.rotate",new RotateInvoker());
        sExpressionInvokerMap.put("transform.rotateZ",new RotateInvoker());
        sExpressionInvokerMap.put("transform.rotateX",new RotateXInvoker());
        sExpressionInvokerMap.put("transform.rotateY",new RotateYInvoker());

        sExpressionInvokerMap.put("width",new WidthInvoker());
        sExpressionInvokerMap.put("height",new HeightInvoker());

        sExpressionInvokerMap.put("background-color",new BackgroundInvoker());
        sExpressionInvokerMap.put("color", new ColorInvoker());

        sExpressionInvokerMap.put("scroll.contentOffset", new ContentOffsetInvoker());
        sExpressionInvokerMap.put("scroll.contentOffsetX", new ContentOffsetXInvoker());
        sExpressionInvokerMap.put("scroll.contentOffsetY", new ContentOffsetYInvoker());
    }

    @NonNull
    static IExpressionInvoker findInvoker(@NonNull String prop) {
        IExpressionInvoker invoker = sExpressionInvokerMap.get(prop);
        if(invoker == null) {
            LogProxy.e("unknown property [" + prop + "]");
            return EMPTY_INVOKER;
        }
        return invoker;
    }

    private static final class NOpInvoker implements IExpressionInvoker {
        @Override
        public void invoke(@NonNull WXComponent component, @NonNull View targetView,
                           @NonNull Object cmd, @NonNull Map<String,Object> config) {
            //oops
        }
    }

    private static void postRunnable(View target, Runnable runnable) {
        if(Build.VERSION.SDK_INT >= 16) {
            target.postOnAnimation(runnable);
        } else {
            target.post(runnable);
        }
    }

    //内容滚动
    private static final class ContentOffsetInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull Object cmd, @NonNull Map<String,Object> config) {
            final View scrollView = findScrollTarget(component);
            if(scrollView == null) {
                return;
            }
            if(cmd instanceof Double) {
                final double val = (double) cmd;
                postRunnable(scrollView, new Runnable() {
                    @Override
                    public void run() {
                        scrollView.setScrollX((int) getRealSize(val));
                        scrollView.setScrollY((int) getRealSize(val));
                    }
                });
            } else if(cmd instanceof ArrayList) {
                ArrayList<Object> l = (ArrayList<Object>) cmd;
                if(l.size() >= 2 && l.get(0) instanceof Double && l.get(1) instanceof Double) {
                    final double x = (double) l.get(0);
                    final double y = (double) l.get(1);
                    postRunnable(scrollView, new Runnable() {
                        @Override
                        public void run() {
                            scrollView.setScrollX((int) getRealSize(x));
                            scrollView.setScrollY((int) getRealSize(y));
                        }
                    });
                }

            }
        }
    }

    private static final class ContentOffsetXInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull Object cmd, @NonNull Map<String,Object> config) {
            final View scrollView = findScrollTarget(component);
            if(scrollView == null) {
                return;
            }
            if(!(cmd instanceof Double)) {
                return;
            }
            final double val = (double) cmd;
            postRunnable(scrollView, new Runnable() {
                @Override
                public void run() {
                    scrollView.setScrollX((int) getRealSize(val));
                }
            });
        }
    }

    private static final class ContentOffsetYInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull Object cmd, @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            final View scrollView = findScrollTarget(component);
            if(scrollView == null) {
                return;
            }
            final double val = (double) cmd;
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    scrollView.setScrollY((int) getRealSize(val));
                }
            });
        }
    }

    private static final class OpacityInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull Object cmd, @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            double val = (double) cmd;
            final float alpha = (float) (val);
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    targetView.setAlpha(alpha);
                }
            });
        }
    }

    private static final class TranslateInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull Object cmd, @NonNull Map<String,Object> config) {

            if(!(cmd instanceof ArrayList)) {
                return;
            }

            ArrayList<Object> l1 = (ArrayList<Object>) cmd;
            if(l1.size() >= 2 && l1.get(0) instanceof Double && l1.get(1) instanceof Double) {
                final double x1 = (double) l1.get(0);
                final double y1 = (double) l1.get(1);
                postRunnable(targetView, new Runnable() {
                    @Override
                    public void run() {
                        targetView.setTranslationX((float) getRealSize(x1));
                        targetView.setTranslationY((float) getRealSize(y1));
                    }
                });
            }
        }
    }

    private static final class TranslateXInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull Object cmd, @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            final double d1 = (double) cmd;
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    targetView.setTranslationX((float) getRealSize(d1));
                }
            });
        }
    }

    private static final class TranslateYInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull Object cmd, @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            final double d2 = (double) cmd;
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    targetView.setTranslationY((float) getRealSize(d2));
                }
            });
        }
    }

    private static final class ScaleInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull final Object cmd, @NonNull final Map<String,Object> config) {
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    int perspective = WXUtils.getInt(config.get(PERSPECTIVE));
                    perspective = normalizedPerspectiveValue(targetView.getContext(),perspective);

                    Pair<Float,Float> pivot = parseTransformOrigin(
                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

                    if(perspective != 0) {
                        targetView.setCameraDistance(perspective);
                    }
                    if(pivot != null) {
                        targetView.setPivotX(pivot.first);
                        targetView.setPivotY(pivot.second);
                    }

                    if(cmd instanceof Double) {
                        final double val = (double) cmd;
                        targetView.setScaleX((float) val);
                        targetView.setScaleY((float) val);
                    } else if(cmd instanceof ArrayList) {
                        ArrayList<Object> l2 = (ArrayList<Object>) cmd;
                        if(l2.size() >= 2 && l2.get(0) instanceof Double && l2.get(1) instanceof Double) {
                            final double x2 = (double) l2.get(0);
                            final double y2 = (double) l2.get(1);
                            targetView.setScaleX((float) x2);
                            targetView.setScaleY((float) y2);
                        }

                    }
                }
            });
        }
    }

    private static final class ScaleXInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull final Object cmd, @NonNull final Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    Pair<Float,Float> pivot = parseTransformOrigin(
                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

                    if(pivot != null) {
                        targetView.setPivotX(pivot.first);
                        targetView.setPivotY(pivot.second);
                    }

                    final double d3 = (double) cmd;
                    targetView.setScaleX((float) d3);
                }
            });
        }
    }

    private static final class ScaleYInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull final Object cmd, @NonNull final Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    Pair<Float,Float> pivot = parseTransformOrigin(
                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

                    if(pivot != null) {
                        targetView.setPivotX(pivot.first);
                        targetView.setPivotY(pivot.second);
                    }

                    final double d4 = (double) cmd;
                    targetView.setScaleY((float) d4);
                }
            });
        }
    }

    private static final class RotateInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull final Object cmd, @NonNull final Map<String,Object> config) {

            if(!(cmd instanceof Double)) {
                return;
            }

            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    int perspective = WXUtils.getInt(config.get(PERSPECTIVE));
                    perspective = normalizedPerspectiveValue(targetView.getContext(),perspective);

                    Pair<Float,Float> pivot = parseTransformOrigin(
                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

                    if(perspective != 0) {
                        targetView.setCameraDistance(perspective);
                    }
                    if(pivot != null) {
                        targetView.setPivotX(pivot.first);
                        targetView.setPivotY(pivot.second);
                    }

                    final double d5 = (double) cmd;
                    targetView.setRotation((float) d5);
                }
            });
        }
    }

    private static final class RotateXInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull final Object cmd, @NonNull final Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    int perspective = WXUtils.getInt(config.get(PERSPECTIVE));
                    perspective = normalizedPerspectiveValue(targetView.getContext(),perspective);

                    Pair<Float,Float> pivot = parseTransformOrigin(
                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

                    if(perspective != 0) {
                        targetView.setCameraDistance(perspective);
                    }
                    if(pivot != null) {
                        targetView.setPivotX(pivot.first);
                        targetView.setPivotY(pivot.second);
                    }

                    final double d6 = (double) cmd;
                    targetView.setRotationX((float) d6);
                }
            });
        }
    }


    private static final class RotateYInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull final Object cmd, @NonNull final Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    int perspective = WXUtils.getInt(config.get(PERSPECTIVE));
                    perspective = normalizedPerspectiveValue(targetView.getContext(),perspective);

                    Pair<Float,Float> pivot = parseTransformOrigin(
                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

                    if(perspective != 0) {
                        targetView.setCameraDistance(perspective);
                    }
                    if(pivot != null) {
                        targetView.setPivotX(pivot.first);
                        targetView.setPivotY(pivot.second);
                    }

                    final double d7 = (double) cmd;
                    targetView.setRotationY((float) d7);
                }
            });
        }
    }


    private static final class WidthInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull Object cmd, @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            double d8 = (double) cmd;
            final ViewGroup.LayoutParams params1 = targetView.getLayoutParams();
            params1.width = (int) getRealSize(d8);
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    targetView.setLayoutParams(params1);
                }
            });
        }
    }

    private static final class HeightInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull Object cmd, @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            double d9 = (double) cmd;
            final ViewGroup.LayoutParams params2 = targetView.getLayoutParams();
            params2.height = (int) getRealSize(d9);
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    targetView.setLayoutParams(params2);
                }
            });
        }
    }

    private static final class BackgroundInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull WXComponent component, @NonNull final View targetView,
                           @NonNull Object cmd, @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Integer)) {
                return;
            }
            final int d10 = (int) cmd;
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    targetView.setBackgroundColor(d10);
                }
            });
        }
    }

    private static final class ColorInvoker implements IExpressionInvoker {

        @Override
        public void invoke(@NonNull final WXComponent component, @NonNull final View targetView,
                           @NonNull final Object cmd, @NonNull Map<String, Object> config) {
            if(!(cmd instanceof Integer)) {
                return;
            }
            final int d = (int) cmd;
            postRunnable(targetView, new Runnable() {
                @Override
                public void run() {
                    if(targetView instanceof TextView) {
                        ((TextView) targetView).setTextColor(d);
                    } else if(component instanceof WXText && targetView instanceof WXTextView) {
                        Layout layout = ((WXTextView) targetView).getTextLayout();
                        if(layout != null) {
                            CharSequence sequence = layout.getText();
                            if(sequence != null && sequence instanceof SpannableString) {
                                /*kind of ugly*/
                                ForegroundColorSpan[] spans = ((SpannableString) sequence).getSpans(
                                        0,
                                        sequence.length(),
                                        ForegroundColorSpan.class);
                                if(spans != null && spans.length == 1) {/*仅处理纯色text*/
                                    ((SpannableString) sequence).removeSpan(spans[0]);
                                    ((SpannableString) sequence).setSpan(new ForegroundColorSpan(d),
                                            0,
                                            sequence.length(),
                                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                                    targetView.invalidate();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private static double getRealSize(double size) {
        return size * WXViewUtils.getScreenWidth() / (double) WXEnvironment.sDefaultWidth;
    }


    @Nullable
    private static View findScrollTarget(@NonNull WXComponent component) {
        if(!(component instanceof WXScroller)) {
            LogProxy.e("scroll offset only support on Scroller Component");
            return null;
        }
        WXScroller scroller = (WXScroller) component;
        return scroller.getInnerView();
    }


    private static int normalizedPerspectiveValue(@NonNull Context context, int raw) {
        //refer: react-native # BaseViewManager
        // The following converts the matrix's perspective to a camera distance
        // such that the camera perspective looks the same on Android and iOS
        float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (scale * raw * 5)/*CAMERA_DISTANCE_NORMALIZATION_MULTIPLIER*/;
    }

    @Nullable
    private static Pair<Float,Float> parseTransformOrigin(@Nullable String value, @NonNull View view) {
        if(TextUtils.isEmpty(value)) {
            return null;
        }
        int firstSpace = value.indexOf(' ');
        if (firstSpace != -1) {
            int i = firstSpace;
            for (; i < value.length(); i++) {
                if (value.charAt(i) != ' ') {
                    break;
                }
            }

            if (i < value.length() && value.charAt(i) != ' ') {
                String x = value.substring(0, firstSpace).trim();
                String y = value.substring(i, value.length()).trim();

                float pivotX,pivotY;
                if("left".equals(x)) {
                    pivotX = 0f;
                } else if("right".equals(x)) {
                    pivotX = view.getWidth();
                } else if("center".equals(x)) {
                    pivotX = view.getWidth()/2;
                } else {
                    pivotX = view.getWidth()/2;
                }

                if("top".equals(y)) {
                    pivotY = 0;
                } else if("bottom".equals(y)) {
                    pivotY = view.getHeight();
                } else if("center".equals(y)) {
                    pivotY = view.getHeight()/2;
                } else {
                    pivotY = view.getHeight()/2;
                }

                return new Pair<>(pivotX,pivotY);
            }
        }
        return null;
    }
}
