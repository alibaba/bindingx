<template>
	<div class="container">


	</div>
</template>

<style>

</style>

<script>
  import Binding from 'weex-bindingx';

	module.exports = {
		data: {
      fake_tabs:[
          'Tab1',
          'Tab2',
          'Tab3',
          'Tab4',
          'Tab5',
          'Tab6'
      ]
		},
		methods: {

			getEl: function(e) {
					return e.ref;
			}

    }
	}
</script>
