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
import Expression from './lib/expression';
import {PanHandler, OrientationHandler, TimingHandler, ScrollHandler} from './lib/handlers';
import {matrixToTransformObj, px, pxTo750, prefixStyle} from './lib/utils';
import Fn from './lib/fn';
import assign from 'object-assign';

// transform
const vendorTransform = prefixStyle('transform');

class Binding {

  _eventHandler = null;

  elTransforms = [];

  token = null;

  constructor(options, callback) {
    this.options = options;
    this.callback = callback;
    this.token = this.genToken();
    this._initElTransforms();
    let {eventType} = options;
    switch (eventType) {
      case 'pan':
        this._eventHandler = new PanHandler(this);
        break;
      case 'orientation':
        this._eventHandler = new OrientationHandler(this);
        break;
      case 'timing':
        this._eventHandler = new TimingHandler(this);
        break;
      case 'scroll':
        this._eventHandler = new ScrollHandler(this);
        break;
    }
  }

  genToken() {
    return parseInt(Math.random() * 10000000);
  }

  _initElTransforms() {
    let {props = []} = this.options;
    let elTransforms = this.elTransforms;
    props.forEach((prop) => {
      let {element} = prop;
      if (!_.find(elTransforms, (o) => {
        return o.element === element && element instanceof HTMLElement;
      })) {
        elTransforms.push({
          element,
          transform: {
            translateX: 0,
            translateY: 0,
            translateZ: 0,
            scaleX: 1,
            scaleY: 1,
            scaleZ: 1,
            rotateX: 0,
            rotateY: 0,
            rotateZ: 0
          }
        });
      }
    });
  }


  getValue(params, expression) {
    return Expression.execute(expression, assign(Fn, params));
  }

  // TODO scroll.contentOffset 待确认及补全
  setProperty(el, property, val) {
    if (el instanceof HTMLElement) {
      let elTransform = _.find(this.elTransforms, (o) => {
        return o.element === el;
      });
      switch (property) {
        case 'transform.translateX':
          elTransform.transform.translateX = px(val);
          break;
        case 'transform.translateY':
          elTransform.transform.translateY = px(val);
          break;
        case 'transform.translateZ':
          elTransform.transform.translateZ = px(val);
          break;
        case 'transform.rotateX':
          elTransform.transform.rotateX = val;
          break;
        case 'transform.rotateY':
          elTransform.transform.rotateY = val;
          break;
        case 'transform.rotateZ':
          elTransform.transform.rotateZ = val;
          break;
        case 'transform.rotate':
          elTransform.transform.rotateZ = val;
          break;
        case 'transform.scaleX':
          elTransform.transform.scaleX = val;
          break;
        case 'transform.scaleY':
          elTransform.transform.scaleY = val;
          break;
        case 'transform.scale':
          elTransform.transform.scaleX = val;
          elTransform.transform.scaleY = val;
          break;
        case 'opacity':
          el.style.opacity = val;
          break;
        case 'background-color':
          el.style['background-color'] = val;
          break;
        case 'color':
          el.style.color = val;
          break;
        case 'width':
        case 'height':
        case 'border-top-left-radius':
        case 'border-top-right-radius':
        case 'border-bottom-left-radius':
        case 'border-bottom-right-radius':
        case 'border-radius':
        case 'margin-top':
        case 'margin-bottom':
        case 'margin-left':
        case 'margin-right':
        case 'padding-top':
        case 'padding-bottom':
        case 'padding-left':
        case 'padding-right':
          el.style[property] = `${px(val)}px`;
          break;
      }
      el.style[vendorTransform] = [
        `translateX(${elTransform.transform.translateX}px)`,
        `translateY(${elTransform.transform.translateY}px)`,
        `translateZ(${elTransform.transform.translateZ}px)`,
        `scaleX(${elTransform.transform.scaleX})`,
        `scaleY(${elTransform.transform.scaleY})`,
        `rotateX(${elTransform.transform.rotateX}deg)`,
        `rotateY(${elTransform.transform.rotateY}deg)`,
        `rotateZ(${elTransform.transform.rotateZ}deg)`
      ].join(' ');
    } else {

      switch (property) {
        case 'lottie-progress':
          // for lottie
          if (typeof el.setProgress == 'function') {
            el.setProgress(val);
          }
          break;
      }

    }

  }

