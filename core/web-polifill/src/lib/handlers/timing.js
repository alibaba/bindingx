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

import Animation, {Easing} from 'animation-util';
import CommonHandler from './common';


export default class TimingHandler extends CommonHandler {

  constructor(binding) {
    super(binding);
    let {props = [], exitExpression} = this.binding.options;


    props.forEach((prop) => {
      let {expression} = prop;
      if (expression && expression.transformed && typeof expression.transformed === 'string') {
        expression.transformed = JSON.parse(expression.transformed);
      }
    });

    let exitTransformed;
    if (exitExpression && exitExpression.transformed) {
      exitTransformed = JSON.parse(exitExpression.transformed);
    }

    let animation = this.animation = new Animation({
      duration: Infinity,
      easing: 'linear',
      onStart: () => {
        this.binding.callback({state: 'start', t: 0});
      },
      onRun: (e) => {
        if (exitTransformed && this.binding.getValue({t: e.t}, exitTransformed)) {
          this.animation.stop();
        }
        props.forEach((prop) => {
          this.animate({
            exitTransformed,
            t: e.t,
            ...prop
          });
        });
      },
      onStop: (e) => {
        this.binding.callback({state: 'exit', t: e.t - 1000 / 60});
      }
    });
    animation.run();
  }

  animate(args) {
    let {element, property, expression, t} = args;
    let value = this.binding.getValue({t}, expression.transformed);
    this.binding.setProperty(element, property, value);
  }

  destroy() {
    if (this.animation) {
      this.animation.stop();
    }
  }

}