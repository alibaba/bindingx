/**
 Copyright 2018 Alibaba Group

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

'use strict';

import _ from 'simple-lodash';
import {Easing, Bezier} from 'animation-util';

// inset function
function colorToDecimal(hexColor) {
  let hex = hexColor.replace(/'|"|#/g, '');
  return parseInt(hex, 16);
}

function decToHex(dec) {
  let hex = dec.toString(16);
  let a = [];
  for (let i = 0; i < 6 - hex.length; i++) {
    a.push('0');
  }
  return a.join('') + hex;
}


function parseColor(hexColor) {
  let hex = hexColor.replace(/'|"|#/g, '');
  hex = hex.length === 3 ? [hex[0], hex[0], hex[1], hex[1], hex[2], hex[2]].join('') : hex;
  let r = `${hex[0]}${hex[1]}`;
  let g = `${hex[2]}${hex[3]}`;
  let b = `${hex[4]}${hex[5]}`;
  return {
    r,
    g,
    b,
    dr: colorToDecimal(r),
    dg: colorToDecimal(g),
    db: colorToDecimal(b)
  };
}


let Fn = {
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
  rgb: function(r, g, b) {
    return `rgb(${parseInt(r)},${parseInt(g)},${parseInt(b)})`;
  },
  rgba: function(r, g, b, a) {
    return `rgb(${parseInt(r)},${parseInt(g)},${parseInt(b)},${a})`;
  },
  getArgs: function() {
    return arguments;
  },
  evaluateColor: function(colorFrom, colorTo, percent) {
    percent = percent > 1 ? 1 : percent;
    let from = parseColor(colorFrom);
    let to = parseColor(colorTo);
    let dr = parseInt((to.dr - from.dr) * percent + from.dr);
    let dg = parseInt((to.dg - from.dg) * percent + from.dg);
    let db = parseInt((to.db - from.db) * percent + from.db);
    let resDec = dr * 16 * 16 * 16 * 16 + dg * 16 * 16 + db;
    return `#${decToHex(resDec)}`;
  },

  svgDrawCmd: function(index, values, cmd) {
    return {
      index,
      values,
      cmd
    };
  },
  asArray: function() {
    return [...arguments];
  }
};

// inset all easing functions
_.map(Easing, (v, k) => {
  if (k !== 'cubicBezier') {
    Fn[k] = function(t, begin, offset, duration) {
      t = Math.max(Math.min(t / duration, 1));
      return v(t) * offset + begin;
    };
  }
});


Fn.cubicBezier = function(t, begin, offset, duration, x1, y1, x2, y2) {
  t = Math.max(Math.min(t / duration, 1));
  let epsilon = (1000 / 60 / duration) / 4;
  return Bezier(x1, y1, x2, y2, epsilon)(t) * offset + begin; // eslint-disable-line
};

export default Fn;