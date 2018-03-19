<template>
	<div class="container">
    <div ref="card" style="background-color:#6A1B9A;width:0;height:0.1;margin-left:-300;margin-top:-300"/>
    <div ref="btn" class="card" style="background-color:#00ff00" @click="clickBtn"/>
	</div>
</template>

<style>
.container {
	flex:1;
}

.card {
	width: 100;
  height: 100;
	position:absolute;
  border-radius: 50;
  top:0;
}
</style>

<script>
  import Binding from 'weex-bindingx';

	module.exports = {
		data: {
		},
		methods: {
			getEl: function(e) {
					return e.ref;
			},
      clickBtn: function() {
        let card = this.getEl(this.$refs.card);

        let duration = 700;
        let radius = 1800;

       	Binding.bind({
          eventType:'timing',
          exitExpression:{
            origin:`t>${duration}`
          },
          props:[
            {
               element:card,
               property:'border-top-right-radius',
               expression:{
               	 origin:`easeInOutQuint(t,0,${radius},${duration})`
               }
            },
            {
               element:card,
               property:'border-bottom-left-radius',
               expression:{
               	 origin:`easeInOutQuint(t,0,${radius},${duration})`
               }
            },
            {
               element:card,
               property:'border-bottom-right-radius',
               expression:{
               	 origin:`easeInOutQuint(t,0,${radius},${duration})`
               }
            },
            {
               element:card,
               property:'width',
               expression:{
               	 origin:`easeInOutQuint(t,0,${radius},${duration})`
               }
            },
            {
               element:card,
               property:'height',
               expression:{
               	 origin:`easeInOutQuint(t,0,${radius},${duration})`
               }
            }
          ]

        });

      }


    }
	}
</script>
