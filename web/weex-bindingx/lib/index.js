(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else {
		var a = factory();
		for(var i in a) (typeof exports === 'object' ? exports : root)[i] = a[i];
	}
})(typeof self !== 'undefined' ? self : this, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/** @jsx createElement */



Object.defineProperty(exports, "__esModule", {
  value: true
});

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var _universalEnv = __webpack_require__(1);

var _bindingxParser = __webpack_require__(3);

function requireModule(moduleName) {
  try {
    if ((typeof weex === 'undefined' ? 'undefined' : _typeof(weex)) !== undefined && weex.requireModule) {
      // eslint-disable-line
      return weex.requireModule(moduleName); // eslint-disable-line
    }
  } catch (err) {}
  return window.require('@weex-module/' + moduleName);
};

var isSupportNewBinding = true;
var isSupportBinding = true;
var WeexBinding = void 0;
var WebBinding = {};
if (_universalEnv.isWeb) {
  WebBinding = __webpack_require__(5);
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
  isSupportNewBinding = !!WeexBinding && WeexBinding.bind && WeexBinding.unbind;
  if (!isSupportNewBinding) {
    try {
      WeexBinding = requireModule('expressionBinding');
      isSupportBinding = true;
    } catch (err) {
      isSupportBinding = false;
    }
  }
  isSupportBinding = !!WeexBinding && (WeexBinding.bind || WeexBinding.createBinding);
}

function formatExpression(expression) {
  if (expression === undefined) return;
  try {
    expression = JSON.parse(expression);
  } catch (err) {}
  var resultExpression = {};
  if (typeof expression === 'string') {
    resultExpression.origin = expression;
  } else if (expression) {
    resultExpression.origin = expression.origin;
    resultExpression.transformed = expression.transformed;
  }
  if (!resultExpression.transformed && !resultExpression.origin) return;
  resultExpression.transformed = resultExpression.transformed || (0, _bindingxParser.parse)(resultExpression.origin);
  return resultExpression;
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

    if (_universalEnv.isWeex) {
      if (WeexBinding && isSupportBinding) {
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
  unbind: function unbind(options) {
    if (!options) {
      throw new Error('should pass options for binding');
    }
    if (_universalEnv.isWeex) {
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
  unbindAll: function unbindAll() {
    if (_universalEnv.isWeex) {
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

/***/ }),
/* 1 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(process) {

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

// https://www.w3.org/TR/html5/webappapis.html#dom-navigator-appcodename
var isWeb = exports.isWeb = (typeof navigator === 'undefined' ? 'undefined' : _typeof(navigator)) === 'object' && (navigator.appCodeName === 'Mozilla' || navigator.product === 'Gecko');
var isNode = exports.isNode = typeof process !== 'undefined' && !!(process.versions && process.versions.node);
var isWeex = exports.isWeex = typeof callNative === 'function';
var isReactNative = exports.isReactNative = typeof __fbBatchedBridgeConfig !== 'undefined';
exports['default'] = module.exports;
exports.default = module.exports;
/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(2)))

/***/ }),
/* 2 */
/***/ (function(module, exports) {

// shim for using process in browser
var process = module.exports = {};

// cached from whatever global is present so that test runners that stub it
// don't break things.  But we need to wrap it in a try catch in case it is
// wrapped in strict mode code which doesn't define any globals.  It's inside a
// function because try/catches deoptimize in certain engines.

var cachedSetTimeout;
var cachedClearTimeout;

function defaultSetTimout() {
    throw new Error('setTimeout has not been defined');
}
function defaultClearTimeout () {
    throw new Error('clearTimeout has not been defined');
}
(function () {
    try {
        if (typeof setTimeout === 'function') {
            cachedSetTimeout = setTimeout;
        } else {
            cachedSetTimeout = defaultSetTimout;
        }
    } catch (e) {
        cachedSetTimeout = defaultSetTimout;
    }
    try {
        if (typeof clearTimeout === 'function') {
            cachedClearTimeout = clearTimeout;
        } else {
            cachedClearTimeout = defaultClearTimeout;
        }
    } catch (e) {
        cachedClearTimeout = defaultClearTimeout;
    }
} ())
function runTimeout(fun) {
    if (cachedSetTimeout === setTimeout) {
        //normal enviroments in sane situations
        return setTimeout(fun, 0);
    }
    // if setTimeout wasn't available but was latter defined
    if ((cachedSetTimeout === defaultSetTimout || !cachedSetTimeout) && setTimeout) {
        cachedSetTimeout = setTimeout;
        return setTimeout(fun, 0);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedSetTimeout(fun, 0);
    } catch(e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't trust the global object when called normally
            return cachedSetTimeout.call(null, fun, 0);
        } catch(e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error
            return cachedSetTimeout.call(this, fun, 0);
        }
    }


}
function runClearTimeout(marker) {
    if (cachedClearTimeout === clearTimeout) {
        //normal enviroments in sane situations
        return clearTimeout(marker);
    }
    // if clearTimeout wasn't available but was latter defined
    if ((cachedClearTimeout === defaultClearTimeout || !cachedClearTimeout) && clearTimeout) {
        cachedClearTimeout = clearTimeout;
        return clearTimeout(marker);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedClearTimeout(marker);
    } catch (e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't  trust the global object when called normally
            return cachedClearTimeout.call(null, marker);
        } catch (e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error.
            // Some versions of I.E. have different rules for clearTimeout vs setTimeout
            return cachedClearTimeout.call(this, marker);
        }
    }



}
var queue = [];
var draining = false;
var currentQueue;
var queueIndex = -1;

function cleanUpNextTick() {
    if (!draining || !currentQueue) {
        return;
    }
    draining = false;
    if (currentQueue.length) {
        queue = currentQueue.concat(queue);
    } else {
        queueIndex = -1;
    }
    if (queue.length) {
        drainQueue();
    }
}

function drainQueue() {
    if (draining) {
        return;
    }
    var timeout = runTimeout(cleanUpNextTick);
    draining = true;

    var len = queue.length;
    while(len) {
        currentQueue = queue;
        queue = [];
        while (++queueIndex < len) {
            if (currentQueue) {
                currentQueue[queueIndex].run();
            }
        }
        queueIndex = -1;
        len = queue.length;
    }
    currentQueue = null;
    draining = false;
    runClearTimeout(timeout);
}

process.nextTick = function (fun) {
    var args = new Array(arguments.length - 1);
    if (arguments.length > 1) {
        for (var i = 1; i < arguments.length; i++) {
            args[i - 1] = arguments[i];
        }
    }
    queue.push(new Item(fun, args));
    if (queue.length === 1 && !draining) {
        runTimeout(drainQueue);
    }
};

// v8 likes predictible objects
function Item(fun, array) {
    this.fun = fun;
    this.array = array;
}
Item.prototype.run = function () {
    this.fun.apply(null, this.array);
};
process.title = 'browser';
process.browser = true;
process.env = {};
process.argv = [];
process.version = ''; // empty string to avoid regexp issues
process.versions = {};

function noop() {}

process.on = noop;
process.addListener = noop;
process.once = noop;
process.off = noop;
process.removeListener = noop;
process.removeAllListeners = noop;
process.emit = noop;
process.prependListener = noop;
process.prependOnceListener = noop;

process.listeners = function (name) { return [] }

process.binding = function (name) {
    throw new Error('process.binding is not supported');
};

process.cwd = function () { return '/' };
process.chdir = function (dir) {
    throw new Error('process.chdir is not supported');
};
process.umask = function() { return 0; };


/***/ }),
/* 3 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


module.exports = __webpack_require__(4);

/***/ }),
/* 4 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var lex = {
  InputElementDiv: '<WhiteSpace>|<LineTerminator>|<ReservedWord>|<Identifier>|<NumericLiteral>|<Punctuator>|<StringLiteral>',
  InputElementRegExp: '<WhiteSpace>|<LineTerminator>|<ReservedWord>|<Identifier>|<NumericLiteral>|<Punctuator>|<StringLiteral>',
  ReservedWord: '<Keyword>|<NullLiteral>|<BooleanLiteral>',
  WhiteSpace: /[\t\v\f\u0020\u00A0\u1680\u180E\u2000-\u200A\u202F\u205f\u3000\uFEFF]/,
  LineTerminator: /[\n\r\u2028\u2029]/,
  Keyword: /new(?![_$a-zA-Z0-9])|void(?![_$a-zA-Z0-9])|delete(?![_$a-zA-Z0-9])|in(?![_$a-zA-Z0-9])|instanceof(?![_$a-zA-Z0-9])|typeof(?![_$a-zA-Z0-9])/,
  NullLiteral: /null(?![_$a-zA-Z0-9])/,
  BooleanLiteral: /(?:true|false)(?![_$a-zA-Z0-9])/,
  Identifier: /[_$a-zA-Z][_$a-zA-Z0-9]*/,
  Punctuator: /\/|=>|\*\*|>>>=|>>=|<<=|===|!==|>>>|<<|%=|\*=|-=|\+=|<=|>=|==|!=|\^=|\|=|\|\||&&|&=|>>|\+\+|--|\:|}|\*|&|\||\^|!|~|-|\+|\?|%|=|>|<|,|;|\.(?![0-9])|\]|\[|\)|\(|{/,
  DivPunctuator: /\/=|\//,
  NumericLiteral: /(?:0[xX][0-9a-fA-F]*|\.[0-9]+|(?:[1-9]+[0-9]*|0)(?:\.[0-9]*|\.)?)(?:[eE][+-]{0,1}[0-9]+)?(?![_$a-zA-Z0-9])/,
  StringLiteral: /"(?:[^"\n\\\r\u2028\u2029]|\\(?:['"\\bfnrtv\n\r\u2028\u2029]|\r\n)|\\x[0-9a-fA-F]{2}|\\u[0-9a-fA-F]{4}|\\[^0-9ux'"\\bfnrtv\n\\\r\u2028\u2029])*"|'(?:[^'\n\\\r\u2028\u2029]|\\(?:['"\\bfnrtv\n\r\u2028\u2029]|\r\n)|\\x[0-9a-fA-F]{2}|\\u[0-9a-fA-F]{4}|\\[^0-9ux'"\\bfnrtv\n\\\r\u2028\u2029])*'/,
  RegularExpressionLiteral: /\/(?:\[(?:\\[\s\S]|[^\]])*\]|[^*\/\\\n\r\u2028\u2029]|\\[^\n\r\u2028\u2029])(?:\[(?:\\[\s\S]|[^\]])*\]|[^\/\\\n\r\u2028\u2029]|\\[^\n\r\u2028\u2029])*\/[0-9a-zA-Z]*/
};
function XRegExp(xregexps, rootname, flag) {
  var expnames = [rootname];

  function buildRegExp(source) {
    var regexp = new RegExp;
    regexp.compile(source.replace(/<([^>]+)>/g,
      function (all, expname) {
        if (!xregexps[expname])
          return '';
        expnames.push(expname);
        if (xregexps[expname] instanceof RegExp)
          return '(' + xregexps[expname].source + ')';
        return '(' + buildRegExp(xregexps[expname]).source + ')';
      }), flag);
    return regexp;
  }

  var regexp = buildRegExp(xregexps[rootname]);
  this.exec = function (string) {
    var matches = regexp.exec(string);
    if (matches == null)
      return null;
    var result = new String(matches[0]);
    for (var i = 0; i < expnames.length; i++)
      if (matches[i])
        result[expnames[i]] = matches[i];
    return result;
  };
  Object.defineProperty(this, 'lastIndex',
    {
      'get': function () {
        return regexp.lastIndex;
      },
      'set': function (v) {
        regexp.lastIndex = v;
      }
    });
}
function LexicalParser() {
  var inputElementDiv = new XRegExp(lex, 'InputElementDiv', 'g');
  var inputElementRegExp = new XRegExp(lex, 'InputElementRegExp', 'g');
  var source;
  Object.defineProperty(this, 'source', {
    'get': function () {
      return source;
    },
    'set': function (v) {
      source = v;
      inputElementDiv.lastIndex = 0;
      inputElementRegExp.lastIndex = 0;
    }
  });
  this.reset = function () {
    inputElementDiv.lastIndex = 0;
    inputElementRegExp.lastIndex = 0;
  };
  this.getNextToken = function (useDiv) {
    var lastIndex = inputElementDiv.lastIndex;
    var inputElement;
    if (useDiv)
      inputElement = inputElementDiv;
    else
      inputElement = inputElementRegExp;
    var token = inputElement.exec(source);
    if (token && inputElement.lastIndex - lastIndex > token.length) {
      throw new SyntaxError('Unexpected token ILLEGAL');
    }
    inputElementDiv.lastIndex = inputElement.lastIndex;
    inputElementRegExp.lastIndex = inputElement.lastIndex;
    return token;
  };
}
var rules = {
  'IdentifierName': [['Identifier']],
  'Literal': [['NullLiteral'], ['BooleanLiteral'], ['NumericLiteral'], ['StringLiteral'], ['RegularExpressionLiteral']],
  'PrimaryExpression': [['Identifier'], ['Literal'], ['(', 'Expression', ')']],
  'CallExpression': [['PrimaryExpression', 'Arguments'], ['CallExpression', 'Arguments']],
  'Arguments': [['(', ')'], ['(', 'ArgumentList', ')']],
  'ArgumentList': [['ConditionalExpression'], ['ArgumentList', ',', 'ConditionalExpression']],
  'LeftHandSideExpression': [['PrimaryExpression'], ['CallExpression']],
  'UnaryExpression': [['LeftHandSideExpression'], ['void', 'UnaryExpression'], ['+', 'UnaryExpression'], ['-', 'UnaryExpression'], ['~', 'UnaryExpression'], ['!', 'UnaryExpression']],
  'ExponentiationExpression': [['UnaryExpression'], ['ExponentiationExpression', '**', 'UnaryExpression']],
  'MultiplicativeExpression': [['MultiplicativeExpression', '/', 'ExponentiationExpression'], ['ExponentiationExpression'], ['MultiplicativeExpression', '*', 'ExponentiationExpression'], ['MultiplicativeExpression', '%', 'ExponentiationExpression']],
  'AdditiveExpression': [['MultiplicativeExpression'], ['AdditiveExpression', '+', 'MultiplicativeExpression'], ['AdditiveExpression', '-', 'MultiplicativeExpression']],
  'ShiftExpression': [['AdditiveExpression'], ['ShiftExpression', '<<', 'AdditiveExpression'], ['ShiftExpression', '>>', 'AdditiveExpression'], ['ShiftExpression', '>>>', 'AdditiveExpression']],
  'RelationalExpression': [['ShiftExpression'], ['RelationalExpression', '<', 'ShiftExpression'], ['RelationalExpression', '>', 'ShiftExpression'], ['RelationalExpression', '<=', 'ShiftExpression'], ['RelationalExpression', '>=', 'ShiftExpression'], ['RelationalExpression', 'instanceof', 'ShiftExpression'], ['RelationalExpression', 'in', 'ShiftExpression']],
  'EqualityExpression': [['RelationalExpression'], ['EqualityExpression', '==', 'RelationalExpression'], ['EqualityExpression', '!=', 'RelationalExpression'], ['EqualityExpression', '===', 'RelationalExpression'], ['EqualityExpression', '!==', 'RelationalExpression']],
  'BitwiseANDExpression': [['EqualityExpression'], ['BitwiseANDExpression', '&', 'EqualityExpression']],
  'BitwiseXORExpression': [['BitwiseANDExpression'], ['BitwiseXORExpression', '^', 'BitwiseANDExpression']],
  'BitwiseORExpression': [['BitwiseXORExpression'], ['BitwiseORExpression', '|', 'BitwiseXORExpression']],
  'LogicalANDExpression': [['BitwiseORExpression'], ['LogicalANDExpression', '&&', 'BitwiseORExpression']],
  'LogicalORExpression': [['LogicalANDExpression'], ['LogicalORExpression', '||', 'LogicalANDExpression']],
  'ConditionalExpression': [['LogicalORExpression'], ['LogicalORExpression', '?', 'LogicalORExpression', ':', 'LogicalORExpression']],
  'Expression': [['ConditionalExpression'], ['Expression', ',', 'ConditionalExpression']],
  'Program': [['Expression']]

};
function Symbol(symbolName, token) {
  this.name = symbolName;
  this.token = token;
  this.childNodes = [];
  this.toString = function (indent) {
    if (!indent)
      indent = '';
    if (this.childNodes.length == 1)
      return this.childNodes[0].toString(indent);
    var str = indent + this.name + (this.token != undefined && this.name != this.token ? ':' + this.token : '') + '\n';
    for (var i = 0; i < this.childNodes.length; i++)
      str += this.childNodes[i].toString(indent + '    ');
    return str;
  };
}
function SyntacticalParser() {
  var currentRule;
  var root = {
    Program: '$'
  };
  var hash = {};

  function closureNode(node) {

    hash[JSON.stringify(node)] = node;

    var queue = Object.getOwnPropertyNames(node);
    while (queue.length) {
      // console.log(queue);
      var symbolName = queue.shift();
      if (!rules[symbolName])
        continue;
      rules[symbolName].forEach(function (rule) {
        if (!node[rule[0]])
          queue.push(rule[0]);
        var rulenode = node;
        var lastnode = null;
        rule.forEach(function (symbol) {
          if (!rulenode[symbol])
            rulenode[symbol] = {};
          lastnode = rulenode;
          rulenode = rulenode[symbol];
        });
        if (node[symbolName].$div)
          rulenode.$div = true;
        rulenode.$reduce = symbolName;
        rulenode.$count = rule.length;
      });
    }

    for (var p in node) {
      if (typeof node[p] != 'object' || p.charAt(0) == '$' || node[p].$closure)
        continue;
      if (hash[JSON.stringify(node[p])])
        node[p] = hash[JSON.stringify(node[p])];
      else {
        closureNode(node[p]);
      }
    }
    node.$closure = true;
  }

  closureNode(root);
  var symbolStack = [];
  var statusStack = [root];
  var current = root;
  this.insertSymbol = function insertSymbol(symbol, haveLineTerminator) {
    // console.log(symbol);
    // console.log(symbolStack)
    while (!current[symbol.name] && current.$reduce) {
      var count = current.$count;
      var newsymbol = new Symbol(current.$reduce);
      while (count--)
        newsymbol.childNodes.push(symbolStack.pop()), statusStack.pop();
      current = statusStack[statusStack.length - 1];
      this.insertSymbol(newsymbol);
    }
    current = current[symbol.name];
    symbolStack.push(symbol), statusStack.push(current);
    if (!current)
      throw new Error();
    return current.$div;
  };
  this.reset = function () {
    current = root;
    symbolStack = [];
    statusStack = [root];
  };
  Object.defineProperty(this, 'grammarTree', {
    'get': function () {
      try {
        // console.log("get!");
        while (current.$reduce) {
          var count = current.$count;
          var newsymbol = new Symbol(current.$reduce);
          while (count--)
            newsymbol.childNodes.push(symbolStack.pop()), statusStack.pop();
          current = statusStack[statusStack.length - 1];
          this.insertSymbol(newsymbol);
        }
        if (symbolStack.length > 0 && current[';']) {
          this.insertSymbol(new Symbol(';', ';'));
          return this.grammarTree;
        }
        if (symbolStack.length != 1 || symbolStack[0].name != 'Program')
          throw new Error();
      } catch (e) {
        throw new SyntaxError('Unexpected end of input');
      }
      return symbolStack[0];
    }
  });
}
function Parser() {
  this.lexicalParser = new LexicalParser();
  this.syntacticalParser = new SyntacticalParser();
  var terminalSymbols = ['NullLiteral', 'BooleanLiteral', 'NumericLiteral', 'StringLiteral', 'RegularExpressionLiteral', 'Identifier', '**', '=>', '{', '}', '(', ')', '[', ']', '.', ';', ',', '<', '>', '<=', '>=', '==', '!=', '===', '!==', '+', '-', '*', '%', '++', '--', '<<', '>>', '>>>', '&', '|', '^', '!', '~', '&&', '||', '?', ':', '=', '+=', '-=', '*=', '%=', '<<=', '>>=', '>>>=', '&=', '|=', '^=', '/', '/=', 'instanceof', 'typeof', 'new', 'void', 'debugger', 'this', 'delete', 'in'];
  var terminalSymbolIndex = {};
  terminalSymbols.forEach(function (e) {
    Object.defineProperty(terminalSymbolIndex, e, {});
  });
  this.reset = function () {
    this.lexicalParser.reset();
    this.syntacticalParser.reset();
  };
  this.parse = function (source, onInputElement) {
    var token;
    var haveLineTerminator = false;
    this.lexicalParser.source = source;
    var useDiv = false;
    while (token = this.lexicalParser.getNextToken(useDiv)) {
      // console.log(token);
      if (onInputElement)
        onInputElement(token);
      try {
        if (Object.getOwnPropertyNames(token).some(
            (e) => {
              if (terminalSymbolIndex.hasOwnProperty(e)) {
                useDiv = this.syntacticalParser.insertSymbol(new Symbol(e, token), haveLineTerminator);
                haveLineTerminator = false;
                return true;
              } else
                return false;
            }))
          continue;
        if ((token.Keyword || token.Punctuator || token.DivPunctuator) && terminalSymbolIndex.hasOwnProperty(token.toString())) {
          useDiv = this.syntacticalParser.insertSymbol(new Symbol(token.toString(), token), haveLineTerminator);
        }
      } catch (e) {
        throw new SyntaxError('Unexpected token ' + token);
      }
    }
    return this.syntacticalParser.grammarTree;
  };
}

var parser = new Parser();

function JavaScriptExpression(text) {
  parser.reset();
  this.tree = (parser.parse(text));
  this.paths = [];
  var context = Object.create(null);
  var me = this;
  var pathIndex = Object.create(null);
  this.isSimple;
  this.isConst;
  walk(this.tree);
  checkSimple(this.tree);
  if (this.paths.length === 0) {
    this.isConst = true;
  }
  this.setter = function (path) {
    var curr = context;
    for (var i = 0; i < path.length - 1; i++) {
      if (!curr[path[i]])
        curr[path[i]] = Object.create(null);
      curr = curr[path[i]];
    }
    return {
      isCompleted: function () {
        for (var p in pathIndex)
          if (!pathIndex[p])
            return false;
        return true;
      },
      set: function (value) {
        if (!pathIndex[path.join('.')]) {
          pathIndex[path.join('.')] = true;
        }
        curr[path[i]] = (value);
        if (this.isCompleted()) {
          return me.exec();
        } else {
          return undefined;
        }
      }
    };
  };

  this.valueOf = this.exec = function () {
    try {
      return function () {
        return eval(text);
      }.call(context);
    } catch (e) {
    }
  };
  function checkSimple(symbol) {

    var curr = symbol;
    while (curr.childNodes.length <= 1 && curr.name !== 'MemberExpression') {
      // console.log(curr.name)
      curr = curr.childNodes[0];
    }
    // TODO: need to point out "[……]"
    if (curr.name === 'MemberExpression') {
      me.isSimple = true;
    } else {
      me.isSimple = false;
    }
  }

  function walk(symbol) {
    if (symbol.name === 'CallExpression' && symbol.childNodes[symbol.childNodes.length - 1].name !== 'CallExpression') {
      var path = getPath(symbol.childNodes[1]);
      walk(symbol.childNodes[0]);
    } else if (symbol.name === 'NewExpression' && symbol.childNodes.length === 1) {
      var path = getPath(symbol.childNodes[0]);
    } else if (symbol.name === 'MemberExpression' && symbol.childNodes.length === 1) {
      var path = getPath(symbol);
    } else {
      for (var i = 0; i < symbol.childNodes.length; i++)
        walk(symbol.childNodes[i]);
    }

  }


  function getPath(symbol) {
    // [["PrimaryExpression"], ["MemberExpression", "[", "Expression", "]"], ["MemberExpression", ".", "IdentifierName"], ["new", "MemberExpression", "Arguments"]],

    if (symbol.childNodes[0].name === 'IdentifierName') { // MemberExpression : MemberExpression "." IdentifierName
      var path = getPath(symbol.childNodes[2]);
      if (path)
        path = path.concat(symbol.childNodes[0].childNodes[0].token.toString());
      createPath(path);
      return path;

    } else if (symbol.childNodes[0].name === 'PrimaryExpression') { // MemberExpression : PrimaryExpression
      if (symbol.childNodes[0].childNodes[0].name === 'Identifier') {
        var path = [symbol.childNodes[0].childNodes[0].token.toString()];
        createPath(path);
        return path;
      } else {
        return null;
      }
    } else if (symbol.childNodes[0].name === ']') { // MemberExpression : MemberExpression "[" Expression "]"
      getPath(symbol.childNodes[3]);
      walk(symbol.childNodes[1]);
      return null;

    } else if (symbol.childNodes[0].name === 'Arguments') { // MemberExpression : "new" MemberExpression Arguments
      walk(symbol.childNodes[0]);
      walk(symbol.childNodes[1]);
      return null;
    } else {
      for (var i = 0; i < symbol.childNodes.length; i++)
        walk(symbol.childNodes[i]);
    }
  }


  function createPath(path) {
    var curr = context;
    for (var i = 0; i < path.length - 1; i++) {
      if (!curr[path[i]])
        curr[path[i]] = Object.create(null);
      curr = curr[path[i]];
    }
    me.paths.push(path);
    pathIndex[path.join('.')] = false;
  }
}

function visit(tree) {
  var childNodes = tree.childNodes.slice().reverse();
  var children = childNodes.filter(e =>
  !e.token || !e.token.Punctuator);

  if (tree.name === 'Arguments') {
    var argumentList = [];
    var listNode = children[0];
    while (listNode) {
      if (listNode.childNodes.length === 3) {
        argumentList.unshift(listNode.childNodes[0]);
        listNode = listNode.childNodes[2];
      }
      if (listNode.childNodes.length === 1) {
        argumentList.unshift(listNode.childNodes[0]);
        listNode = null;
      }
    }
    return {
      type: 'Arguments',
      children: argumentList.map(e => visit(e))
    };
  }


  if (children && children.length === 1)
    return visit(children[0]);

  if (tree.token && ['NullLiteral', 'BooleanLiteral', 'NumericLiteral', 'StringLiteral', 'Identifier'].some(e => tree.token[e])) {
    var type = Object.keys(tree.token).filter(e => e.match(/Literal/) || e.match(/Identifier/))[0];
    var value = {
      'NullLiteral': null,
      'BooleanLiteral': Boolean(tree.token),
      'NumericLiteral': Number(tree.token),
      'StringLiteral': tree.token,
      'Identifier': tree.token,
    }[type];

    return {
      type: type,
      value: value
    };
  }

  if (tree.name === 'CallExpression')
    return {
      type: 'CallExpression',
      children: [visit(childNodes[0]), visit(childNodes[1])]
    };

  return {
    type: childNodes.filter(e => e.token && e.token.Punctuator)[0].name,
    children: childNodes.filter(e => !e.token || !e.token.Punctuator).map(e => visit(e))
  };
}

function parse(originExp) {
  let exp = new JavaScriptExpression(originExp);
  return JSON.stringify(visit(exp.tree), null);
}

module.exports = {
  parse: parse
};

/***/ }),
/* 5 */
/***/ (function(module, exports, __webpack_require__) {

(function webpackUniversalModuleDefinition(root, factory) {
	if(true)
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else {
		var a = factory();
		for(var i in a) (typeof exports === 'object' ? exports : root)[i] = a[i];
	}
})(typeof self !== 'undefined' ? self : this, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 10);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/*
object-assign
(c) Sindre Sorhus
@license MIT
*/


/* eslint-disable no-unused-vars */
var getOwnPropertySymbols = Object.getOwnPropertySymbols;
var hasOwnProperty = Object.prototype.hasOwnProperty;
var propIsEnumerable = Object.prototype.propertyIsEnumerable;

function toObject(val) {
	if (val === null || val === undefined) {
		throw new TypeError('Object.assign cannot be called with null or undefined');
	}

	return Object(val);
}

function shouldUseNative() {
	try {
		if (!Object.assign) {
			return false;
		}

		// Detect buggy property enumeration order in older V8 versions.

		// https://bugs.chromium.org/p/v8/issues/detail?id=4118
		var test1 = new String('abc');  // eslint-disable-line no-new-wrappers
		test1[5] = 'de';
		if (Object.getOwnPropertyNames(test1)[0] === '5') {
			return false;
		}

		// https://bugs.chromium.org/p/v8/issues/detail?id=3056
		var test2 = {};
		for (var i = 0; i < 10; i++) {
			test2['_' + String.fromCharCode(i)] = i;
		}
		var order2 = Object.getOwnPropertyNames(test2).map(function (n) {
			return test2[n];
		});
		if (order2.join('') !== '0123456789') {
			return false;
		}

		// https://bugs.chromium.org/p/v8/issues/detail?id=3056
		var test3 = {};
		'abcdefghijklmnopqrst'.split('').forEach(function (letter) {
			test3[letter] = letter;
		});
		if (Object.keys(Object.assign({}, test3)).join('') !==
				'abcdefghijklmnopqrst') {
			return false;
		}

		return true;
	} catch (err) {
		// We don't expect any of the above to throw, but better to be safe.
		return false;
	}
}

module.exports = shouldUseNative() ? Object.assign : function (target, source) {
	var from;
	var to = toObject(target);
	var symbols;

	for (var s = 1; s < arguments.length; s++) {
		from = Object(arguments[s]);

		for (var key in from) {
			if (hasOwnProperty.call(from, key)) {
				to[key] = from[key];
			}
		}

		if (getOwnPropertySymbols) {
			symbols = getOwnPropertySymbols(from);
			for (var i = 0; i < symbols.length; i++) {
				if (propIsEnumerable.call(from, symbols[i])) {
					to[symbols[i]] = from[symbols[i]];
				}
			}
		}
	}

	return to;
};


/***/ }),
/* 1 */
/***/ (function(module, exports, __webpack_require__) {

(function webpackUniversalModuleDefinition(root, factory) {
	if(true)
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else {
		var a = factory();
		for(var i in a) (typeof exports === 'object' ? exports : root)[i] = a[i];
	}
})(typeof self !== 'undefined' ? self : this, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


function findIndex(o, condition) {
  return o.indexOf(find(o, condition));
}

function dropWhile(o, condition) {
  var result = [];
  map(o, function (v, k) {
    if (!condition(v, k)) {
      result.push(v);
    }
  });
  return result;
}

function filter(o, condition) {
  var result = [];
  forEach(o, function (v, k) {
    if (condition(v, k)) {
      result.push(v);
    }
  });
  return result;
}

function map(o, fn) {
  if (o instanceof Array) {
    return Array.prototype.map.call(o, fn);
  } else {
    var result = [];
    forEach(o, function (v, k) {
      result.push(fn(v, k));
    });
    return result;
  }
}

/*
 forEach({a: 1, b: 2}, (v, k) => {
 console.log({
 v, k
 })
 })

 forEach([1,2,3],(v,k)=>{
 console.log({
 v,k
 })
 })
 */

function forEach(o, fn) {
  if (o instanceof Array) {
    return Array.prototype.forEach.call(o, fn);
  }
  Object.keys(o).forEach(function (key) {
    fn(o[key], key);
  });
}

/* console.log(
 find([{name: 1}, {name: 2}], (o) => {
 return o.name === 2;
 }))

 console.log(find([{name: 1,age:2}, {name: 2}], {name:1}))
 */
function find(o, condition) {
  var result = null;
  forEach(o, function (v, k) {
    if (typeof condition === 'function') {
      if (condition(v, k)) {
        result = v;
      }
    } else {
      var propName = Object.keys(condition)[0];
      if (propName && v[propName] === condition[propName]) {
        result = v;
      }
    }
  });
  return result;
}

module.exports = {
  find: find,
  forEach: forEach,
  map: map,
  filter: filter,
  dropWhile: dropWhile,
  findIndex: findIndex
};

/***/ })
/******/ ]);
});

