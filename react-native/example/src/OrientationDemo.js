/**
 * Created by rowandjj on 2018/2/1.
 */
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

export default class TimingDemo extends Component {


    _token = null;

    componentWillMount() {
        let self = this;
        DeviceEventEmitter.addListener('stateChanged', function(e: Event) {
            ToastAndroid.show('event:'+JSON.stringify(e), ToastAndroid.SHORT);
        });
    }


    onBind(){
        let anchor = findNodeHandle(this.refs._anchor);
        let token = NativeModules.bindingx.bind({
            eventType: 'orientation',
            options: {
                sceneType: '2d' //2d场景会返回x,y分量
            },
            props: [
                {
                    element: anchor,
                    property: 'transform.translateX',
                    expression: {
                        origin: 'x+0',
                        transformed: "{\"type\":\"+\",\"children\":[{\"type\":\"Identifier\",\"value\":\"x\"},{\"type\":\"NumericLiteral\",\"value\":0}]}"
                    }
                },
                {
                    element: anchor,
                    property: 'transform.translateY',
                    expression: {
                        origin: 'y+0',
                        transformed: "{\"type\":\"+\",\"children\":[{\"type\":\"Identifier\",\"value\":\"y\"},{\"type\":\"NumericLiteral\",\"value\":0}]}"
                    }
                }
            ]
        });

        this._token = token.token;
    }

    onUnBind(){
        if(this._token === null) {
            return;
        }
        NativeModules.bindingx.unbind({
            token:this._token,
            eventType:'orientation'
        });
    }

    render() {
        return (
            <View style={styles.container}>

                <TouchableHighlight
                    onPress={()=>{this.onBind()}}
                    style={styles.button}
                >
                    <Text style={styles.text}>Bind</Text>
                </TouchableHighlight>

                <TouchableHighlight
                    onPress={()=>{this.onUnBind()}}
                    style={styles.button}
                >
                    <Text style={styles.text}>Unbind</Text>
                </TouchableHighlight>

                <View style={{
                    width: 240,
                    height: 240,
                    backgroundColor: '#0000ff',
                    justifyContent: 'center',
                     alignItems: 'center',
                     marginTop: 20,
                     marginLeft: 80
                  }}>
                    <View ref="_anchor" style={{
                      width: 100,
                      height: 100,
                      backgroundColor: '#00ff00',
                      justifyContent: 'center',
                      alignItems: 'center'
                    }}>
                        <Text style={styles.text}>Target</Text>

                    </View>
                </View>

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
        color: '#000000'
    },
    button:{
        height:50,
        backgroundColor: '#00ff00',
        justifyContent: 'center',
        alignItems: 'center',
        marginTop:20
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
