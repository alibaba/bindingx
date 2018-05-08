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
  PanResponder
} from 'react-native';

import {DeviceEventEmitter} from 'react-native';

import bindingx from 'react-native-bindingx';

export default class TimingDemo extends Component {


  _token = null;

  componentWillMount() {
    ToastAndroid.show('componentWillMount', ToastAndroid.SHORT);
    // DeviceEventEmitter.addListener('stateChanged', function(e: Event) {
    //     ToastAndroid.show('event:'+JSON.stringify(e), ToastAndroid.SHORT);
    // });
  }


  onBind() {

    const duration = 5000;

    let exit_origin = `t>${duration}`;

    let x_origin = `linear(t,0,500,${duration})`;

    let y_origin = `easeOutElastic(t,0,200,${duration})`;

    let o_origin = `0.5*(1-t/${duration})+0.5`;

    let anchor = findNodeHandle(this.refs._anchor);
    let token = bindingx.bind({
      eventType: 'timing',
      exitExpression: exit_origin,
      props: [
        {
          element: anchor,
          property: 'transform.translateX',
          expression: x_origin
        },
        {
          element: anchor,
          property: 'transform.translateY',
          expression: y_origin
        },
        {
          element: anchor,
          property: 'opacity',
          expression: o_origin
        }
      ]
    },this.onStateChange);

    this._token = token.token;
  }

  onStateChange = (e)=>{
    ToastAndroid.show('event:'+JSON.stringify(e), ToastAndroid.SHORT);
  }

  onUnBind() {
    if (this._token === null) {
      return;
    }
    bindingx.unbind({
      token: this._token,
      eventType: 'timing'
    });
  }

  render() {
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

        <View
          ref="_anchor"
          style={styles.anchor}
        />

      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF',
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 10,
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
