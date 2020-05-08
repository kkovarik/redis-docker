/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.openwise.redis.massimport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import cz.openwise.redis.clientlist.ClientlistApplication;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;


/**
 * @author Petr Juza
 * @since 2.0
 */
@SpringBootApplication
public class MassImportGeneration {

    private static final long RECORD_COUNT = 500000;

    private static final long RECORD_FILE_COUNT = 70000;

    private static final String KEY_PREFIX = "test-";

    private static final int MIN_VALUE_LENGTH = 50;

    private static final int MAX_VALUE_LENGTH = 6000;

    public static void main(String[] args) throws IOException {
        Assert.notNull(args, "args can't be null");

        SpringApplication.run(ClientlistApplication.class, args);

        if (args.length == 0) {
            System.out.println("ERR: there is no defined input file for generation");
        } else {
            System.out.println("Starting generating ...");
            long now = System.currentTimeMillis();
            File mainFile = new File(args[0]);
            long cycles = (RECORD_COUNT / RECORD_FILE_COUNT) + 1;

            for (int i = 0; i < cycles; i++) {
                File file = new File(mainFile.getAbsolutePath() + "_" + i);

                System.out.println(file);

                generateFile(file, i*RECORD_FILE_COUNT);
            }

            System.out.println("End of generating (time: " + (System.currentTimeMillis() - now) + " ms)");
        }
    }

    private static void generateFile(File file, long start) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        Random r = new Random();

        for (int i = 0; i < RECORD_FILE_COUNT; i++) {
            String key = KEY_PREFIX + (i+start);
            int valueLength = r.nextInt((MAX_VALUE_LENGTH - MIN_VALUE_LENGTH) + 1) + MIN_VALUE_LENGTH;
            String value = StringUtils.leftPad("", valueLength, "=");

            writer.write(generateSetCmd(key, value));
        }

        writer.close();
    }

    private static String generateSetCmd(String key, String value) {
        return "*3\r\n$3\r\nSET\r\n$" + key.length() + "\r\n" + key
                + "\r\n$" + value.length() + "\r\n" + value + "\r\n";
    }
}
