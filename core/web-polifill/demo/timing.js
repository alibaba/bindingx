'use strict';

import {parse} from 'bindingx-parser';
import bindingx from '../src/';

const easings = [
  'easeInQuad', 'easeOutQuad', 'easeInCubic', 'easeOutCubic',
  'easeInOutCubic', 'easeInQuart', 'easeOutQuart', 'easeInOutQuart',
  'easeInQuint', 'easeOutQuint', 'easeInOutQuint', 'easeInSine',
  'easeOutSine', 'easeInOutSine', 'easeInExpo', 'easeOutExpo',
  'easeInOutExpo', 'easeInCirc', 'easeOutCirc', 'easeInOutCirc',
  'easeInElastic', 'easeOutElastic', 'easeInOutElastic', 'easeInBack',
  'easeOutBack', 'easeInOutBack', 'easeInBounce', 'easeOutBounce',
  'easeInOutBounce', 'cubicBezier'
];

const block = document.getElementById('block');
const easing = document.getElementById('easing');

let options = '';
easings.forEach((v) => {
  options += `<option>${v}</option>`;
});

easing.innerHTML = options;

easing.addEventListener('change', (e) => {
  animate(e.target.value);
});

animate('easeInQuad');

// animate('linear');
function animate(timingFunc) {

  const begin = 0;
  const offset = 300;
  const duration = 2000;
  const delay = 1000;
  let expression = `t>${delay}?${timingFunc}(t-1000,${begin},${offset},${duration}):0`;
  // expression = `${timingFunc}(t,${begin},${offset},${duration})`;
  const exitExpression = `t>${duration + delay}`;
  const x1 = 0.1;
  const y1 = 0.57;
  const x2 = 0.1;
  const y2 = 1;
  // expression = `t>${delay}?cubicBezier(t-1000, ${begin}, ${offset}, ${duration}, ${x1}, ${y1}, ${x2}, ${y2}):0`


  const borderExpression = 'linear(t,0,187.5,2000)';

  bindingx.bind({
    eventType: 'timing',
    exitExpression: {
      origin: exitExpression,
      transformed: parse(exitExpression)
    },
    props: [
      // {
      //   element: block,
      //   property: 'border-radius',
      //   expression: {
      //     origin: borderExpression,
      //     transformed: parse(borderExpression)
      //   }
      // },
      {
        element: block,
        property: 'border-top-left-radius',
        expression: {
          origin: borderExpression,
          transformed: parse(borderExpression)
        }
      },
      {
        element: block,
        property: 'border-top-right-radius',
        expression: {
          origin: borderExpression,
          transformed: parse(borderExpression)
        }
      },
      // {
      //   element: block,
      //   property: 'border-bottom-right-radius',
      //   expression: {
      //     origin: borderExpression,
      //     transformed: parse(borderExpression)
      //   }
      // },
      // {
      //   element: block,
      //   property: 'border-bottom-left-radius',
      //   expression: {
      //     origin: borderExpression,
      //     transformed: parse(borderExpression)
      //   }
      // },
      {
        element: block,
        property: 'transform.translateX',
        expression: {
          origin: expression,
          transformed: parse(expression)
        }
      }
    ]
  }, (e) => {
    console.log(e);
  });

}

setInterval(() => {
  console.log(bindingx.getComputedStyle(block)['border-top-left-radius'], bindingx.getComputedStyle(block)['border-top-right-radius']);
}, 100);





