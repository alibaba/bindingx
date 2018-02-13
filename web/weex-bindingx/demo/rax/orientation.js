/** @jsx createElement */
import {createElement, Component, render, findDOMNode} from 'rax';
import View from 'rax-view';
import {isWeex} from 'universal-env';
import bindingx from '../../src/';

function getEl(el) {
  return isWeex ? findDOMNode(el).ref : findDOMNode(el);
}

class App extends Component {

  componentDidMount() {
    setTimeout(() => {
      this.bind();
    }, 100);
  }

  bind() {
    let blockEl = getEl(this.refs.block);

    bindingx.bind({
      eventType: 'orientation',
      props: [
        {
          element: blockEl,
          property: 'transform.translateX',
          expression: 'x+0'
        },
        {
          element: blockEl,
          property: 'transform.translateY',
          expression: {
            origin: 'y+0'
          }
        }
      ]
    }, (e) => {
      console.log(e);
    });
  }

  render() {
    return (<View style={{width: 750, height: 750}}>
      <View ref="block" style={{
        position: 'absolute',
        left: 225,
        top: 200,
        width: 300,
        height: 300,
        backgroundColor: 'red'
      }}>block</View>
    </View>);
  }

}


render(<App />);
