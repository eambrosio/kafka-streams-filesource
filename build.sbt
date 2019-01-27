import com.typesafe.sbt.packager.docker._
import sbt._

name := "kafka-streams"
version := "0.1"
scalaVersion := "2.12.8"

enablePlugins(DockerPlugin, JavaAppPackaging, DockerSpotifyClientPlugin, SbtAvro)
fork in run := true

lazy val dockerSettings: Seq[Def.Setting[_]] = Seq(
  maintainer in Docker := "Log parser",
  packageSummary in Docker := "Log parser for Clarity.ai",
  packageDescription := "Log parser for Clarity.ai",
  dockerUpdateLatest := false,
  version in Docker := version.value,
  dockerBaseImage := "java:8-jre-alpine",
  dockerCommands ++= Seq(
    ExecCmd("RUN", "apk", "add", "--no-cache", "bash")
  ),
  daemonUser in Docker := "root"
)

lazy val logParser = project
  .in(file("log-parser"))
  .settings(moduleName := "kafka-streams-log-parser")
  .settings(logParserSettings)
  .settings(dockerSettings)
  .settings(packageName in Docker := "kafka-streams-log-parser")
  .settings(stringType in AvroConfig := "String")
  .enablePlugins(JavaAppPackaging, JavaAgent, SbtAvro)



lazy val allModules: Seq[ProjectReference] = Seq(
  logParser
)

lazy val allModulesDeps: Seq[ClasspathDependency] =
  allModules.map(
    ClasspathDependency(_, None)
  )

lazy val root = project
  .in(file("."))
  .settings(
    name := "kafka-streams",
    scalaVersion := "2.12.8",
  )
  .aggregate(allModules: _*)
