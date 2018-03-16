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
						url:'https://jsplayground.taobao.com/bundle/073b10f5-a4fd-41dc-9265-6865458d1043/raxbundle.js?wh_weex=true&wh_ttid=native&_wx_tpl=https://jsplayground.taobao.com/bundle/073b10f5-a4fd-41dc-9265-6865458d1043/raxbundle.js'
					},
					{
						title:'Slide Layout',
						url:'https://jsplayground.taobao.com/bundle/a62b34cb-9174-4667-83d8-3f452d6a1829/raxbundle.js?wh_weex=true&wh_ttid=native&_wx_tpl=https://jsplayground.taobao.com/bundle/a62b34cb-9174-4667-83d8-3f452d6a1829/raxbundle.js'
					},
					{
						title:'Circle Menu',
						url:'https://jsplayground.taobao.com/bundle/e04fd2ca-f3f6-4a4c-8500-0d784168bd77/raxbundle.js?wh_weex=true&wh_ttid=native&_wx_tpl=https://jsplayground.taobao.com/bundle/e04fd2ca-f3f6-4a4c-8500-0d784168bd77/raxbundle.js'
					},
					{
						title:'Navigation with Tab',
						url:'https://jsplayground.taobao.com/bundle/af2aba4c-d805-4545-a2d1-9f9db0bf2b50/raxbundle.js?wh_weex=true&wh_ttid=native&_wx_tpl=https://jsplayground.taobao.com/bundle/af2aba4c-d805-4545-a2d1-9f9db0bf2b50/raxbundle.js&wx_navbar_hidden=true'
					},
					{
						title:'Ripple Effect',
						url:'https://jsplayground.taobao.com/bundle/c295cf39-ec9e-4128-a968-fb051fb48022/raxbundle.js?wh_weex=true&wh_ttid=native&_wx_tpl=https://jsplayground.taobao.com/bundle/c295cf39-ec9e-4128-a968-fb051fb48022/raxbundle.js'
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
