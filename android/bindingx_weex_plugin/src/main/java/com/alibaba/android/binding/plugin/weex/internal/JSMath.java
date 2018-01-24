package com.alibaba.android.binding.plugin.weex.internal;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.text.TextUtils;

import com.taobao.weex.WXEnvironment;
import com.taobao.weex.utils.WXViewUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

class JSMath {

    private JSMath(){}

    private static Object sin = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.sin((double) arguments.get(0));
        }
    };

    private static Object cos = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.cos((double) arguments.get(0));
        }
    };

    private static Object tan = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.tan((double) arguments.get(0));
        }
    };

    private static Object asin = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.asin((double) arguments.get(0));
        }
    };

    private static Object acos = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.acos((double) arguments.get(0));
        }
    };

    private static Object atan = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.atan((double) arguments.get(0));
        }
    };

    private static Object atan2 = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.atan2((double) arguments.get(0), (double) arguments.get(1));
        }
    };

    private static Object pow = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.pow((double) arguments.get(0), (double) arguments.get(1));
        }
    };

    private static Object exp = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.exp((double) arguments.get(0));
        }
    };

    private static Object sqrt = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.sqrt((double) arguments.get(0));
        }
    };

    private static Object cbrt = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.cbrt((double) arguments.get(0));
        }
    };

    private static Object log = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.log((double) arguments.get(0));
        }
    };

    private static Object abs = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.abs((double) arguments.get(0));
        }
    };

    private static Object sign = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            double v = (double) arguments.get(0);
            if (v > 0)
                return 1;
            if (v == 0)
                return 0;
            if (v < 0)
                return -1;
            return Double.NaN;
        }
    };

    private static Object ceil = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.ceil((double) arguments.get(0));
        }
    };

    private static Object floor = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.floor((double) arguments.get(0));
        }
    };

    private static Object round = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            return Math.round((double) arguments.get(0));
        }
    };

    private static Object max = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            if(arguments != null && arguments.size() >= 1) {
                double max = (double) arguments.get(0);
                for (int i = 1,len = arguments.size();i < len; i++) {
                    double val = (double) arguments.get(i);
                    if(val > max) {
                        max = val;
                    }
                }
                return max;
            }
            return null;
        }
    };

    private static Object min = new JSFunctionInterface() {
        public Object execute(ArrayList<Object> arguments) {
            if(arguments != null && arguments.size() >= 1) {
                double min = (double) arguments.get(0);
                for (int i = 1,len = arguments.size();i < len; i++) {
                    double val = (double) arguments.get(i);
                    if(val < min) {
                        min = val;
                    }
                }
                return min;
            }
            return null;
        }
    };
    private static Object PI = Math.PI;
    public static Object E = Math.E;
    //static public Object LN2 = Math.LN2;
    //static public Object LN10 = Math.LN10;


    //transform

    private static Object translate = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            if(arguments == null || arguments.size() < 2) {
                return null;
            }
            return arguments;
        }
    };

    private static Object scale = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            if(arguments == null || arguments.size() < 2) {
                return null;
            }
            return arguments;
        }
    };


    private static Object matrix = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            if(arguments == null || arguments.size() < 6) {
                return null;
            }
            return arguments;
        }
    };


    private static Object real = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            double d = (double) arguments.get(0);
            return (double) WXViewUtils.getRealPxByWidth((float) d);
        }
    };

    private static Object rgb = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            if(arguments == null || arguments.size() < 3) {
                return null;
            }

            double r = (double) arguments.get(0);
            double g = (double) arguments.get(1);
            double b = (double) arguments.get(2);

            return Color.rgb((int)r,(int)g,(int)b);
        }
    };

    private static Object rgba = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            if(arguments == null || arguments.size() < 4) {
                return null;
            }
            /*rgb==0~255*/
            double r = (double) arguments.get(0);
            double g = (double) arguments.get(1);
            double b = (double) arguments.get(2);
            /*a=0~1*/
            double a = ((double) arguments.get(3))*255;
            return Color.argb((int)a,(int)r,(int)g,(int)b);
        }
    };

    private static ArgbEvaluator sArgbEvaluator = new ArgbEvaluator();

    private static Object evaluateColor = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            int fromColor = parseColor((String) arguments.get(0));
            int toColor = parseColor((String) arguments.get(1));
            double fraction = (double) arguments.get(2);
            fraction = Math.min(1.0d,Math.max(0.0d,fraction));
            return sArgbEvaluator.evaluate((float) fraction,fromColor,toColor);
        }
    };

    private static int parseColor(String str) {
        if(TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Unknown color");
        }
        String colorStr = str;
        if(str.startsWith("'") || str.startsWith("\"")) {
            colorStr = colorStr.substring(1,colorStr.length()-1);
        }
        int color = Color.parseColor(colorStr);
        color = Color.argb(255,Color.red(color),Color.green(color),Color.blue(color));
        return color;
    }

    private static Object asArray = new JSFunctionInterface() {
        @Override
        public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
            return arguments;
        }
    };

    static void applyXYToScope(Map<String, Object> scope, double x,double y){
        scope.put("x",(x* WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth()));
        scope.put("y",(y* WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth()));
        scope.put("internal_x",x);
        scope.put("internal_y",y);
    }

    static void applyOrientationValuesToScope(Map<String,Object> scope, double alpha, double beta, double gamma,
                                              double startAlpha, double startBeta, double startGamma,
                                              double x, double y, double z) {
        scope.put("alpha", alpha);
        scope.put("beta", beta);
        scope.put("gamma", gamma);

        scope.put("dalpha", alpha-startAlpha);
        scope.put("dbeta", beta-startBeta);
        scope.put("dgamma", gamma-startGamma);

        scope.put("x", x);
        scope.put("y", y);
        scope.put("z", z);

    }

    static void applyTimingValuesToScope(Map<String,Object> scope, double t) {
        scope.put("t", t);
    }

    static void applyScrollValuesToScope(Map<String,Object> scope, double x, double y
                    , double dx, double dy, double tdx, double tdy) {
        scope.put("x",(x* WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth()));
        scope.put("y",(y* WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth()));

        scope.put("dx",(dx* WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth()));
        scope.put("dy",(dy* WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth()));

        scope.put("tdx",(tdx* WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth()));
        scope.put("tdy",(tdy* WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth()));

        scope.put("internal_x",x);
        scope.put("internal_y",y);
    }

    static void applyToScope(Map<String, Object> scope) {
        scope.put("sin", JSMath.sin);
        scope.put("cos", JSMath.cos);
        scope.put("tan", JSMath.tan);

        scope.put("asin", JSMath.asin);
        scope.put("acos", JSMath.acos);
        scope.put("atan", JSMath.atan);
        scope.put("atan2", JSMath.atan2);

        scope.put("pow", JSMath.pow);
        scope.put("exp", JSMath.exp);
        scope.put("sqrt", JSMath.sqrt);
        scope.put("cbrt", JSMath.cbrt);
        scope.put("log", JSMath.log);

        scope.put("abs", JSMath.abs);
        scope.put("sign", JSMath.sign);

        scope.put("ceil", JSMath.ceil);
        scope.put("floor", JSMath.floor);
        scope.put("round", JSMath.round);

        scope.put("max", JSMath.max);
        scope.put("min", JSMath.min);

        scope.put("PI", JSMath.PI);
        scope.put("E", JSMath.E);

        //transform
        scope.put("translate",JSMath.translate);
        scope.put("scale",JSMath.scale);
        scope.put("matrix",JSMath.matrix);

        scope.put("real",JSMath.real);

        //rgb
        scope.put("rgb",JSMath.rgb);
        scope.put("rgba",JSMath.rgba);
        scope.put("evaluateColor", JSMath.evaluateColor);

        scope.put("asArray",JSMath.asArray);
    }

}