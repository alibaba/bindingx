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
import {
  matrixToTransformObj,
  px,
  pxTo750,
  prefixStyle,
  interceptSVGPath,
  parseSVGPath,
  stringifySVGPath
} from './lib/utils';
import Fn from './lib/fn';
import assign from 'object-assign';

// transform
const vendorTransform = prefixStyle('transform');


function setTransform(transformObj, property, value) {
  transformObj.transform[property] = value;
  transformObj.shouldTransform = true;
}


function bindingXGetComputedStyle(elRef) {
  if (elRef instanceof HTMLElement || elRef instanceof SVGElement) {
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


class Binding {

  _eventHandler = null;

  elTransforms = [];

  elPaths = [];

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
        return o.element === element;
      })) {

        let initialTransform = {
          translateX: 0,
          translateY: 0,
          translateZ: 0,
          scaleX: 1,
          scaleY: 1,
          scaleZ: 1,
          rotateX: 0,
          rotateY: 0,
          rotateZ: 0,
          skewX: 0,
          skewY: 0
        };

        // only for svg element to have the initial style
        if (element instanceof SVGElement) {
          let style = bindingXGetComputedStyle(element);
          initialTransform.translateX = px(style.translateX);
          initialTransform.translateY = px(style.translateY);
          initialTransform.rotateZ = style.rotateZ;
          initialTransform.scaleX = style.scaleX;
          initialTransform.scaleY = style.scaleY;
          initialTransform.skewX = style.skewX;
          initialTransform.skewY = style.skewY;
        }

        elTransforms.push({
          element,
          transform: initialTransform
        });
      }
    });
  }


  getValue(params, expression) {
    return Expression.execute(expression, assign(Fn, params));
  }

  setProperty(el, property, val) {
    // for debug
    if (this.options.debug) {
      console.log('property:', property, ' value:', val);
    }

    if (el instanceof HTMLElement) {
      let elTransform = _.find(this.elTransforms, (o) => {
        return o.element === el;
      });
      switch (property) {
        // case 'scroll.contentOffset':
        //   el.scrollTop = px(val);
        //   el.scrollLeft = px(val);
        //   break;
        case 'scroll.contentOffsetY':
          el.scrollTop = px(val);
          break;
        case 'scroll.contentOffsetX':
          el.scrollLeft = px(val);
          break;
        case 'transform.translateX':
          setTransform(elTransform, 'translateX', px(val));
          break;
        case 'transform.translateY':
          setTransform(elTransform, 'translateY', px(val));
          break;
        case 'transform.translateZ':
          setTransform(elTransform, 'translateZ', px(val));
          break;
        case 'transform.rotateX':
          setTransform(elTransform, 'rotateX', val);
          break;
        case 'transform.rotateY':
          setTransform(elTransform, 'rotateY', val);
          break;
        case 'transform.rotateZ':
        case 'transform.rotate':
          setTransform(elTransform, 'rotateZ', val);
          break;
        case 'transform.scaleX':
          setTransform(elTransform, 'scaleX', val);
          break;
        case 'transform.scaleY':
          setTransform(elTransform, 'scaleY', val);
          break;
        case 'transform.scale':
          setTransform(elTransform, 'scaleX', val);
          setTransform(elTransform, 'scaleY', val);
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
      if (elTransform && elTransform.shouldTransform) {
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
      }
    } else if (el instanceof SVGElement) {
      let elTransform = _.find(this.elTransforms, (o) => {
        return o.element === el;
      });
      switch (property) {
        case 'svg-dashoffset':
          el.setAttribute('stroke-dashoffset', px(val));
          break;
        case 'svg-transform.translateX':
          setTransform(elTransform, 'translateX', px(val));
          break;
        case 'svg-transform.translateY':
          setTransform(elTransform, 'translateY', px(val));
          break;
        case 'svg-transform.translateZ':
          setTransform(elTransform, 'translateZ', px(val));
          break;
        case 'svg-transform.rotateX':
          setTransform(elTransform, 'rotateX', val);
          break;
        case 'svg-transform.rotateY':
          setTransform(elTransform, 'rotateY', val);
          break;
        case 'svg-transform.rotateZ':
        case 'svg-transform.rotate':
          setTransform(elTransform, 'rotateZ', val);
          break;
        case 'svg-transform.scaleX':
          setTransform(elTransform, 'scaleX', val);
          break;
        case 'svg-transform.scaleY':
          setTransform(elTransform, 'scaleY', val);
          break;
        case 'svg-transform.scale':
          setTransform(elTransform, 'scaleX', val);
          setTransform(elTransform, 'scaleY', val);
          break;
        case 'svg-transform.skewX':
          setTransform(elTransform, 'skewX', val);
          break;
        case 'svg-transform.skewY':
          setTransform(elTransform, 'skewY', val);
          break;
        case 'svg-path':
          let exist = _.find(this.elPaths, (o) => {
            return o.element === el;
          });
          if (!exist || !exist.path) {
            exist = {
              element: el,
              path: parseSVGPath(el.getAttribute('d'), pxTo750)
            };
            this.elPaths.push(exist);
          }

          if (exist && exist.path) {
            if (val && val.length) {
              for (let i = 0; i < val.length; i++) {
                exist.path = interceptSVGPath(exist.path, val[i].index, val[i].values, val[i].cmd);
              }
            } else {
              exist.path = interceptSVGPath(exist.path, val.index, val.values, val.cmd);
            }

          }
          break;
      }

      let exist = _.find(this.elPaths, (o) => {
        return o.element === el;
      });
      if (exist && exist.path) {
        el.setAttribute('d', stringifySVGPath(exist.path, px));
      }

      if (elTransform.shouldTransform) {
        el.style[vendorTransform] = [
          `translateX(${elTransform.transform.translateX}px)`,
          `translateY(${elTransform.transform.translateY}px)`,
          `translateZ(${elTransform.transform.translateZ}px)`,
          `scaleX(${elTransform.transform.scaleX})`,
          `scaleY(${elTransform.transform.scaleY})`,
          `rotateX(${elTransform.transform.rotateX}deg)`,
          `rotateY(${elTransform.transform.rotateY}deg)`,
          `rotateZ(${elTransform.transform.rotateZ}deg)`,
          `skewX(${elTransform.transform.skewX}deg)`,
          `skewY(${elTransform.transform.skewY}deg)`,
        ].join(' ');
      }

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
  getComputedStyle: bindingXGetComputedStyle
};



