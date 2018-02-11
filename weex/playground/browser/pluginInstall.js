import BindingxWeexPlugin from "../../js/src";

if (window.Weex) {
  Weex.install(BindingxWeexPlugin);
} else if (window.weex) {
  weex.install(BindingxWeexPlugin);
}