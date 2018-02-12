### BindingX Parser

#### Install

```
npm i bindingx-parser --save

```

#### Usage

```
import {parse} from 'bindingx-parser';

const originExpression = 'x+0';

parse(originExpression);  // will return expression syntax tree

```