<template>
<div class="container">
  <div class="border">
    <div ref="my" class="box" @touchstart="touchStart">
      <div class="head">
        <div class="avatar" />
        <text class="username">HACKER</text>
      </div>

      <div class="content">
        <text class="desc">Google announced a new version of Nearby Connections for fully offline.high
                bandwidth peer to peer device communications.</text>
      </div>

      <div class="footer">
        <text class="action">SHARE</text>
        <text class="action" style="color:#7C4DFF">EXPLORE</text>
      </div>
    </div>
  </div>

  <div style="width:750;align-items:center;justify-content:center">
    <text style="font-size:40">Swipeable Card</text>
  </div>
</div>
</template>

<style>
.container {
  flex: 1;
  background-color: #eeeeee;

}

.border {
  height: 1000;
  padding-left: 35;
  padding-right: 35;
  padding-top: 100;
}

.box {
  width: 680;
  height: 450;
  background-color: #651FFF;
}

.head {
  background-color: #651FFF;
  width: 680;
  height: 120;
  flex-direction: row;
  align-items: center;
}

.content {
  width: 680;
  height: 240;
  background-color: #651FFF;
  padding-left: 24;
  padding-top: 24;
  padding-right: 24;
  box-sizing: border-box;
}

.footer {
  width: 680;
  height: 90;
  background-color: #fff;
  align-items: center;
  justify-content: flex-end;
  padding-right: 25;
  flex-direction: row;
  box-sizing: border-box;
}

.action {
  font-size: 35;
  padding-right: 20;
}

.desc {
  font-size: 32;
  color: #fff;
  padding-left: 24;
}


.avatar {
  width: 96;
  height: 96;
  border-radius: 48;
  background-color: #CDDC39;
  margin-left: 36;
  margin-right: 48;
}

.username {
  color: #fff;
  font-size: 32;
}
</style>

<script>
import Binding from 'weex-bindingx';


module.exports = {
  data() {
    return {
      x: 0,
      y: 0,
      isInAnimation: false,
      gesToken: 0,
      opacity: 1
    }
  },
  methods: {

    getEl: function(e) {
      return e.ref;
    },
    touchStart: function(e) {
      var self = this;
      if (this.isInAnimation === true) {
        console.log('we are in animation, drop pan gesture...');
        if (this.gesToken) {
          Binding.unbind({
            eventType: 'pan',
            token: self.gesToken
          });
          this.gesToken = undefined;
        }
        return;
      }

      var my = this.getEl(this.$refs.my);
      var translate_x_origin = 'x+0';

      var opacity_x_origin = '1-abs(x)/600';

      var gesTokenObj = Binding.bind({
        anchor: my,
        eventType: 'pan',
        props: [{
            element: my,
            property: 'transform.translateX',
            expression: translate_x_origin
          },
          {
            element: my,
            property: 'opacity',
            expression: opacity_x_origin
          }
        ]
      }, function(e) {
        if (e.state === 'end') {
          self.x += e.deltaX;
          self.y += e.deltaY;
          self.opacity = 1 - Math.abs(e.deltaX) / 600;

          // anim
          self.bindTiming();
        }
      });


      self.gesToken = gesTokenObj.token;
    },
    bindTiming: function() {
      this.isInAnimation = true;

      var my = this.getEl(this.$refs.my);
      var self = this;

      // should equal with timing duration
      var exit_origin = 't>1000';

      var changed_x;
      var final_x;

      var final_opacity;

      var translate_x_origin;

      var shouldDismiss = false;

      if (self.x >= -750 / 2 && self.x <= 750 / 2) {

        shouldDismiss = false;
        final_x = 0;
        changed_x = 0 - self.x;
        final_opacity = 1;
        translate_x_origin = `easeOutElastic(t,${self.x},${changed_x},1000)`
      } else if (self.x < -750 / 2) {
        shouldDismiss = true;
        final_x = -750;
        changed_x = -750 - self.x;
        final_opacity = 0;
        translate_x_origin = `easeOutExpo(t,${self.x},${changed_x},1000)`
      } else { // x > 750/2
        final_x = 750;
        shouldDismiss = true;
        changed_x = 750 - self.x;
        final_opacity = 0;
        translate_x_origin = `easeOutExpo(t,${self.x},${changed_x},1000)`
      }

      var changed_opacity = final_opacity - self.opacity;
      var opacity_origin = `linear(t,${self.opacity},${changed_opacity},1000)`;

      var result = Binding.bind({
        eventType: 'timing',
        exitExpression: exit_origin,
        props: [{
            element: my,
            property: 'transform.translateX',
            expression: translate_x_origin
          },
          {
            element: my,
            property: 'opacity',
            expression: opacity_origin
          }
        ]

      }, function(e) {
        if (e.state === 'end' ||
          e.state === 'exit') {
          // reset x
          self.x = final_x;
          self.isInAnimation = false;

          if (shouldDismiss) {
            // remove card from hierarchy
          }
        }
      });
    }
  }
}
</script>
