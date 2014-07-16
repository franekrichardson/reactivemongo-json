import sbt._
import Keys._

object HmrcBuild extends Build {


  import uk.gov.hmrc.DefaultBuildSettings

  val nameApp = "reactivemongo-json"
  val versionApp = "1.0.0-SNAPSHOT"

  val appDependencies = {
    import Dependencies._

    Seq(
      Compile.reactiveMongo,
      Compile.playJson,

      Test.scalaTest,
      Test.pegdown
    )
  }

  lazy val root = Project(nameApp, file("."), settings = DefaultBuildSettings(nameApp, versionApp, scalaversion = "2.11.1",  targetJvm = "jvm-1.7")() ++ Seq(
    libraryDependencies ++= appDependencies,
    publishArtifact in Test := false,
    resolvers := Seq(
      Opts.resolver.sonatypeReleases,
      Opts.resolver.sonatypeSnapshots,
      "typesafe-releases" at "http://repo.typesafe.com/typesafe/releases/",
      "typesafe-snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
    ),
    crossScalaVersions := Seq("2.11.1", "2.10.4")
  ) ++ SonatypeBuild()
  )

}

object Dependencies {

  object Compile {
    val reactiveMongo = "org.reactivemongo" %% "reactivemongo" % "0.10.5.akka23-SNAPSHOT" % "provided" cross CrossVersion.binary
    val playJson = "com.typesafe.play" %% "play-json" % "[2.1.0,2.3.1]" % "provided" cross CrossVersion.binary
  }

  sealed abstract class Test(scope: String) {

    val scalaTest = "org.scalatest" %% "scalatest" % "2.2.0" % scope cross CrossVersion.binary
    val pegdown = "org.pegdown" % "pegdown" % "1.4.2" % scope cross CrossVersion.Disabled
  }

  object Test extends Test("test")

  object IntegrationTest extends Test("it")

}


object SonatypeBuild {

  import xerial.sbt.Sonatype._

  def apply() = {
    sonatypeSettings ++ Seq(
      pomExtra := (<url>https://www.gov.uk/government/organisations/hm-revenue-customs</url>
        <licenses>
          <license>
            <name>Apache 2</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
          </license>
        </licenses>
        <scm>
          <connection>scm:git@github.com:hmrc/reactivemongo-json.git</connection>
          <developerConnection>scm:git@github.com:hmrc/reactivemongo-json.git</developerConnection>
          <url>git@github.com:hmrc/reactivemongo-json.git</url>
        </scm>
        <developers>
          <developer>
            <id>sgodbillon</id>
            <name>Stephane Godbillon</name>
            <url>http://stephane.godbillon.com</url>
          </developer>
          <developer>
            <id>mandubian</id>
            <name>Pascal Voitot</name>
            <url>http://mandubian.com</url>
          </developer>
          <developer>
            <id>xnejp03</id>
            <name>Petr Nejedly</name>
            <url>http://www.equalexperts.com</url>
          </developer>
          <developer>
            <id>duncancrawford</id>
            <name>Duncan Crawford</name>
            <url>http://www.equalexperts.com</url>
          </developer>
        </developers>)
    )
  }
}

