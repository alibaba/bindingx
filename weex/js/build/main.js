(this.nativeLog || function(s) {console.log(s)})('START WEEX PICKER: 0.1.1 Build 20170329');

(function (global, factory) {
  typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
  typeof define === 'function' && define.amd ? define(factory) :
  (global.WeexR = factory());
}(this, (function () { 'use strict';

function __$styleInject(css, returnValue) {
  if (typeof document === 'undefined') {
    return returnValue;
  }
  css = css || '';
  var head = document.head || document.getElementsByTagName('head')[0];
  var style = document.createElement('style');
  style.type = 'text/css';
  if (style.styleSheet){
    style.styleSheet.cssText = css;
  } else {
    style.appendChild(document.createTextNode(css));
  }
  head.appendChild(style);
  return returnValue;
}
var WeexR = {
  create: function create(options, callbackID) {

  }
};

if(window.Vue) {
  weex.registerModule('WeexR', WeexR);
}

function init(weex) {
  weex.registerApiModule('WeexR', WeexR, meta);
}

var index = {
  init: init
};

return index;

})));
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjpudWxsLCJzb3VyY2VzIjpbIi4uL3NyYy9pbmRleC5qcyJdLCJzb3VyY2VzQ29udGVudCI6WyJcblxuY29uc3QgV2VleFIgPSB7XG4gIGNyZWF0ZShvcHRpb25zLCBjYWxsYmFja0lEKSB7XG5cbiAgfVxufTtcblxuaWYod2luZG93LlZ1ZSkge1xuICB3ZWV4LnJlZ2lzdGVyTW9kdWxlKCdXZWV4UicsIFdlZXhSKTtcbn1cblxuZnVuY3Rpb24gaW5pdCh3ZWV4KSB7XG4gIHdlZXgucmVnaXN0ZXJBcGlNb2R1bGUoJ1dlZXhSJywgV2VleFIsIG1ldGEpO1xufVxuXG5tb2R1bGUuZXhwb3J0cyA9IHtcbiAgaW5pdFxufTtcbiJdLCJuYW1lcyI6WyJjb25zdCJdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0FBRUFBLElBQU0sS0FBSyxHQUFHO0VBQ1osTUFBTSxpQkFBQSxDQUFDLE9BQU8sRUFBRSxVQUFVLEVBQUU7O0dBRTNCO0NBQ0YsQ0FBQzs7QUFFRixHQUFHLE1BQU0sQ0FBQyxHQUFHLEVBQUU7RUFDYixJQUFJLENBQUMsY0FBYyxDQUFDLE9BQU8sRUFBRSxLQUFLLENBQUMsQ0FBQztDQUNyQzs7QUFFRCxTQUFTLElBQUksQ0FBQyxJQUFJLEVBQUU7RUFDbEIsSUFBSSxDQUFDLGlCQUFpQixDQUFDLE9BQU8sRUFBRSxLQUFLLEVBQUUsSUFBSSxDQUFDLENBQUM7Q0FDOUM7O0FBRUQsU0FBYyxHQUFHO0VBQ2YsTUFBQSxJQUFJO0NBQ0wsQ0FBQzs7OzsifQ==
