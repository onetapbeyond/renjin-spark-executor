/*
 * Copyright 2016 David Russell
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
package io.onetapbeyond.renjin.spark.executor.examples;

import io.onetapbeyond.renjin.r.executor.*;
import org.apache.spark.*;
import org.apache.spark.api.java.*;
import java.util.*;

/*
 * HelloWorld
 *
 * The canonical "Hello, World!" example application that demonstrates
 * the basic usage of REX to deliver R analytics capabilities within a
 * Spark batch solution.
 */
public class HelloWorld {

  public static void main(String[] args) {

    try {

      JavaSparkContext sc = initSparkContext();

      /*
       * Initialize a basic batch data source for the example by
       * generating a dataRDD<Integer>.
       */
      JavaRDD<Integer> dataRDD =
      	sc.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));

      /*
       * Map over dataRDD<Integer> to produce an rTaskRDD<RenjinTask>.
       * Each RenjinTask executes the R stats::rnorm function on
       * the input data provided to generate a random 
       * normal distribution of result values.
       */
      JavaRDD<RenjinTask> rTaskRDD = dataRDD.map(number -> {

        /*
         * Build RenjinTask for R stats::rnorm call.
         */	
        return Renjin.R()
                     .code("rnorm(n,mean)")
                     .input("n", 4)
                     .input("mean", number)
                     .build();
      });

      /*
       * Execute REX R analysis on rTaskRDD[RenjinTask]
       * in order to generate rTaskResultRDD[RenjinResult].
       */
      JavaRDD<RenjinResult> rTaskResultRDD =
                      rTaskRDD.map(rTask -> rTask.execute());

      /*
       * As this is an example application we can simply use the
       * foreach() operation on the RDD to force the computation
       * and to output the results.
       */
      rTaskResultRDD.foreach(rTaskResult -> {
      	System.out.println("HelloWorld: stats::rnorm input=" +
      		rTaskResult.input() + " returned=" + rTaskResult.output());
      });

    } catch(Exception ex) {
      System.out.println("HelloWorld: caught ex=" + ex);
    }

  }

  private static JavaSparkContext initSparkContext() {
    SparkConf conf = new SparkConf().setAppName(APP_NAME);
    return new JavaSparkContext(conf);
  }

  private static String APP_NAME = "REX Hello World Example";

}