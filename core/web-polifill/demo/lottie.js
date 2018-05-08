'use strict';

import {parse} from 'bindingx-parser';
import bindingx from '../src/';

let x = 0;
let y = 0;

const lottie = document.getElementById('lottie');

var animation = window.bodymovin.loadAnimation({
  container: lottie, // Required
  path: 'https://raw.githubusercontent.com/acton393/WeexLottie/master/examples/animations/9squares-AlBoardman.json', // Required
  renderer: 'svg', // Required
  autoplay: false, // Optional
})


let element = {
  setProgress: (progress) => {
    // console.log(animation.currentFrame/animation.totalFrames)
    animation.goToAndStop(animation.totalFrames * progress || 0, true)
  }
}

lottie.addEventListener('touchstart', () => {

  let expression = `min(1,max(0,x/750))`

  bindingx.bind({
    anchor: lottie,
    eventType: 'pan',
    props: [
      {
        element: element,
        property: 'lottie-progress',
        expression: {
          origin: expression,
          transformed: parse(expression)
        }
      }
    ]
  }, (e) => {

    if (e.state === 'end') {
      x += e.deltaX;
      y += e.deltaY;
      // console.log(bindingx.getComputedStyle(element));
    }
  });
});





