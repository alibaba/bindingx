/** @jsx createElement */
import {createElement, Component, render, findDOMNode} from 'rax';
import View from 'rax-view';
import {isWeex} from 'universal-env';
import Text from 'rax-text';
import Picture from 'rax-picture';
import bindingx from '../../src/';

function getEl(el) {
  return isWeex ? findDOMNode(el).ref : findDOMNode(el);
}

const menuHeight = 500;
const navHeight = 100;


class Menu extends Component {

  itemHeight = 100;

  rate = 0.2;

  duration = 250;

  delayOffset = 30;


  slideDown() {
    let bindingProps = [];
    let rate = this.rate;
    let duration = this.duration;
    const easing = 'easeOutSine';

    bindingProps.push({
      element: getEl(this.refs.menu),
      property: 'height',
      expression: `${easing}(t,${navHeight},${menuHeight - navHeight},${duration})`
    });

    bindingProps.push({
      element: getEl(this.refs.close),
      property: 'opacity',
      expression: `${easing}(t,0,1,${duration})`
    });

    bindingProps.push({
      element: getEl(this.refs.open),
      property: 'opacity',
      expression: `${easing}(t,1,-1,${duration})`
    });


    let maxDelay = 0;
    for (let i = 0; i < 4; i++) {
      let style = getStyle(this.refs[`item_${i}`]);
      let destY = i * this.itemHeight + navHeight;
      let offset = destY - style.translateY;
      let delay = i * this.delayOffset;
      maxDelay = Math.max(delay, maxDelay);
      bindingProps = bindingProps.concat([
        {
          config: {
            transformOrigin: 'left top'
          },
          element: getEl(this.refs[`item_${i}`]),
          property: 'transform.scale',
          expression: `${easing}(t,${rate},1,${duration})`
        },
        {
          element: getEl(this.refs[`item_${i}`]),
          property: 'opacity',
          expression: `${easing}(t,0,1,${duration})`
        },
        {
          element: getEl(this.refs[`item_${i}`]),
          property: 'transform.translateX',
          expression: `t>${delay}?${easing}(t-${delay},0,200,${duration}):0`
        },
        {
          element: getEl(this.refs[`item_${i}`]),
          property: 'transform.translateY',
          expression: `t>${delay}?${easing}(t-${delay},${style.translateY},${offset},${duration}):${style.translateY}`
        }
      ]);


    }

    bindingx.bind({
      eventType: 'timing',
      exitExpression: `t>${duration + maxDelay}`,
      props: bindingProps
    });
  }

  slideUp = () => {

    let bindingProps = [];
    let rate = this.rate;
    let duration = this.duration;
    const easing = 'easeOutSine';

    bindingProps.push({
      element: getEl(this.refs.menu),
      property: 'height',
      expression: `${easing}(t,${menuHeight},${navHeight - menuHeight},${duration})`
    });

    bindingProps.push({
      element: getEl(this.refs.close),
      property: 'opacity',
      expression: `${easing}(t,1,-1,${duration})`
    });

    bindingProps.push({
      element: getEl(this.refs.open),
      property: 'opacity',
      expression: `${easing}(t,0,1,${duration})`
    });


    let maxDelay = 0;
    for (let i = 0; i < 4; i++) {
      let style = getStyle(this.refs[`item_${i}`]);
      let destY = i * this.itemHeight * rate;
      let offset = destY - style.translateY;
      let delay = i * this.delayOffset;
      maxDelay = Math.max(delay, maxDelay);
      bindingProps = bindingProps.concat([
        {
          config: {
            // transformOrigin: 'left top'
          },
          element: getEl(this.refs[`item_${i}`]),
          property: 'transform.scale',
          expression: `${easing}(t,1,${rate - 1},${duration})`
        },
        {
          element: getEl(this.refs[`item_${i}`]),
          property: 'opacity',
          expression: `${easing}(t,1,-1,${duration})`
        },
        {
          element: getEl(this.refs[`item_${i}`]),
          property: 'transform.translateX',
          expression: `t>${delay}?${easing}(t-${delay},200,-200,${duration}):200`
        },
        {
          element: getEl(this.refs[`item_${i}`]),
          property: 'transform.translateY',
          expression: `t>${delay}?${easing}(t-${delay},${style.translateY},${offset},${duration}):${style.translateY}`
        }
      ]);


    }

    bindingx.bind({
      eventType: 'timing',
      exitExpression: `t>${duration + maxDelay}`,
      props: bindingProps
    });
  }

