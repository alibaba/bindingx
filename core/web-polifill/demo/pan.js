'use strict';

import {parse} from 'bindingx-parser';
import bindingx from '../src/';

let x = 0;
let y = 0;

const block = document.getElementById('block');

block.addEventListener('touchstart', () => {
  bindingx.bind({
    anchor: block,
    eventType: 'pan',
    props: [
      {
        element: block,
        property: 'transform.translateX',
        expression: {
          origin: `x+${x}`,
          transformed: parse(`x+${x}`)
        }
      },
      {
        element: block,
        property: 'transform.translateY',
        expression: {
          origin: `y+${y}`,
          transformed: parse(`y+${y}`)
        }
      }
    ]
  }, (e) => {

    if (e.state === 'end') {
      x += e.deltaX;
      y += e.deltaY;
    }
  });
});





