import React, { Component } from 'react';
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

import { DeviceEventEmitter } from 'react-native';

export default class AnimatedBall extends Component {

  _panResponder = PanResponder.create({
    onStartShouldSetPanResponder: (evt, gestureState) => true,
    onPanResponderGrant: (evt, gestureState) => {
      this.onBind();
    }
  });

  _x = 0;
  _y = 0;

  componentWillMount() {
    let self = this;
    DeviceEventEmitter.addListener('stateChanged', function(e: Event) {
        if(e.state === 'end') {
          self._x += e.deltaX;
          self._y += e.deltaY;
        }
    });
  }

  onBind(){

    let expression_x_origin = "x+" + this._x;
    let expression_x_transformed = '{"type":"+","children":[{"type":"Identifier","value":"x"},{"type":"NumericLiteral","value":"'+this._x+'"}]}';

    let expression_y_origin = "y+" + this._y;
    let expression_y_transformed = '{"type":"+","children":[{"type":"Identifier","value":"y"},{"type":"NumericLiteral","value":"'+this._y+'"}]}';

    let anchor = findNodeHandle(this.refs._anchor);
    let token = NativeModules.bindingx.bind({
      eventType:'pan',
      anchor:anchor,
      props:[
        {
          element: anchor,
          property:'transform.translateX',
          expression:{
            transformed:expression_x_transformed,
            origin:expression_x_origin
        }},
        {
          element: anchor,
          property:'transform.translateY',
          expression:{
            transformed:expression_y_transformed,
            origin:expression_y_origin
        }}
      ]
    });
    ToastAndroid.show('token>>>>>'+JSON.stringify(token), ToastAndroid.SHORT);
  }

  onUnBind(){
    let anchor = findNodeHandle(this.refs._anchor);

    NativeModules.bindingx.unbind({
      token:anchor,
      eventType:'pan'
    });
  }

  render() {
    return (
      <View style={styles.container}>
        <View
            ref="_anchor"
            style={styles.anchor}
            {...this._panResponder.panHandlers}
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
  text:{
    textAlign: 'center',
    color: '#ffffff',
  },
  wrapper:{
    backgroundColor:'#0000ff',
    height:48,
    alignItems:'center',
    justifyContent:'center'
  },
  margin:{
    marginTop:20
  },
  anchor:{
    width:80,
    height:80,
    backgroundColor:'#ff0000',
    marginTop:48
  }


});