  destroy() {
    this._eventHandler.destroy();
  }

}


module.exports = {
  _bindingInstances: [],
  /**
   * 绑定
   * @param options 参数
   * @example
   {
     anchor:blockRef,
     eventType:'pan',
     props: [
     {
       element:blockRef,
       property:'transform.translateX',
       expression:{
         origin:"x+1",
         transformed:"{\"type\":\"+\",\"children\":[{\"type\":\"Identifier\",\"value\":\"x\"},{\"type\":\"NumericLiteral\",\"value\":1}]}"
       }
     }
    ]
   }
   */
  bind(options, callback = function() {
  }) {
    if (!options) {
      throw new Error('should pass options for binding');
    }
    let existInstances = _.filter(this._bindingInstances, (instance) => {
      if (options.anchor) {
        return instance.options.anchor === options.anchor && instance.options.eventType === options.eventType;
      }
    });
    // 销毁上次实例
    if (existInstances) {
      _.forEach(existInstances, (inst) => {
        inst.destroy();
        this._bindingInstances = _.dropWhile(this._bindingInstances, (bindInst) => {
          return bindInst === inst;
        });
      });
    }
    let binding = new Binding(options, callback);
    this._bindingInstances.push(binding);
    return {token: binding.token};
  },
  /**
   *  @param {object} options
   *  @example
   *  {eventType:'pan',
   *   token:self.gesToken}
   */
  unbind(options) {
    if (!options) {
      throw new Error('should pass options for binding');
    }
    let inst = _.find(this._bindingInstances, (instance) => {
      return instance.options.eventType === options.eventType && instance.token === options.token;
    });
    if (inst) {
      inst.destroy();
    }

  },
  unbindAll() {
    this._bindingInstances.forEach((inst) => {
      inst.destroy({
        eventType: inst.options.eventType,
        token: inst.token
      });
    });
  },
  getComputedStyle(elRef) {
    if (elRef instanceof HTMLElement) {
      let computedStyle = window.getComputedStyle(elRef);
      let style = matrixToTransformObj(computedStyle[vendorTransform]);
      style.opacity = Number(computedStyle.opacity);
      style['background-color'] = computedStyle['background-color'];
      style.color = computedStyle.color;
      style.width = pxTo750(computedStyle.width.replace('px', ''));
      style.height = pxTo750(computedStyle.height.replace('px', ''));
      style['border-top-left-radius'] = pxTo750(computedStyle['border-top-left-radius'].replace('px', ''));
      style['border-top-right-radius'] = pxTo750(computedStyle['border-top-right-radius'].replace('px', ''));
      style['border-bottom-left-radius'] = pxTo750(computedStyle['border-bottom-left-radius'].replace('px', ''));
      style['border-bottom-right-radius'] = pxTo750(computedStyle['border-bottom-right-radius'].replace('px', ''));
      style['margin-top'] = pxTo750(computedStyle['margin-top'].replace('px', ''));
      style['margin-bottom'] = pxTo750(computedStyle['margin-bottom'].replace('px', ''));
      style['margin-left'] = pxTo750(computedStyle['margin-left'].replace('px', ''));
      style['margin-right'] = pxTo750(computedStyle['margin-right'].replace('px', ''));
      style['padding-top'] = pxTo750(computedStyle['padding-top'].replace('px', ''));
      style['padding-bottom'] = pxTo750(computedStyle['padding-bottom'].replace('px', ''));
      style['padding-left'] = pxTo750(computedStyle['padding-left'].replace('px', ''));
      style['padding-right'] = pxTo750(computedStyle['padding-right'].replace('px', ''));
      return style;
    } else {
      // TODO lottie support
      // if(typeof elRef.setProgress == 'function') {
      //   return {
      // 'lottie-progress':
      // }
      // }
    }
  }

};