/***/ }),
/* 2 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _slicedToArray = function () { function sliceIterator(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"]) _i["return"](); } finally { if (_d) throw _e; } } return _arr; } return function (arr, i) { if (Array.isArray(arr)) { return arr; } else if (Symbol.iterator in Object(arr)) { return sliceIterator(arr, i); } else { throw new TypeError("Invalid attempt to destructure non-iterable instance"); } }; }();

/**
 * Transforms matrix into an object
 *
 * @param string matrix
 * @return object
 */

// TODO matrix4 3D场景下待实现  目前仅仅实现matrix  2D
var matrixToTransformObj = function matrixToTransformObj(matrix) {
  // this happens when there was no rotation yet in CSS
  if (matrix === 'none') {
    matrix = 'matrix(0,0,0,0,0)';
  }
  var obj = {},
      values = matrix.match(/([-+]?[\d\.]+)/g);

  var _values = _slicedToArray(values, 6),
      a = _values[0],
      b = _values[1],
      c = _values[2],
      d = _values[3],
      e = _values[4],
      f = _values[5];

  obj.rotate = obj.rotateZ = Math.round(Math.atan2(parseFloat(b), parseFloat(a)) * (180 / Math.PI)) || 0;
  obj.translateX = e !== undefined ? pxTo750(e) : 0;
  obj.translateY = f !== undefined ? pxTo750(f) : 0;
  obj.scaleX = Math.sqrt(a * a + b * b);
  obj.scaleY = Math.sqrt(c * c + d * d);
  return obj;
};

