<template>
<div class="app">
  <list class="list" ref="list">
    <cell>
      <div class="header">
        <div class="header_bg_wrapper">
          <image class="header_bg" src="https://gw.alicdn.com/tfs/TB1PDWiXSBYBeNjy0FeXXbnmFXa-1024-576.jpg" />
        </div>
        <div class="header_card">
          <div class="card_wrapper">
            <div class="card_content" style="background-color:#ffffff">
            </div>
          </div>
        </div>
      </div>
    </cell>

    <cell v-for="c in cells">
      <div class="card_wrapper">
        <div class="card_content" />
      </div>
    </cell>
  </list>

  <div class="app_bar" ref="app_bar">
    <div class="app_bar_bg" ref="app_bar_bg" />

    <div class="nav_wrapper">
      <image class="nav_back" src="https://gw.alicdn.com/tfs/TB1ufOrXTtYBeNjy1XdXXXXyVXa-128-128.png" />
      <text class="nav_title">Title</text>
    </div>

    <div class="tab_wrapper_container">
      <div class="tab_wrapper" v-for="tab in fake_tabs">
        <text class="tab">{{tab}}</text>
      </div>
    </div>
  </div>

</div>
</template>

<style>
.app {
  flex: 1;
  justify-content: center;
  align-items: center;
  background-color: #E0E0E0;
}

.list {
  flex-direction: column;
  overflow: hidden;
  width: 750;
  background-color: #f2f3f4;
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
}

.header_bg {
  width: 750;
  height: 500;
}

.header_bg_wrapper {
  width: 750;
  height: 600;
  position: absolute;
  top: 0;
}

.header {
  width: 750;
  height: 600;
}

.header_card {
  margin-top: 330;
}

.app_bar {
  width: 750;
  height: 250;
  position: absolute;
  top: 0;
}

.app_bar_bg {
  width: 750;
  height: 200;
  background-color: #03A9F4;
  position: absolute;
  top: 0;
  opacity: 0;
}

.card_wrapper {
  width: 750;
  height: 250;
  margin-top: 20;
  justify-content: center;
  align-items: center;
  background-color: transparent;
}

.card_content {
  border-radius: 25;
  width: 710;
  height: 250;
  background-color: #ffffff;

}

.nav_wrapper {
  width: 750;
  height: 100;
  align-items: center;
  background-color: transparent;
  flex-direction: row;
}

.nav_title {
  font-size: 35;
  font-weight: bold;
  margin-left: 250;
  color: #ffffff;
}

.nav_back {
  width: 40;
  margin-left: 20;
  height: 40;
}

.tab_wrapper_container {
  width: 750;
  height: 100;
  align-items: center;
  background-color: transparent;
  flex-direction: row;
}

.tab_wrapper {
  width: 125;
  height: 100;
  align-items: center;
  justify-content: center;
}

.tab {
  font-size: 25;
  color: #ffffff;
}
</style>

<script>
import Binding from 'weex-bindingx';

module.exports = {
  data() {
    return {
      fake_tabs: [
        'Tab1',
        'Tab2',
        'Tab3',
        'Tab4',
        'Tab5',
        'Tab6'
      ],
      cells: [1, 2, 3, 4, 5, 6, 7, 8, 9]
    }
  },
  mounted() {

    let self = this;
    let list = this.getEl(self.$refs.list);
    let target_app_bar = this.getEl(self.$refs.app_bar);
    let target_bg = this.getEl(self.$refs.app_bar_bg);

    Binding.bind({
      eventType: 'scroll',
      anchor: list,
      props: [{
          element: target_bg,
          property: 'opacity',
          expression: {
            origin: 'min(100,y)/100'
          }
        },
        {
          element: target_app_bar,
          property: 'transform.translateY',
          expression: {
            origin: 'y<100?0:(0-min(y-100,100))'
          }
        }
      ]
    });
  },
  methods: {

    getEl: function(e) {
      return e.ref;
    }

  }
}
</script>
