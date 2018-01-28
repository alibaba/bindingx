"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
var _Math = {
  DEG2RAD: Math.PI / 180,
  RAD2DEG: 180 / Math.PI,
  degToRad: function degToRad(degrees) {
    return degrees * _Math.DEG2RAD;
  },
  radToDeg: function radToDeg(radians) {
    return radians * _Math.RAD2DEG;
  }
};

exports.default = _Math;
module.exports = exports["default"];