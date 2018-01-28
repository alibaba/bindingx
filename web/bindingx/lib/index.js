/** @jsx createElement */

'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _universalEnv = require('universal-env');

var _expression_parser = require('./mods/expression_parser');

var isSupportNewBinding = true;
var isSupportBinding = true;
var WeexBinding = void 0;
var WebBinding = {};
if (_universalEnv.isWeb) {
  WebBinding = require('./mods/binding');
}
try {
  WeexBinding = __weex_require__('@weex-module/binding');
  isSupportNewBinding = typeof WeexBinding.bind === 'function' && typeof WeexBinding.unbind === 'function' && typeof WeexBinding.unbindAll === 'function' && typeof WeexBinding.getComputedStyle === 'function';
} catch (e) {
  isSupportNewBinding = false;
}

if (!isSupportNewBinding) {
  try {
    WeexBinding = __weex_require__('@weex-module/expressionBinding');
  } catch (err) {
    isSupportBinding = false;
  }
}

function formatExpression(expression) {
  if (expression && expression.origin) {
    expression.transformed = expression.transformed || (0, _expression_parser.parse)(expression.origin);
  }
  return expression;
}

// 统一回调参数
function fixCallback(callback) {
  return function () {
    var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};

    if (typeof callback === 'function') {
      return callback({
        state: params.state === 'end' ? 'exit' : params.state,
        t: params.t !== undefined ? params.t : params.deltaT
      });
    }
  };
}

exports.default = {
  // 是否支持新版本的binding
  isSupportNewBinding: isSupportNewBinding,
  // 是否支持binding
  isSupportBinding: isSupportBinding,
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
  bind: function bind(options) {
    var callback = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : function () {};

    if (!options) {
      throw new Error('should pass options for binding');
    }

    options.exitExpression = formatExpression(options.exitExpression);

    if (options.props) {
      options.props.forEach(function (prop) {
        prop.expression = formatExpression(prop.expression);
      });
    }

    if (_universalEnv.isWeex && WeexBinding) {
      if (isSupportNewBinding) {
        return WeexBinding.bind(options, options && options.eventType === 'timing' ? fixCallback(callback) : callback);
      } else {
        WeexBinding.enableBinding(options.anchor, options.eventType);
        // 处理expression的参数格式
        var expressionArgs = options.props.map(function (prop) {
          return {
            element: prop.element,
            property: prop.property,
            expression: prop.expression.transformed
          };
        });
        WeexBinding.createBinding(options.anchor, options.eventType, '', expressionArgs, callback);
        return;
      }
    }

    return WebBinding.bind(options, callback);
  },

  /**
   *  @param {object} options
   *  @example
   *  {eventType:'pan',
   *   token:self.gesToken}
   */
  unbind: function unbind(options) {
    if (!options) {
      throw new Error('should pass options for binding');
    }
    if (_universalEnv.isWeex && WeexBinding) {
      if (isSupportNewBinding) {
        return WeexBinding.unbind(options);
      } else {
        return WeexBinding.disableBinding(options.anchor, options.eventType);
      }
      return;
    }
    return WebBinding.unbind(options);
  },
  unbindAll: function unbindAll() {
    if (_universalEnv.isWeex && WeexBinding) {
      if (isSupportNewBinding) {
        return WeexBinding.unbindAll();
      } else {
        return WeexBinding.disableAll();
      }
      return;
    }
    return WebBinding.unbindAll();
  },
  getComputedStyle: function getComputedStyle(el) {
    if (_universalEnv.isWeex) {
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
module.exports = exports['default'];