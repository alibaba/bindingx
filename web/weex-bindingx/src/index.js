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

import {isWeex, isWeb} from 'universal-env';
import {parse} from 'bindingx-parser';

function requireModule(moduleName) {
  try {
    if (typeof weex !== undefined && weex.requireModule) { // eslint-disable-line
      return weex.requireModule(moduleName);  // eslint-disable-line
    }
  } catch (err) {
  }
  return window.require('@weex-module/' + moduleName);
};


let isSupportNewBinding = true;
let isSupportBinding = true;
let WeexBinding;
let WebBinding = {};
if (isWeb) {
  WebBinding = require('bindingx-web-polyfill');
} else {
  try {
    WeexBinding = requireModule('bindingx');
    isSupportNewBinding = true;
  } catch (e) {
    isSupportNewBinding = false;
  }
  if (!WeexBinding || !WeexBinding.bind) {
    try {
      WeexBinding = requireModule('binding');
      isSupportNewBinding = true;
    } catch (e) {
      isSupportNewBinding = false;
    }
  }
  isSupportNewBinding = !!(WeexBinding && WeexBinding.bind && WeexBinding.unbind);
  if (!isSupportNewBinding) {
    try {
      WeexBinding = requireModule('expressionBinding');
      isSupportBinding = true;
    } catch (err) {
      isSupportBinding = false;
    }
  }
  isSupportBinding = !!(WeexBinding && (WeexBinding.bind || WeexBinding.createBinding));
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

function formatExpressions(options) {
  if (!options) return options;
  options.exitExpression = formatExpression(options.exitExpression);

  if (options.props) {
    options.props.forEach((prop) => {
      prop.expression = formatExpression(prop.expression);
    });
  }
  return options;
}

function transformOldProps(options) {
  if (!options || !options.props) return;
  return options.props.map((prop) => {
    return {
      element: prop.element,
      property: prop.property,
      expression: prop.expression.transformed
    };
  });
}

// 统一回调参数
function fixCallback(callback) {
  return function(params = {}) {
    if (typeof callback === 'function') {
      return callback({
        state: params.state === 'end' ? 'exit' : params.state,
        t: params.t !== undefined ? params.t : params.deltaT
      });
    }
  };
}


export default {
  // 是否支持新版本的binding
  isSupportNewBinding,
  // 是否支持binding
  isSupportBinding,
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
    // transform origin expression
    formatExpressions(options);

    if (isWeex) {
      if (WeexBinding && isSupportBinding) {
        if (isSupportNewBinding) {
          return WeexBinding.bind(options, options && options.eventType === 'timing' ? fixCallback(callback) : callback);
        } else {
          WeexBinding.enableBinding(options.anchor, options.eventType);

          WeexBinding.createBinding(options.anchor, options.eventType, '', transformOldProps(options), callback);
        }
      }
    } else {
      return WebBinding.bind(options, callback);
    }
  },

  bindAsync(options, callback = function() {
  }) {
    return new Promise((resolve, reject) => {
      if (!options) {
        reject('should pass options for binding');
      }
      // transform origin expression
      formatExpressions(options);

      if (isWeex) {
        if (WeexBinding && isSupportBinding) {
          if (isSupportNewBinding) {
            if (options && options.eventType === 'timing') {
              callback = fixCallback(callback);
            }
            if (typeof WeexBinding.bindAsync == 'function') {
              WeexBinding.bindAsync(options, callback, (res) => {
                resolve(res);
              });
            } else {
              resolve(WeexBinding.bind(options, callback));
            }
          } else {

            WeexBinding.enableBinding(options.anchor, options.eventType);

            resolve(WeexBinding.createBinding(options.anchor, options.eventType, '', transformOldProps(options), callback));
          }
        }
      } else {
        resolve(WebBinding.bind(options, callback));
      }
    });
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
    if (isWeex) {
      if (WeexBinding && isSupportBinding) {
        if (isSupportNewBinding) {
          return WeexBinding.unbind(options);
        } else {
          return WeexBinding.disableBinding(options.anchor, options.eventType);
        }
      }
    } else {
      return WebBinding.unbind(options);
    }
  },
  unbindAll() {
    if (isWeex) {
      if (WeexBinding && isSupportBinding) {
        if (isSupportNewBinding) {
          return WeexBinding.unbindAll();
        } else {
          return WeexBinding.disableAll();
        }
      }
    } else {
      return WebBinding.unbindAll();
    }
  },
  prepare(options) {
    if (isWeex) {
      if (WeexBinding && isSupportBinding) {
        if (isSupportNewBinding) {
          return WeexBinding.prepare(options);
        } else {
          return WeexBinding.enableBinding(options.anchor, options.eventType);
        }
      }
    }
  },
  getComputedStyle(el) {
    if (isWeex) {
      if (isSupportNewBinding) {
        return WeexBinding.getComputedStyle(el);
      } else {
        return {};
      }
    } else {
      return WebBinding.getComputedStyle(el);
    }
  },
  getComputedStyleAsync(el) {
    return new Promise(resolve => {
      if (isWeex) {
        if (isSupportNewBinding) {
          if (typeof WeexBinding.getComputedStyleAsync == 'function') {
            WeexBinding.getComputedStyleAsync(el, (style) => {
              resolve(style);
            });
          } else {
            resolve(WeexBinding.getComputedStyle(el));
          }
        } else {
          resolve({});
        }
      } else {
        resolve(WebBinding.getComputedStyle(el));
      }
    });
  }
};


