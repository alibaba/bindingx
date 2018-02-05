'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _simpleLodash = require('simple-lodash');

var _simpleLodash2 = _interopRequireDefault(_simpleLodash);

var _easing = require('./easing');

var _easing2 = _interopRequireDefault(_easing);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

// 内置方法
function colorToDecimal(hexColor) {
  var hex = hexColor.replace(/'|"|#/g, '');
  return parseInt(hex, 16);
}

function decToHex(dec) {
  var hex = dec.toString(16);
  var a = [];
  for (var i = 0; i < 6 - hex.length; i++) {
    a.push('0');
  }
  return a.join('') + hex;
}

function parseColor(hexColor) {
  var hex = hexColor.replace(/'|"|#/g, '');
  hex = hex.length === 3 ? [hex[0], hex[0], hex[1], hex[1], hex[2], hex[2]].join('') : hex;
  var r = '' + hex[0] + hex[1];
  var g = '' + hex[2] + hex[3];
  var b = '' + hex[4] + hex[5];
  return {
    r: r,
    g: g,
    b: b,
    dr: colorToDecimal(r),
    dg: colorToDecimal(g),
    db: colorToDecimal(b)
  };
}

var Fn = {
  max: Math.max,
  min: Math.min,
  sin: Math.sin,
  cos: Math.cos,
  tan: Math.tan,
  sqrt: Math.sqrt,
  cbrt: Math.cbrt,
  log: Math.log,
  abs: Math.abs,
  atan: Math.atan,
  floor: Math.floor,
  ceil: Math.ceil,
  pow: Math.pow,
  exp: Math.exp,
  PI: Math.PI,
  E: Math.E,
  acos: Math.acos,
  asin: Math.asin,
  sign: Math.sign,
  atan2: Math.atan2,
  round: Math.round,
  rgb: function rgb(r, g, b) {
    return 'rgb(' + parseInt(r) + ',' + parseInt(g) + ',' + parseInt(b) + ')';
  },
  rgba: function rgba(r, g, b, a) {
    return 'rgb(' + parseInt(r) + ',' + parseInt(g) + ',' + parseInt(b) + ',' + a + ')';
  },
  // 用来获取参数
  getArgs: function getArgs() {
    return arguments;
  },
  // 颜色估值算法
  evaluateColor: function evaluateColor(colorFrom, colorTo, percent) {
    percent = percent > 1 ? 1 : percent;
    var from = parseColor(colorFrom);
    var to = parseColor(colorTo);
    var dr = parseInt((to.dr - from.dr) * percent + from.dr);
    var dg = parseInt((to.dg - from.dg) * percent + from.dg);
    var db = parseInt((to.db - from.db) * percent + from.db);
    var resDec = dr * 16 * 16 * 16 * 16 + dg * 16 * 16 + db;
    return '#' + decToHex(resDec);
  }
};

// 内置easing所有方法
_simpleLodash2.default.map(_easing2.default, function (v, k) {
  Fn[k] = function (t) {
    return t;
  };
});

exports.default = Fn;
module.exports = exports['default'];