  render() {

    let itemHeight = this.itemHeight;

    return (<View ref={'menu'} style={{
      width: 750,
      position: 'absolute',
      left: 0,
      top: 0,
      height: navHeight,
      overflow: 'hidden',
      backgroundColor: 'rgb(86,57,91)'
    }}>
      <View>
        {['DASHBOARD', 'HISTORY', 'STATISTICS', 'SETTINGS'].map((item, i) => {
          return (<View key={i} ref={`item_${i}`}
            style={{
              position: 'absolute',
              left: 0,
              top: 0,
              opacity: 0,
              transform: `translateY(${i * itemHeight + navHeight}rem) translateX(200rem)`,
              height: itemHeight,
              width: 750
            }}>
            <Text ref={`txt_${i}`}
              style={{
                color: 'rgb(234,115,102)',
                lineHeight: itemHeight,
                fontSize: 32
              }}>{item}</Text>
          </View>);
        })}
        <View onClick={this.props.onCloseBtnClick} style={{
          position: 'absolute',
          left: 0,
          top: 0,
          width: 100,
          height: 100,
          alignItems: 'center',
          justifyContent: 'center'
        }}>
          <Picture
            ref={'close'}
            source={{uri: '//gw.alicdn.com/tfs/TB193sowmtYBeNjSspkXXbU8VXa-64-64.png'}}
            style={{width: 60, height: 60, opacity: 0, position: 'absolute', top: 20, left: 20}} />
          <Picture
            ref={'open'}
            source={{uri: '//gw.alicdn.com/tfs/TB1grCUweSSBuNjy0FlXXbBpVXa-64-64.png'}}
            style={{width: 60, height: 60, opacity: 1, position: 'absolute', top: 20, left: 20}} />
        </View>
      </View>
    </View>);

  }

}


function getTranslateX(el) {
  return getStyle(el).translateX;
}


function getTranslateY(el) {
  return getStyle(el).translateY;
}

function getStyle(el) {
  return bindingx.getComputedStyle(getEl(el));
}


class App extends Component {

  componentDidMount() {

  }

  bindExp = () => {

    bindingx.unbindAll();

    let translateY1 = getTranslateY(this.refs.block1);
    let translateY2 = getTranslateY(this.refs.block2);
    let bindingProps = [
      {
        element: getEl(this.refs.block1),
        property: 'transform.translateY',
        expression: `max(0,y+${translateY1})`
      },
      {
        element: getEl(this.refs.block2),
        property: 'transform.translateY',
        expression: `max(0,y-200+${translateY2})`
      },
      {
        element: getEl(this.refs.bar),
        property: 'transform.translateY',
        expression: `max(0,y-200+${translateY2})`
      },
      {
        element: getEl(this.refs.ball),
        property: 'transform.translateY',
        expression: `max(0,y-200+${translateY2})`
      },
      {
        element: getEl(this.refs.mask),
        property: 'opacity',
        expression: `max(0,min(1,(y+${translateY2})/${menuHeight}*0.5))`
      }
    ];

    console.log(bindingProps);

    bindingx.bind({
      anchor: getEl(this.refs.ctn),
      eventType: 'pan',
      props: bindingProps
    }, (e) => {
      if (e && e.state === 'end') {
        if (e.deltaY > 0) {
          this.slideDown();
        } else if (e.deltaY < 0) {
          this.slideUp();
        }
      }
    });
  }

