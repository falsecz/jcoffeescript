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

import org.mozilla.javascript.JavaScriptException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;

public class Main {
    private static final int BUFFER_SIZE = 262144;
    private static final int BUFFER_OFFSET = 0;

    public static void main(String[] args) throws IOException {
        new Main().execute(args, System.out, System.in);
    }

    public void execute(String[] args, PrintStream out, InputStream in) throws IOException {
        final Collection<Option> options = readOptionsFrom(args);
        if(args.length  < 2) {
            System.out.println("arguments: coffee-script-1.8.0.js file.coffee");
            System.exit(1);
        }
        String coffeeLibPath = args[0];
        String coffeeFilePath = args[1];
        byte[] bytes = Files.readAllBytes(Paths.get(coffeeFilePath));
        String source= new String(bytes, "UTF-8");


        try {
            out.print(new JCoffeeScriptCompiler(coffeeLibPath, readOptionsFrom(args)).compile(source));
        } catch (JCoffeeScriptCompileException e) {
            JavaScriptException jse = (JavaScriptException)e.getCause();
            System.out.println(Paths.get(args[1]).toAbsolutePath().toString() + " " + jse.getValue());
        }
    }

    private String readSourceFrom(InputStream inputStream) {
        final InputStreamReader streamReader = new InputStreamReader(inputStream);
        try {
            try {
                StringBuilder builder = new StringBuilder(BUFFER_SIZE);
                char[] buffer = new char[BUFFER_SIZE];
                int numCharsRead = streamReader.read(buffer, BUFFER_OFFSET, BUFFER_SIZE);
                while (numCharsRead >= 0) {
                    builder.append(buffer, BUFFER_OFFSET, numCharsRead);
                    numCharsRead = streamReader.read(buffer, BUFFER_OFFSET, BUFFER_SIZE);
                }
                return builder.toString();
            } finally {
                streamReader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<Option> readOptionsFrom(String[] args) {
        final Collection<Option> options = new LinkedList<Option>();

        if (args.length == 3 && args[2].equals("--bare")) {
            options.add(Option.BARE);
        }
        return options;
    }
}
