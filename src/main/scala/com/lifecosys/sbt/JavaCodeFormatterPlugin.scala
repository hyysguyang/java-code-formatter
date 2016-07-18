/*
 * Copyright 2011-2012 Typesafe Inc.
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

package com.lifecosys.sbt

import sbt.Keys._
import sbt.{ AutoPlugin, File, SettingKey, TaskKey, _ }

import scala.collection.immutable.Seq

/**
 *
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 * @author <a href="mailto:guyang@lansent.com">Young Gu</a>
 */
object JavaCodeFormatterPlugin extends AutoPlugin {

  object JavaCodeFormatterKeys {

    val javaCodeFormatter: TaskKey[Seq[File]] = TaskKey[Seq[File]]("format-java", "Format (Java) sources using SbtJavaCodeFormatter")

    val eclipseFormatterFile: SettingKey[Option[File]] =
      SettingKey[Option[File]](
        "eclipseFormatterFile",
        "SbtJavaCodeFormatter eclipse formatter file"
      )

    val eclipseFormatterOptions: SettingKey[String] =
      SettingKey[String](
        "eclipseFormatterOptions",
        "SbtJavaCodeFormatter eclipse formatter Optioins"
      )

  }

  import JavaCodeFormatterKeys._

  override lazy val projectSettings = javaFormattingProjectSettings
  override val trigger = allRequirements

  def javaFormattingProjectSettings: Seq[Setting[_]] =
    defaultFormatSettings ++ List(
      compileInputs in (Compile, compile) <<= (compileInputs in (Compile, compile)) dependsOn (javaCodeFormatter in Compile),
      compileInputs in (Test, compile) <<= (compileInputs in (Test, compile)) dependsOn (javaCodeFormatter in Test)
    )

  def defaultFormatSettings: Seq[Setting[_]] =
    noConfigSettings ++ inConfig(Compile)(configSettings) ++ inConfig(Test)(configSettings)

  def noConfigSettings = {
    List(
      eclipseFormatterFile in javaCodeFormatter := None,
      eclipseFormatterOptions in javaCodeFormatter := "",
      includeFilter in Global in javaCodeFormatter := "*.java"
    )
  }

  def configSettings: Seq[Setting[_]] = {
    List(
      (sourceDirectories in Global in javaCodeFormatter) := List(javaSource.value),
      javaCodeFormatter := JavaCodeFormatter(
        (eclipseFormatterFile in javaCodeFormatter).value,
        (eclipseFormatterOptions in javaCodeFormatter).value,
        (sourceDirectories in javaCodeFormatter).value.toList,
        (includeFilter in javaCodeFormatter).value,
        (excludeFilter in javaCodeFormatter).value,
        thisProjectRef.value,
        configuration.value,
        streams.value
      )
    )

  }
}
