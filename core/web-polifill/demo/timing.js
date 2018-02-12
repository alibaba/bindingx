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
  options += `<option>${v}</option>`
});

easing.innerHTML = options;

easing.addEventListener('change', (e) => {
  animate(e.target.value);
});

animate('easeInQuad');


function animate(timingFunc) {

  const begin = 0;
  const end = 300;
  const duration = 1000;
  const expression = `${timingFunc}(t,${begin},${end},${duration})`;

  bindingx.bind({
    eventType: 'timing',
    props: [
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

  });

}