function pxTo750(n) {
  return Math.round(n / document.body.clientWidth * 750);
}

function px(n) {
  return n / 750 * document.body.clientWidth;
  // return Math.round(n / 750 * document.body.clientWidth);
}

function clamp(n, min, max) {
  return n < min ? min : n > max ? max : n;
}

var vendor = function () {
  var el = document.createElement('div').style;
  var vendors = ['t', 'webkitT', 'MozT', 'msT', 'OT'],
      transform,
      i = 0,
      l = vendors.length;
  for (; i < l; i++) {
    transform = vendors[i] + 'ransform';
    if (transform in el) return vendors[i].substr(0, vendors[i].length - 1);
  }
  return false;
}();

/**
 *  add vendor to attribute
 *  @memberOf Util
 *  @param {String} attrName name of attribute
 *  @return { String }
 **/
function prefixStyle(attrName) {
  if (vendor === false) return false;
  if (vendor === '') return attrName;
  return vendor + attrName.charAt(0).toUpperCase() + attrName.substr(1);
}

exports.matrixToTransformObj = matrixToTransformObj;
exports.pxTo750 = pxTo750;
exports.px = px;
exports.clamp = clamp;
exports.prefixStyle = prefixStyle;

/***/ }),
/* 3 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _simpleLodash = __webpack_require__(1);

var _simpleLodash2 = _interopRequireDefault(_simpleLodash);

var _utils = __webpack_require__(2);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var TimingHandler = function () {
  function TimingHandler(binding) {
    _classCallCheck(this, TimingHandler);

    this.binding = null;

    this.binding = binding;
    var props = binding.options.props;


    _simpleLodash2.default.map(props, function (prop) {
      var element = prop.element,
          config = prop.config;

      if (config && element) {
        if (config.perspective) {
          if (element.parentNode) {
            element.parentNode.style[(0, _utils.prefixStyle)('transformStyle')] = 'preserve-3d';
            element.parentNode.style[(0, _utils.prefixStyle)('perspective')] = config.perspective + 'px';
            element.parentNode.style[(0, _utils.prefixStyle)('perspectiveOrigin')] = '0 0';
          }
        }
        if (config.transformOrigin) {
          element.style[(0, _utils.prefixStyle)('transformOrigin')] = config.transformOrigin;
        }
      }
    });
  }

  _createClass(TimingHandler, [{
    key: 'destroy',
    value: function destroy() {}
  }]);

  return TimingHandler;
}();

exports.default = TimingHandler;
;

/***/ }),
/* 4 */
/***/ (function(module, exports, __webpack_require__) {

(function webpackUniversalModuleDefinition(root, factory) {
	if(true)
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else {
		var a = factory();
		for(var i in a) (typeof exports === 'object' ? exports : root)[i] = a[i];
	}
})(this, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 3);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var PI = Math.PI,
    sin = Math.sin,
    cos = Math.cos,
    sqrt = Math.sqrt,
    pow = Math.pow;

var c1 = 1.70158;
var c2 = c1 * 1.525;
var c3 = c1 + 1;
var c4 = 2 * PI / 3;
var c5 = 2 * PI / 4.5;

// x is the fraction of animation progress, in the range 0..1
function bounceOut(x) {
  var n1 = 7.5625,
      d1 = 2.75;
  if (x < 1 / d1) {
    return n1 * x * x;
  } else if (x < 2 / d1) {
    return n1 * (x -= 1.5 / d1) * x + .75;
  } else if (x < 2.5 / d1) {
    return n1 * (x -= 2.25 / d1) * x + .9375;
  } else {
    return n1 * (x -= 2.625 / d1) * x + .984375;
  }
}

var Easing = {
  linear: function linear(x) {
    return x;
  },
  easeInQuad: function easeInQuad(x) {
    return x * x;
  },
  easeOutQuad: function easeOutQuad(x) {
    return 1 - (1 - x) * (1 - x);
  },
  easeInOutQuad: function easeInOutQuad(x) {
    return x < 0.5 ? 2 * x * x : 1 - pow(-2 * x + 2, 2) / 2;
  },
  easeInCubic: function easeInCubic(x) {
    return x * x * x;
  },
  easeOutCubic: function easeOutCubic(x) {
    return 1 - pow(1 - x, 3);
  },
  easeInOutCubic: function easeInOutCubic(x) {
    return x < 0.5 ? 4 * x * x * x : 1 - pow(-2 * x + 2, 3) / 2;
  },
  easeInQuart: function easeInQuart(x) {
    return x * x * x * x;
  },
  easeOutQuart: function easeOutQuart(x) {
    return 1 - pow(1 - x, 4);
  },
  easeInOutQuart: function easeInOutQuart(x) {
    return x < 0.5 ? 8 * x * x * x * x : 1 - pow(-2 * x + 2, 4) / 2;
  },
  easeInQuint: function easeInQuint(x) {
    return x * x * x * x * x;
  },
  easeOutQuint: function easeOutQuint(x) {
    return 1 - pow(1 - x, 5);
  },
  easeInOutQuint: function easeInOutQuint(x) {
    return x < 0.5 ? 16 * x * x * x * x * x : 1 - pow(-2 * x + 2, 5) / 2;
  },
  easeInSine: function easeInSine(x) {
    return 1 - cos(x * PI / 2);
  },
  easeOutSine: function easeOutSine(x) {
    return sin(x * PI / 2);
  },
  easeInOutSine: function easeInOutSine(x) {
    return -(cos(PI * x) - 1) / 2;
  },
  easeInExpo: function easeInExpo(x) {
    return x === 0 ? 0 : pow(2, 10 * x - 10);
  },
  easeOutExpo: function easeOutExpo(x) {
    return x === 1 ? 1 : 1 - pow(2, -10 * x);
  },
  easeInOutExpo: function easeInOutExpo(x) {
    return x === 0 ? 0 : x === 1 ? 1 : x < 0.5 ? pow(2, 20 * x - 10) / 2 : (2 - pow(2, -20 * x + 10)) / 2;
  },
  easeInCirc: function easeInCirc(x) {
    return 1 - sqrt(1 - pow(x, 2));
  },
  easeOutCirc: function easeOutCirc(x) {
    return sqrt(1 - pow(x - 1, 2));
  },
  easeInOutCirc: function easeInOutCirc(x) {
    return x < 0.5 ? (1 - sqrt(1 - pow(2 * x, 2))) / 2 : (sqrt(1 - pow(-2 * x + 2, 2)) + 1) / 2;
  },
  easeInElastic: function easeInElastic(x) {
    return x === 0 ? 0 : x === 1 ? 1 : -pow(2, 10 * x - 10) * sin((x * 10 - 10.75) * c4);
  },
  easeOutElastic: function easeOutElastic(x) {
    return x === 0 ? 0 : x === 1 ? 1 : pow(2, -10 * x) * sin((x * 10 - 0.75) * c4) + 1;
  },
  easeInOutElastic: function easeInOutElastic(x) {
    return x === 0 ? 0 : x === 1 ? 1 : x < 0.5 ? -(pow(2, 20 * x - 10) * sin((20 * x - 11.125) * c5)) / 2 : pow(2, -20 * x + 10) * sin((20 * x - 11.125) * c5) / 2 + 1;
  },
  easeInBack: function easeInBack(x) {
    return c3 * x * x * x - c1 * x * x;
  },
  easeOutBack: function easeOutBack(x) {
    return 1 + c3 * pow(x - 1, 3) + c1 * pow(x - 1, 2);
  },
  easeInOutBack: function easeInOutBack(x) {
    return x < 0.5 ? pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2) / 2 : (pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
  },
  easeInBounce: function easeInBounce(x) {
    return 1 - bounceOut(1 - x);
  },
  easeOutBounce: bounceOut,
  easeInOutBounce: function easeInOutBounce(x) {
    return x < 0.5 ? (1 - bounceOut(1 - 2 * x)) / 2 : (1 + bounceOut(2 * x - 1)) / 2;
  },
  cubicBezier: function cubicBezier() {}
};

module.exports = Easing;

/***/ }),
/* 1 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


function Bezier(x1, y1, x2, y2, epsilon) {
  var curveX = function curveX(t) {
    var v = 1 - t;
    return 3 * v * v * t * x1 + 3 * v * t * t * x2 + t * t * t;
  };

  var curveY = function curveY(t) {
    var v = 1 - t;
    return 3 * v * v * t * y1 + 3 * v * t * t * y2 + t * t * t;
  };

  var derivativeCurveX = function derivativeCurveX(t) {
    var v = 1 - t;
    return 3 * (2 * (t - 1) * t + v * v) * x1 + 3 * (-t * t * t + 2 * v * t) * x2;
  };

  return function (t) {

    var x = t,
        t0,
        t1,
        t2,
        x2,
        d2,
        i;

    // First try a few iterations of Newton's method -- normally very fast.
    for (t2 = x, i = 0; i < 8; i++) {
      x2 = curveX(t2) - x;
      if (Math.abs(x2) < epsilon) return curveY(t2);
      d2 = derivativeCurveX(t2);
      if (Math.abs(d2) < 1e-6) break;
      t2 = t2 - x2 / d2;
    }

    t0 = 0, t1 = 1, t2 = x;

    if (t2 < t0) return curveY(t0);
    if (t2 > t1) return curveY(t1);

    // Fallback to the bisection method for reliability.
    while (t0 < t1) {
      x2 = curveX(t2);
      if (Math.abs(x2 - x) < epsilon) return curveY(t2);
      if (x > x2) t0 = t2;else t1 = t2;
      t2 = (t1 - t0) * .5 + t0;
    }

    // Failure
    return curveY(t2);
  };
};

module.exports = Bezier;

/***/ }),
/* 2 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var raf = window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame || function (callback) {
  window.setTimeout(callback, 1000 / 60);
};

var cancelRAF = window.cancelAnimationFrame || window.webkitCancelAnimationFrame || window.mozCancelAnimationFrame || window.oCancelAnimationFrame || window.msCancelAnimationFrame || window.clearTimeout;

module.exports = {
  raf: raf,
  cancelRAF: cancelRAF
};

/***/ }),
/* 3 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


module.exports = __webpack_require__(4);

/***/ }),
/* 4 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var easing = __webpack_require__(0);
var bezier = __webpack_require__(1);

var _require = __webpack_require__(2),
    raf = _require.raf,
    cancelRAF = _require.cancelRAF;

var assign = __webpack_require__(5);

var TYPES = {
  START: 'start',
  END: 'end',
  RUN: 'run',
  STOP: 'stop'
};

var noop = function noop() {};

var MIN_DURATION = 1;

function Timer(cfg) {
  this.init(cfg);
}

Timer.prototype = {
  init: function init(cfg) {
    this.cfg = assign({
      easing: 'linear',
      duration: Infinity,
      onStart: noop,
      onRun: noop,
      onStop: noop,
      onEnd: noop
    }, cfg);
  },
  run: function run() {
    var _cfg = this.cfg,
        duration = _cfg.duration,
        onStart = _cfg.onStart,
        onRun = _cfg.onRun;

    if (duration <= MIN_DURATION) {
      this.isfinished = true;
      typeof onRun === 'function' ? onRun({ percent: 1 }) : null;
      this.stop();
    }
    if (this.isfinished) return;
    this._hasFinishedPercent = this._stop && this._stop.percent || 0;
    this._stop = null;
    this.start = Date.now();
    this.percent = 0;
    typeof onStart === 'function' ? onStart({ percent: 0, type: TYPES.START }) : null;
    // epsilon determines the precision of the solved values
    var epsilon = 1000 / 60 / duration / 4;
    var b = this.cfg.bezierArgs;
    this.easingFn = b && b.length === 4 ? bezier(b[0], b[1], b[2], b[3], epsilon) : easing[this.cfg.easing];
    this._run();
  },

  _run: function _run() {
    var _this = this;

    var _cfg2 = this.cfg,
        onRun = _cfg2.onRun,
        onStop = _cfg2.onStop;

    cancelRAF(this._raf);
    this._raf = raf(function () {
      _this.now = Date.now();
      _this.t = _this.now - _this.start;
      _this.duration = _this.now - _this.start >= _this.cfg.duration ? _this.cfg.duration : _this.now - _this.start;
      _this.progress = _this.easingFn(_this.duration / _this.cfg.duration);
      _this.percent = _this.duration / _this.cfg.duration + _this._hasFinishedPercent;
      if (_this.percent >= 1 || _this._stop) {
        _this.percent = _this._stop && _this._stop.percent ? _this._stop.percent : 1;
        _this.duration = _this._stop && _this._stop.duration ? _this._stop.duration : _this.duration;

        typeof onRun === 'function' ? onRun({
          percent: _this.progress,
          originPercent: _this.percent,
          t: _this.t,
          type: TYPES.RUN
        }) : null;

        typeof onStop === 'function' ? onStop({
          percent: _this.percent,
          t: _this.t,
          type: TYPES.STOP
        }) : null;

        if (_this.percent >= 1) {
          _this.isfinished = true;
          _this.stop();
        }
        return;
      }

      typeof onRun === 'function' ? onRun({
        percent: _this.progress,
        originPercent: _this.percent,
        t: _this.t,
        type: TYPES.RUN
      }) : null;

      _this._run();
    });
  },

  stop: function stop() {
    var onEnd = this.cfg.onEnd;

    this._stop = {
      percent: this.percent,
      now: this.now
    };
    typeof onEnd === 'function' ? onEnd({
      percent: 1,
      t: this.t,
      type: TYPES.END
    }) : null;
    cancelRAF(this._raf);
  }
};

Timer.Easing = easing;
Timer.Bezier = bezier;
Timer.raf = raf;
Timer.cancelRAF = cancelRAF;
module.exports = Timer;

/***/ }),
/* 5 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/*
object-assign
(c) Sindre Sorhus
@license MIT
*/


