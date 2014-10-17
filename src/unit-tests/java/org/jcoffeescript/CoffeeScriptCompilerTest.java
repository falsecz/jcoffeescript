/*
 * Copyright 2010 David Yeung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jcoffeescript;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.mozilla.javascript.JavaScriptException;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class CoffeeScriptCompilerTest {
    private static String coffeePath = "./coffee-script-1.8.0.js";

    @Test
    public void shouldCompileWithDefaultOptions() throws JCoffeeScriptCompileException {
        assertThat(compiling("a = 1"),
                allOf(
                        containsString("a = 1"),
                        containsFunctionWrapper()
                )
        );
    }

    @Test
    public void shouldCompileWithoutFunctionWrapper() throws JCoffeeScriptCompileException {
        assertThat(compiling("a = 1", Option.BARE), not(containsFunctionWrapper()));
    }


    @Test
    public void shouldFailOnError()  {
        try {
            String result = compiling("\na=10\n\n\nclass class eee");
//            System.out.println(result);
            assert(false);

        }catch (JCoffeeScriptCompileException e) {
//            System.out.println(e.getStackTrace());
            JavaScriptException jse = (JavaScriptException)e.getCause();

//            System.out.println(jse.sourceName() + " " +  jse.lineNumber() + ":" + jse.columnNumber() + " " + jse.getValue());
//            jse.printStackTrace();
//            System.out.println(jse.getValue());
            assert(jse.getValue().toString().equals("SyntaxError: unexpected class"));

        }

//        assertThat(compiling("a = 1", Option.BARE), not(containsFunctionWrapper()));
    }

    private Matcher<String> containsFunctionWrapper() {
        return allOf(startsWith("(function() {\n"), endsWith("\n}).call(this);\n"));
    }

    private String compiling(String coffeeScriptSource, Option... options) throws JCoffeeScriptCompileException {
//        return new JCoffeeScriptCompiler(coffeeScriptSource, Arrays.asList(options)).compile(coffeeScriptSource);
        String result = new JCoffeeScriptCompiler(coffeePath, Arrays.asList(options)).compile(coffeeScriptSource);
//        System.out.println(result);
        return result;
    }
}
