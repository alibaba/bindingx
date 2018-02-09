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

        let exit_origin = 't>2000';
        let exit_transformed = "{\"type\":\">\",\"children\":[{\"type\":\"Identifier\",\"value\":\"t\"},{\"type\":\"NumericLiteral\",\"value\":2000}]}";

        let x_origin = 'linear(t,0,1000,2000)';
        let x_transformed = "{\"type\":\"CallExpression\",\"children\":[{\"type\":\"Identifier\",\"value\":\"linear\"},{\"type\":\"Arguments\",\"children\":[{\"type\":\"Identifier\",\"value\":\"t\"},{\"type\":\"NumericLiteral\",\"value\":0},{\"type\":\"NumericLiteral\",\"value\":1000},{\"type\":\"NumericLiteral\",\"value\":2000}]}]}";

        let y_origin = 'easeOutElastic(t,0,500,2000)';
        let y_transformed = "{\"type\":\"CallExpression\",\"children\":[{\"type\":\"Identifier\",\"value\":\"easeOutElastic\"},{\"type\":\"Arguments\",\"children\":[{\"type\":\"Identifier\",\"value\":\"t\"},{\"type\":\"NumericLiteral\",\"value\":0},{\"type\":\"NumericLiteral\",\"value\":500},{\"type\":\"NumericLiteral\",\"value\":2000}]}]}";

        let o_origin = '0.5*(1-t/2000)+0.5';
        let o_transformed = "{\"type\":\"+\",\"children\":[{\"type\":\"*\",\"children\":[{\"type\":\"NumericLiteral\",\"value\":0.5},{\"type\":\"-\",\"children\":[{\"type\":\"NumericLiteral\",\"value\":1},{\"type\":\"/\",\"children\":[{\"type\":\"Identifier\",\"value\":\"t\"},{\"type\":\"NumericLiteral\",\"value\":2000}]}]}]},{\"type\":\"NumericLiteral\",\"value\":0.5}]}";

        let anchor = findNodeHandle(this.refs._anchor);
        let token = NativeModules.bindingx.bind({
            eventType:'timing',
            exitExpression: {
              transformed: exit_transformed,
              origin: exit_origin
            },
            props:[
                {
                    element: anchor,
                    property:'transform.translateX',
                    expression:{
                        transformed:x_transformed,
                        origin:x_origin
                    }},
                {
                    element: anchor,
                    property:'transform.translateY',
                    expression:{
                        transformed:y_transformed,
                        origin:y_origin
                    }},
                {
                    element: anchor,
                    property: 'opacity',
                    expression: {
                        transformed: o_transformed,
                        origin: o_origin
                    }
                }
            ]
        });
        ToastAndroid.show('token>>>>>'+JSON.stringify(token), ToastAndroid.SHORT);

        this._token = token.token;
    }

    onUnBind(){
        let anchor = findNodeHandle(this.refs._anchor);
        if(this._token === null) {
            return;
        }
        NativeModules.bindingx.unbind({
            token:this._token,
            eventType:'timing'
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