/* eslint-disable no-unused-vars */
var getOwnPropertySymbols = Object.getOwnPropertySymbols;
var hasOwnProperty = Object.prototype.hasOwnProperty;
var propIsEnumerable = Object.prototype.propertyIsEnumerable;

function toObject(val) {
	if (val === null || val === undefined) {
		throw new TypeError('Object.assign cannot be called with null or undefined');
	}

	return Object(val);
}

function shouldUseNative() {
	try {
		if (!Object.assign) {
			return false;
		}

		// Detect buggy property enumeration order in older V8 versions.

		// https://bugs.chromium.org/p/v8/issues/detail?id=4118
		var test1 = new String('abc');  // eslint-disable-line no-new-wrappers
		test1[5] = 'de';
		if (Object.getOwnPropertyNames(test1)[0] === '5') {
			return false;
		}

		// https://bugs.chromium.org/p/v8/issues/detail?id=3056
		var test2 = {};
		for (var i = 0; i < 10; i++) {
			test2['_' + String.fromCharCode(i)] = i;
		}
		var order2 = Object.getOwnPropertyNames(test2).map(function (n) {
			return test2[n];
		});
		if (order2.join('') !== '0123456789') {
			return false;
		}

		// https://bugs.chromium.org/p/v8/issues/detail?id=3056
		var test3 = {};
		'abcdefghijklmnopqrst'.split('').forEach(function (letter) {
			test3[letter] = letter;
		});
		if (Object.keys(Object.assign({}, test3)).join('') !==
				'abcdefghijklmnopqrst') {
			return false;
		}

		return true;
	} catch (err) {
		// We don't expect any of the above to throw, but better to be safe.
		return false;
	}
}

module.exports = shouldUseNative() ? Object.assign : function (target, source) {
	var from;
	var to = toObject(target);
	var symbols;

	for (var s = 1; s < arguments.length; s++) {
		from = Object(arguments[s]);

		for (var key in from) {
			if (hasOwnProperty.call(from, key)) {
				to[key] = from[key];
			}
		}

		if (getOwnPropertySymbols) {
			symbols = getOwnPropertySymbols(from);
			for (var i = 0; i < symbols.length; i++) {
				if (propIsEnumerable.call(from, symbols[i])) {
					to[symbols[i]] = from[symbols[i]];
				}
			}
		}
	}

	return to;
};


/***/ })
/******/ ]);
});

