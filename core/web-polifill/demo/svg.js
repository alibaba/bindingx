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

const polygon = document.getElementById('polygon');
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

  let expression = 'easeOutQuint(t,960,960,2000)';
  const exitExpression = 't>2000';

  let expression2 = 'easeOutQuint(t,1,-0.5,2000)';

  let expression3 = 'easeOutQuint(t,0,45,2000)';


  bindingx.bind({
    eventType: 'timing',
    exitExpression: {
      origin: exitExpression,
      transformed: parse(exitExpression)
    },
    props: [
      {
        element: polygon,
        property: 'svg-dashoffset',
        expression: {
          origin: expression,
          transformed: parse(expression)
        }
      },
      {
        element: polygon,
        property: 'svg-transform.scale',
        config: {
          // transformOrigin:'45px 45px'
        },
        expression: {
          origin: expression2,
          transformed: parse(expression2)
        }
      },
      {
        element: polygon,
        property: 'svg-transform.rotate',
        expression: {
          origin: expression3,
          transformed: parse(expression3)
        }
      },
      {
        element: polygon,
        property: 'svg-transform.translateX',
        expression: {
          origin: expression3,
          transformed: parse(expression3)
        }
      },
      {
        element: polygon,
        property: 'svg-transform.translateY',
        expression: {
          origin: expression3,
          transformed: parse(expression3)
        }
      },
    ]
  }, (e) => {
    console.log(e);
  });

}