  slideDown = () => {

    this.isSlideDown = true;

    if (this.token) {
      bindingx.unbind({
        token: this.token,
        eventType: 'timing'
      });
    }

    let start1 = getTranslateY(this.refs.block1);
    let end1 = menuHeight;
    let offset1 = end1 - start1;


    let start2 = getTranslateY(this.refs.block2);
    let end2 = menuHeight;
    let offset2 = end2 - start2;

    this.token = bindingx.bind({
      eventType: 'timing',
      props: [
        {
          element: getEl(this.refs.block1),
          property: 'transform.translateY',
          expression: `t>50?easeOutCubic(t-50,${start1},${offset1},350):${start1}`
        },
        {
          element: getEl(this.refs.block2),
          property: 'transform.translateY',
          expression: `easeOutElastic(t,${start2},${offset2},600)`
        },
        {
          element: getEl(this.refs.ball),
          property: 'transform.translateY',
          expression: `easeOutElastic(t,${start2},${offset2},600)`
        },
        {
          element: getEl(this.refs.bar),
          property: 'transform.translateY',
          expression: `easeOutElastic(t,${start2},${offset2},600)`
        },
        {
          element: getEl(this.refs.mask),
          property: 'opacity',
          expression: 'easeOutCubic(t,0,.5,600)'
        },
      ]
    }, (e) => {
      if (e && e.state === 'exit') {

      }
    });


    this.refs.menu.slideDown();

  }

  slideUp = () => {

    this.isSlideDown = false;

    if (this.token) {
      bindingx.unbind({
        token: this.token,
        eventType: 'timing'
      });
    }


    let style1 = bindingx.getComputedStyle(getEl(this.refs.block1));
    let start1 = style1.translateY;
    let end1 = 0;
    let offset1 = end1 - start1;


    let style2 = bindingx.getComputedStyle(getEl(this.refs.block2));
    let start2 = style2.translateY;
    let end2 = 0;
    let offset2 = end2 - start2;

    this.token = bindingx.bind({
      eventType: 'timing',
      props: [
        {
          element: getEl(this.refs.block1),
          property: 'transform.translateY',
          expression: `easeOutElastic(t,${start1},${offset1},600)`
        },
        {
          element: getEl(this.refs.block2),
          property: 'transform.translateY',
          expression: `t>50?easeOutCubic(t-50,${start2},${offset2},${350}):${start2}`
        },
        {
          element: getEl(this.refs.ball),
          property: 'transform.translateY',
          expression: `t>50?easeOutCubic(t-50,${start2},${offset2},${350}):${start2}`
        },
        {
          element: getEl(this.refs.bar),
          property: 'transform.translateY',
          expression: `t>50?easeOutCubic(t-50,${start2},${offset2},${350}):${start2}`
        },
        {
          element: getEl(this.refs.mask),
          property: 'opacity',
          expression: 'easeOutCubic(t,0.5,-0.5,600)'
        },
      ]
    });


    this.refs.menu.slideUp();

  }

  toggleSlide = () => {
    this.isSlideDown ? this.slideUp() : this.slideDown();
  }

  render() {
    return (<View ref={'ctn'} style={{flex: 1, backgroundColor: 'rgb(86,57,91)', alignItems: 'center'}}
      onTouchStart={this.bindExp}>
      <View ref={'ball'}
        style={{width: 200, height: 200, backgroundColor: 'rgb(234,115,102)', marginTop: 100, borderRadius: 200}} />
      <View ref={'bar'} style={{width: 400, height: 40, marginTop: 50, backgroundColor: 'rgb(234,115,102)'}} />
      <View style={{width: 750, alignItems: 'flex-start', marginTop: 100}}>
        <View ref={'block1'} style={{width: 750, position: 'absolute', alignItems: 'center'}}>
          <View style={{backgroundColor: 'rgb(155,87,124)', width: 700, height: 600, marginTop: 100}} />
        </View>
        <View ref={'block2'} style={{width: 750, position: 'absolute', alignItems: 'center'}}>
          <View style={{
            backgroundColor: 'rgb(247,185,128)',
            width: 600,
            height: 800,
            boxShadow: '0rem 10rem 50rem rgba(0,0,0,.5)'
          }} />
        </View>
      </View>
      <View ref={'mask'}
        style={{backgroundColor: '#000', opacity: 0, position: 'absolute', top: 0, bottom: 0, left: 0, right: 0}} />
      <Menu ref={'menu'} onCloseBtnClick={this.toggleSlide} />
    </View>);
  }

}

render(<App />);


