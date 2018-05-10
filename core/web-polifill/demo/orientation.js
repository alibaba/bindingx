'use strict';

import {parse} from 'bindingx-parser';
import bindingx from '../src/';

let x = 0;
let y = 0;

const block = document.getElementById('block');


bindingx.bind({
  eventType: 'orientation',
  props: [
    {
      element: block,
      property: 'transform.translateX',
      expression: {
        origin: `x+0`,
        transformed: parse(`x+0`)
      }
    },
    {
      element: block,
      property: 'transform.translateY',
      expression: {
        origin: `y+0`,
        transformed: parse(`y+0`)
      }
    },

  ]
}, (e) => {

});





