/** @jsx createElement */
import {createElement, Component, render, findDOMNode} from 'rax';
import View from 'rax-view';
import {isWeex} from 'universal-env';
import ScrollView from 'rax-scrollview';
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
      console.error(bindingx.getComputedStyle(block));
    }, 50);


  }

  render() {
    return (<View style={{flex: 1}}>
      <ScrollView ref={'scrollView'} style={{borderWidth: 2, borderColor: 'red', width: 600, height: 600}}>
        <View>
          {[0, 1, 2, 3, 4, 5].map((i) => {
            return <View style={{height: 500}}>{i}</View>
          })}
        </View>
      </ScrollView>
      <View ref="block" onTouchStart={this.bindExp}
            style={{
              width: 200,
              height: 200,
              position: 'absolute',
              top: 0,
              left: 0,
              backgroundColor: 'red'
            }}>hello</View>
    </View>);
  }

  bindExp = () => {
    let block = getEl(this.refs.block);
    let scrollView = getEl(this.refs.scrollView);
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
        // {
        //   element: scrollView,
        //   property: 'scroll.contentOffsetY',
        //   expression: `y+${this.y}`
        // },
        {
          element: scrollView,
          property: 'scroll.contentOffset',
          expression: `x+${this.x}`
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


render(<App/>);


