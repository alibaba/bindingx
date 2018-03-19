<template>
<div class="container">

  <div class="btn" style="background-color:#6A1B9A" ref="b1" @click="clickBtn">
    <text class="text" ref="main_image">A</text>
  </div>

  <div class="btn" style="background-color:#0277BD" ref="b2" @click="clickBtn">
    <text class="text" ref="main_image">B</text>
  </div>

  <div class="btn" style="background-color:#FF9800" ref="b3" @click="clickBtn">
    <text class="text" ref="main_image">C</text>
  </div>

  <div class="btn" style="background-color:#009688" ref="b4" @click="clickBtn">
    <text class="text" ref="main_image">D</text>
  </div>

  <div class="btn" style="background-color:#795548" ref="b5" @click="clickBtn">
    <text class="text" ref="main_image">E</text>
  </div>

  <div class="btn" ref="main_btn" @click="clickBtn">
    <image class="image" ref="main_image" src="https://gw.alicdn.com/tfs/TB1PZ25antYBeNjy1XdXXXXyVXa-128-128.png" />
  </div>


  <div class="desc">
    <text class="desc_text">
        A Menu with a circular layout
      </text>
  </div>
</div>
</template>

<style>
.container {
  flex: 1;
}

.image {
  width: 60;
  height: 60;
}

.text {
  color: #ffffff;
  font-size: 30;
}

.btn {
  width: 100;
  height: 100;
  background-color: #ff0000;
  align-items: center;
  justify-content: center;
  position: absolute;
  border-radius: 50;
  left: 350;
  bottom: 300;
}

.desc {
  width: 750;
  height: 100;
  position: absolute;
  top: 0;
  justify-content: center;
  align-items: center;
}

.desc_text {
  color: #000000;
  font-size: 30;
}
</style>

<script>
import Binding from 'weex-bindingx';

