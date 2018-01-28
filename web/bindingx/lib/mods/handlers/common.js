'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _simpleLodash = require('simple-lodash');

var _simpleLodash2 = _interopRequireDefault(_simpleLodash);

var _utils = require('../utils');

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
module.exports = exports['default'];