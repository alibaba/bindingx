/** @jsx createElement */
import {createElement, Component, render, findDOMNode} from 'rax';
import View from 'rax-view';
import Text from 'rax-text';
import ListView from 'rax-listview';
import {isWeex} from 'universal-env';
import bindingx from '../../src/';

function getEl(el){
  return isWeex ? findDOMNode(el).ref : findDOMNode(el);
}

let listData = [
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
  {name1: 'tom'}, {name1: 'tom'}, {name1: 'tom'},
];



const styles = {
  container: {
    borderStyle: 'solid',
    borderColor: '#dddddd',
    borderWidth: 1,
    position:'absolute',
    top:0,
    bottom:0,
    width:750
  },
  title: {
    margin: 50
  },
  text: {
    fontSize: 28,
    color: '#000000',
    fontSize: 28,
    padding: 40
  },
  item1: {
    height: 110,
    backgroundColor: '#909090',
    marginBottom: 3
  },
  item2: {
    height: 110,
    backgroundColor: '#e0e0e0',
    marginBottom: 3
  },
  loading: {
    padding: 50,
    textAlign: 'center',
  }
};


function getTranslateY(el) {
  let style = bindingx.getComputedStyle(el);
  return style.translateY;
}


class App extends Component {

  token = null;

  translateY1 = 0;

  translateY2 = 0;

  constructor(props) {
    super(props);
    this.state = {
      index: 0,
      data: listData
    };
  }

  listHeader = () => {
    return (
      <View style={styles.title}>
        <Text style={styles.text}>列表头部</Text>
      </View>
    );
  }
  listLoading = () => {
    if (this.state.index < 4) {
      return (
        <View style={styles.loading}>
          <Text style={styles.text}>加载中...</Text>
        </View>
      );
    } else {
      return null;
    }
  }
  listItem = (item, index) => {
    if (index % 2 == 0) {
      return (
        <View style={styles.item1}>
          <Text style={styles.text}>{item.name1}</Text>
        </View>
      );
    } else {
      return (
        <View style={styles.item2}>
          <Text style={styles.text}>{item.name1}</Text>
        </View>
      );
    }
  }
  handleLoadMore = () => {
    setTimeout(() => {
      this.state.index++;
      if (this.state.index < 5) {
        this.state.data.push(
          {name1: 'loadmore 2'},
          {name1: 'loadmore 3'},
          {name1: 'loadmore 4'},
          {name1: 'loadmore 5'},
          {name1: 'loadmore 2'},
          {name1: 'loadmore 3'},
          {name1: 'loadmore 4'},
          {name1: 'loadmore 5'}
        );
      }
      this.setState(this.state);
    }, 1000);
  }


  componentDidMount() {
    this.bindExp();
  }

  bindExp() {
    let anchor = getEl(this.refs.list);
    let topEl = getEl(this.refs.top);
    let bottomEl = getEl(this.refs.bottom);
    let topEl2 = getEl(this.refs.top2);
    let top_origin = `max(0-100,min(0-abs(${this.translateY1})-tdy,0))`;
    let bottom_origin = `max(0,min(${this.translateY2}+tdy,100))`;

    let res = bindingx.bind({
      anchor: anchor,
      eventType: 'scroll',
      exitExpression: {},
      props: [
        {
          element: topEl2,
          property: 'transform.translateY',
          expression: top_origin
        },
        {
          element: topEl,
          property: 'opacity',
          expression: `1-(max(0,min(${this.translateY2}+tdy,100))/100)`
        },
        {
          element: topEl,
          property: 'transform.translateY',
          expression: top_origin
        },
        {
          element: bottomEl,
          property: 'transform.translateY',
          expression: bottom_origin
        }
      ]
    }, (e) => {
      if (e.state === 'turn') {
        this.translateY1 = getTranslateY(topEl);
        this.translateY2 = getTranslateY(bottomEl);
        this.bindExp();
      }
    });

    this.token = res && res.token;


  }

  render() {
    return (

      <View style={styles.container}>
        <ListView
          ref="list"
          style={{position: 'absolute', top: 0, width: 750, bottom: 0}}
          renderHeader={this.listHeader}
          renderFooter={this.listLoading}
          renderRow={this.listItem}
          dataSource={this.state.data}
          onEndReached={this.handleLoadMore}
        />
        <View ref="bottom" style={{
          position: 'absolute',
          width: 750,
          bottom: 0,
          height: 100,
          backgroundColor: 'blue',
          textAlign: 'center'
        }}>
          <Text style={{color: '#fff', textAlign: 'center', lineHeight: 100}}>bottom bar</Text>
        </View>
        <View ref="top"
              style={{width: 750, height: 100, backgroundColor: 'red', position: 'absolute', top: 0, zIndex: 1000}} >
          <Text style={{color: '#fff', textAlign: 'center', lineHeight: 100}}>top bar</Text>
        </View>
        <View ref="top2"
              style={{width: 750, height: 100, backgroundColor: 'green', position: 'absolute', top: 100, zIndex: 1000}} >
          <Text style={{color: '#fff', textAlign: 'center', lineHeight: 100}}>tab bar</Text>
        </View>
      </View>
    );
  }

}


render(<App />);
