import {createElement, Component, render, findDOMNode} from 'rax';
import Text from 'rax-text';
import View from 'rax-view';
import bindingx from '../../src/';
import ScrollView from 'rax-scrollview';
import {isWeex} from 'universal-env';


function getEl(el) {
  return isWeex ? findDOMNode(el).ref : findDOMNode(el);
}

class App extends Component {

  componentDidMount() {

  }

  bindExp = (index) => {

    let anchor = this.refs[`cell_${index}`];
    if (this.slideIndex === index) {
      this.stop();
    }

    let start = bindingx.getComputedStyle(getEl(anchor)).translateX || 0;

    bindingx.bind({
      eventType: 'pan',
      anchor: getEl(anchor),
      options: {
        touchAction: 'pan-x'
      },
      props: [
        {
          element: getEl(anchor),
          property: 'transform.translateX',
          expression: `x+${start}`
        }
      ]
    });
  }

  onHorizontalPan = (e, index) => {
    if (e.state == 'start') {
      this.bindExp(index);
    }
    if (e.state == 'end') {
      this.slideIn(index);
    }
  }

  onTouchStart = (e, index) => {
    this.bindExp(index);
  }

  onTouchEnd = (e, index) => {
    this.slideIn(index);
  }

  stop = () => {

    if (this.slideToken) {
      bindingx.unbind({
        eventType: 'timing',
        token: this.slideToken
      });
    }
  }

  slideIn = (index) => {
    let el = this.refs[`cell_${index}`];
    let start = bindingx.getComputedStyle(getEl(el)).translateX;
    let end = 0;
    let offset = end - start;
    let duration = 500;

    this.slideIndex = index;

    let res = bindingx.bind({
      eventType: 'timing',
      exitExpression: `t>${duration}`,
      props: [{
        element: getEl(el),
        property: 'transform.translateX',
        expression: `easeOutSine(t,${start},${offset},${duration})`
      }]
    }, (e) => {
      if (e.state == 'exit') {
        this.slideIndex = undefined;
      }
    });

    this.slideToken = res.token;
  }


  render() {


    return (
      <View style={styles.container}>
        <ScrollView>
          <View className="content">
            {[0, 1, 2, 3, 4, 5, 6].map((i, index) => {
              return (<View ref={`cell_${index}`} key={index} {...isWeex ? {
                onHorizontalPan: (e) => this.onHorizontalPan(e, index)
              } : {
                onTouchStart: (e) => this.onTouchStart(e, index),
                onTouchEnd: (e) => this.onTouchEnd(e, index)
              }} style={styles.cell}>
                <Text className="text">
                  drag me!
                </Text>
              </View>);
            })}
          </View>
        </ScrollView>
      </View>
    );
  }
}


const styles = {
  container: {
    flex: 1
  },
  cell: {
    width: 300,
    backgroundColor: 'red',
    borderWidth: 2,
    borderColor: '#000',
    height: 300
  }
};

render(<App />);

