<template>
    <div :ref="'block'" class="box"></div>
</template>

<style scoped>
    .box {
        border-width: 2px;
        border-style: solid;
        border-color: #BBBBBB;
        width: 250px;
        height: 250px;
        margin-top: 50px;
        margin-left: 50px;
        background-color: #EEEEEE;
        margin-bottom:500px;
    }
</style>

<script>
  import {isWeex} from 'universal-env';
  import bindingx from '../../src/index';

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

  function getEl(el) {
    if (typeof el === 'string' || typeof el === 'number') return el;
    return isWeex ? el.ref : el instanceof HTMLElement ? el : el.$el;
  }


  export default {
//    name:'app',
    data () {
      return {
        x: 0,
        y: 0,
        easing: easings[0]
      }
    },
    mounted(){
      this.bindExp();
    },
    methods: {
      bindExp() {
        let blockEl = getEl(this.$refs.block);
        let begin = 0;
        let end = 300;
        let duration = 2000;
        let x1 = 0.1;
        let y1 = 0.57;
        let x2 = 0.1;
        let y2 = 1;
        let origin = this.easing === 'cubicBezier' ? `cubicBezier(t, ${begin}, ${end}, ${duration}, ${x1}, ${y1}, ${x2}, ${y2})` : `${this.easing}(t,${begin},${end},${duration})`;
        bindingx.bind({
          eventType: 'timing',
          exitExpression: {
            origin: `t>${duration}`
          },
          props: [
            {
              element: blockEl,
              property: 'transform.rotate',
              expression: {
                origin
              },
              config: {
                perspective: 1000,
                transformOrigin: 'center',
              }
            },
            {
              element: blockEl,
              property: 'transform.translateX',
              expression: {
                origin
              },
              config: {
                perspective: 1000,
                transformOrigin: 'center',
              }
            }
          ]
        }, (e) => {
          console.log(e);
        });
      }
    }
  }
</script>