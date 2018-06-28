import {createElement, Component, render, findDOMNode} from 'rax';
import View from 'rax-view';
import Text from 'rax-text';
import {isWeex} from 'universal-env';
import RecyclerView from 'rax-recyclerview';
import Touchable from 'rax-touchable';
import RefreshControl from 'rax-refreshcontrol';
import Svg, {Path} from '@ali/rax-svg';

import binding from '../../src/';

function getEl(el) {
  return isWeex ? findDOMNode(el).ref : findDOMNode(el);
}

class Row extends Component {

  render() {
    return (
      <Touchable>
        <View style={styles.row}>
          <Text style={styles.text}>
            {this.props.data.text}
          </Text>
        </View>
      </Touchable>
    );
  }
}

let rowData = [];

for (let i = 0; i < 20; i++) {
  rowData.push({text: 'Initial row ' + i, clicks: 0})
}

class RefreshControlDemo extends Component {
  state = {
    isRefreshing: false,
    loaded: 0,
    refreshText: '↓ Pull To Refresh',
    rowData
  };

  componentDidMount() {
    setTimeout(() => {
      binding.bind({
        eventType: 'scroll',
        anchor: getEl(this.refs.list),
        debug: true,
        props: [
          {
            element: getEl(this.refs.path),
            property: 'svg-path',
            expression: {
              origin: `svgDrawCmd(2,asArray(375,abs(y)<100?100:min(abs(y*1.3),380),750,100),'Q')`
            }
          },

        ]
      })
    }, 100)

  }

  handleRefresh = (e) => {
    this.setState({
      isRefreshing: true,
      refreshText: 'Refreshing',
    });

    setTimeout(() => {
      // prepend 10 items
      rowData.map((val, i) => ({
        text: 'Loaded row ' + (+this.state.loaded + i),
        clicks: 0,
      }))
        .concat(this.state.rowData);

      this.setState({
        loaded: this.state.loaded + 10,
        isRefreshing: false,
        rowData: rowData,
        refreshText: '↓ Pull To Refresh',
      });

    }, 100);
  };

  render() {
    const rows = this.state.rowData.map((row, ii) => {
      return (<RecyclerView.Cell style={styles.cell}>
        <Row key={ii} data={row}/>
      </RecyclerView.Cell>);
    });
    return (
      <View style={styles.container}>

        <Svg
          height="300"
          width="750"
          style={styles.svg}
          ref="anchor"
        >

          <Path ref="path" d="M 0 0 L 0 100Q375 100 750 100 L 750 0 L 0 0"
                fill="rgb(242,93,150)"
                stroke="red"
                strokeWidth="1"
          />
        </Svg>

        <RecyclerView
          ref='list'
          style={styles.list}
          refreshControl={null}>
          <RefreshControl
            style={styles.refreshView}
            refreshing={this.state.isRefreshing}
            onRefresh={this.handleRefresh}
          >


          </RefreshControl>
          {rows}
        </RecyclerView>


      </View>
    );
  }
}

const styles = {
  cell: {
    backgroundColor: '#ffffff',
  },
  svg: {

    position: 'absolute',
    top: 0
  },

  circle: {
    position: 'absolute',
    width: 60,
    height: 60,
    backgroundColor: '#00ff00',
    right: 10,
    bottom: 10
  },

  container: {
    flex: 1
  },
  button: {
    margin: 7,
    padding: 5,
    alignItems: 'center',
    backgroundColor: '#eaeaea',
    borderRadius: 3,
  },
  box: {
    width: 64,
    height: 64,
  },
  eventLogBox: {

    margin: 10,
    height: 80,
    borderWidth: 1,
    borderColor: '#f0f0f0',
    backgroundColor: '#f9f9f9',
  },
  row: {
    width: 710,
    height: 200,
    justifyContent: 'center',
    alignItems: 'center',
    margin: 20,
    backgroundColor: '#e0e0e0',
    borderRadius: 16


  },
  text: {
    alignSelf: 'center',
    color: 'black',
  },
  refreshView: {
    height: 300,
    width: 750,
    justifyContent: 'center',
    alignItems: 'center',
    position: 'absolute',
    top: 0
  },
  refreshArrow: {
    fontSize: 30,
    color: '#45b5f0',
    flexDirection: 'row'
  },
};

render(<RefreshControlDemo/>);