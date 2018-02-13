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

import PanGesture from '../pan';
import CommonHandler from './common';

export default class PanHandler extends CommonHandler {

  constructor(binding) {
    super(binding);
    let {anchor} = binding.options;
    let panGesture = this.panGesture = new PanGesture(anchor, binding.options.options);
    panGesture.on('pan', this._onPan);
    panGesture.on('panstart', this._onPanStart);
    panGesture.on('panend', this._onPanEnd);
  }

  _onPan = (e) => {
    let x = e.deltaX;
    let y = e.deltaY;
    let {props = []} = this.binding.options;
    props.forEach((prop) => {
      let {element, property, expression} = prop;
      let transformed = JSON.parse(expression.transformed);
      let val = this.binding.getValue({x, y}, transformed);
      this.binding.setProperty(element, property, val);
    });
  }

  _onPanStart = () => {
    this.binding.callback({deltaX: 0, state: 'start', deltaY: 0});
  }

  _onPanEnd = (e) => {
    this.binding.callback({deltaX: parseInt(e.deltaX), state: 'end', deltaY: parseInt(e.deltaY)});
  }

  destroy() {
    let panGesture = this.panGesture;
    panGesture.off('pan', this._onPan);
    panGesture.off('panstart', this._onPanStart);
    panGesture.off('panend', this._onPanEnd);
    panGesture.destroy();
  }


};