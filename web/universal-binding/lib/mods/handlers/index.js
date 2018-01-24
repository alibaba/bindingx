'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ScrollHandler = exports.TimingHandler = exports.OrientationHandler = exports.PanHandler = undefined;

var _pan = require('./pan');

var _pan2 = _interopRequireDefault(_pan);

var _orientation = require('./orientation');

var _orientation2 = _interopRequireDefault(_orientation);

var _timing = require('./timing');

var _timing2 = _interopRequireDefault(_timing);

var _scroll = require('./scroll');

var _scroll2 = _interopRequireDefault(_scroll);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

exports.PanHandler = _pan2.default;
exports.OrientationHandler = _orientation2.default;
exports.TimingHandler = _timing2.default;
exports.ScrollHandler = _scroll2.default;
exports['default'] = module.exports;
exports.default = module.exports;