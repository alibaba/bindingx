'use strict';

import _ from 'simple-lodash';
import Fn from '../fn';
import Animation, {Easing} from 'animation-util';
import Expression from '../expression';
import CommonHandler from './common';
import assign from 'object-assign';

function getArgsFromExp(expression) {
  let newExp = JSON.parse(JSON.stringify(expression));
  if (newExp.type === 'CallExpression') {
    let easing = newExp.children[0].value;
    let isBezier = easing === 'cubicBezier';
    newExp.children[0].value = 'getArgs';
    let args = Expression.execute(newExp, assign(Fn, {}));
    if (isBezier) {
      return {
        duration: args[3],
        begin: args[1],
        offset: args[2],
        end: args[1] + args[2],
        easing,
        x1: args[4],
        y1: args[5],
        x2: args[6],
        y2: args[7]
      };
    } else if (easing && Easing[easing]) {
      return {
        duration: args[3],
        begin: args[1],
        offset: args[2] || 0,
        end: args[1] + args[2],
        easing
      };
    }

  }
  return {
    easing: 'linear',
    duration: Infinity,
    isNormal: true
  };


}

export default class TimingHandler extends CommonHandler {

  animations = [];

  isStart = false;

  callbackCounts = 0;

  constructor(binding) {
    super(binding);
    let {props = [], exitExpression} = this.binding.options;
    props.forEach((prop) => {
      let {element, property, expression} = prop;
      this.animate({
        element,
        property,
        expression,
        exitExpression
      });
    });
  }

  callback = (args = {}) => {
    let {props = []} = this.binding.options;
    if (props && props.length > 0) {
      this.callbackCounts++;
      if (this.callbackCounts === props.length) {
        this.binding.callback(args);
      }
    }
  }

  animate(args) {
    let {element, property, expression, exitExpression} = args;
    let transformed = JSON.parse(expression.transformed);
    let exitTransformed;
    if (exitExpression && exitExpression.transformed) {
      exitTransformed = JSON.parse(exitExpression.transformed);
    }
    let {duration, begin, end, easing, x1, y1, x2, y2, isNormal} = getArgsFromExp(transformed);
    if (duration !== undefined) {
      let animation = new Animation({
        duration,
        easing,
        bezierArgs: easing === 'cubicBezier' ? [x1, y1, x2, y2] : undefined,
        onStart: () => {
          if (!this.isStart) {
            this.binding.callback({state: 'start', t: 0});
            this.isStart = true;
          }
        },
        onRun: (e) => {
          if (exitTransformed && this.binding.getValue({t: e.t}, exitTransformed)) {
            animation.stop();
          }
          let realVal;
          if (isNormal) {
            realVal = this.binding.getValue({t: e.t}, transformed);
          } else {
            let val = this.binding.getValue({t: e.percent}, transformed);
            realVal = (end - begin) * val + begin;
          }
          this.binding.setProperty(element, property, realVal);
        },
        onStop: (e) => {
          this.callback({state: 'exit', t: e.t - 1000 / 60});
        }
      });
      animation.run();
      this.animations.push(animation);
    }
  }

  destroy() {
    _.forEach(this.animations, (animation) => {
      animation.stop();
    });
  }

}