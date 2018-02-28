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
import CommonHandler from './common';
import {pxTo750} from '../utils';

function isTurner(prev, now) {

  return prev / now < 0;
}

export default class ScrollHandler extends CommonHandler {

  dx = 0;
  dy = 0;
  prevX = null;
  prevY = null;
  tx = 0;
  ty = 0;
  tdx = 0;
  tdy = 0;

  constructor(binding) {
    super(binding);
    let {anchor} = binding.options;
    this.tx = pxTo750(anchor.scrollLeft);
    this.ty = pxTo750(anchor.scrollTop);
    anchor.addEventListener('scroll', this._onScroll);
  }

  _onScroll = (e) => {
    let {props} = this.binding.options;
    let callback = this.binding.callback;
    let x = pxTo750(e.target.scrollLeft);
    let y = pxTo750(e.target.scrollTop);
    props.forEach((prop) => {
      let {element, property, expression} = prop;
      let transformed = JSON.parse(expression.transformed);
      let val = this.binding.getValue({
        x,
        y,
        dx: this.dx,
        dy: this.dy,
        tdx: this.tdx,
        tdy: this.tdy
      }, transformed);

      this.binding.setProperty(element, property, val);
    });


    if (this.prevX !== null && this.prevY !== null) {
      let dx = x - this.prevX;
      let dy = y - this.prevY;
      let cbParams = {
        x,
        y
      };
      // 拐点
      if (isTurner(this.dx, dx)) {
        this.tx = x;
        cbParams.state = 'turn';
      }
      if (isTurner(this.dy, dy)) {
        this.ty = y;
        cbParams.state = 'turn';
      }

      this.dx = cbParams.dx = x - this.prevX;
      this.dy = cbParams.dy = y - this.prevY;
      this.tdx = cbParams.tdx = x - this.tx;
      this.tdy = cbParams.tdy = y - this.ty;
      if (cbParams.state) {
        callback(cbParams);
      }
    }

    this.prevX = x;
    this.prevY = y;


  }

  destroy() {
    let {anchor} = this.binding.options;
    anchor.removeEventListener('scroll', this._onScroll);
  }

}