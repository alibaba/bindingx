<template>
	<div class="conatiner">
		<list class="list" >
			<cell v-for="item in items">
					<div @click="clickItem(item.url)" class="card">
							<text class="title">{{item.title}}</text>
					</div>
			</cell>
		</list>
	</div>
</template>

<style>
	.container{
		flex: 1;
	}

	.list {
	  flex:1;
		position:absoulte;
		top:0;
		background-color:#f2f3f4;
	}

  .card {
		width: 710;
	  height: 150;
	  background-color:#ffffff;
	  border-radius: 25;
	  margin: 20;
	  padding: 20;
	  justify-content:center;
	  align-items:center;

	}

	.title {
		font-size: 35;
	}
</style>

<script>
	const modal = weex.requireModule('modal')
	const navigator = weex.requireModule('navigator')

	const root = weex.config.bundleUrl;

	module.exports = {
		data: {
			items: [
					{
						title:'Draggable Ball',
						url:'draggable_ball.js'
					},
					{
						title:'Swipeable Card',
						url:'swipeable_card.js'
					},
					{
						title:'Expandable Menu',
						url:'expandable_menu.js'
					},
					{
						title:'Slide Layout',
						url:'slide_layout.js'
					},
					{
						title:'Circle Menu',
						url:'circle_menu.js'
					},
					{
						title:'Navigation with Tab',
						url:'navigation.js'
					},
					{
						title:'Ripple Effect',
						url:'ripple.js'
					}
			  ]
		},
		methods: {
			clickItem: function(e) {
				 navigator.push({
				 		url:root.replace('index.js',e),
						animated:'true'
				 }, function(){})
			}
		}
	}
</script>
