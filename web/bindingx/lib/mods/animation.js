'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _events = require('events');

var _events2 = _interopRequireDefault(_events);

var _easing = require('./easing');

var _easing2 = _interopRequireDefault(_easing);

var _bezier = require('./bezier');

var _bezier2 = _interopRequireDefault(_bezier);

var _raf = require('./raf');

var _objectAssign = require('object-assign');

var _objectAssign2 = _interopRequireDefault(_objectAssign);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

var Animation = function (_EventEmitter) {
  _inherits(Animation, _EventEmitter);

  function Animation(cfg) {
    _classCallCheck(this, Animation);

    var _this = _possibleConstructorReturn(this, (Animation.__proto__ || Object.getPrototypeOf(Animation)).call(this, cfg));

    _this.cfg = null;

    _this.cfg = (0, _objectAssign2.default)({
      easing: 'linear',
      duration: Infinity
    }, cfg);
    return _this;
  }

  _createClass(Animation, [{
    key: 'run',
    value: function run() {
      var duration = this.cfg.duration;
      if (duration <= Animation.MIN_DURATION) {
        this.isfinished = true;
        this.emit('run', {
          percent: 1
        });
        this.emit('end', {
          percent: 1
        });
      }
      if (this.isfinished) return;
      this._hasFinishedPercent = this._stop && this._stop.percent || 0;
      this._stop = null;
      this.start = Date.now();
      this.percent = 0;
      this.emit('start', {
        percent: 0
      });
      // epsilon determines the precision of the solved values
      var epsilon = 1000 / 60 / duration / 4;
      var b = this.cfg.bezierArgs;
      this.easingFn = b && b.length === 4 ? (0, _bezier2.default)(b[0], b[1], b[2], b[3], epsilon) : _easing2.default[this.cfg.easing];
      this._run();
    }
  }, {
    key: '_run',
    value: function _run() {
      var _this2 = this;

      (0, _raf.cancelRAF)(this._raf);
      this._raf = (0, _raf.raf)(function () {
        _this2.now = Date.now();
        _this2.t = _this2.now - _this2.start;
        _this2.duration = _this2.now - _this2.start >= _this2.cfg.duration ? _this2.cfg.duration : _this2.now - _this2.start;
        _this2.progress = _this2.easingFn(_this2.duration / _this2.cfg.duration);
        _this2.percent = _this2.duration / _this2.cfg.duration + _this2._hasFinishedPercent;
        if (_this2.percent >= 1 || _this2._stop) {
          _this2.percent = _this2._stop && _this2._stop.percent ? _this2._stop.percent : 1;
          _this2.duration = _this2._stop && _this2._stop.duration ? _this2._stop.duration : _this2.duration;
          var param = {
            percent: _this2.percent,
            t: _this2.t
          };
          _this2.emit('run', {
            percent: _this2.progress,
            originPercent: _this2.percent,
            t: _this2.t
          });
          _this2.emit('stop', param);
          if (_this2.percent >= 1) {
            _this2.isfinished = true;
            _this2.emit('end', {
              percent: 1,
              t: _this2.t
            });
          }
          return;
        }
        _this2.emit('run', {
          percent: _this2.progress,
          originPercent: _this2.percent,
          t: _this2.t
        });
        _this2._run();
      });
    }
  }, {
    key: 'stop',
    value: function stop() {
      this._stop = {
        percent: this.percent,
        now: this.now
      };
      (0, _raf.cancelRAF)(this._raf);
    }
  }]);

  return Animation;
}(_events2.default);

Animation.MIN_DURATION = 1;
exports.default = Animation;
;
module.exports = exports['default'];