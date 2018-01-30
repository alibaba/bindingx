'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _vector = require('../vector3');

var _vector2 = _interopRequireDefault(_vector);

var _orientation_controls = require('../orientation_controls');

var _orientation_controls2 = _interopRequireDefault(_orientation_controls);

var _math = require('../math');

var _math2 = _interopRequireDefault(_math);

var _raf = require('../raf');

var _common = require('./common');

var _common2 = _interopRequireDefault(_common);

var _objectAssign = require('object-assign');

var _objectAssign2 = _interopRequireDefault(_objectAssign);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

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
      this.timer = (0, _raf.raf)(function () {
        _this2.run();
      });
    }
  }, {
    key: 'destroy',
    value: function destroy() {
      if (this.timer) {
        (0, _raf.cancelRAF)(this.timer);
        this.timer = null;
      }
    }
  }]);

  return OrientationHandler;
}(_common2.default);

exports.default = OrientationHandler;
module.exports = exports['default'];