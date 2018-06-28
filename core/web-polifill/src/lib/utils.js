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

/**
 * Transforms matrix into an object
 *
 * @param string matrix
 * @return object
 *
 * see https://stackoverflow.com/questions/9818702/is-there-js-plugin-convert-the-matrix-parameter-to-css3-transform-property
 */

// TODO matrix4 for 3D
const matrixToTransformObj = function(matrix) {
  // this happens when there was no rotation yet in CSS
  if (matrix === 'none') {
    matrix = 'matrix(1,0,0,1,0,0)';
  }
  let {atan, atan2, round, sqrt, PI} = Math;
  let obj = {
      skewY: 0,
      skewX: 0
    },
    values = matrix.match(/([-+]?[\d\.]+)/g);
  let [a, b, c, d, e, f] = values;
  obj.rotate = obj.rotateZ = round(
    atan2(parseFloat(b), parseFloat(a)) * (180 / Math.PI)) || 0;
  obj.translateX = e !== undefined ? pxTo750(e) : 0;
  obj.translateY = f !== undefined ? pxTo750(f) : 0;
  obj.scaleX = sqrt(a * a + b * b);
  obj.scaleY = sqrt(c * c + d * d);

  // if (a) {
  //   obj.skewX = atan(c / a) * 180 / PI;
  //   obj.skewY = atan(b / a) * 180 / PI;
  // } else if (b) {
  //   obj.skewX = atan(d / b) * 180 / PI;
  // } else {
  //   obj.skewX = PI * 0.25 * 180 / PI;
  // }
  return obj;
};

function pxTo750(n) {
  return n / document.body.clientWidth * 750;
}

function px(n) {
  return n / 750 * document.body.clientWidth;
  // return Math.round(n / 750 * document.body.clientWidth);
}

function clamp(n, min, max) {
  return n < min ? min : n > max ? max : n;
}

const vendor = (function() {
  var el = document.createElement('div').style;
  var vendors = ['t', 'webkitT', 'MozT', 'msT', 'OT'],
    transform,
    i = 0,
    l = vendors.length;
  for (; i < l; i++) {
    transform = vendors[i] + 'ransform';
    if (transform in el) return vendors[i].substr(0, vendors[i].length - 1);
  }
  return false;
})();

/**
 *  add vendor to attribute
 *  @memberOf Util
 *  @param {String} attrName name of attribute
 *  @return { String }
 **/
function prefixStyle(attrName) {
  if (vendor === false) return false;
  if (vendor === '') return attrName;
  return vendor + attrName.charAt(0).toUpperCase() + attrName.substr(1);
}


// Parses an SVG path into an object.
var length = {a: 7, c: 6, h: 1, l: 2, m: 2, q: 4, s: 4, t: 2, v: 1, z: 0};
var segment = /([astvzqmhlc])([^astvzqmhlc]*)/ig;


function parseValues(args) {
  var numbers = args.match(/-?[0-9]*\.?[0-9]+(?:e[-+]?\d+)?/ig);
  return numbers ? numbers.map(Number) : [];
}

/*
// var d='M3,7 5-6 L1,7 1e2-.4 m-10,10 l10,0  \
//   V27 89 H23           v10 h10             \
//   C33,43 38,47 43,47   c0,5 5,10 10,10     \
//   S63,67 63,67         s-10,10 10,10       \
//   Q50,50 73,57         q20,-5 0,-10        \
//   T70,40               t0,-15              \
//   A5,5 45 1,0 40,20    a5,5 20 0,1 -10-10  Z';
//
// console.log(parseSVGPath(d))

 */
function parseSVGPath(path, fn) {
  var data = [];
  path.replace(segment, function(_, command, args) {
    var type = command.toLowerCase();
    args = parseValues(args);

    // overloaded moveTo
    if (type === 'm' && args.length > 2) {
      data.push([command].concat(args.splice(0, 2)));
      type = 'l';
      command = command === 'm' ? 'l' : 'L';
    }

    while (args.length >= 0) {
      if (args.length === length[type]) {
        args.unshift(command);
        return data.push(args);
      }
      if (args.length < length[type]) {
        throw new Error('malformed path data');
      }
      data.push([command].concat(args.splice(0, length[type])));
    }
  });


  if (typeof fn === 'function') {
    return data.map((path) => {
      return path.map((o, i) => {
        return i > 0 ? fn(o) : o;
      });
    });
  }

  return data;
}


function stringifySVGPath(pathArray, fn) {

  if (typeof fn === 'function') {
    pathArray = pathArray.map((path) => {
      return path.map((o, i) => {
        return i > 0 ? fn(o) : o;
      });
    });
  }


  return pathArray.map((path) => {
    return path.join(' ');
  }).join(' ');
}


function interceptSVGPath(pathObj, index, values, cmd) {
  if (pathObj && pathObj[index]) {
    cmd = (cmd && cmd.replace(/'|"/g, '') || pathObj[index][0]).replace(/'|"/g, '');
    values = [cmd, ...values];
    pathObj[index] = values;
    // Array.prototype.splice.call(pathObj[index], 0, values.length, ...values);
  }

  return pathObj;
}


export {
  matrixToTransformObj,
  pxTo750,
  px,
  clamp,
  prefixStyle,
  parseSVGPath,
  stringifySVGPath,
  interceptSVGPath
};