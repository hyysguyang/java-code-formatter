import sbt.Keys._
import sbt._

/**
  * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
  * @author <a href="mailto:Young.Gu@lifcosys.com">Young Gu</a>
  */
object ProjectBuild extends Build {

  import Dependencies._

  lazy val project = Project("java-code-formatter", file("."))
    .settings(BuildSettings.projectBuildSettings: _*)
    .settings(libraryDependencies ++= compile(eclipse) ++ test(scalatest))
    .settings(sbtPlugin := true)
}

object Dependencies {

  val eclipse = "org.eclipse.jdt" % "org.eclipse.jdt.core" % "3.10.0"
  val scalatest = "org.scalatest" %% "scalatest" % "2.2.5"

  def compile(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")

  def test(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")

}


object BuildSettings {
  val VERSION = "0.1"

  lazy val projectBuildSettings = basicSettings ++ formattingSettings ++ publishSettings

  val basicSettings = Defaults.coreDefaultSettings ++ Seq(
    version := VERSION,
    homepage := Some(new URL("https://lifecosys.com/developer/sbt-java-code-formatter")),
    organization := "com.lifecosys.sbt",
    organizationHomepage := Some(new URL("https://lifecosys.com")),
    description := "Sbt java code fromatter, base on eclipse formatter.",
    licenses +=("Apache 2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    startYear := Some(2016),
    credentials += Credentials(Path.userHome / ".bintray" / ".credentials"),
    scalacOptions ++= List(
      "-unchecked",
      "-deprecation",
      "-Xlint",
      "-language:_",
      "-target:jvm-1.6",
      "-encoding", "UTF-8"
    )
  )

  val formattingSettings = {
    import com.typesafe.sbt.SbtScalariform.ScalariformKeys
    import scalariform.formatter.preferences._
    ScalariformKeys.preferences :=
      FormattingPreferences()
        .setPreference(AlignParameters, true)
        .setPreference(CompactStringConcatenation, true)
        .setPreference(CompactControlReadability, false)
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 40)
        .setPreference(SpacesWithinPatternBinders, true)
        .setPreference(DoubleIndentClassDeclaration, true)
        .setPreference(SpacesAroundMultiImports, true)
  }

  val publishSettings = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    publishArtifact in(Compile, packageSrc) := true,
    pomExtra :=
      <url>https://github.com/hyysguyang/java-code-formatter</url>
        <licenses>
          <license>
            <name>Apache 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>https://github.com/hyysguyang/java-code-formatter.git</url>
          <connection>scm:https://github.com/hyysguyang/java-code-formatter.git</connection>
        </scm>
        <developers>
          <developer>
            <id>hyysguyang</id>
            <name>Young Gu</name>
            <url>https://plus.google.com/u/0/+YoungGu</url>
          </developer>
        </developers>
  )

}
