
![BindingX_250.png](https://img.alicdn.com/tfs/TB1ZG58bb1YBuNjSszeXXablFXa-400-400.png_250x250.jpg "")

# BindingX

[![Join the chat at https://gitter.im/alibaba/bindingx](https://badges.gitter.im/alibaba/bindingx.svg)](https://gitter.im/alibaba/bindingx?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

![image | left](https://img.shields.io/badge/PRs-welcome-brightgreen.svg "")
![image | left](https://img.shields.io/badge/license-Apache--2.0-brightgreen.svg "")
[![CircleCI](https://circleci.com/gh/alibaba/bindingx/tree/master.svg?style=svg)](https://circleci.com/gh/alibaba/bindingx/tree/master)

* [Read Documentation](https://alibaba.github.io/bindingx/guide/introduce)
* [中文](https://github.com/alibaba/bindingx/blob/master/README_cn.md)

A new interaction way based on `weex` & `react native` & `html5` .

It provides a way called `expression binding` for handling complex user interaction with views at 60 FPS in React Native and weex :tada: :tada: :tada: .

# Description

The async nature of the js-native bridge in react native and weex incurs an inherent performance penalty. This traditionally prevents JavaScript code from running at high framerates.

We exploreed and implemented a completely new approach to solve the problem. It's main idea is translate the user interaction into expression, and transfer those expressions into native environment. When events occurs (events such as user gesture), all computing task is running on the native side, NO redundant js-bridge calls any more. [Read More](https://alibaba.github.io/bindingx/guide/introduce)

# Glance

Below are some examples which is using bindingx. You can get more examples by running our playground app. Or you can write your own example use our [online playground](https://alibaba.github.io/bindingx/playground), have fun:)

<div align="center">
    <img style="margin-right:10px" src="https://gw.alicdn.com/tfs/TB1fES5bhGYBuNjy0FnXXX5lpXa-320-563.gif" width = "200" height = "350"/>
    <img style="margin-right:10px" src="https://gw.alicdn.com/tfs/TB1hOaKbbGYBuNjy0FoXXciBFXa-320-563.gif" width = "200" height = "350"/>
    <img style="margin-right:10px" src="https://gw.alicdn.com/tfs/TB1LCmUbkyWBuNjy0FpXXassXXa-320-563.gif" width = "200" height = "350"/>
    <img src="https://gw.alicdn.com/tfs/TB1FRGZbeuSBuNjy1XcXXcYjFXa-320-563.gif" width = "200" height = "350"/>
</div>

# Demo examples
  1. pan gesture: [rax](https://jsplayground.taobao.org/raxplayground/34ceb3e5-8927-4e0c-a282-2dd37c9d7b74)  [vue](https://jsplayground.taobao.org/vueplayground/1518d8ac-4403-414f-ba83-616eb8b77dc6)  [rn](https://github.com/alibaba/bindingx/blob/master/react-native/example/src/AnimatedBall.js)
  2. timing: [rax](https://jsplayground.taobao.org/raxplayground/31211efb-d643-4cd0-8e9e-46b0c29ddd50)  [vue](https://jsplayground.taobao.org/vueplayground/6a016074-225c-461e-bfa7-b73b8336ea3d)  [rn](https://github.com/alibaba/bindingx/blob/master/react-native/example/src/TimingDemo.js)
  3. scroll: [rax](https://jsplayground.taobao.org/raxplayground/8e3b0234-f218-41e2-b146-db76a00e4096)  [vue](https://jsplayground.taobao.org/vueplayground/0fe39539-f08c-4be0-a589-499be32f6351)  [rn](https://github.com/alibaba/bindingx/blob/master/react-native/example/src/ScrollViewDemo.js)
  4. orientation: [rax](https://jsplayground.taobao.org/raxplayground/1d3ed4e1-506b-4308-bffa-ecf241a0cc70)  [vue](https://jsplayground.taobao.org/vueplayground/18a9115c-c85b-4a12-a1b2-2b0c401a6eb6)  [rn](https://github.com/alibaba/bindingx/blob/master/react-native/example/src/OrientationDemo.js)

# RealLife examples

  1. Draggable ball: [rax](https://jsplayground.taobao.org/raxplayground/3ec5c8ef-42ff-47fb-9791-4bd7c257b4a7)  [vue](https://jsplayground.taobao.org/vueplayground/de9c7e84-2dc0-4873-8bb0-ce899e64f6ab)
  2. Swipeable card: [rax](https://jsplayground.taobao.org/raxplayground/7ac0f12b-72e7-4aa5-b398-693ba7b34cd6)  [vue](https://jsplayground.taobao.org/vueplayground/9e4899f6-0fe1-4ffa-86ec-b9c28d22bae9)
  3. Expandable menu: [rax](https://jsplayground.taobao.org/raxplayground/3f93ffd1-3028-4a9e-9e94-0188973bc44b)  [vue](https://jsplayground.taobao.org/vueplayground/3a388c50-18f8-45d3-b1cf-3f5f0c226c19)
  4. Slide layout: [rax](https://jsplayground.taobao.org/raxplayground/34e0eae9-ca2d-481f-94b5-239732651eeb)  [vue](https://jsplayground.taobao.org/vueplayground/925802dc-c7c9-4309-b1e1-f83458bb39c4)
  5. Circle menu: [rax](https://jsplayground.taobao.org/raxplayground/0b2fa94c-b107-422f-8c2c-60481af89d31)  [vue](https://jsplayground.taobao.org/vueplayground/42ffd6b2-9ff6-4161-8224-34779b3af7e6)
  6. Navigation with Tab: [rax](https://jsplayground.taobao.org/raxplayground/b8583160-f63f-4ab6-9f98-af7a3da283f8)  [vue](https://jsplayground.taobao.org/vueplayground/2f9e0733-b853-4d97-b350-2630c1a50c83)
  7. Ripple effect: [rax](https://jsplayground.taobao.org/raxplayground/c4a295f5-bec2-485e-8e05-de80c7274191)  [vue](https://jsplayground.taobao.org/vueplayground/2741ac64-3956-4dc9-ad61-d59b5768d97f)

Note: Weex has two DSL (rax & vue). The link is jumping to JS-Playground. But now our JS-Playground not support React-Native code, so it is jumping to plain source code.

> You can also contribute your examples to us by open an `pull request`. And we will display your example here if it's cool enough.

# Feature

* Complex but fluid user interaction
* Powerful expression parsing engine
* Plenty of easing functions


# Installation

### Weex

*Prerequisites*: integrate [weex sdk](https://github.com/apache/incubator-weex) to your application.

##### Android

We provide two ways to integrate bindingx plugin.

 1. manual integration

    * add dependencies in your application's build.gradle

        ```
        implementation 'com.alibaba.android:bindingx-core:1.0.1'
        implementation 'com.alibaba.android:bindingx_weex_plugin:1.0.1'
        ```

    * register bindingx plugin in code. (`Application#onCreate`, for example)

        ```
        BindingX.register()
        ```

 2. use weex plugin loader

    * add dependencies in your application's build.gradle

      ```
      implementation 'com.alibaba.android:bindingx-core:1.0.1'
      implementation 'com.alibaba.android:bindingx_weex_plugin:1.0.1'
      implementation 'org.weex.plugin:plugin-loader:1.0.0'
      ```

    * register bindingx plugin use plugin loader.

      ```
      WeexPluginContainer.loadAll(getApplicationContext());
      ```

#### iOS

add dependencies in your application's `Podfile`

```
  pod 'BindingX', '~> 1.0.3'
```

module will be registed automatically.

### React Native

*Prerequisites*: integrate [react native](https://facebook.github.io/react-native/) to your application.

1. install dependencies: `npm install react-native-bindingx --save`;
2. link library: `react-native link react-native-bindingx`;

#### Android 

add `bindingx-core` library to `build.gradle` manually:

  ```
  implementation 'com.alibaba.android:bindingx-core:{latest_version}'
  ```  

1. migrate to [android gradle plugin 3.0](https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration);
2. use `implementation` instead of `compile` in your build.gradle;
3. add google repository to your root build.gradle:

  ```
  repositories {
    google()
    ...
  }
  ```

# Who is using

| taobao | tmall | youku | fliggy |
| :--- | :--- | :--- | :--- |
| ![Taobao](https://img.alicdn.com/tfs/TB1N.thdzuhSKJjSspjXXci8VXa-256-256.png_60x60.jpg "") | ![tmall](https://img.alicdn.com/tps/TB15a7wOFXXXXcgXVXXXXXXXXXX-256-256.png_60x60.jpg "") | ![youku](https://img.alicdn.com/tfs/TB1jjyxhwoQMeJjy1XaXXcSsFXa-256-256.png_60x60.jpg "") | ![fliggy](https://img.alicdn.com/tfs/TB11rPqRXXXXXc_apXXXXXXXXXX-256-256.png_60x60.jpg "") |


## Contributing
* Any PR is welcome
* Dingding chat group.


![Snip20180115_20.png | left | 229x229](https://gw.alipayobjects.com/zos/skylark/fcc2b92e-06c2-4d8f-88ff-5cfb983735bf/2018/png/dfae0a43-4ecb-4f62-a5fb-d3f092cad66a.png "")

## License
```
Copyright 2018 Alibaba Group

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
