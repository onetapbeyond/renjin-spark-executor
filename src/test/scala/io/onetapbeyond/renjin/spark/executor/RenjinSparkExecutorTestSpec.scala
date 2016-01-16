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
package io.onetapbeyond.renjin.spark.executor

import io.onetapbeyond.renjin.spark.executor.R._
import io.onetapbeyond.renjin.r.executor._
import org.apache.spark._
import org.scalatest._
import scala.collection.JavaConverters._

class RenjinSparkExecutorTestSpec
	extends FlatSpec with Matchers with BeforeAndAfter {

	private val batchDataSize = 10
	private val master = "local[2]"
	private val appName = "renjin-spark-executor-test"
	private var sc: SparkContext = _

	// Prepare SparkContext (sc) ahead of each unit test.
	before {
		val conf = new SparkConf().setMaster(master)
								  .setAppName(appName)
	    sc = new SparkContext(conf)
	}

	// Release SparkContext (sc) following each unit test.
	after {
		if (sc != null)
		sc.stop()
	}

	"Spark RDD[RenjinTask] analyze transformation" should "execute RenjinTask on JVM." in {

		// Prepare sample Spark batch test data.
		val numRDD = sc.parallelize(1 to batchDataSize)

		// Prepare RDD[RenjinTask], the sample RenjinTasks that will
		// analyze the Spark batch sample data.
		val taskRDD = numRDD.map(num => { 

			// Build and return sample RenjinTask instance.
			Renjin.R()
    				.code("rnorm(n,mean)")
    				.input("n", 4)
            .input("mean", num)
    				.build()
		})

		// Generate RDD[RenjinResult] by executing the analyze operation
		// on RDD[RenjinTask] using the Renjin JVM-based interpreter.
		val resultRDD = taskRDD.analyze
		resultRDD.cache

		// Process sample RDD[RenjinResult].
		val resultCount = resultRDD.count
		val successCount = resultRDD.filter(result => result.success).count

		// Verify RDD[RenjinResult] data.
		assert(resultCount == batchDataSize)
		assert(resultCount == successCount)
	}

}
