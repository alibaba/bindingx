'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _simpleLodash = require('simple-lodash');

var _simpleLodash2 = _interopRequireDefault(_simpleLodash);

var _objectAssign = require('object-assign');

var _objectAssign2 = _interopRequireDefault(_objectAssign);

var _utils = require('./utils');

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
module.exports = exports['default'];