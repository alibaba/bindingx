/**
 * Created by rowandjj on 2018/2/1.
 */
/**
 * Created by rowandjj on 2018/2/1.
 */
import React, {Component} from 'react';
import {
  StyleSheet,
  View,
  Text,
  NativeModules,
  findNodeHandle,
  TouchableHighlight,
  ToastAndroid,
  PanResponder,
  ScrollView,
  Platform,
  NativeEventEmitter,
  DeviceEventEmitter,
} from 'react-native';

import bindingx from 'react-native-bindingx';

export default class ScrollViewDemo extends Component {


  _token = null;

  componentWillMount() {
  }


  onBind() {
    let anchor = findNodeHandle(this.refs._anchor);
    let target = findNodeHandle(this.refs._target);
    let text = findNodeHandle(this.refs._text);


    let token = bindingx.bind({
      eventType: 'scroll',
      anchor: anchor,
      props: [
        {
          element: target,
          property: 'transform.translateX',
          expression: 'y+0'
        },
        {
          element: target,
          property: 'transform.rotateZ',
          expression: 'y+0'
        },
        {
          element: target,
          property: 'transform.scaleX',
          expression: '1+max(100,y)/100'
        },
        {
          element: target,
          property: 'transform.scaleY',
          expression: '1+max(100,y)/100'
        },
        {
          element: target,
          property: 'background-color',
          expression: 'evaluateColor(\'#0000ff\',\'#ff0000\',min(200,y)/200)'
        },
        {
          element: text,
          property: 'color',
          expression: 'evaluateColor(\'#0000ff\',\'#ff0000\',min(200,y)/200)'
        }
      ]
    }, (e) => {
      console.log('callback:', e)
    });

    this._token = token.token;
  }

  onUnBind() {
    if (this._token === null) {
      return;
    }
    bindingx.unbind({
      token: this._token,
      eventType: 'scroll'
    });
  }

  getComputedStyle() {
    let styles = bindingx.getComputedStyle(findNodeHandle(this.refs._target));
    console.log('getComputedStyle:',styles);
    // ToastAndroid.show('styles:'+JSON.stringify(styles), ToastAndroid.SHORT);
  }

  render() {

    let data = [];
    for (let i = 0; i < 100; i++) {
      data.push("hello world");
    }

    return (
      <View style={styles.container}>

        <TouchableHighlight
          onPress={() => {
            this.onBind()
          }}
          style={styles.button}
        >
          <Text style={styles.text}>Bind</Text>
        </TouchableHighlight>

        <TouchableHighlight
          onPress={() => {
            this.onUnBind()
          }}
          style={styles.button}
        >
          <Text style={styles.text}>Unbind</Text>
        </TouchableHighlight>

        <TouchableHighlight
          onPress={() => {
            this.getComputedStyle()
          }}
          style={styles.button}
        >
          <Text style={styles.text}>getComputedStyle</Text>
        </TouchableHighlight>

        <View ref="_target" style={{
          width: 100,
          height: 100,
          backgroundColor: '#0000ff',
          justifyContent: 'center',
          alignItems: 'center',
          marginTop: 20
        }}

        >
          <Text ref="_text" style={{
            textAlign: 'center',
            color: '#ffffff',
          }}>Target</Text>
        </View>

        <ScrollView style={styles.scrollView} ref="_anchor">
          {
            data.map((cell, index) => {
              return (
                <View style={styles.cell} key={index}>
                  <Text style={styles.cell_text}>{cell} {index}</Text>
                </View>

              );
            })
          }
        </ScrollView>

      </View>
    );
  }
}

const styles = StyleSheet.create({
  scrollView: {
    marginTop: 20
  },
  cell: {
    height: 48,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#e0e0e0',
    marginTop: 1
  },
  cell_text: {
    textAlign: 'center',
    color: '#000000'
  },
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF'
  },
  text: {
    textAlign: 'center',
    color: '#000000'
  },
  button: {
    height: 50,
    backgroundColor: '#00ff00',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 20
  },
  wrapper: {
    backgroundColor: '#0000ff',
    height: 48,
    alignItems: 'center',
    justifyContent: 'center'
  },
  margin: {
    marginTop: 20
  },
  anchor: {
    width: 80,
    height: 80,
    backgroundColor: '#ff0000',
    marginTop: 48
  }

});
