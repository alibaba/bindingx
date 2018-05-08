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

import {parse} from 'bindingx-parser';
import {NativeModules, NativeEventEmitter, DeviceEventEmitter, Platform} from 'react-native';


const nativeBindingX = NativeModules.bindingx;
let bindingx = {
  __instances__: {},
  /**
   * bind
   * @param options
   * @example
   {
     anchor:blockRef,
     eventType:'pan',
     props: [
     {
       element:blockRef,
       property:'transform.translateX',
       expression:"x+1"
     }
    ]
   }
   */
  bind(options, callback = function () {
  }) {
    if (!options) {
      throw new Error('should pass options for binding');
    }
    options.exitExpression = formatExpression(options.exitExpression);
    console.log('options:',options)
    if (options.props) {
      options.props.forEach((prop) => {
        prop.expression = formatExpression(prop.expression);
      });
    }
    let res;

    if (nativeBindingX) {



      res = nativeBindingX.bind(options);
      let token = res && res.token;
      this.__instances__[token] = {
        callback
      };
    }
    return res;
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
    return nativeBindingX.unbind(options);
  },
  unbindAll() {
    return nativeBindingX.unbindAll();
  },
  prepare(options) {
    return nativeBindingX.prepare(options);
  },
  getComputedStyle(el) {
    return nativeBindingX.getComputedStyle(el);
  },
  // { y: 0, state: 'start', x: 0, token: '592' }
  __triggerCallback: (event) => {
    let instances = bindingx.__instances__;
    if (event && event.token &&
      instances[event.token] &&
      typeof instances[event.token].callback == 'function') {
      // trigger global event for callback function
      instances[event.token].callback(event);
    }
  }
};


if (Platform.OS == 'ios') {
  const bindingXEmitter = new NativeEventEmitter(nativeBindingX);
  bindingXEmitter.addListener('bindingx:statechange', bindingx.__triggerCallback);
} else {
  DeviceEventEmitter.addListener('bindingx:statechange', bindingx.__triggerCallback);
}


function formatExpression(expression) {
  if (expression === undefined) return;
  try {
    expression = JSON.parse(expression);
  } catch (err) {

  }
  let resultExpression = {};
  if (typeof expression === 'string') {
    resultExpression.origin = expression;
  } else if (expression) {
    resultExpression.origin = expression.origin;
    resultExpression.transformed = expression.transformed;
  }
  if (!resultExpression.transformed && !resultExpression.origin) return;
  resultExpression.transformed = resultExpression.transformed || parse(resultExpression.origin);
  return resultExpression;
}


export default bindingx;