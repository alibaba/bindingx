<template>
    <div class="container">
        <list class="scroller" :ref="'list'" @appear="onappear">
            <cell class="cell" v-for="(row,index) in listData" :key="index">
                <text class="txt">{{row.name1}}</text>
            </cell>
        </list>
        <div :ref="'bottom'" class="bottom">
            <text class="bottom-txt">bottom bar</text>
        </div>
        <div :ref="'top'" class="top">
            <text class="top-txt">top bar</text>
        </div>
        <div :ref="'top2'" class="top2">
            <text class="top-txt">tab bar</text>
        </div>
    </div>
</template>

<style scoped>
    .container {
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
    }

    .scroller {
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
    }

    .bottom {
        position: absolute;
        width: 750px;
        bottom: 0;
        height: 100px;
        background-color: #0000FF;
        text-align: center
    }

    .bottom-txt {
        color: #FFFFFF;
        text-align: center;
        line-height: 100px;
    }

    .top {
        width: 750px;
        height: 100px;
        background-color: #FF0000;
        position: absolute;
        top: 0;
        z-index: 1000;
    }

    .top2 {
        background-color: #008000;
        top: 100px;
        width: 750px;
        height: 100px;
        position: absolute;
        z-index: 1000;
    }

    .top-txt {
        color: #FFFFFF;
        text-align: center;
        line-height: 100px;
    }

    .cell {
        height: 200px;
    }

    .txt {
        line-height: 40px;
        text-align: center;
    }
</style>

<script>
  import {isWeex} from 'universal-env';
  import bindingx from '../../src/index';

  function getEl(el) {
    if (typeof el === 'string' || typeof el === 'number') return el;
    return isWeex ? el.ref : el instanceof HTMLElement ? el : el.$el;
  }

  let listData = [
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
    {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  ];

  function getTranslateY(el) {
    let style = bindingx.getComputedStyle(el);
    return style.translateY;
  }

  export default {
    data () {
      return {
        listData,
        translateY1: 0,
        translateY2: 0
      }
    },
    mounted () {
      setTimeout(() => {
        this.bind();
      }, 100)

    },
    methods: {
      bind () {
        let anchor = getEl(this.$refs.list);
        let topEl = getEl(this.$refs.top);
        let bottomEl = getEl(this.$refs.bottom);
        let topEl2 = getEl(this.$refs.top2);
        let top_origin = `max(0-100,min(0-abs(${this.translateY1})-tdy,0))`;
        let bottom_origin = `max(0,min(${this.translateY2}+tdy,100))`;


        let res = bindingx.bind({
          anchor: anchor,
          eventType: 'scroll',
          props: [
            {
              element: topEl2,
              property: 'transform.translateY',
              expression: top_origin
            },
            {
              element: topEl,
              property: 'opacity',
              expression: `1-(max(0,min(${this.translateY2}+tdy,100))/100)`
            },
            {
              element: topEl,
              property: 'transform.translateY',
              expression: top_origin
            },
            {
              element: bottomEl,
              property: 'transform.translateY',
              expression: bottom_origin
            }
          ]
        }, (e) => {
          if (e.state === 'turn') {
            this.translateY1 = getTranslateY(topEl);
            this.translateY2 = getTranslateY(bottomEl);
            this.bind();
          }
        });

        this.token = res && res.token;
      }

    }
  }
</script>