

![BindingX_250.png | center | 259x249](https://gw.alipayobjects.com/zos/skylark/5a72d0d4-c8b1-43c3-b968-d6b7d9245821/2018/png/e6c6f604-771b-4518-9d06-d6c1b48da4f9.png "")

### BindingX

![PRs welcome | left](https://img.shields.io/badge/PRs-welcome-brightgreen.svg "")
![license | left](https://img.shields.io/badge/license-Apache--2.0-brightgreen.svg "")
基于 `weex / React Native` 的富交互解决方案。官网: TODO

它提供了一种称之为 `表达式绑定(Expression Binding)` 的机制可以在 weex 上让手势等复杂交互操作以60fps的帧率流畅执行，而不会导致卡顿，因而带来了更优秀的用户体验 :tada: :tada: :tada:。

### 简要介绍

由于 `weex/RN`框架底层使用的 `JS-Native Bridge` 具有天然的异步特性，这使得 `JS` 和 `Native` 之间的通信会有固定的性能损耗，因此在一些复杂的实时交互场景中(如手势)，JS 代码很难以高帧率运行，这极大地限制了框架的能力。目前官方并没有很好的方式解决。

而我们通过探索，提出了一种全新的方式用来解决这个问题，方案称之为 `Expression Binding` 。它的核心思想是**将"交互行为"以表达式的方式描述，并提前预置到Native从而避免Native与JS频繁通信。**

进一步阅读: TODO (超链接到 教程-简介 )

### 示例展示

TODO 准备几组gif截图。

### 特性

* 复杂但流畅的交互效果
* 强大的表达式解析引擎
* 丰富的缓动函数


### weex接入

#### 前置条件

确保你已经集成了[weex\_sdk](https://github.com/apache/incubator-weex)。

#### Android:

手动集成。

* 在你项目中的`build.gradle`中添加依赖:


```
compile 'com.alibaba.android:bindingx:{latest_version}'
```

* 在工程的合适位置(如Application#onCreate)注入`BindingX`模块。


```java
BindingX.register()
```

#### iOS:

TODO // 待补充

### React Native接入
#### 前置条件

确保你已经集成了react native。

#### android

TODO

#### iOS

TODO

### 文档与教程

TODO 超链接

### 谁在使用


<div class="bi-table">
 <table>
   <colgroup><col width="90px"><col width="90px"><col width="90px"><col width="90px"></colgroup>
   <tbody>
    <tr>
      <td><div data-type="p">淘宝</div></td>
      <td><div data-type="p">天猫</div></td>
      <td><div data-type="p">优酷</div></td>
      <td><div data-type="p">飞猪</div></td>
    </tr>
    <tr>
      <td><div data-type="p"></div><div data-type="image" data-display="block" data-align="left" data-src="https://img.alicdn.com/tfs/TB1N.thdzuhSKJjSspjXXci8VXa-256-256.png_60x60.jpg" data-width=><span><img src="https://img.alicdn.com/tfs/TB1N.thdzuhSKJjSspjXXci8VXa-256-256.png_60x60.jpg" width=""/></span></div></td>
      <td><div data-type="p"></div><div data-type="image" data-display="block" data-align="left" data-src="https://gw.alipayobjects.com/zos/skylark/f893e0a7-b7d6-4bdf-8f7a-986d48ad3db6/2018/png/2d0020da-e40a-431e-8ddc-49a6b7e118c7.png" data-width=60><span><img src="https://gw.alipayobjects.com/zos/skylark/f893e0a7-b7d6-4bdf-8f7a-986d48ad3db6/2018/png/2d0020da-e40a-431e-8ddc-49a6b7e118c7.png" width="60"/></span></div><div data-type="p"></div></td>
      <td><div data-type="p"></div><div data-type="image" data-display="block" data-align="left" data-src="https://gw.alipayobjects.com/zos/skylark/3c32d6f3-1336-4d25-ab9a-1c1d82316c4f/2018/png/5122a38d-f4c3-47a2-b4e5-6d3c9095f8c6.png" data-width=60><span><img src="https://gw.alipayobjects.com/zos/skylark/3c32d6f3-1336-4d25-ab9a-1c1d82316c4f/2018/png/5122a38d-f4c3-47a2-b4e5-6d3c9095f8c6.png" width="60"/></span></div></td>
      <td><div data-type="p"></div><div data-type="image" data-display="block" data-align="left" data-src="https://gw.alipayobjects.com/zos/skylark/c1c0b23b-e10b-400c-a05b-b59e031fcc55/2018/png/bc25abf1-6c1a-4af9-b42a-6f314af02979.png" data-width=60><span><img src="https://gw.alipayobjects.com/zos/skylark/c1c0b23b-e10b-400c-a05b-b59e031fcc55/2018/png/bc25abf1-6c1a-4af9-b42a-6f314af02979.png" width="60"/></span></div><div data-type="p"></div></td>
    </tr>
   </tbody>
 </table>
</div>

### 支持

* 如果您有任何想法或者建议，欢迎提交PR。
* 使用过程中，如果碰到了无法解决的问题，可以新建一个issue。
* 欢迎加入钉钉聊天群


![Snip20180115_20.png | left | 229x229](https://gw.alipayobjects.com/zos/skylark/fc869da6-10d4-4f27-b21e-104f1c27fcb5/2018/png/229d5857-b02b-44a3-b654-794fb1a47ddf.png "")

### 协议

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
