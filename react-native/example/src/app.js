
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
  Button
} from 'react-native';


class MyHomeScreen extends React.Component {
  static navigationOptions = ({navigation, screenProps}) => {
    return {
      headerStyle: {
        // backgroundColor:'green',
        // height:1,
        // top:0,
        // paddingTop:0,
        // marginTop:0
      },
      headerLeft:<Button onPress={()=>navigation.goBack()} title={'Back'}/>,
      // headerRight: <Button title="000" onPress={()=>navigation.navigate('Detail')}/>
    }
  }

  componentWillMount() {

    let {navigation} = this.props;
    // navigation.addListener('willFocus',(e)=>{
    //  console.log('willFocus',e);
    // });
    // navigation.addListener('willBlur',(e)=>{
    //  console.log('willBlur',e);
    // });
    navigation.addListener('didFocus', (e) => {
      console.log('didFocus', e);
    });
    navigation.addListener('didBlur', (e) => {
      console.log('didBlur', e);
    });
  }


  render() {
    return (
      <View style={{flex: 1}}>
        <Button
          onPress={() => this.props.navigation.goBack()}
          title="Go back"
        />
        <Button
          onPress={() => this.props.navigation.navigate('Notifications')}
          title="Go to notifications"
        />
        <Button
          onPress={() => this.props.navigation.navigate('Detail')}
          title="Go to detail stack"
        />
      </View>
    );
  }
}

class MyNotificationsScreen extends React.Component {
  static navigationOptions = {
    // headerTitle: 'Notificationsxx',
    header: null,
    //   tabBarLabel: 'Notifications',
    //   tabBarIcon: ({tintColor}) => (
    //     <Image
    //       source={{uri:'https://gw.alicdn.com/tfs/TB1JApXXmB_XuNjy1XdXXcGEFXa-64-64.png'}}
    //       style={[styles.icon, {tintColor: tintColor}]}
    //     />
    //   ),
  };

  componentWillMount() {

    let {navigation} = this.props;
    // navigation.addListener('willFocus',(e)=>{
    //  console.log('willFocus',e);
    // });
    // navigation.addListener('willBlur',(e)=>{
    //  console.log('willBlur',e);
    // });
    navigation.addListener('didFocus', (e) => {
      console.log('MyNotificationsScreen didFocus', e);
    });
    navigation.addListener('didBlur', (e) => {
      console.log('MyNotificationsScreen didBlur', e);
    });
  }

  render() {
    return (
      <View style={{flex: 1}}>
        <Button
          onPress={() => this.props.navigation.goBack()}
          title="Go back"
        />
        <Button
          onPress={() => this.props.navigation.goBack()}
          title="Go back home"
        />
        <Button
          onPress={() => this.props.navigation.navigate('Detail')}
          title="Go to detail"
        />
        <Button
          onPress={() => this.props.navigation.navigate('Home')}
          title="Go to home"
        />
      </View>
    );
  }
}

class CommentScreen extends React.Component {
  render() {
    return (<View><Text>CommentScreen</Text></View>);
  }
}

class DetailScreen extends React.Component {

  static navigationOptions = ({navigation}) => {
    return {
      // header:null,
      // header:<View style={{height:10,backgroundColor:'red'}}><Text>test</Text></View>,
      // title: 'test2:' + navigation.state.routeName,
      headerRight: <Button title="Right" onPress={() => alert('share')}/>,
      headerBackTitle: '返回'
    }
  }

  componentWillMount() {

    let {navigation} = this.props;
    // navigation.addListener('willFocus',(e)=>{
    //  console.log('willFocus',e);
    // });
    // navigation.addListener('willBlur',(e)=>{
    //  console.log('willBlur',e);
    // });
    navigation.addListener('willFocus', (e) => {
      console.log('willFocus', e);
    });
    navigation.addListener('didBlur', (e) => {
      console.log('didBlur', e);
    });
  }

  render() {

    console.log('render detail')

    return (<View style={{flex: 1}}>
      <Button
        onPress={() => this.props.navigation.navigate('Comment')}
        title="Go to comment"
      />
    </View>);
  }
}

const styles = StyleSheet.create({
  icon: {
    width: 26,
    height: 26,
  },
});


