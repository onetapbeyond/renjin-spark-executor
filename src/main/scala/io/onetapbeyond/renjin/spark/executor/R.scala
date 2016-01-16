package io.onetapbeyond.renjin.spark.executor

import org.apache.spark.rdd.RDD
import io.onetapbeyond.renjin.r.executor.RenjinTask
import io.onetapbeyond.renjin.r.executor.RenjinResult

class R(rdd:RDD[RenjinTask]) {
  def analyze():RDD[RenjinResult] = rdd.map(rTask => rTask.execute)
}

object R {
  implicit def addR(rdd: RDD[RenjinTask]) = new R(rdd)
}
