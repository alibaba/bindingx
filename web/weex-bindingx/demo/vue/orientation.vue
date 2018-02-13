<template>
    <div class="container" >
        <div :ref="'box'" class="box" @touchstart="ontouchstart"  @appear="onappear"></div>
    </div>
</template>

<style scoped>
    .container{
        flex:1;
    }
    .box {
        border-width: 2px;
        border-style: solid;
        border-color: #BBBBBB;
        width: 250px;
        height: 250px;
        margin-top: 250px;
        margin-left: 250px;
        background-color: #EEEEEE;
        margin-bottom:500px;
    }
</style>

<script>
  import {isWeex} from 'universal-env';
  import bindingx from '../../src/index';

  function getEl(el) {
    if (typeof el === 'string' || typeof el === 'number') return el;
    return isWeex ? el.ref : el instanceof HTMLElement ? el : el.$el;
  }


  export default {
    data () {
      return {
        x: 0,
        y: 0,
        flag: 0
      }
    },
    mounted(){
      this.bind();
    },
    methods: {
      bind () {
        let box = getEl(this.$refs.box);
        bindingx.bind({
          eventType: 'orientation',
          props: [
            {
              element: box,
              property: 'transform.translateX',
              expression: 'x+0'
            },
            {
              element: box,
              property: 'transform.translateY',
              expression: {
                origin: 'y+0'
              }
            }
          ]
        }, (e) => {
          console.log(e);
        });
      },
      ontouchstart (event) {
        this.bind();
      }
    }
  }
</script>