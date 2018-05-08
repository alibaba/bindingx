import React, {Component} from 'react';

import {TabNavigator, StackNavigator, NavigationActions} from 'react-navigation';
import CardStackStyleInterpolator from 'react-navigation/src/views/CardStack/CardStackStyleInterpolator';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  Image,
  AppRegistry,
  TouchableHighlight,
  Button,
  SectionList,
} from 'react-native';

import AnimatedBall from './pan/AnimatedBall';
import OrientationDemo from './orientation/OrientationDemo';
import TimingDemo from './timing/TimingDemo';
import ScrollViewDemo from './scroll/ScrollViewDemo';

class ListItem extends React.Component {
  render() {
    return <TouchableHighlight {...this.props}><View {...this.props} style={{padding: 10}}>
      <Text style={{fontSize: 16}}>{this.props.title}</Text>
    </View></TouchableHighlight>
  }
}

class Header extends React.Component {
  render() {
    return <TouchableHighlight><View style={{backgroundColor: '#ccc', padding: 10}}>
      <Text style={{color: '#fff', fontSize: 24}}>{this.props.title}</Text>
    </View></TouchableHighlight>
  }
}

class HomeScreen extends React.Component {

  static navigationOptions = {
    title: 'BindingX'
  }

  state = {
    sections: [{
      data: [{
        title: 'A draggable ball',
        path: 'AnimatedBall'
      }], title: 'pan'
    }, {
      data: [{
        title: 'TimingDemo',
        path: 'TimingDemo'
      }], title: 'timing'
    }, {
      data: [{
        title: 'ScrollViewDemo',
        path: 'ScrollViewDemo'
      }], title: 'scroll'
    }, {
      data: [{
        title: 'OrientationDemo',
        path: 'OrientationDemo'
      }], title: 'orientation'
    }],

  };



  render() {
    return (
      <View style={{flex: 1}} onPress={()=>alert(2)}>
        <SectionList
          renderItem={({item, i}) => <ListItem key={i} onPress={() => {
            this.props.navigation.navigate('Detail', item);
          }} title={item.title}/>}
          renderSectionHeader={({section, i}) => <Header key={i} title={section.title}/>}
          sections={this.state.sections}
        />
      </View>
    );
  }
}

class DetailScreen extends React.Component {

  static navigationOptions = ({navigation}) => {
    return {
      title:navigation.getParam('title')
    }
  }

  componentWillMount() {

  }

  render() {
    let path = this.props.navigation.getParam('path');
    switch (path){
      case 'AnimatedBall':
        return (<AnimatedBall/>);
        break;
      case 'ScrollViewDemo':
        return (<ScrollViewDemo/>);
        break;
      case 'TimingDemo':
        return (<TimingDemo/>);
        break;
      case 'OrientationDemo':
        return (<OrientationDemo/>);
        break;
    }


  }
}

const styles = StyleSheet.create({
  icon: {
    width: 26,
    height: 26,
  },
});


const AppStackNavigator = StackNavigator({
  Home: {
    screen: HomeScreen
  },
  Detail: {
    screen: DetailScreen
  }
}, {
  cardStyle: {},
  headerMode: 'screen',
})


class MyApp extends React.Component {
  render() {
    return <AppStackNavigator/>
  }
}


AppRegistry.registerComponent('BindingXSample', () => MyApp);
