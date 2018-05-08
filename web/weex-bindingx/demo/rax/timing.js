/** @jsx createElement */
import {createElement, Component, render, findDOMNode} from 'rax';
import View from 'rax-view';
import Text from 'rax-text';
import Picker from 'rax-picker';
import {isWeex} from 'universal-env';
import bindingx from '../../src/';

function getEl(el) {
  return isWeex ? findDOMNode(el).ref : findDOMNode(el);
}


const easings = [
  'easeInQuad', 'easeOutQuad', 'easeInCubic', 'easeOutCubic',
  'easeInOutCubic', 'easeInQuart', 'easeOutQuart', 'easeInOutQuart',
  'easeInQuint', 'easeOutQuint', 'easeInOutQuint', 'easeInSine',
  'easeOutSine', 'easeInOutSine', 'easeInExpo', 'easeOutExpo',
  'easeInOutExpo', 'easeInCirc', 'easeOutCirc', 'easeInOutCirc',
  'easeInElastic', 'easeOutElastic', 'easeInOutElastic', 'easeInBack',
  'easeOutBack', 'easeInOutBack', 'easeInBounce', 'easeOutBounce',
  'easeInOutBounce', 'cubicBezier'
];

class App extends Component {


  state = {
    easing: 'linear'
  };

  componentWillMount() {
    this.setState({easings: easings});
  }

  componentDidMount() {

    this.bindExp();
  }

  bindExp() {
    let blockEl = getEl(this.refs.block);
    let begin = 0;
    let end = 300;
    let duration = 2000;
    let x1 = 0.1;
    let y1 = 0.57;
    let x2 = 0.1;
    let y2 = 1;
    let origin = this.state.easing === 'cubicBezier' ? `cubicBezier(t, ${begin}, ${end}, ${duration}, ${x1}, ${y1}, ${x2}, ${y2})` : `${this.state.easing}(t,${begin},${end},${duration})`;
    bindingx.bind({
      eventType: 'timing',
      exitExpression: `t>${duration}`,
      props: [
        {
          element: blockEl,
          property: 'transform.rotate',
          expression: origin,
          config: {
            perspective: 1000,
            transformOrigin: 'center',
          }
        },
        {
          element: blockEl,
          property: 'transform.translateX',
          expression: origin,
          config: {
            perspective: 1000,
            transformOrigin: 'center',
          }
        }
      ]
    }, (e) => {
      console.log(e);
    });
  }

  render() {
    return (
      <View style={{position: 'absolute', width: 750, bottom: 0, top: 0}}>
        <View ref="block"
          style={{position: 'absolute', top: 0, width: 250, height: 250, backgroundColor: 'red'}}>block</View>
        <View style={{width: 750, position: 'absolute', bottom: 0, height: 400, backgroundColor: '#f7f7f7'}}>
          <Text>选择缓动曲线</Text>
          {this.state.easings ?
            <Picker
              style={{}}
              selectedValue={''}
              onValueChange={(v) => {
                this.setState({easing: v});
                this.bindExp();
              }}>
              <Picker.Item value={''} label={'请选择'} />
              {this.state.easings.map((k, i) => {
                return <Picker.Item key={i} value={k} label={k} />;
              })}

            </Picker>
            : null}
        </View>
      </View>
    );
  }

}


render(<App />);
