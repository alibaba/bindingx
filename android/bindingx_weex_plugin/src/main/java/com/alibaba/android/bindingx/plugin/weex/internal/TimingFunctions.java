package com.alibaba.android.bindingx.plugin.weex.internal;

import android.support.annotation.Nullable;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;

import org.json.JSONException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;


/**
 * Description:
 *
 * 一组预置的缓动函数
 *
 * Created by rowandjj(chuyi)<br/>
 */

class TimingFunctions {
    private TimingFunctions() {}

    static void applyToScope(Map<String, Object> scope){
        scope.put("linear", linear);

        scope.put("easeInQuad", easeInQuad);
        scope.put("easeOutQuad", easeOutQuad);
        scope.put("easeInOutQuad", easeInOutQuad);
        scope.put("easeInCubic", easeInCubic);
        scope.put("easeOutCubic", easeOutCubic);
        scope.put("easeInOutCubic", easeInOutCubic);
        scope.put("easeInQuart", easeInQuart);
        scope.put("easeOutQuart", easeOutQuart);
        scope.put("easeInOutQuart", easeInOutQuart);
        scope.put("easeInQuint", easeInQuint);
        scope.put("easeOutQuint", easeOutQuint);
        scope.put("easeInOutQuint", easeInOutQuint);
        scope.put("easeInSine", easeInSine);
        scope.put("easeOutSine", easeOutSine);
        scope.put("easeInOutSine", easeInOutSine);
        scope.put("easeInExpo", easeInExpo);
        scope.put("easeOutExpo", easeOutExpo);
        scope.put("easeInOutExpo", easeInOutExpo);
        scope.put("easeInCirc", easeInCirc);
        scope.put("easeOutCirc", easeOutCirc);
        scope.put("easeInOutCirc", easeInOutCirc);
        scope.put("easeInElastic", easeInElastic);
        scope.put("easeOutElastic", easeOutElastic);
        scope.put("easeInOutElastic", easeInOutElastic);
        scope.put("easeInBack", easeInBack);
        scope.put("easeOutBack", easeOutBack);
        scope.put("easeInOutBack", easeInOutBack);
        scope.put("easeInBounce", easeInBounce);
        scope.put("easeOutBounce", easeOutBounce);
        scope.put("easeInOutBounce", easeInOutBounce);

        scope.put("cubicBezier", cubicBezier);
    }

    // t: current time, b: begInnIng value, c: change In value, d: duration

