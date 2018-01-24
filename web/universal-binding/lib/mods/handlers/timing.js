'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _simpleLodash = require('simple-lodash');

var _simpleLodash2 = _interopRequireDefault(_simpleLodash);

var _fn = require('../fn');

var _fn2 = _interopRequireDefault(_fn);

var _animation = require('../animation');

var _animation2 = _interopRequireDefault(_animation);

var _expression = require('../expression');

var _expression2 = _interopRequireDefault(_expression);

var _common = require('./common');

var _common2 = _interopRequireDefault(_common);

var _easing = require('../easing');

var _easing2 = _interopRequireDefault(_easing);

var _objectAssign = require('object-assign');

var _objectAssign2 = _interopRequireDefault(_objectAssign);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

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
    } else if (easing && _easing2.default[easing]) {
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

  // TODO timing单例 临时先解决单一callback问题


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
        var animation = new _animation2.default({
          duration: duration,
          easing: easing,
          bezierArgs: easing === 'cubicBezier' ? [x1, y1, x2, y2] : undefined
        });
        animation.on('start', function () {
          if (!_this2.isStart) {
            _this2.binding.callback({ state: 'start', t: 0 });
            _this2.isStart = true;
          }
        });
        animation.on('run', function (e) {
          // 满足退出条件停止动画
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
        });
        animation.on('stop', function (e) {
          _this2.callback({ state: 'exit', t: e.t - 1000 / 60 });
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
module.exports = exports['default'];