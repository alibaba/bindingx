/** @jsx createElement */
import {createElement, Component, render, findDOMNode} from 'rax';
import View from 'rax-view';
import {isWeex} from 'universal-env';
import bindingx from '../../src/';
import Lottie from '@ali/rax-lottie';

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
      // console.error(bindingx.getComputedStyle(block))
    }, 50);



  }

  render() {

    const source ='https://raw.githubusercontent.com/acton393/WeexLottie/master/examples/animations/9squares-AlBoardman.json'

    return (<View ref="block" onTouchStart={this.bindExp}
    style={{width: 400, height: 400, backgroundColor: 'red'}}>
      <Lottie

        ref={'lottie'}
        autoplay={false}
        source={{
          uri:source
        }}
        style={{
          width: 400,
          height: 400,
          backgroundColor: 'blue'
        }}
      />
    </View>);
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
        },
        {
          element: isWeex ? findDOMNode(this.refs.lottie).ref : this.refs.lottie,
          property:'lottie-progress',
          expression:`max(0,min(1,y/750))`
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


