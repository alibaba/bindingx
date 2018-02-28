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

import Quaternion from './quaternion';

function Vector3(x, y, z) {

  this.x = x || 0;
  this.y = y || 0;
  this.z = z || 0;

}

Vector3.prototype = {

  constructor: Vector3,

  isVector3: true,

  set: function(x, y, z) {

    this.x = x;
    this.y = y;
    this.z = z;

    return this;

  },

  applyEuler: function() {

    var quaternion;

    return function applyEuler(euler) {

      if ((euler && euler.isEuler) === false) {

        console.error('THREE.Vector3: .applyEuler() now expects an Euler rotation rather than a Vector3 and order.');

      }

      if (quaternion === undefined) quaternion = new Quaternion();

      return this.applyQuaternion(quaternion.setFromEuler(euler));

    };

  }(),




  applyQuaternion: function(q) {

    var x = this.x,
      y = this.y,
      z = this.z;
    var qx = q.x,
      qy = q.y,
      qz = q.z,
      qw = q.w;

    // calculate quat * vector

    var ix = qw * x + qy * z - qz * y;
    var iy = qw * y + qz * x - qx * z;
    var iz = qw * z + qx * y - qy * x;
    var iw = -qx * x - qy * y - qz * z;

    // calculate result * inverse quat

    this.x = ix * qw + iw * -qx + iy * -qz - iz * -qy;
    this.y = iy * qw + iw * -qy + iz * -qx - ix * -qz;
    this.z = iz * qw + iw * -qz + ix * -qy - iy * -qx;

    return this;

  }



};

export default Vector3;
