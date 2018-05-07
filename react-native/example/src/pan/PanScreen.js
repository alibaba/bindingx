import React, {Component} from 'react';
import {
  StyleSheet,
  View,
  Text,
  ScrollView,
  findNodeHandle,
  TouchableHighlight,
  NativeModules,
  NativeEventEmitter
} from 'react-native';

export default class PanScreen extends Component{
  render(){
    return <ScrollView>
      <View>
        <Text>pan</Text>
      </View>
    </ScrollView>;
  }
}