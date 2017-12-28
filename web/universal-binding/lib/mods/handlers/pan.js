'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _pan = require('../pan');

var _pan2 = _interopRequireDefault(_pan);

var _common = require('./common');

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
module.exports = exports['default'];