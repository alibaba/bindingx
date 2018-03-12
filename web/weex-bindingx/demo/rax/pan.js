/** @jsx createElement */
import {createElement, Component, render, findDOMNode} from 'rax';
import View from 'rax-view';
import {isWeex} from 'universal-env';
import bindingx from '../../src/';

function getEl(el) {
  return isWeex ? findDOMNode(el).ref : findDOMNode(el);
}

class App extends Component {

  x = 0;

  y = 0;

  componentDidMount() {
    setTimeout(() => {
      let block = getEl(this.refs.block);
      bindingx.prepare({
        anchor: block,
        eventType: 'pan'
      });
    }, 50);

  }

  render() {
    return (<View ref="block" onTouchStart={this.bindExp}
      style={{width: 200, height: 200, backgroundColor: 'red'}}>hello</View>);
  }

  bindExp = () => {
    let block = getEl(this.refs.block);
    bindingx.bind({
      anchor: block,
      eventType: 'pan',
      props: [
        {
          element: block,
          property: 'transform.translateX',
          expression: `x+${this.x}`
        },
        {
          element: block,
          property: 'transform.translateY',
          expression: `y+${this.y}`
        }
      ]
    }, (e) => {
      if (e.state === 'end') {
        this.x += e.deltaX;
        this.y += e.deltaY;
      }
    });
  }
}


render(<App />);