const HomeTabNavigator = TabNavigator({
  Home: {
    screen: MyHomeScreen,
  },
  Notification: {
    screen: MyNotificationsScreen,
  }
}, {
  // initialRouteName: 'Home',
  navigationOptions: ({navigation}) => {
    return {
      // header: null,
      // title: navigation.state.routeName,
      headerTitle: navigation.state.routeName,
      // headerStyle: {backgroundColor: 'transparent'}
      //headerLeft: <View><Text>{navigation.state.routeName}</Text></View>
      // tabBarLabel:'test'
    }
  },
  // tabBarPosition: 'top',
  animationEnabled: true,
  swipeEnabled: true,
  tabBarOptions: {
    // showLabel: true,
    // labelStyle: {
    //   color: '#f60'
    // },
    // style:{
    //   opacity:.5
    // },
    // tabStyle: {
    // backgroundColor:'#f7f7f7'
    // },
    activeTintColor: '#e91e63',
    activeBackgroundColor: 'rgba(0,0,0,0)',
    // inactiveBackgroundColor: '#ccc'
  },
});


const AppStackNavigator = StackNavigator({
  Home: {
    screen: HomeTabNavigator
  },
  Detail: {
    screen: DetailScreen
  },
  Comment: {
    screen: CommentScreen
  }
}, {
  cardStyle: {
    // backgroundColor: 'rgba(0,0,0,0)'
  },
  //headerMode:'none',
  headerMode: 'screen',
  // mode:'modal',
  // Optional: Override the `navigationOptions` for the screen
  navigationOptions: ({navigation}) => {


    return {
      // gesturesEnabled:navigation.state.routeName === 'Detail'
      //header:<View style={{height:100,backgroundColor:'#fff'}}><Text style={{lineHeight:100,textAlign:'center'}}>xxx</Text></View>,
      //title: <Text style={{color:'red'}}>{navigation.state.routeName + 'xx'} </Text>,
      //headerLeft:<View><Text>{navigation.state.routeName}</Text></View>
    }
  },
  transitionConfig: () => ({
    // console.log('transitionConfig',arguments)
    // transitionSpec: {
    // duration: 300,
    // easing: Easing.out(Easing.poly(4)),
    // timing: Animated.timing,
    // },
    /*
     headerMode
     index
     layout
     navigation
     position
     progress
     router
     scene
     scenes
     screenProps
     */
    // screenInterpolator:CardStackStyleInterpolator.forVertical
    // screenInterpolator: (sceneProps) => {
    //   //console.log(sceneProps)
    //   const {layout, position, scene} = sceneProps;
    //   let {index} = scene;
    //
    //   const height = layout.initHeight;
    //   const width = layout.initWidth;
    //   const translateY = position.interpolate({
    //     inputRange: [index - 1, index, index + 1],
    //     outputRange: [height, 0, 0]
    //   });
    //   //
    //   const rotate = position.interpolate({
    //     inputRange: [index - 1, index, index + 1],
    //     outputRange: ['10deg', '0deg', '0deg']
    //   });
    //
    //
    //   // const translateX = position.interpolate({
    //   //   inputRange: [index - 1, index, index + 1],
    //   //   outputRange: [width, 0, 0],
    //   // });
    //
    //   const opacity = position.interpolate({
    //     inputRange: [index - 1, index, index + 1],
    //     outputRange: [0, 1, 1],
    //   });
    //
    //   return {transform: [{translateY}]};
    // }
  })
})


// gets the current screen from navigation state
function getCurrentRouteName(navigationState) {
  if (!navigationState) {
    return null;
  }
  const route = navigationState.routes[navigationState.index];
  // dive into nested navigators
  if (route.routes) {
    return getCurrentRouteName(route);
  }
  return route.routeName;
}


class MyApp extends React.Component {
  render() {
    return <AppStackNavigator onNavigationStateChange={(prevState, currentState) => {


      const currentScreen = getCurrentRouteName(currentState);
      const prevScreen = getCurrentRouteName(prevState);

      // console.log({
      //   currentScreen,
      //   prevScreen,
      //   prevState,
      //   currentState
      // })

      if (prevScreen !== currentScreen) {
        // the line below uses the Google Analytics tracker
        // change the tracker here to use other Mobile analytics SDK.
        //tracker.trackScreenView(currentScreen);
      }
    }}/>
  }
}


// export default MyApp;

AppRegistry.registerComponent('BindingXSample', () => MyApp);
