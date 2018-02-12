## bindingx-web-polyfill  


## Install & Use

```

import bindingx from 'bindingx-web-polyfill';

var blockEl = document.getElementById('block');

var x = 0;

function bind() {
  bindingx.bind({
    anchor: blockEl,
    eventType: 'pan',
    options: {
      touchAction: 'pan-x'
    },
    props: [
      {
        element: blockEl,
        property: 'transform.translateX',
        expression: {
          transformed: `{\"type\":\"+\",\"children\":[{\"type\":\"Identifier\",\"value\":\"x\"},{\"type\":\"NumericLiteral\",\"value\":${x}}]}`
        }
      }
    ]
  }, (e) => {
    if (e.state === 'end') {
      x += e.deltaX;
    }
  });
}

bind();

blockEl.addEventListener('touchstart', () => {
  bind();
});



```





