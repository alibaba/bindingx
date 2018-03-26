'use strict';

import {parse} from 'bindingx-parser';
import bindingx from '../src/';

const scrollView = document.getElementById('scrollView');
const block = document.getElementById('block');

const expressionOpacity = '';

bindingx.bind({
  anchor: scrollView,
  eventType: 'scroll',
  props: [
    {
      element: block,
      property: 'opacity',
      expression: {
        origin: expressionOpacity,
        transformed: parse(expressionOpacity)
      }
    }
  ]
}, (e) => {


});





