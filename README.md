
![BindingX_250.png | center | 259x249](https://gw.alipayobjects.com/zos/skylark/d52fede9-451f-4b9f-930e-0db65be6d012/2018/png/b062da91-8d5d-4184-b6b3-6c31ee399b98.png "")

# BindingX

![image | left](https://img.shields.io/badge/PRs-welcome-brightgreen.svg "")
![image | left](https://img.shields.io/badge/license-Apache--2.0-brightgreen.svg "")

[文档链接](https://lark.alipay.com/bindingx/doc_cn)

A new interaction way based on `weex` and `react native`.

It provides a way called `expression binding` for handling complex user interaction with views at 60 FPS in React Native and weex.

# Description

The async nature of the js-native bridge in react native and weex incurs an inherent performance penalty. This traditionally prevents JavaScript code from running at high framerates.

We exploreed and implemented a completely new approach to solve the problem. It's main idea is translate the user interaction into expression, and transfer those expressions into native environment. When events occurs (events such as user gesture), all computing task is running on the native side, NO redundant js-bridge calls any more.

# Glance

gif example

# Feature

* Complex but fluid user interaction
* Powerful expression parsing engine
* Plenty of easing functions


# Installation

#### on Android with Weex.

First，add dependency in `build.gradle`
```markup
compile 'com.alibaba.android:bindingx:{latest_version}'
```
Second, init it, for example at the Application.onCreate()
```markup
Binding.register()
```

#### on iOS with Weex.
carry on

#### on Android with ReactNative
supporting

#### on Android with ReactNative
supporting

# Document And Guide
coming soon

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
