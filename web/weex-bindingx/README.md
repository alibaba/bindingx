# BindingX

## Install

```bash
$ npm install bindingx --save
```

## Usage

```jsx
import Binding from 'bindingx';
```


## API


### Methods

|name|args|returns|description|
|:---------------|:--------|:----|:----------|
|bind|{object} options|{object}|bind an expression|
|unbind|{object} options| void |unbind an expression|
|unbindAll|| void |unbind for all|

### Arguments Introduction

#### options

##### anchor {ElementReference|HTMLElement}

- element to trigger the animation ，
	- pass the element in web,such as ``` findDOMNode(this.refs.block) ```
	- pass the element ref in weex, `findDOMNode(this.refs.block).ref`

##### eventType {String}

- pass the type of event to trigger the binding, like `scroll`,`pan`,`timing`,`orientation`

##### instanceId {String}

- pass the instanceId in weex, you can use `document.id` to get it，you should't pass it in web

##### options {Object}

- option configs for binding
	- touchAction (web support only) ,you can pass `auto` or `pan-x` or `pan-y`,default value is `auto`
	- thresholdX (web support only)  default value is `10`,it means the `panstart` event won't be triggerred until the distance of touchmove `>10`
	- thresholdY (web support only)  default value is`10`
	- touchActionRatio (web support only) default value is `0.5`, it means the ratio of width/height

##### props {Array}

- elements for animation
   - element {ElementReference|HTMLElement}
	- expression {Object|String}
		- origin {String} binding expression
		- transformed {String}
	- property {String} property for animation
	- instanceId


## Example

### RAX

```jsx
import {createElement, Component, render} from 'rax';
import bindingx from 'bindingx';
import View from 'rax-view';
import {isWeex} from 'universal-env';

function getEl(el){
   return isWeex ? findDOMNode(el).ref : findDOMNode(el);
}

class App extends Component {

  x = 0;
  y = 0;

  componentDidMount(){
  	this.bindEl();
  }

  onTouchStart(){
    this.bindEl();
  }

  bindEl(){
    let blockEl = getEl(this.refs.block);
    let token = bindingx.bind({
      anchor: blockEl,
      eventType: 'pan',
      props: [
        {
          element: blockEl,
          property: 'transform.translateX',
          expression: `x+${this.x}`
        },
        {
          element: blockEl,
          property: 'transform.translateY',
          expression: `y+${this.y}`
        }]
      },(e)=>{

      if (e.state === 'end') {
        this.x += e.deltaX;
        this.y += e.deltaY;
      }

    });

  }

  render(){
		 return (<View onTouchStart={(e) => this.onTouchStart(e)} ref="block" style={{
		        position: 'absolute',
		        left: 0,
		        top: 0,
		        width: 300,
		        height: 300,
		        backgroundColor: 'red'
      }}>block</View>)
  }
}

render(<App />);
```

### Vue

```
<template>
    <scroller class="scroller" >
        <div :ref="'box'" class="box" @touchstart="ontouchstart"  @appear="onappear"></div>
    </scroller>
</template>

<style scoped>
    .scroller {
        flex: 1;

    }
    .box {
        border-width: 2px;
        border-style: solid;
        border-color: #BBBBBB;
        width: 250px;
        height: 250px;
        margin-top: 250px;
        margin-left: 250px;
        background-color: #EEEEEE;
        margin-bottom:500px;
    }
</style>

<script>
  import {isWeex} from 'universal-env';
  import bindingx from '../../src/index';

  function getEl(el) {
    if (typeof el === 'string' || typeof el === 'number') return el;
    return isWeex ? el.ref : el instanceof HTMLElement ? el : el.$el;
  }


  export default {
    data () {
      return {
        x: 0,
        y: 0,
        flag: 0
      }
    },
    methods: {
      onappear () {
        this.bind();
      },
      bind () {
        var box = getEl(this.$refs.box);
        bindingx.bind({
          anchor: box,
          eventType: 'pan',
          props: [
            {
              element: box,
              property: 'transform.translateX',
              expression: {
                origin: `x+${this.x}`,
                transformed: `{\"type\":\"+\",\"children\":[{\"type\":\"Identifier\",\"value\":\"x\"},{\"type\":\"NumericLiteral\",\"value\":\"${this.x}\"}]}`
              }
            },
            {
              element: box,
              property: 'transform.translateY',
              expression: {
                origin: `y+${this.y}`,
                transformed: `{\"type\":\"+\",\"children\":[{\"type\":\"Identifier\",\"value\":\"y\"},{\"type\":\"NumericLiteral\",\"value\":\"${this.y}\"}]}`
              }
            }
          ]
        }, (e) => {
          if (e.state === 'end') {
            this.x += e.deltaX;
            this.y += e.deltaY;
          }
        });
      },
      ontouchstart (event) {
        this.bind();
      }
    }
  }
</script>

```



