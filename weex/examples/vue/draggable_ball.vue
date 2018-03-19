<template>
<div class="container">
  <div ref="ball" class="ball" @touchstart="onPanStart" />
  <div class="desc_wrapper">
    <text class="desc">Draggable Ball</text>
  </div>
</div>
</template>

<style>
.container {
  flex: 1;
  background-color: #00B0FF;
}

.ball {
  width: 100;
  height: 100;
  border-radius: 50;
  background-color: #00ff00;
  margin: 48;
}

.desc {
  color: #ffffff;
  font-size: 35;
}

.desc_wrapper {
  position: absolute;
  bottom: 0;
  width: 750;
  height: 100;
  justify-content: center;
  align-items: center;
}
</style>

<script>
import Binding from 'weex-bindingx';

module.exports = {
  data: {
    x: 0,
    y: 0,
    isInAnimation: false,
    gesToken: 0
  },
  methods: {

    getEl: function(e) {
      return e.ref;
    },
    onPanStart: function(e) {
      if (this.isInAnimation === true) {
        console.log("we are in animation, drop pan gesture...")
        if (this.gesToken != 0) {
          Binding.unbind({
            eventType: 'pan',
            token: this.gesToken
          })
          this.gesToken = 0;
        }
        return
      }

      var my = this.getEl(this.$refs.ball);
      var expression_x_origin = `x+${this.x}`;
      var expression_y_origin = `y+${this.y}`;

      var gesTokenObj = Binding.bind({
        anchor: my,
        eventType: 'pan',
        props: [{
            element: my,
            property: 'transform.translateX',
            expression: expression_x_origin
          },
          {
            element: my,
            property: 'transform.translateY',
            expression: expression_y_origin
          }
        ]
      }, (e) => {
        if (e.state === 'end') {
          this.x += e.deltaX;
          this.y += e.deltaY;

          //anim
          this.bindTiming();
        }
      })

      this.gesToken = gesTokenObj.token;
    },
    bindTiming: function() {
      this.isInAnimation = true;
      var my = this.getEl(this.$refs.ball);

      //should equal with timing duration
      var exit_origin = "t>1000";

      var changed_x;
      var final_x;
      var final_y;
      if (this.x > (750 - 50 * 2) / 2) { //right
        changed_x = 550 - this.x;
        final_x = 550;
      } else {
        changed_x = 0 - this.x;
        final_x = 0;
      }

      var totalHeight = 1000;
      var changed_y;
      if (this.y > totalHeight / 2) {
        changed_y = totalHeight - 200 - this.y;
        final_y = totalHeight - 200;
      } else {
        changed_y = 0 - this.y;
        final_y = 0;
      }

      var expression_x = `easeOutElastic(t,${this.x},${changed_x},1000)`;
      var expression_y = `easeOutElastic(t,${this.y},${changed_y},1000)`;

      var result = Binding.bind({
        eventType: 'timing',
        exitExpression: exit_origin,
        props: [{
            element: my,
            property: 'transform.translateX',
            expression: expression_x
          },
          {
            element: my,
            property: 'transform.translateY',
            expression: expression_y
          }
        ]

      }, (e) => {
        if (e.state === 'end' ||
          e.state === 'exit') {
          // reset x & y
          this.x = final_x;
          this.y = final_y;
          this.isInAnimation = false;
        }
      })

    }
  }
}
</script>