module.exports = {
  data: {
    isExpanded: false
  },
  methods: {

    getEl: function(e) {
      return e.ref;
    },

    collapse: function() {
      let main_btn = this.getEl(this.$refs.main_btn);
      let main_image = this.getEl(this.$refs.main_image);
      let b1 = this.getEl(this.$refs.b1);
      let b2 = this.getEl(this.$refs.b2);
      let b3 = this.getEl(this.$refs.b3);
      let b4 = this.getEl(this.$refs.b4);
      let b5 = this.getEl(this.$refs.b5);

      let radius = 150;
      let duration = 400;

      Binding.bind({
        eventType: 'timing',
        exitExpression: {
          origin: `t>${duration}`
        },
        props: [{
            element: main_image,
            property: 'transform.rotateZ',
            expression: {
              origin: 'easeOutQuint(t,45,0-45,800)'
            }
          },
          {
            element: main_btn,
            property: 'background-color',
            expression: {
              origin: "evaluateColor('#607D8B','#ff0000',min(t,800)/800)"
            }
          }
        ]

      });


      Binding.bind({
        eventType: 'timing',
        exitExpression: {
          origin: 't>800'
        },
        props: [{
            element: b1,
            property: 'transform.translateY',
            expression: {
              origin: `easeOutCubic(t,sin(2*PI*18/360)*${0-radius},sin(2*PI*18/360)*${radius},${duration})`
            }
          },
          {
            element: b1,
            property: 'transform.translateX',
            expression: {
              origin: `easeOutCubic(t,cos(2*PI*18/360)*${0-radius},cos(2*PI*18/360)*${radius},${duration})`
            }
          },
          // b2
          {
            element: b2,
            property: 'transform.translateY',
            expression: {
              origin: `easeOutCubic(t,${0-radius},${radius},${duration})`
            }
          },
          // b3
          {
            element: b3,
            property: 'transform.translateY',
            expression: {
              origin: `easeOutCubic(t,sin(2*PI*18/360)*${0-radius},sin(2*PI*18/360)*${radius},${duration})`
            }
          },
          {
            element: b3,
            property: 'transform.translateX',
            expression: {
              origin: `easeOutCubic(t,cos(2*PI*18/360)*${radius},cos(2*PI*18/360)*${0-radius},${duration})`
            }
          },
          // b4
          {
            element: b4,
            property: 'transform.translateY',
            expression: {
              origin: `easeOutCubic(t,sin(2*PI*54/360)*${radius},sin(2*PI*54/360)*${0-radius},${duration})`
            }
          },
          {
            element: b4,
            property: 'transform.translateX',
            expression: {
              origin: `easeOutCubic(t,cos(2*PI*54/360)*${radius},cos(2*PI*54/360)*${0-radius},${duration})`
            }
          },
          // b5
          {
            element: b5,
            property: 'transform.translateY',
            expression: {
              origin: `easeOutCubic(t,sin(2*PI*54/360)*${radius},sin(2*PI*54/360)*${0-radius},${duration})`
            }
          },
          {
            element: b5,
            property: 'transform.translateX',
            expression: {
              origin: `easeOutCubic(t,cos(2*PI*54/360)*${0-radius},cos(2*PI*54/360)*${radius},${duration})`
            }
          },

        ]
      })
    },
    expand: function() {
      let main_btn = this.getEl(this.$refs.main_btn);
      let main_image = this.getEl(this.$refs.main_image);
      let b1 = this.getEl(this.$refs.b1);
      let b2 = this.getEl(this.$refs.b2);
      let b3 = this.getEl(this.$refs.b3);
      let b4 = this.getEl(this.$refs.b4);
      let b5 = this.getEl(this.$refs.b5);

      let radius = 150;
      let duration = 500;

      Binding.bind({
        eventType: 'timing',
        exitExpression: {
          origin: 't>100'
        },
        props: [{
            element: main_image,
            property: 'transform.rotateZ',
            expression: {
              origin: 'linear(t,0,45,100)'
            }
          },
          {
            element: main_btn,
            property: 'background-color',
            expression: {
              origin: "evaluateColor('#ff0000','#607D8B',min(t,100)/100)"
            }
          }
        ]
      });

      Binding.bind({
        eventType: 'timing',
        exitExpression: {
          origin: `t>${duration}`
        },
        props: [{
            element: b1,
            property: 'transform.translateY',
            expression: {
              origin: `easeOutBack(t,0,sin(2*PI*18/360)*${0-radius},${duration})`
            }
          },
          {
            element: b1,
            property: 'transform.translateX',
            expression: {
              origin: `easeOutBack(t,0,cos(2*PI*18/360)*${0-radius},${duration})`
            }
          },
          // b2
          {
            element: b2,
            property: 'transform.translateY',
            expression: {
              origin: `easeOutBack(t,0,${0-radius},${duration})`
            }
          },
          // b3
          {
            element: b3,
            property: 'transform.translateY',
            expression: {
              origin: `easeOutBack(t,0,sin(2*PI*18/360)*${0-radius},${duration})`
            }
          },
          {
            element: b3,
            property: 'transform.translateX',
            expression: {
              origin: `easeOutBack(t,0,cos(2*PI*18/360)*${radius},${duration})`
            }
          },
          // b4
          {
            element: b4,
            property: 'transform.translateY',
            expression: {
              origin: `easeOutBack(t,0,sin(2*PI*54/360)*${radius},${duration})`
            }
          },
          {
            element: b4,
            property: 'transform.translateX',
            expression: {
              origin: `easeOutBack(t,0,cos(2*PI*54/360)*${radius},${duration})`
            }
          },
          // b5
          {
            element: b5,
            property: 'transform.translateY',
            expression: {
              origin: `easeOutBack(t,0,sin(2*PI*54/360)*${radius},${duration})`
            }
          },
          {
            element: b5,
            property: 'transform.translateX',
            expression: {
              origin: `easeOutBack(t,0,cos(2*PI*54/360)*${0-radius},${duration})`
            }
          },

        ]
      })
    },

    clickBtn: function(e) {
      if (this.isExpanded) {
        this.collapse();
      } else {
        this.expand();
      }
      this.isExpanded = !this.isExpanded;
    }
  }
}
</script>