/***/ }),
/* 5 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
function toNumber(value) {
  return Number(value);
}

function toBoolean(value) {
  return !!value;
}

function equal(v1, v2) {
  return v1 == v2;
}

function strictlyEqual(v1, v2) {
  return v1 === v2;
}

function execute(node, scope) {

  var type = node.type;
  var children = node.children;
  switch (type) {
    case 'StringLiteral':
      return String(node.value);
    case 'NumericLiteral':
      return parseFloat(node.value);
    case 'BooleanLiteral':
      return !!node.value;
    case 'Identifier':
      return scope[node.value];
    case 'CallExpression':
      var fn = execute(children[0], scope);
      // console.log('fn:',fn)
      var args = [];
      var jsonArguments = children[1].children;
      for (var i = 0; i < jsonArguments.length; i++) {
        args.push(execute(jsonArguments[i], scope));
      }
      return fn.apply(null, args);
    case '?':
      if (execute(children[0], scope)) {
        return execute(children[1], scope);
      }
      return execute(children[2], scope);
    case '+':
      return toNumber(execute(children[0], scope)) + toNumber(execute(children[1], scope));
    case '-':
      return toNumber(execute(children[0], scope)) - toNumber(execute(children[1], scope));
    case '*':
      return toNumber(execute(children[0], scope)) * toNumber(execute(children[1], scope));
    case '/':
      return toNumber(execute(children[0], scope)) / toNumber(execute(children[1], scope));
    case '%':
      return toNumber(execute(children[0], scope)) % toNumber(execute(children[1], scope));
    case '**':
      return Math.pow(toNumber(execute(children[0], scope)), toNumber(execute(children[1], scope)));

    case '>':
      return toNumber(execute(children[0], scope)) > toNumber(execute(children[1], scope));
    case '<':
      return toNumber(execute(children[0], scope)) < toNumber(execute(children[1], scope));
    case '>=':
      return toNumber(execute(children[0], scope)) >= toNumber(execute(children[1], scope));
    case '<=':
      return toNumber(execute(children[0], scope)) <= toNumber(execute(children[1], scope));

    case '==':
      return equal(execute(children[0], scope), execute(children[1], scope));
    case '===':
      return strictlyEqual(execute(children[0], scope), execute(children[1], scope));
    case '!=':
      return !equal(execute(children[0], scope), execute(children[1], scope));
    case '!==':
      return !strictlyEqual(execute(children[0], scope), execute(children[1], scope));

    case '&&':
      var result = void 0;
      result = execute(children[0], scope);
      if (!toBoolean(result)) return result;
      return execute(children[1], scope);
    case '||':
      result = execute(children[0], scope);
      if (toBoolean(result)) return result;
      return execute(children[1], scope);
    case '!':
      return !toBoolean(execute(children[0], scope));

  }
  return null;
}

exports.default = {
  execute: execute
};

/***/ }),
/* 6 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _quaternion = __webpack_require__(7);

var _quaternion2 = _interopRequireDefault(_quaternion);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function Vector3(x, y, z) {

  this.x = x || 0;
  this.y = y || 0;
  this.z = z || 0;
}

Vector3.prototype = {

  constructor: Vector3,

  isVector3: true,

  set: function set(x, y, z) {

    this.x = x;
    this.y = y;
    this.z = z;

    return this;
  },

  applyEuler: function () {

    var quaternion;

    return function applyEuler(euler) {

      if ((euler && euler.isEuler) === false) {

        console.error('THREE.Vector3: .applyEuler() now expects an Euler rotation rather than a Vector3 and order.');
      }

      if (quaternion === undefined) quaternion = new _quaternion2.default();

      return this.applyQuaternion(quaternion.setFromEuler(euler));
    };
  }(),

  applyQuaternion: function applyQuaternion(q) {

    var x = this.x,
        y = this.y,
        z = this.z;
    var qx = q.x,
        qy = q.y,
        qz = q.z,
        qw = q.w;

    // calculate quat * vector

    var ix = qw * x + qy * z - qz * y;
    var iy = qw * y + qz * x - qx * z;
    var iz = qw * z + qx * y - qy * x;
    var iw = -qx * x - qy * y - qz * z;

    // calculate result * inverse quat

    this.x = ix * qw + iw * -qx + iy * -qz - iz * -qy;
    this.y = iy * qw + iw * -qy + iz * -qx - ix * -qz;
    this.z = iz * qw + iw * -qz + ix * -qy - iy * -qx;

    return this;
  }

};

exports.default = Vector3;

/***/ }),
/* 7 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _objectAssign = __webpack_require__(0);

var _objectAssign2 = _interopRequireDefault(_objectAssign);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function Quaternion(x, y, z, w) {

  this._x = x || 0;
  this._y = y || 0;
  this._z = z || 0;
  this._w = w !== undefined ? w : 1;
}

Quaternion.prototype = {

  constructor: Quaternion,

  get x() {

    return this._x;
  },

  set x(value) {

    this._x = value;
    this.onChangeCallback();
  },

  get y() {

    return this._y;
  },

  set y(value) {

    this._y = value;
    this.onChangeCallback();
  },

  get z() {

    return this._z;
  },

  set z(value) {

    this._z = value;
    this.onChangeCallback();
  },

  get w() {

    return this._w;
  },

  set w(value) {

    this._w = value;
    this.onChangeCallback();
  },

  set: function set(x, y, z, w) {

    this._x = x;
    this._y = y;
    this._z = z;
    this._w = w;

    this.onChangeCallback();

    return this;
  },

  clone: function clone() {

    return new this.constructor(this._x, this._y, this._z, this._w);
  },

  copy: function copy(quaternion) {

    this._x = quaternion.x;
    this._y = quaternion.y;
    this._z = quaternion.z;
    this._w = quaternion.w;

    this.onChangeCallback();

    return this;
  },

  setFromEuler: function setFromEuler(euler, update) {

    if ((euler && euler.isEuler) === false) {

      throw new Error('THREE.Quaternion: .setFromEuler() now expects an Euler rotation rather than a Vector3 and order.');
    }

    // http://www.mathworks.com/matlabcentral/fileexchange/
    //  20696-function-to-convert-between-dcm-euler-angles-quaternions-and-euler-vectors/
    //  content/SpinCalc.m

    var c1 = Math.cos(euler._x / 2);
    var c2 = Math.cos(euler._y / 2);
    var c3 = Math.cos(euler._z / 2);
    var s1 = Math.sin(euler._x / 2);
    var s2 = Math.sin(euler._y / 2);
    var s3 = Math.sin(euler._z / 2);

    var order = euler.order;

    if (order === 'XYZ') {

      this._x = s1 * c2 * c3 + c1 * s2 * s3;
      this._y = c1 * s2 * c3 - s1 * c2 * s3;
      this._z = c1 * c2 * s3 + s1 * s2 * c3;
      this._w = c1 * c2 * c3 - s1 * s2 * s3;
    } else if (order === 'YXZ') {

      this._x = s1 * c2 * c3 + c1 * s2 * s3;
      this._y = c1 * s2 * c3 - s1 * c2 * s3;
      this._z = c1 * c2 * s3 - s1 * s2 * c3;
      this._w = c1 * c2 * c3 + s1 * s2 * s3;
    } else if (order === 'ZXY') {

      this._x = s1 * c2 * c3 - c1 * s2 * s3;
      this._y = c1 * s2 * c3 + s1 * c2 * s3;
      this._z = c1 * c2 * s3 + s1 * s2 * c3;
      this._w = c1 * c2 * c3 - s1 * s2 * s3;
    } else if (order === 'ZYX') {

      this._x = s1 * c2 * c3 - c1 * s2 * s3;
      this._y = c1 * s2 * c3 + s1 * c2 * s3;
      this._z = c1 * c2 * s3 - s1 * s2 * c3;
      this._w = c1 * c2 * c3 + s1 * s2 * s3;
    } else if (order === 'YZX') {

      this._x = s1 * c2 * c3 + c1 * s2 * s3;
      this._y = c1 * s2 * c3 + s1 * c2 * s3;
      this._z = c1 * c2 * s3 - s1 * s2 * c3;
      this._w = c1 * c2 * c3 - s1 * s2 * s3;
    } else if (order === 'XZY') {

      this._x = s1 * c2 * c3 - c1 * s2 * s3;
      this._y = c1 * s2 * c3 - s1 * c2 * s3;
      this._z = c1 * c2 * s3 + s1 * s2 * c3;
      this._w = c1 * c2 * c3 + s1 * s2 * s3;
    }

    if (update !== false) this.onChangeCallback();

    return this;
  },

  setFromAxisAngle: function setFromAxisAngle(axis, angle) {

    // http://www.euclideanspace.com/maths/geometry/rotations/conversions/angleToQuaternion/index.htm

    // assumes axis is normalized

    var halfAngle = angle / 2,
        s = Math.sin(halfAngle);

    this._x = axis.x * s;
    this._y = axis.y * s;
    this._z = axis.z * s;
    this._w = Math.cos(halfAngle);

    this.onChangeCallback();

    return this;
  },

  // normalize: function() {
  //
  //   var l = this.length();
  //
  //   if (l === 0) {
  //
  //     this._x = 0;
  //     this._y = 0;
  //     this._z = 0;
  //     this._w = 1;
  //
  //   } else {
  //
  //     l = 1 / l;
  //
  //     this._x = this._x * l;
  //     this._y = this._y * l;
  //     this._z = this._z * l;
  //     this._w = this._w * l;
  //
  //   }
  //
  //   this.onChangeCallback();
  //
  //   return this;
  //
  // },

  multiply: function multiply(q, p) {

    if (p !== undefined) {

      console.warn('THREE.Quaternion: .multiply() now only accepts one argument. Use .multiplyQuaternions( a, b ) instead.');
      return this.multiplyQuaternions(q, p);
    }

    return this.multiplyQuaternions(this, q);
  },

  multiplyQuaternions: function multiplyQuaternions(a, b) {

    // from http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/code/index.htm

    var qax = a._x,
        qay = a._y,
        qaz = a._z,
        qaw = a._w;
    var qbx = b._x,
        qby = b._y,
        qbz = b._z,
        qbw = b._w;

    this._x = qax * qbw + qaw * qbx + qay * qbz - qaz * qby;
    this._y = qay * qbw + qaw * qby + qaz * qbx - qax * qbz;
    this._z = qaz * qbw + qaw * qbz + qax * qby - qay * qbx;
    this._w = qaw * qbw - qax * qbx - qay * qby - qaz * qbz;

    this.onChangeCallback();

    return this;
  },

  slerp: function slerp(qb, t) {

    if (t === 0) return this;
    if (t === 1) return this.copy(qb);

    var x = this._x,
        y = this._y,
        z = this._z,
        w = this._w;

    // http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/slerp/

    var cosHalfTheta = w * qb._w + x * qb._x + y * qb._y + z * qb._z;

    if (cosHalfTheta < 0) {

      this._w = -qb._w;
      this._x = -qb._x;
      this._y = -qb._y;
      this._z = -qb._z;

      cosHalfTheta = -cosHalfTheta;
    } else {

      this.copy(qb);
    }

    if (cosHalfTheta >= 1.0) {

      this._w = w;
      this._x = x;
      this._y = y;
      this._z = z;

      return this;
    }

    var sinHalfTheta = Math.sqrt(1.0 - cosHalfTheta * cosHalfTheta);

    if (Math.abs(sinHalfTheta) < 0.001) {

      this._w = 0.5 * (w + this._w);
      this._x = 0.5 * (x + this._x);
      this._y = 0.5 * (y + this._y);
      this._z = 0.5 * (z + this._z);

      return this;
    }

    var halfTheta = Math.atan2(sinHalfTheta, cosHalfTheta);
    var ratioA = Math.sin((1 - t) * halfTheta) / sinHalfTheta,
        ratioB = Math.sin(t * halfTheta) / sinHalfTheta;

    this._w = w * ratioA + this._w * ratioB;
    this._x = x * ratioA + this._x * ratioB;
    this._y = y * ratioA + this._y * ratioB;
    this._z = z * ratioA + this._z * ratioB;

    this.onChangeCallback();

    return this;
  },

  onChange: function onChange(callback) {

    this.onChangeCallback = callback;

    return this;
  },

  onChangeCallback: function onChangeCallback() {}

};

(0, _objectAssign2.default)(Quaternion, {

  slerp: function slerp(qa, qb, qm, t) {

    return qm.copy(qa).slerp(qb, t);
  },

  slerpFlat: function slerpFlat(dst, dstOffset, src0, srcOffset0, src1, srcOffset1, t) {

    // fuzz-free, array-based Quaternion SLERP operation

    var x0 = src0[srcOffset0 + 0],
        y0 = src0[srcOffset0 + 1],
        z0 = src0[srcOffset0 + 2],
        w0 = src0[srcOffset0 + 3],
        x1 = src1[srcOffset1 + 0],
        y1 = src1[srcOffset1 + 1],
        z1 = src1[srcOffset1 + 2],
        w1 = src1[srcOffset1 + 3];

    if (w0 !== w1 || x0 !== x1 || y0 !== y1 || z0 !== z1) {

      var s = 1 - t,
          cos = x0 * x1 + y0 * y1 + z0 * z1 + w0 * w1,
          dir = cos >= 0 ? 1 : -1,
          sqrSin = 1 - cos * cos;

      // Skip the Slerp for tiny steps to avoid numeric problems:
      if (sqrSin > Number.EPSILON) {

        var sin = Math.sqrt(sqrSin),
            len = Math.atan2(sin, cos * dir);

        s = Math.sin(s * len) / sin;
        t = Math.sin(t * len) / sin;
      }

      var tDir = t * dir;

      x0 = x0 * s + x1 * tDir;
      y0 = y0 * s + y1 * tDir;
      z0 = z0 * s + z1 * tDir;
      w0 = w0 * s + w1 * tDir;

      // Normalize in case we just did a lerp:
      if (s === 1 - t) {

        var f = 1 / Math.sqrt(x0 * x0 + y0 * y0 + z0 * z0 + w0 * w0);

        x0 *= f;
        y0 *= f;
        z0 *= f;
        w0 *= f;
      }
    }

    dst[dstOffset] = x0;
    dst[dstOffset + 1] = y0;
    dst[dstOffset + 2] = z0;
    dst[dstOffset + 3] = w0;
  }

});

exports.default = Quaternion;

/***/ }),
/* 8 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
var _Math = {
  DEG2RAD: Math.PI / 180,
  RAD2DEG: 180 / Math.PI,
  degToRad: function degToRad(degrees) {
    return degrees * _Math.DEG2RAD;
  },
  radToDeg: function radToDeg(radians) {
    return radians * _Math.RAD2DEG;
  }
};

exports.default = _Math;

/***/ }),
/* 9 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _simpleLodash = __webpack_require__(1);

var _simpleLodash2 = _interopRequireDefault(_simpleLodash);

var _animationUtil = __webpack_require__(4);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

// 内置方法
function colorToDecimal(hexColor) {
  var hex = hexColor.replace(/'|"|#/g, '');
  return parseInt(hex, 16);
}

function decToHex(dec) {
  var hex = dec.toString(16);
  var a = [];
  for (var i = 0; i < 6 - hex.length; i++) {
    a.push('0');
  }
  return a.join('') + hex;
}

function parseColor(hexColor) {
  var hex = hexColor.replace(/'|"|#/g, '');
  hex = hex.length === 3 ? [hex[0], hex[0], hex[1], hex[1], hex[2], hex[2]].join('') : hex;
  var r = '' + hex[0] + hex[1];
  var g = '' + hex[2] + hex[3];
  var b = '' + hex[4] + hex[5];
  return {
    r: r,
    g: g,
    b: b,
    dr: colorToDecimal(r),
    dg: colorToDecimal(g),
    db: colorToDecimal(b)
  };
}

var Fn = {
  max: Math.max,
  min: Math.min,
  sin: Math.sin,
  cos: Math.cos,
  tan: Math.tan,
  sqrt: Math.sqrt,
  cbrt: Math.cbrt,
  log: Math.log,
  abs: Math.abs,
  atan: Math.atan,
  floor: Math.floor,
  ceil: Math.ceil,
  pow: Math.pow,
  exp: Math.exp,
  PI: Math.PI,
  E: Math.E,
  acos: Math.acos,
  asin: Math.asin,
  sign: Math.sign,
  atan2: Math.atan2,
  round: Math.round,
  rgb: function rgb(r, g, b) {
    return 'rgb(' + parseInt(r) + ',' + parseInt(g) + ',' + parseInt(b) + ')';
  },
  rgba: function rgba(r, g, b, a) {
    return 'rgb(' + parseInt(r) + ',' + parseInt(g) + ',' + parseInt(b) + ',' + a + ')';
  },
  // 用来获取参数
  getArgs: function getArgs() {
    return arguments;
  },
  // 颜色估值算法
  evaluateColor: function evaluateColor(colorFrom, colorTo, percent) {
    percent = percent > 1 ? 1 : percent;
    var from = parseColor(colorFrom);
    var to = parseColor(colorTo);
    var dr = parseInt((to.dr - from.dr) * percent + from.dr);
    var dg = parseInt((to.dg - from.dg) * percent + from.dg);
    var db = parseInt((to.db - from.db) * percent + from.db);
    var resDec = dr * 16 * 16 * 16 * 16 + dg * 16 * 16 + db;
    return '#' + decToHex(resDec);
  }
};

// 内置easing所有方法
_simpleLodash2.default.map(_animationUtil.Easing, function (v, k) {
  Fn[k] = function (t) {
    return t;
  };
});

exports.default = Fn;

/***/ }),
/* 10 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _simpleLodash = __webpack_require__(1);

var _simpleLodash2 = _interopRequireDefault(_simpleLodash);

var _expression = __webpack_require__(5);

var _expression2 = _interopRequireDefault(_expression);

var _handlers = __webpack_require__(11);

var _utils = __webpack_require__(2);

var _fn = __webpack_require__(9);

var _fn2 = _interopRequireDefault(_fn);

var _objectAssign = __webpack_require__(0);

var _objectAssign2 = _interopRequireDefault(_objectAssign);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

// transform
var vendorTransform = (0, _utils.prefixStyle)('transform');

var Binding = function () {
  function Binding(options, callback) {
    _classCallCheck(this, Binding);

    this._eventHandler = null;
    this.elTransforms = [];
    this.token = null;

    this.options = options;
    this.callback = callback;
    this.token = this.genToken();
    this._initElTransforms();
    var eventType = options.eventType;

    switch (eventType) {
      case 'pan':
        this._eventHandler = new _handlers.PanHandler(this);
        break;
      case 'orientation':
        this._eventHandler = new _handlers.OrientationHandler(this);
        break;
      case 'timing':
        this._eventHandler = new _handlers.TimingHandler(this);
        break;
      case 'scroll':
        this._eventHandler = new _handlers.ScrollHandler(this);
        break;
    }
  }

  _createClass(Binding, [{
    key: 'genToken',
    value: function genToken() {
      return parseInt(Math.random() * 10000000);
    }
  }, {
    key: '_initElTransforms',
    value: function _initElTransforms() {
      var _options$props = this.options.props,
          props = _options$props === undefined ? [] : _options$props;

      var elTransforms = this.elTransforms;
      props.forEach(function (prop) {
        var element = prop.element;

        if (!_simpleLodash2.default.find(elTransforms, function (o) {
          return o.element === element;
        })) {
          elTransforms.push({
            element: element,
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
  }, {
    key: 'getValue',
    value: function getValue(params, expression) {
      return _expression2.default.execute(expression, (0, _objectAssign2.default)(_fn2.default, params));
    }

    // TODO scroll.contentOffset 待确认及补全

  }, {
    key: 'setProperty',
    value: function setProperty(el, property, val) {
      var elTransform = _simpleLodash2.default.find(this.elTransforms, function (o) {
        return o.element === el;
      });
      switch (property) {
        case 'transform.translateX':
          elTransform.transform.translateX = (0, _utils.px)(val);
          break;
        case 'transform.translateY':
          elTransform.transform.translateY = (0, _utils.px)(val);
          break;
        case 'transform.translateZ':
          elTransform.transform.translateZ = (0, _utils.px)(val);
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
        case 'width':
          el.style.width = (0, _utils.px)(val) + 'px';
          break;
        case 'height':
          el.style.height = (0, _utils.px)(val) + 'px';
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
      }
      el.style[vendorTransform] = ['translateX(' + elTransform.transform.translateX + 'px)', 'translateY(' + elTransform.transform.translateY + 'px)', 'translateZ(' + elTransform.transform.translateZ + 'px)', 'scaleX(' + elTransform.transform.scaleX + ')', 'scaleY(' + elTransform.transform.scaleY + ')', 'rotateX(' + elTransform.transform.rotateX + 'deg)', 'rotateY(' + elTransform.transform.rotateY + 'deg)', 'rotateZ(' + elTransform.transform.rotateZ + 'deg)'].join(' ');
    }
  }, {
    key: 'destroy',
    value: function destroy() {
      this._eventHandler.destroy();
    }
  }]);

  return Binding;
}();

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
  bind: function bind(options) {
    var _this = this;

    var callback = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : function () {};

    if (!options) {
      throw new Error('should pass options for binding');
    }
    var existInstances = _simpleLodash2.default.filter(this._bindingInstances, function (instance) {
      if (options.anchor) {
        return instance.options.anchor === options.anchor && instance.options.eventType === options.eventType;
      }
    });
    // 销毁上次实例
    if (existInstances) {
      _simpleLodash2.default.forEach(existInstances, function (inst) {
        inst.destroy();
        _this._bindingInstances = _simpleLodash2.default.dropWhile(_this._bindingInstances, function (bindInst) {
          return bindInst === inst;
        });
      });
    }
    var binding = new Binding(options, callback);
    this._bindingInstances.push(binding);
    return { token: binding.token };
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
    var inst = _simpleLodash2.default.find(this._bindingInstances, function (instance) {
      return instance.options.eventType === options.eventType && instance.token === options.token;
    });
    if (inst) {
      inst.destroy();
    }
  },
  unbindAll: function unbindAll() {
    this._bindingInstances.forEach(function (inst) {
      inst.destroy({
        eventType: inst.options.eventType,
        token: inst.token
      });
    });
  },
  getComputedStyle: function getComputedStyle(elRef) {
    var computedStyle = window.getComputedStyle(elRef);
    var style = (0, _utils.matrixToTransformObj)(computedStyle[vendorTransform]);
    style.opacity = Number(computedStyle.opacity);
    style.width = (0, _utils.pxTo750)(computedStyle.width.replace('px', ''));
    style.height = (0, _utils.pxTo750)(computedStyle.height.replace('px', ''));
    style['background-color'] = computedStyle['background-color'];
    style.color = computedStyle.color;
    return style;
  }
};

/***/ }),
/* 11 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ScrollHandler = exports.TimingHandler = exports.OrientationHandler = exports.PanHandler = undefined;

var _pan = __webpack_require__(12);

var _pan2 = _interopRequireDefault(_pan);

var _orientation = __webpack_require__(14);

var _orientation2 = _interopRequireDefault(_orientation);

var _timing = __webpack_require__(17);

var _timing2 = _interopRequireDefault(_timing);

var _scroll = __webpack_require__(18);

var _scroll2 = _interopRequireDefault(_scroll);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

exports.PanHandler = _pan2.default;
exports.OrientationHandler = _orientation2.default;
exports.TimingHandler = _timing2.default;
exports.ScrollHandler = _scroll2.default;

/***/ }),
/* 12 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _pan = __webpack_require__(13);

var _pan2 = _interopRequireDefault(_pan);

var _common = __webpack_require__(3);

var _common2 = _interopRequireDefault(_common);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

var PanHandler = function (_CommonHandler) {
  _inherits(PanHandler, _CommonHandler);

  function PanHandler(binding) {
    _classCallCheck(this, PanHandler);

    var _this = _possibleConstructorReturn(this, (PanHandler.__proto__ || Object.getPrototypeOf(PanHandler)).call(this, binding));

    _this._onPan = function (e) {
      var x = e.deltaX;
      var y = e.deltaY;
      var _this$binding$options = _this.binding.options.props,
          props = _this$binding$options === undefined ? [] : _this$binding$options;

      props.forEach(function (prop) {
        var element = prop.element,
            property = prop.property,
            expression = prop.expression;

        var transformed = JSON.parse(expression.transformed);
        var val = _this.binding.getValue({ x: x, y: y }, transformed);
        _this.binding.setProperty(element, property, val);
      });
    };

    _this._onPanStart = function () {
      _this.binding.callback({ deltaX: 0, state: 'start', deltaY: 0 });
    };

    _this._onPanEnd = function (e) {
      _this.binding.callback({ deltaX: parseInt(e.deltaX), state: 'end', deltaY: parseInt(e.deltaY) });
    };

    var anchor = binding.options.anchor;

    var panGesture = _this.panGesture = new _pan2.default(anchor, binding.options.options);
    panGesture.on('pan', _this._onPan);
    panGesture.on('panstart', _this._onPanStart);
    panGesture.on('panend', _this._onPanEnd);
    return _this;
  }

  _createClass(PanHandler, [{
    key: 'destroy',
    value: function destroy() {
      var panGesture = this.panGesture;
      panGesture.off('pan', this._onPan);
      panGesture.off('panstart', this._onPanStart);
      panGesture.off('panend', this._onPanEnd);
      panGesture.destroy();
    }
  }]);

  return PanHandler;
}(_common2.default);

exports.default = PanHandler;
;

/***/ }),
/* 13 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _simpleLodash = __webpack_require__(1);

var _simpleLodash2 = _interopRequireDefault(_simpleLodash);

var _objectAssign = __webpack_require__(0);

var _objectAssign2 = _interopRequireDefault(_objectAssign);

var _utils = __webpack_require__(2);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var abs = Math.abs;


var DEFAULT_CONFIG = {
  thresholdX: 10,
  thresholdY: 10,
  touchAction: 'auto',
  touchActionRatio: 1 / 2 // 默认1:2
};

var PanGesture = function () {
  function PanGesture(el, config) {
    var _this = this;

    _classCallCheck(this, PanGesture);

    this.startX = null;
    this.startY = null;
    this.panStartX = null;
    this.panStartY = null;
    this.deltaX = 0;
    this.deltaY = 0;
    this.events = {
      'panstart': [],
      'pan': [],
      'panend': [],
      'pancancel': []
    };

    this.onTouchStart = function (e) {
      // e.preventDefault();
    };

    this.handlePanStart = function (e) {
      e.preventDefault();
      if (_this.panStartX === null || _this.panStartY === null) {
        _this.panStartX = (0, _utils.pxTo750)(e.touches[0].pageX);
        _this.panStartY = (0, _utils.pxTo750)(e.touches[0].pageY);
        _this.events.panstart.forEach(function (handler) {
          handler(e);
        });
        return;
      }
    };

    this.onTouchMove = function (e) {
      var _config = _this.config,
          thresholdX = _config.thresholdX,
          thresholdY = _config.thresholdY,
          touchAction = _config.touchAction,
          touchActionRatio = _config.touchActionRatio;

      if (_this.startX === null || _this.startY === null) {
        _this.startX = e.touches[0].pageX;
        _this.startY = e.touches[0].pageY;
      }
      var dx = e.touches[0].pageX - _this.startX;
      var dy = e.touches[0].pageY - _this.startY;

      switch (touchAction) {
        case 'auto':
          e.preventDefault();
          if (abs(dx) >= thresholdX || abs(dy) >= thresholdY) {
            _this.handlePanStart(e);
          }
          break;
        case 'pan-x':
          if (abs(dx) >= thresholdX && abs(dy / dx) < touchActionRatio && abs(dy) < thresholdY) {
            _this.handlePanStart(e);
          }
          break;
        case 'pan-y':
          if (abs(dy) >= thresholdY && abs(dx / dy) < touchActionRatio && abs(dx) < thresholdX) {
            _this.handlePanStart(e);
          }
          break;
      }

      if (_this.panStartX !== null && _this.panStartY !== null) {
        switch (touchAction) {
          case 'auto':
            _this.deltaX = (0, _utils.pxTo750)(e.touches[0].pageX) - _this.panStartX;
            _this.deltaY = (0, _utils.pxTo750)(e.touches[0].pageY) - _this.panStartY;
            break;
          case 'pan-x':
            _this.deltaX = (0, _utils.pxTo750)(e.touches[0].pageX) - _this.panStartX;
            _this.deltaY = 0;
            break;
          case 'pan-y':
            _this.deltaX = 0;
            _this.deltaY = (0, _utils.pxTo750)(e.touches[0].pageY) - _this.panStartY;
            break;
        }
        e.deltaX = _this.deltaX;
        e.deltaY = _this.deltaY;
        _this.events.pan.forEach(function (handler) {
          handler(e);
        });
      }
    };

    this.onTouchEnd = function (e) {
      e.deltaX = _this.deltaX;
      e.deltaY = _this.deltaY;
      _this.events.panend.forEach(function (handler) {
        handler(e);
      });
    };

    this.onTouchCancel = function (e) {
      e.deltaX = _this.deltaX;
      e.deltaY = _this.deltaY;
      _this.events.pancancel.forEach(function (handler) {
        handler(e);
      });
    };

    this.el = el;
    this.config = (0, _objectAssign2.default)(DEFAULT_CONFIG, config);
    this.el.addEventListener('touchstart', this.onTouchStart);
    this.el.addEventListener('touchmove', this.onTouchMove);
    this.el.addEventListener('touchend', this.onTouchEnd);
    this.el.addEventListener('touchcancel', this.onTouchCancel);
  }

  _createClass(PanGesture, [{
    key: 'on',
    value: function on(fn, handler) {
      if (!this.events[fn]) return;
      this.events[fn].push(handler);
    }
  }, {
    key: 'destroy',
    value: function destroy() {
      this.el.removeEventListener('touchstart', this.onTouchStart);
      this.el.removeEventListener('touchmove', this.onTouchMove);
      this.el.removeEventListener('touchend', this.onTouchEnd);
      this.el.removeEventListener('touchcancel', this.onTouchCancel);
      this.offAll();
      this.startX = null;
      this.startY = null;
      this.panStartX = null;
      this.panStartY = null;
    }
  }, {
    key: 'offAll',
    value: function offAll() {
      var _this2 = this;

      _simpleLodash2.default.map(this.events, function (handlers, fn) {
        _simpleLodash2.default.forEach(handlers, function (handler) {
          _this2.off(fn, handler);
        });
      });
    }
  }, {
    key: 'off',
    value: function off(fn, handler) {
      if (!fn) return;
      if (fn && this.events[fn] && this.events[fn].length) {
        if (!handler) return;
        var h = _simpleLodash2.default.find(this.events[fn], function (o) {
          return o === handler;
        });
        var i = _simpleLodash2.default.findIndex(this.events[fn], function (o) {
          return o === handler;
        });
        if (h) {
          this.events[fn].splice(i, 1);
        }
      }
    }
  }]);

  return PanGesture;
}();

exports.default = PanGesture;

/***/ }),
/* 14 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _vector = __webpack_require__(6);

var _vector2 = _interopRequireDefault(_vector);

var _orientation_controls = __webpack_require__(15);

var _orientation_controls2 = _interopRequireDefault(_orientation_controls);

var _math = __webpack_require__(8);

var _math2 = _interopRequireDefault(_math);

var _animationUtil = __webpack_require__(4);

var _common = __webpack_require__(3);

var _common2 = _interopRequireDefault(_common);

var _objectAssign = __webpack_require__(0);

var _objectAssign2 = _interopRequireDefault(_objectAssign);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }
// import {raf, cancelRAF} from '../raf';


var OrientationHandler = function (_CommonHandler) {
  _inherits(OrientationHandler, _CommonHandler);

  function OrientationHandler(binding) {
    _classCallCheck(this, OrientationHandler);

    var _this = _possibleConstructorReturn(this, (OrientationHandler.__proto__ || Object.getPrototypeOf(OrientationHandler)).call(this, binding));

    _this.binding = null;
    _this.control = null;
    _this.start = null;
    _this.timer = null;

    _this._onOrientation = function (e) {
      var props = _this.binding.options.props;

      props.forEach(function (prop) {
        var element = prop.element,
            property = prop.property,
            expression = prop.expression;

        var transformed = JSON.parse(expression.transformed);
        var val = _this.binding.getValue(e, transformed);
        _this.binding.setProperty(element, property, val);
      });
    };

    _this.options = (0, _objectAssign2.default)({
      sceneType: '2d'
    }, binding.options.options);
    _this.binding = binding;
    if (_this.options.sceneType.toLowerCase() === '2d') {
      _this.controlX = new _orientation_controls2.default({ beta: 90 });
      _this.controlY = new _orientation_controls2.default({ gamma: 90, alpha: 0 });
    } else {
      _this.control = new _orientation_controls2.default();
    }
    _this.run();
    return _this;
  }

  _createClass(OrientationHandler, [{
    key: 'run',
    value: function run() {
      var _this2 = this;

      // 2d场景
      if (this.options.sceneType.toLowerCase() === '2d') {
        this.controlX.update();
        this.controlY.update();
        var _controlX$deviceOrien = this.controlX.deviceOrientation,
            alpha = _controlX$deviceOrien.alpha,
            beta = _controlX$deviceOrien.beta,
            gamma = _controlX$deviceOrien.gamma,
            dalpha = _controlX$deviceOrien.dalpha,
            dbeta = _controlX$deviceOrien.dbeta,
            dgamma = _controlX$deviceOrien.dgamma;

        var vecX = new _vector2.default(0, 0, 1);
        vecX.applyQuaternion(this.controlX.quaternion);
        var vecY = new _vector2.default(0, 1, 1);
        vecY.applyQuaternion(this.controlY.quaternion);
        // 0,180 -> -90,90
        var x = _math2.default.radToDeg(Math.acos(vecX.x)) - 90;
        var y = _math2.default.radToDeg(Math.acos(vecY.y)) - 90;
        if (!this.start && !isNaN(x) && !isNaN(y)) {
          this.start = {
            x: x,
            y: y
          };
        }
        if (this.start) {
          var dx = x - this.start.x;
          var dy = y - this.start.y;
          this._onOrientation({
            x: x, y: y, dx: dx, dy: dy, alpha: alpha, beta: beta, gamma: gamma, dalpha: dalpha, dbeta: dbeta, dgamma: dgamma
          });
        }
      } else {
        // 3d场景
        this.control.update();
        var _control$deviceOrient = this.control.deviceOrientation,
            _alpha = _control$deviceOrient.alpha,
            _beta = _control$deviceOrient.beta,
            _gamma = _control$deviceOrient.gamma,
            _dalpha = _control$deviceOrient.dalpha,
            _dbeta = _control$deviceOrient.dbeta,
            _dgamma = _control$deviceOrient.dgamma;
        var _control$quaternion = this.control.quaternion,
            _x = _control$quaternion.x,
            _y = _control$quaternion.y,
            z = _control$quaternion.z;

        this._onOrientation({ alpha: _alpha, beta: _beta, gamma: _gamma, dalpha: _dalpha, dbeta: _dbeta, dgamma: _dgamma, x: _x, y: _y, z: z });
      }
      this.timer = (0, _animationUtil.raf)(function () {
        _this2.run();
      });
    }
  }, {
    key: 'destroy',
    value: function destroy() {
      if (this.timer) {
        (0, _animationUtil.cancelRAF)(this.timer);
        this.timer = null;
      }
    }
  }]);

  return OrientationHandler;
}(_common2.default);

exports.default = OrientationHandler;

/***/ }),
/* 15 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _quaternion = __webpack_require__(7);

var _quaternion2 = _interopRequireDefault(_quaternion);

var _vector = __webpack_require__(6);

var _vector2 = _interopRequireDefault(_vector);

var _euler = __webpack_require__(16);

var _euler2 = _interopRequireDefault(_euler);

var _math = __webpack_require__(8);

var _math2 = _interopRequireDefault(_math);

var _objectAssign = __webpack_require__(0);

var _objectAssign2 = _interopRequireDefault(_objectAssign);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function isNaNOrUndefined(n) {
  return n === undefined || isNaN(n) || n === null;
}

function DeviceOrientationControls(object) {
  var scope = this;
  this.object = (0, _objectAssign2.default)({
    alphaOffsetAngle: 0,
    betaOffsetAngle: 0,
    gammaOffsetAngle: 0
  }, object);

  this.alphaOffsetAngle = this.object.alphaOffsetAngle;
  this.betaOffsetAngle = this.object.betaOffsetAngle;
  this.gammaOffsetAngle = this.object.gammaOffsetAngle;

  this.quaternion = new _quaternion2.default(0, 0, 0, 1);
  this.enabled = true;
  this.deviceOrientation = {};
  this.screenOrientation = 0;
  this.start = null;

  this.recordsAlpha = [];

  function formatRecords(records, threshold) {
    var l = records.length;
    var times = 0;
    if (l > 1) {
      for (var i = 0; i < l; i++) {
        if (records[i - 1] != undefined && records[i] != undefined) {
          if (records[i] - records[i - 1] < -threshold / 2) {
            times = Math.floor(records[i - 1] / threshold) + 1;
            records[i] = records[i] + times * threshold;
          }
          if (records[i] - records[i - 1] > threshold / 2) {
            records[i] = records[i] - threshold;
          }
        }
      }
    }
    return records;
  }

  var onDeviceOrientationChangeEvent = function onDeviceOrientationChangeEvent(e) {

    var alpha = e.alpha;
    var beta = e.beta;
    var gamma = e.gamma;
    var recordsAlpha = scope.recordsAlpha;

    if (!scope.start) {
      scope.start = {
        alpha: alpha,
        beta: beta,
        gamma: gamma
      };
    }
    recordsAlpha.push(alpha);
    if (recordsAlpha.length > 5) {
      recordsAlpha = formatRecords(recordsAlpha, 360);
      recordsAlpha.shift();
    }

    var formatAlpha = (recordsAlpha[recordsAlpha.length - 1] - scope.start.alpha) % 360;
    if (!isNaNOrUndefined(alpha) && !isNaNOrUndefined(beta) && !isNaNOrUndefined(gamma)) {
      scope.enabled = true;
    }

    scope.deviceOrientation = {
      alpha: alpha,
      beta: beta,
      gamma: gamma,
      formatAlpha: formatAlpha,
      dalpha: alpha - scope.start.alpha,
      dbeta: beta - scope.start.beta,
      dgamma: gamma - scope.start.gamma
    };
  };

  var onScreenOrientationChangeEvent = function onScreenOrientationChangeEvent() {

    scope.screenOrientation = window.orientation || 0;
  };

  // The angles alpha, beta and gamma form a set of intrinsic Tait-Bryan angles of type Z-X'-Y''

  var setObjectQuaternion = function () {

    var zee = new _vector2.default(0, 0, 1);

    var euler = new _euler2.default();

    var q0 = new _quaternion2.default();

    var q1 = new _quaternion2.default(-Math.sqrt(0.5), 0, 0, Math.sqrt(0.5)); // - PI/2 around the x-axis

    return function (quaternion, alpha, beta, gamma, orient) {

      euler.set(beta, alpha, -gamma, 'YXZ'); // 'ZXY' for the device, but 'YXZ' for us

      quaternion.setFromEuler(euler); // orient the device

      quaternion.multiply(q1); // camera looks out the back of the device, not the top

      quaternion.multiply(q0.setFromAxisAngle(zee, -orient)); // adjust for screen orientation
    };
  }();

  this.connect = function () {
    onScreenOrientationChangeEvent(); // run once on load
    window.addEventListener('orientationchange', onScreenOrientationChangeEvent, false);
    window.addEventListener('deviceorientation', onDeviceOrientationChangeEvent, false);
  };

  this.disconnect = function () {
    window.removeEventListener('orientationchange', onScreenOrientationChangeEvent, false);
    window.removeEventListener('deviceorientation', onDeviceOrientationChangeEvent, false);
    scope.enabled = false;
  };

  this.update = function () {
    if (scope.enabled === false) return;
    var alpha = !isNaNOrUndefined(scope.deviceOrientation.formatAlpha) ? _math2.default.degToRad(!isNaNOrUndefined(scope.object.alpha) ? scope.object.alpha : scope.deviceOrientation.formatAlpha + scope.alphaOffsetAngle) : 0; // Z
    var beta = !isNaNOrUndefined(scope.deviceOrientation.beta) ? _math2.default.degToRad(!isNaNOrUndefined(scope.object.beta) ? scope.object.beta : scope.deviceOrientation.beta + scope.betaOffsetAngle) : 0; // X'
    var gamma = !isNaNOrUndefined(scope.deviceOrientation.gamma) ? _math2.default.degToRad(!isNaNOrUndefined(scope.object.gamma) ? scope.object.gamma : scope.deviceOrientation.gamma + scope.gammaOffsetAngle) : 0; // Y''
    var orient = scope.screenOrientation ? _math2.default.degToRad(scope.screenOrientation) : 0; // O
    setObjectQuaternion(scope.quaternion, alpha, beta, gamma, orient);
  };

  this.updateAlphaOffsetAngle = function (angle) {
    this.alphaOffsetAngle = angle;
    this.update();
  };
  this.updateBetaOffsetAngle = function (angle) {
    this.betaOffsetAngle = angle;
    this.update();
  };
  this.updateGammaOffsetAngle = function (angle) {
    this.gammaOffsetAngle = angle;
    this.update();
  };

  this.dispose = function () {
    this.disconnect();
  };

  this.connect();
};

exports.default = DeviceOrientationControls;

/***/ }),
/* 16 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var Euler = function () {
  function Euler(x, y, z, order) {
    _classCallCheck(this, Euler);

    this.isEuler = true;
    this._x = 0;
    this._y = 0;
    this._z = 0;

    this._x = x || 0;
    this._y = y || 0;
    this._z = z || 0;
    this._order = order || Euler.DefaultOrder;
  }

  _createClass(Euler, [{
    key: 'set',
    value: function set(x, y, z, order) {
      this._x = x;
      this._y = y;
      this._z = z;
      this._order = order || this._order;
      this.onChangeCallback();
      return this;
    }
  }, {
    key: 'onChange',
    value: function onChange(callback) {
      this.onChangeCallback = callback;
      return this;
    }
  }, {
    key: 'onChangeCallback',
    value: function onChangeCallback() {}
  }, {
    key: 'order',
    get: function get() {
      return this._order;
    },
    set: function set(value) {
      this._order = value;
      this.onChangeCallback();
    }
  }]);

  return Euler;
}();

Euler.RotationOrders = ['XYZ', 'YZX', 'ZXY', 'XZY', 'YXZ', 'ZYX'];
Euler.DefaultOrder = 'XYZ';
exports.default = Euler;

/***/ }),
/* 17 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _simpleLodash = __webpack_require__(1);

var _simpleLodash2 = _interopRequireDefault(_simpleLodash);

var _fn = __webpack_require__(9);

var _fn2 = _interopRequireDefault(_fn);

var _animationUtil = __webpack_require__(4);

var _animationUtil2 = _interopRequireDefault(_animationUtil);

var _expression = __webpack_require__(5);

var _expression2 = _interopRequireDefault(_expression);

var _common = __webpack_require__(3);

var _common2 = _interopRequireDefault(_common);

var _objectAssign = __webpack_require__(0);

var _objectAssign2 = _interopRequireDefault(_objectAssign);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }
// import Easing from 'animation-util/lib/easing';


function getArgsFromExp(expression) {
  var newExp = JSON.parse(JSON.stringify(expression));
  if (newExp.type === 'CallExpression') {
    var easing = newExp.children[0].value;
    var isBezier = easing === 'cubicBezier';
    newExp.children[0].value = 'getArgs';
    var args = _expression2.default.execute(newExp, (0, _objectAssign2.default)(_fn2.default, {}));
    if (isBezier) {
      return {
        duration: args[3],
        begin: args[1],
        offset: args[2],
        end: args[1] + args[2],
        easing: easing,
        x1: args[4],
        y1: args[5],
        x2: args[6],
        y2: args[7]
      };
    } else if (easing && _animationUtil.Easing[easing]) {
      return {
        duration: args[3],
        begin: args[1],
        offset: args[2] || 0,
        end: args[1] + args[2],
        easing: easing
      };
    }
  }
  return {
    easing: 'linear',
    duration: Infinity,
    isNormal: true
  };
}

var TimingHandler = function (_CommonHandler) {
  _inherits(TimingHandler, _CommonHandler);

  function TimingHandler(binding) {
    _classCallCheck(this, TimingHandler);

    var _this = _possibleConstructorReturn(this, (TimingHandler.__proto__ || Object.getPrototypeOf(TimingHandler)).call(this, binding));

    _initialiseProps.call(_this);

    var _this$binding$options = _this.binding.options,
        _this$binding$options2 = _this$binding$options.props,
        props = _this$binding$options2 === undefined ? [] : _this$binding$options2,
        exitExpression = _this$binding$options.exitExpression;

    props.forEach(function (prop) {
      var element = prop.element,
          property = prop.property,
          expression = prop.expression;

      _this.animate({
        element: element,
        property: property,
        expression: expression,
        exitExpression: exitExpression
      });
    });
    return _this;
  }

  _createClass(TimingHandler, [{
    key: 'animate',
    value: function animate(args) {
      var _this2 = this;

      var element = args.element,
          property = args.property,
          expression = args.expression,
          exitExpression = args.exitExpression;

      var transformed = JSON.parse(expression.transformed);
      var exitTransformed = void 0;
      if (exitExpression && exitExpression.transformed) {
        exitTransformed = JSON.parse(exitExpression.transformed);
      }

      var _getArgsFromExp = getArgsFromExp(transformed),
          duration = _getArgsFromExp.duration,
          begin = _getArgsFromExp.begin,
          end = _getArgsFromExp.end,
          easing = _getArgsFromExp.easing,
          x1 = _getArgsFromExp.x1,
          y1 = _getArgsFromExp.y1,
          x2 = _getArgsFromExp.x2,
          y2 = _getArgsFromExp.y2,
          isNormal = _getArgsFromExp.isNormal;

      if (duration !== undefined) {
        var animation = new _animationUtil2.default({
          duration: duration,
          easing: easing,
          bezierArgs: easing === 'cubicBezier' ? [x1, y1, x2, y2] : undefined,
          onStart: function onStart() {
            if (!_this2.isStart) {
              _this2.binding.callback({ state: 'start', t: 0 });
              _this2.isStart = true;
            }
          },
          onRun: function onRun(e) {
            if (exitTransformed && _this2.binding.getValue({ t: e.t }, exitTransformed)) {
              animation.stop();
            }
            var realVal = void 0;
            if (isNormal) {
              realVal = _this2.binding.getValue({ t: e.t }, transformed);
            } else {
              var val = _this2.binding.getValue({ t: e.percent }, transformed);
              realVal = (end - begin) * val + begin;
            }
            _this2.binding.setProperty(element, property, realVal);
          },
          onStop: function onStop(e) {
            _this2.callback({ state: 'exit', t: e.t - 1000 / 60 });
          }
        });
        animation.run();
        this.animations.push(animation);
      }
    }
  }, {
    key: 'destroy',
    value: function destroy() {
      _simpleLodash2.default.forEach(this.animations, function (animation) {
        animation.stop();
      });
    }
  }]);

  return TimingHandler;
}(_common2.default);

var _initialiseProps = function _initialiseProps() {
  var _this3 = this;

  this.animations = [];
  this.isStart = false;
  this.callbackCounts = 0;

  this.callback = function () {
    var args = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
    var _binding$options$prop = _this3.binding.options.props,
        props = _binding$options$prop === undefined ? [] : _binding$options$prop;

    if (props && props.length > 0) {
      _this3.callbackCounts++;
      if (_this3.callbackCounts === props.length) {
        _this3.binding.callback(args);
      }
    }
  };
};

exports.default = TimingHandler;

/***/ }),
/* 18 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _common = __webpack_require__(3);

var _common2 = _interopRequireDefault(_common);

var _utils = __webpack_require__(2);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

function isTurner(prev, now) {

  return prev / now < 0;
}

var ScrollHandler = function (_CommonHandler) {
  _inherits(ScrollHandler, _CommonHandler);

  function ScrollHandler(binding) {
    _classCallCheck(this, ScrollHandler);

    var _this = _possibleConstructorReturn(this, (ScrollHandler.__proto__ || Object.getPrototypeOf(ScrollHandler)).call(this, binding));

    _this.dx = 0;
    _this.dy = 0;
    _this.prevX = null;
    _this.prevY = null;
    _this.tx = 0;
    _this.ty = 0;
    _this.tdx = 0;
    _this.tdy = 0;

    _this._onScroll = function (e) {
      var props = _this.binding.options.props;

      var callback = _this.binding.callback;
      var x = (0, _utils.pxTo750)(e.target.scrollLeft);
      var y = (0, _utils.pxTo750)(e.target.scrollTop);
      props.forEach(function (prop) {
        var element = prop.element,
            property = prop.property,
            expression = prop.expression;

        var transformed = JSON.parse(expression.transformed);
        var val = _this.binding.getValue({
          x: x,
          y: y,
          dx: _this.dx,
          dy: _this.dy,
          tdx: _this.tdx,
          tdy: _this.tdy
        }, transformed);

        _this.binding.setProperty(element, property, val);
      });

      if (_this.prevX !== null && _this.prevY !== null) {
        var dx = x - _this.prevX;
        var dy = y - _this.prevY;
        var cbParams = {
          x: x,
          y: y
        };
        // 拐点
        if (isTurner(_this.dx, dx)) {
          _this.tx = x;
          cbParams.state = 'turn';
        }
        if (isTurner(_this.dy, dy)) {
          _this.ty = y;
          cbParams.state = 'turn';
        }

        _this.dx = cbParams.dx = x - _this.prevX;
        _this.dy = cbParams.dy = y - _this.prevY;
        _this.tdx = cbParams.tdx = x - _this.tx;
        _this.tdy = cbParams.tdy = y - _this.ty;
        if (cbParams.state) {
          callback(cbParams);
        }
      }

      _this.prevX = x;
      _this.prevY = y;
    };

    var anchor = binding.options.anchor;

    _this.tx = (0, _utils.pxTo750)(anchor.scrollLeft);
    _this.ty = (0, _utils.pxTo750)(anchor.scrollTop);
    anchor.addEventListener('scroll', _this._onScroll);
    return _this;
  }

  _createClass(ScrollHandler, [{
    key: 'destroy',
    value: function destroy() {
      var anchor = this.binding.options.anchor;

      anchor.removeEventListener('scroll', this._onScroll);
    }
  }]);

  return ScrollHandler;
}(_common2.default);

exports.default = ScrollHandler;

/***/ })
/******/ ]);
});

/***/ })
/******/ ]);
});