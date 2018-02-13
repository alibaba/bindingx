/** @jsx createElement */

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

    options.exitExpression = formatExpression(options.exitExpression);

    if (options.props) {
      options.props.forEach((prop) => {
        prop.expression = formatExpression(prop.expression);
      });
    }

    if (isWeex) {
      if (WeexBinding && isSupportBinding) {
        if (isSupportNewBinding) {
          return WeexBinding.bind(options, options && options.eventType === 'timing' ? fixCallback(callback) : callback);
        } else {
          WeexBinding.enableBinding(options.anchor, options.eventType);
          // 处理expression的参数格式
          let expressionArgs = options.props.map((prop) => {
            return {
              element: prop.element,
              property: prop.property,
              expression: prop.expression.transformed
            };
          });
          WeexBinding.createBinding(options.anchor, options.eventType, '', expressionArgs, callback);
        }
      }
    } else {
      return WebBinding.bind(options, callback);
    }
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
  }
};


