'use strict';

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _simpleLodash = require('simple-lodash');

var _simpleLodash2 = _interopRequireDefault(_simpleLodash);

var _expression = require('./expression');

var _expression2 = _interopRequireDefault(_expression);

var _handlers = require('./handlers');

var _utils = require('./utils');

var _fn = require('./fn');

var _fn2 = _interopRequireDefault(_fn);

var _objectAssign = require('object-assign');

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