    private static Object linear = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);

            t = Math.min(t,d);

            return c*(t/d)+b;
        }
    };

    private static Object cubicBezier = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);

            double x1 = (double) arguments.get(4);
            double y1 = (double) arguments.get(5);
            double x2 = (double) arguments.get(6);
            double y2 = (double) arguments.get(7);

            t = Math.min(t,d);

            if(t == d) {
                return b+c;
            }

            // 避免短时间创建大量临时对象
            BezierInterpolatorWrapper bezierInterpolator = isCacheHit((float) x1,(float) y1,(float) x2,(float) y2);
            if(bezierInterpolator == null) {
                bezierInterpolator = new BezierInterpolatorWrapper((float) x1,(float) y1,(float) x2,(float) y2);
                cache.add(bezierInterpolator);
            }

            float input = (float) (t / d);
            return c * bezierInterpolator.getInterpolation(input) + b;

        }
    };

    private static final InnerCache<BezierInterpolatorWrapper> cache = new InnerCache<>(4);

    @Nullable
    private static BezierInterpolatorWrapper isCacheHit(float x1, float y1, float x2, float y2) {
        Deque<BezierInterpolatorWrapper> deque = cache.getAll();
        for(BezierInterpolatorWrapper bezier : deque) {
            if(Float.compare(bezier.x1,x1)==0 && Float.compare(bezier.x2,x2) ==0
                    && Float.compare(bezier.y1, y1)==0 && Float.compare(bezier.y2,y2)==0) {
                return bezier;
            }
        }

        return null;
    }


    private static Object easeInQuad = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return c*(t/=d)*t + b;
        }
    };

    private static Object easeOutQuad = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return -c *(t/=d)*(t-2) + b;
        }
    };

    private static Object easeInOutQuad = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            if ((t/=d/2) < 1) {
                return c/2*t*t + b;
            }
            return -c/2 * ((--t)*(t-2) - 1) + b;
        }
    };

    private static Object easeInCubic = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return c*(t/=d)*t*t + b;
        }
    };

    private static Object easeOutCubic = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return c*((t=t/d-1)*t*t + 1) + b;
        }
    };

    private static Object easeInOutCubic = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            if ((t/=d/2) < 1) {
                return c/2*t*t*t + b;
            }
            return c/2*((t-=2)*t*t + 2) + b;
        }
    };

    private static Object easeInQuart = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return c*(t/=d)*t*t*t + b;
        }
    };

    private static Object easeOutQuart = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return -c * ((t=t/d-1)*t*t*t - 1) + b;
        }
    };

    private static Object easeInOutQuart = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            if ((t/=d/2) < 1) {
                return c/2*t*t*t*t + b;
            }
            return -c/2 * ((t-=2)*t*t*t - 2) + b;
        }
    };

    private static Object easeInQuint = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return c*(t/=d)*t*t*t*t + b;
        }
    };

    private static Object easeOutQuint = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return c*((t=t/d-1)*t*t*t*t + 1) + b;
        }
    };

    private static Object easeInOutQuint = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            if ((t/=d/2) < 1) {
                return c/2*t*t*t*t*t + b;
            }
            return c/2*((t-=2)*t*t*t*t + 2) + b;
        }
    };

    private static Object easeInSine = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return -c * Math.cos(t/d * (Math.PI/2)) + c + b;
        }
    };

    private static Object easeOutSine = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return c * Math.sin(t/d * (Math.PI/2)) + b;
        }
    };

    private static Object easeInOutSine = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return -c/2 * (Math.cos(Math.PI*t/d) - 1) + b;
        }
    };

    private static Object easeInExpo = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return (t==0) ? b : c * Math.pow(2, 10 * (t/d - 1)) + b;
        }
    };

    private static Object easeOutExpo = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return (t==d) ? b+c : c * (-Math.pow(2, -10 * t/d) + 1) + b;
        }
    };

    private static Object easeInOutExpo = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            if (t==0) {
                return b;
            }
            if (t==d) {
                return b+c;
            }
            if ((t/=d/2) < 1) {
                return c/2 * Math.pow(2, 10 * (t - 1)) + b;
            }
            return c/2 * (-Math.pow(2, -10 * --t) + 2) + b;
        }
    };

    private static Object easeInCirc = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return -c * (Math.sqrt(1 - (t/=d)*t) - 1) + b;
        }
    };

    private static Object easeOutCirc = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return c * Math.sqrt(1 - (t=t/d-1)*t) + b;
        }
    };

    private static Object easeInOutCirc = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            if ((t/=d/2) < 1) {
                return -c/2 * (Math.sqrt(1 - t*t) - 1) + b;
            }
            return c/2 * (Math.sqrt(1 - (t-=2)*t) + 1) + b;
        }
    };


    private static Object easeInElastic = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            double s;
            double p;
            double a=c;
            if (t==0) {
                return b;
            }
            if ((t/=d)==1) {
                return b+c;
            }
            p=d*.3;
            if (a < Math.abs(c)) {
                a=c;
                s=p/4;
            } else {
                s = p/(2*Math.PI) * Math.asin (c/a);
            }
            return -(a*Math.pow(2,10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
        }
    };

    private static Object easeOutElastic = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            double s;
            double p;
            double a=c;
            if (t==0) {
                return b;
            }
            if ((t/=d)==1) {
                return b+c;
            }
            p=d*.3;
            if (a < Math.abs(c)) {
                a=c;
                s=p/4;
            } else {
                s = p/(2*Math.PI) * Math.asin (c/a);
            }
            return a*Math.pow(2,-10*t) * Math.sin( (t*d-s)*(2*Math.PI)/p ) + c + b;
        }
    };

    private static Object easeInOutElastic = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            double s;
            double p;
            double a=c;
            if (t==0) {
                return b;
            }
            if ((t/=d/2)==2) {
                return b+c;
            }
            p=d*(.3*1.5);
            if (a < Math.abs(c)) {
                a=c;
                s=p/4;
            } else {
                s = p/(2*Math.PI) * Math.asin (c/a);
            }
            if (t < 1) {
                return -.5*(a*Math.pow(2,10*(t-=1)) * Math.sin((t*d-s)*(2*Math.PI)/p )) + b;
            }
            return a*Math.pow(2,-10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )*.5 + c + b;
        }
    };

    private static Object easeInBack = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            double s = 1.70158;
            return c*(t/=d)*t*((s+1)*t - s) + b;
        }
    };

    private static Object easeOutBack = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            double s = 1.70158;
            return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
        }
    };

    private static Object easeInOutBack = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            double s = 1.70158;
            if ((t/=d/2) < 1) {
                return c/2*(t*t*(((s*=(1.525))+1)*t - s)) + b;
            }
            return c/2*((t-=2)*t*(((s*=(1.525))+1)*t + s) + 2) + b;
        }
    };

    private static Object easeInBounce = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return easeInBounce(t,b,c,d);
        }
    };

    private static Object easeOutBounce = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            return easeOutBounce(t,b,c,d);
        }
    };

    private static Object easeInOutBounce = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double t = (double) arguments.get(0);
            double b = (double) arguments.get(1);
            double c = (double) arguments.get(2);
            double d = (double) arguments.get(3);
            t = Math.min(t,d);

            if (t < d/2) {
                return easeInBounce (t*2, 0, c, d) * .5 + b;
            }
            return easeOutBounce (t*2-d, 0, c, d) * .5 + c*.5 + b;
        }
    };

    private static double easeInBounce(double t,double b, double c,double d) {
        return c - easeOutBounce(d-t, 0, c, d) + b;
    }

    private static double easeOutBounce(double t,double b, double c,double d) {
        if ((t/=d) < (1/2.75)) {
            return c*(7.5625*t*t) + b;
        } else if (t < (2/2.75)) {
            return c*(7.5625*(t-=(1.5/2.75))*t + .75) + b;
        } else if (t < (2.5/2.75)) {
            return c*(7.5625*(t-=(2.25/2.75))*t + .9375) + b;
        } else {
            return c*(7.5625*(t-=(2.625/2.75))*t + .984375) + b;
        }
    }


    private static class InnerCache<T> {
        private final ArrayDeque<T> deque;

        InnerCache(int size) {
            deque = new ArrayDeque<>(size);
        }

        void add(T t) {
            if(deque.size() >= 4) {
                deque.removeFirst();
                deque.addLast(t);
            } else {
                deque.addLast(t);
            }
        }

        Deque<T> getAll() {
            return deque;
        }
    }

    private static class BezierInterpolatorWrapper implements Interpolator{

        float x1,y1,x2,y2;
        private Interpolator mInnerInterpolator;
        BezierInterpolatorWrapper(float x1,float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            mInnerInterpolator = PathInterpolatorCompat.create(x1,y1,x2,y2);
        }

        @Override
        public float getInterpolation(float input) {
            return mInnerInterpolator.getInterpolation(input);
        }

    }

}
