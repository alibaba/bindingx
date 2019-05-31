<template>
<div class="container">

  <div class="menu">
    <div class="item_container" style="opacity:0" ref="menu1">
      <text class="item">Menu1</text>
    </div>
    <div class="item_container" style="opacity:0" ref="menu2">
      <text class="item">Menu2</text>
    </div>
    <div class="item_container" style="opacity:0" ref="menu3">
      <text class="item">Menu3</text>
    </div>
    <div class="item_container" style="opacity:0" ref="menu4">
      <text class="item">Menu4</text>
    </div>
    <div class="item_container" style="opacity:0" ref="menu5">
      <text class="item">Menu5</text>
    </div>
  </div>

  <div class="main" ref="main" @touchstart="onTouchStart">
    <div class="block" />
    <div class="block" style="margin-top:15" />
    <div class="block" style="margin-top:15" />
    <div class="block" style="margin-top:15" />
  </div>

</div>
</template>

<style>
.container {
  flex: 1;
  background-color: #03A9F4;
}

.main {
  flex: 1;
  background-color: #ffffff;
}

.menu {
  width: 375;
  position: absolute;
  top: 0;
  bottom: 0;
  justify-content: center;
  align-items: center;
  margin-left: 35;
  margin-top: 100;
}

.item {
  color: #ffffff;
  font-size: 40;
}

.item_container {
  height: 60;
  width: 375;
  justify-content: center;
  align-items: center;
  margin-top: 35;
}

.block {
  width: 720;
  height: 350;
  background-color: #FF9800;
  margin: 15;
  border-radius: 15;
}
</style>

<script>
import Binding from 'weex-bindingx';

