'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});
var raf = window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame || function (callback) {
  window.setTimeout(callback, 1000 / 60);
};

var cancelRAF = window.cancelAnimationFrame || window.webkitCancelAnimationFrame || window.mozCancelAnimationFrame || window.oCancelAnimationFrame || window.msCancelAnimationFrame || window.clearTimeout;

exports.raf = raf;
exports.cancelRAF = cancelRAF;
exports['default'] = module.exports;
exports.default = module.exports;