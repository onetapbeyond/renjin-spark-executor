resolvers += 
  "BeDataDriven" at "https://nexus.bedatadriven.com/content/groups/public"

lazy val root = (project in file(".")).
  settings(
    name := "hello-world",
    organization := "io.onetapbeyond",
    version := "1.0",
    scalaVersion := "2.10.6",
    libraryDependencies ++= Seq(
      "org.apache.spark" % "spark-core_2.10" % "1.6.0" % "provided",
      "io.onetapbeyond" % "renjin-spark-executor_2.10" % "1.0",
      "org.renjin" % "renjin-script-engine" % "0.8.1890"
    ),
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
  )
  