module.exports = {
  data() {
    return {
      _is_expanded: false,
      _menu_animation_flag: false,
      _opacity: 0
    }
  },
  methods: {

    getEl: function(e) {
      return e.ref;
    },

    onTouchStart: function() {
      if (!this._is_expanded) {
        this.expandWithDrag();
      } else {
        this.collapseWithDrag();
      }
    },
    expandWithDrag: function() {
      var self = this;
      let page = this.getEl(this.$refs.main);
      let result = Binding.bind({
        eventType: 'pan',
        anchor: page,
        props: [{
            element: page,
            property: 'transform.translateX',
            expression: {
              origin: 'min(375,max(0,x))'
            }
          },
          {
            element: page,
            property: 'transform.scaleX',
            expression: {
              origin: '1-min(375,max(0,x))/375*0.2' //1-->0.8
            }
          },
          {
            element: page,
            property: 'transform.scaleY',
            expression: {
              origin: '1-min(375,max(0,x))/375*0.2'
            }
          }
        ]
      }, function(e) {
        if (e.state === 'end' && !self._is_expanded) {
          let offset = e.deltaX;
          if (offset < 375 / 2 && offset > 0) {
            self.collapseWithAnimation();
          } else if (offset >= 375 / 2) {
            self.expandWithAnimation();
          }

          if (result) {
            Binding.unbind({
              token: result.token,
              eventType: 'pan'
            });
          }
        }
      });
    },

    collapseWithDrag: function() {
      var self = this;
      let page = this.getEl(this.$refs.main);
      let result = Binding.bind({
        eventType: 'pan',
        anchor: page,
        props: [{
            element: page,
            property: 'transform.translateX',
            expression: {
              origin: '375+min(0,max(0-375,x))'
            }
          },
          {
            element: page,
            property: 'transform.scaleX',
            expression: {
              origin: '0.8-min(0,max(0-375,x))/375*0.2'
            }
          },
          {
            element: page,
            property: 'transform.scaleY',
            expression: {
              origin: '0.8-min(0,max(0-375,x))/375*0.2'
            }
          }
        ]
      }, function(e) {
        if (e.state === 'end' && self._is_expanded) {
          let offset = Math.abs(e.deltaX);
          if (offset < 375 / 2 && offset > 0) {
            self.expandWithAnimation();
          } else if (offset >= 375 / 2 && offset <= 375) {
            self.collapseWithAnimation();
          }

          if (result) {
            Binding.unbind({
              token: result.token,
              eventType: 'pan'
            });
          }
        }
      });

    },
    collapseWithAnimation: function() {
      this._is_expanded = false;
      this._menu_animation_flag = false;
      let page = this.getEl(this.$refs.main);;

      let offset = Binding.getComputedStyle(page).translateX;
      let duration = 200; //ms
      let scale = Binding.getComputedStyle(page).scaleX;
      let self = this;

      Binding.bind({
        eventType: 'timing',
        exitExpression: {
          origin: `t>${duration}`
        },
        props: [{
            element: page,
            property: 'transform.translateX',
            expression: {
              origin: `easeOutCubic(t,${offset},${0-offset},${duration})`
            }
          },
          {
            element: page,
            property: 'transform.scaleX',
            expression: {
              origin: `easeOutCubic(t,${scale},${1-scale},${duration})`
            }
          },
          {
            element: page,
            property: 'transform.scaleY',
            expression: {
              origin: `easeOutCubic(t,${scale},${1-scale},${duration})`
            }
          }

        ]
      }, function(e) {})
    },
    expandWithAnimation: function() {
      let self = this;
      this._is_expanded = true;
      let page = this.getEl(this.$refs.main);
      let offset = Binding.getComputedStyle(page).translateX;
      let duration = 200; //ms
      let scale = Binding.getComputedStyle(page).scaleX;

      Binding.bind({
        eventType: 'timing',
        exitExpression: {
          origin: `t>${duration}`
        },
        props: [{
            element: page,
            property: 'transform.translateX',
            expression: {
              origin: `easeOutCubic(t,${offset},${375-offset},${duration})`
            }
          },
          {
            element: page,
            property: 'transform.scaleX',
            expression: {
              origin: `easeOutCubic(t,${scale},${0.8-scale},${duration})` //scale-->1
            }
          },
          {
            element: page,
            property: 'transform.scaleY',
            expression: {
              origin: `easeOutCubic(t,${scale},${0.8-scale},${duration})` //scale-->1
            }
          }
        ]
      }, function(e) {
        if (e.state === 'exit' && !self._menu_animation_flag) {
          self.showMenu();
          self._menu_animation_flag = true;
        }
      })
    },
    showMenu: function() {
      let menu1 = this.getEl(this.$refs.menu1);
      let menu2 = this.getEl(this.$refs.menu2);
      let menu3 = this.getEl(this.$refs.menu3);
      let menu4 = this.getEl(this.$refs.menu4);
      let menu5 = this.getEl(this.$refs.menu5);

      let duration = 1000; //ms
      let parallax = 50;

      Binding.bind({
        eventType: 'timing',
        exitExpression: {
          origin: `t>${duration*5}`
        },
        props: [{
            element: menu1,
            property: 'transform.translateY',
            expression: {
              origin: `easeOutElastic(t,0,0-100,${duration})`
            }
          },
          {
            element: menu2,
            property: 'transform.translateY',
            expression: {
              origin: `t<${parallax}?0:easeOutElastic(t,0,0-100,${duration-parallax})`
            }
          },
          {
            element: menu3,
            property: 'transform.translateY',
            expression: {
              origin: `t<${parallax*2}?0:easeOutElastic(t,0,0-100,${duration-parallax*2})`
            }
          },
          {
            element: menu4,
            property: 'transform.translateY',
            expression: {
              origin: `t<${parallax*3}?0:easeOutElastic(t,0,0-100,${duration-parallax*3})`
            }
          },
          {
            element: menu5,
            property: 'transform.translateY',
            expression: {
              origin: `t<${parallax*4}?0:easeOutElastic(t,0,0-100,${duration-parallax*4})`
            }
          },
          // opacity
          {
            element: menu1,
            property: 'opacity',
            expression: {
              origin: `easeOutElastic(t,0,1,${duration})`
            }
          },
          {
            element: menu2,
            property: 'opacity',
            expression: {
              origin: `t<${parallax}?0:easeOutElastic(t,0,1,${duration-parallax})`
            }
          },
          {
            element: menu3,
            property: 'opacity',
            expression: {
              origin: `t<${parallax*2}?0:easeOutElastic(t,0,1,${duration-parallax*2})`
            }
          },
          {
            element: menu4,
            property: 'opacity',
            expression: {
              origin: `t<${parallax*3}?0:easeOutElastic(t,0,1,${duration-parallax*3})`
            }
          },
          {
            element: menu5,
            property: 'opacity',
            expression: {
              origin: `t<${parallax*4}?0:easeOutElastic(t,0,1,${duration-parallax*4})`
            }
          }
        ]
      });
    }

  }
}
</script>
