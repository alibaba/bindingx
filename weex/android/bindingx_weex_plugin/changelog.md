### bindingx-weex-plugin changelog

#### 1.0.0

1. 初始版本。

#### 1.0.1

1. 暂时去除weex-plugin注解。

#### 1.0.2

1. 支持改变border-radius属性
2. getComputedStyle支持border-radius

#### 1.0.3

1. 支持改变margin/padding/width/height等layout属性，调用weex统一接口。 (依赖最低weex sdk版本: 0.18.1.57)

#### 1.0.4

1. 修复视图更新逻辑中潜在的内存泄露问题(view#post)