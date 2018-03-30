import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  Button,
  ToastAndroid,
  NativeModules,
  findNodeHandle,
  TouchableHighlight,
  ScrollView,
  AppRegistry
} from 'react-native';


import AnimatedBall from './AnimatedBall';
// import TimingDemo from './TimingDemo';
// import OrientationDemo from './OrientationDemo';
import ScrollViewDemo from './ScrollViewDemo';

export default class App extends Component {

  constructor(props) {
    super(props);
    this.state = {
      currentExample: undefined
    }
  }


  onExamplePress(currentExample) {
    this.setState({currentExample});
  }

  render() {
    if (this.state.currentExample) {
      const ExampleComponent = this.state.currentExample;
      return <ExampleComponent />;
    }

    return (
      <ScrollView style={styles.container}>
        <Text
          style={styles.instructions}>
          BindingX Show Case (ReactNative)
        </Text>

        <TouchableHighlight
          onPress={this.onExamplePress.bind(this,AnimatedBall)}
          style={[styles.wrapper,styles.margin]}
          >
          <Text style={styles.text}>Pan Demo</Text>
        </TouchableHighlight>

        {/*<TouchableHighlight*/}
            {/*onPress={this.onExamplePress.bind(this,TimingDemo)}*/}
            {/*style={[styles.wrapper,styles.margin]}*/}
        {/*>*/}
          {/*<Text style={styles.text}>Timing Demo</Text>*/}
        {/*</TouchableHighlight>*/}

        {/*<TouchableHighlight*/}
            {/*onPress={this.onExamplePress.bind(this,OrientationDemo)}*/}
            {/*style={[styles.wrapper,styles.margin]}*/}
        {/*>*/}
          {/*<Text style={styles.text}>Orientation Demo</Text>*/}
        {/*</TouchableHighlight>*/}

        <TouchableHighlight
            onPress={this.onExamplePress.bind(this,ScrollViewDemo)}
            style={[styles.wrapper,styles.margin]}
        >
          <Text style={styles.text}>ScrollView Demo</Text>
        </TouchableHighlight>

      </ScrollView>
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
  }

});

AppRegistry.registerComponent('BindingXSample', () => App);
