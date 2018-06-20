/**
 * Copyright 2018 Alibaba Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.android.bindingx.core.internal;

import com.alibaba.android.bindingx.core.BindingXJSFunctionRegister;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ExpressionTest {

    // x+1
    private static final String EXP_1 = "{\"type\":\"+\",\"children\":[{\"type\":\"Identifier\",\"value\":\"x\"},{\"type\":\"NumericLiteral\",\"value\":1}]}";

    // (x+5)*10-y/2
    private static final String EXP_2 = "{\"type\":\"-\",\"children\":[{\"type\":\"*\",\"children\":[{\"type\":\"+\",\"children\":[{\"type\":\"Identifier\",\"value\":\"x\"},{\"type\":\"NumericLiteral\",\"value\":5}]},{\"type\":\"NumericLiteral\",\"value\":10}]},{\"type\":\"/\",\"children\":[{\"type\":\"Identifier\",\"value\":\"y\"},{\"type\":\"NumericLiteral\",\"value\":2}]}]}";

    // max(x/2,y+10)
    private static final String EXP_3 = "{\"type\":\"CallExpression\",\"children\":[{\"type\":\"Identifier\",\"value\":\"max\"},{\"type\":\"Arguments\",\"children\":[{\"type\":\"/\",\"children\":[{\"type\":\"Identifier\",\"value\":\"x\"},{\"type\":\"NumericLiteral\",\"value\":2}]},{\"type\":\"+\",\"children\":[{\"type\":\"Identifier\",\"value\":\"y\"},{\"type\":\"NumericLiteral\",\"value\":10}]}]}]}";

    // y > 19
    private static final String EXP_4 = "{\"type\":\">\",\"children\":[{\"type\":\"Identifier\",\"value\":\"y\"},{\"type\":\"NumericLiteral\",\"value\":19}]}";

    // (y-2)/x > 1 ? 100 : 200
    private static final String EXP_5 = "{\"type\":\"?\",\"children\":[{\"type\":\">\",\"children\":[{\"type\":\"/\",\"children\":[{\"type\":\"-\",\"children\":[{\"type\":\"Identifier\",\"value\":\"y\"},{\"type\":\"NumericLiteral\",\"value\":2}]},{\"type\":\"Identifier\",\"value\":\"x\"}]},{\"type\":\"NumericLiteral\",\"value\":1}]},{\"type\":\"NumericLiteral\",\"value\":100},{\"type\":\"NumericLiteral\",\"value\":200}]}";

    // foo(1,2,'M')
    private static final String EXP_6 = "{\"type\":\"CallExpression\",\"children\":[{\"type\":\"Identifier\",\"value\":\"foo\"},{\"type\":\"Arguments\",\"children\":[{\"type\":\"NumericLiteral\",\"value\":1},{\"type\":\"NumericLiteral\",\"value\":2},{\"type\":\"StringLiteral\",\"value\":\"'M'\"}]}]}";

    @Test
    public void execute() throws Exception {
        Map<String,Object> scope = new HashMap<>();
        JSMath.applyToScope(scope);

        scope.put("x", 10d);
        scope.put("y", 12d);

        Expression e1 = new Expression(EXP_1);
        double value1 = (double) e1.execute(scope);
        assertEquals(11d, value1, 0.1);

        Expression e2 = new Expression(EXP_2);
        double value2 = (double) e2.execute(scope);
        assertEquals(144d, value2, 0.1);

        Expression e3 = new Expression(EXP_3);
        double value3 = (double) e3.execute(scope);
        assertEquals(22d, value3,0.1);


        Expression e4 = new Expression(EXP_4);
        boolean value4 = (boolean) e4.execute(scope);
        assertEquals(false, value4);

        Expression e5 = new Expression(EXP_5);
        double value5 = (double) e5.execute(scope);
        assertEquals(200d, value5, 0.1);

        Expression e6 = new Expression(EXP_6);

        BindingXJSFunctionRegister.getInstance().registerJSFunction("foo", new JSFunctionInterface() {
            @Override
            public Object execute(ArrayList<Object> arguments) throws NumberFormatException, JSONException {
                return arguments;
            }
        });

        scope.putAll(BindingXJSFunctionRegister.getInstance().getJSFunctions());

        ArrayList<Object> result = (ArrayList<Object>) e6.execute(scope);
        assertEquals(1.0, result.get(0));
        assertEquals(2.0, result.get(1));
        assertEquals("'M'", result.get(2));
    }

}