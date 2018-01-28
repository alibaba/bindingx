'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _common = require('./common');

var _common2 = _interopRequireDefault(_common);

var _utils = require('../utils');

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
module.exports = exports['default'];