import React, { Component } from 'react';
import {
    StyleSheet,
    View,
    Text,
    NativeModules,
    findNodeHandle,
    TouchableHighlight,
    ToastAndroid
} from 'react-native';

export default class AnimatedBall extends Component {

  onBind(){

    let expression_x_origin = "x+" + 0;
    let expression_x_transformed = '{"type":"+","children":[{"type":"Identifier","value":"x"},{"type":"NumericLiteral","value":"'+0+'"}]}';

    let expression_y_origin = "y+" + 0;
    let expression_y_transformed = '{"type":"+","children":[{"type":"Identifier","value":"y"},{"type":"NumericLiteral","value":"'+0+'"}]}';

    let anchor = findNodeHandle(this.refs._anchor);
    NativeModules.bindingX.bind({
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
    }, function(e){
        ToastAndroid.show('A pikachu appeared nearby !'+JSON.stringify(e), ToastAndroid.SHORT);
    });

  }

  onUnBind(){
    let anchor = findNodeHandle(this.refs._anchor);

    NativeModules.bindingX.unbind({
      token:anchor,
      eventType:'pan'
    });
  }

  render() {
    return (
      <View style={styles.container}>
        <Text
          ref="my_text"
          style={styles.instructions}>
          REACT NATIVE Awesome Binding
        </Text>

        <TouchableHighlight
          onPress={()=>{this.onBind()}}
          style={styles.wrapper}
          >
          <Text style={styles.text}>bind</Text>
        </TouchableHighlight>

        <TouchableHighlight
          onPress={()=>{this.onUnBind()}}
          style={[styles.wrapper,styles.margin]}
          >
          <Text style={styles.text}>unbind</Text>
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
