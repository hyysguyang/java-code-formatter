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

import java.io.{ ByteArrayInputStream, FileInputStream, InputStream }
import java.util.Properties

import org.eclipse.jdt.core.JavaCore
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

    val eclipseProfileFile: SettingKey[Option[File]] =
      SettingKey[Option[File]](
        "eclipseProfileFile",
        "SbtJavaCodeFormatter eclipse profile formatter file"
      )
    val eclipsePrefFile: SettingKey[Option[File]] =
      SettingKey[Option[File]](
        "eclipsePrefFile",
        "SbtJavaCodeFormatter eclipse preference formatter file"
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
      eclipseProfileFile in javaCodeFormatter := None,
      eclipsePrefFile in javaCodeFormatter := None,
      eclipseFormatterOptions in javaCodeFormatter := "",
      includeFilter in Global in javaCodeFormatter := "*.java"
    )
  }

  def configSettings: Seq[Setting[_]] = {
    List(
      (sourceDirectories in Global in javaCodeFormatter) := List(javaSource.value),
      javaCodeFormatter := format(
        (eclipseProfileFile in javaCodeFormatter).value,
        (eclipsePrefFile in javaCodeFormatter).value,
        (eclipseFormatterOptions in javaCodeFormatter).value,
        javacOptions.value.toList,
        (sourceDirectories in javaCodeFormatter).value.toList,
        (includeFilter in javaCodeFormatter).value,
        (excludeFilter in javaCodeFormatter).value,
        thisProjectRef.value,
        configuration.value,
        streams.value
      )
    )

  }

  def format(
    eclipseProfileFile:      Option[File],
    eclipsePrefFile:         Option[File],
    eclipseFormatterOptions: String,
    javacOptions:            List[String],
    sourceDirectories:       Seq[File],
    includeFilter:           FileFilter,
    excludeFilter:           FileFilter,
    ref:                     ProjectRef,
    configuration:           Configuration,
    streams:                 TaskStreams
  ): Seq[File] = {

    import EclipseJavaFormatter._
    val logTag = s"${Reference.display(ref)}($configuration): "
    streams.log.info(s"$logTag Starting java code formatter task:")
    val inlineOptions = Option(eclipseFormatterOptions).map {
      o => loadProperties(new ByteArrayInputStream(o.getBytes))
    }

    def extractValue(key: String) = javacOptions(javacOptions.indexOf(key) + 1)
    val sourceLevel = javacOptions.filter(v => v == "-source" || v == "-target").headOption.map(extractValue).getOrElse(sys.props("java.specification.version"))

    val sourceLevelOptions = Map(
      JavaCore.COMPILER_SOURCE -> sourceLevel,
      JavaCore.COMPILER_COMPLIANCE -> sourceLevel,
      JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM -> sourceLevel
    )
    val options = formatOptions(eclipseProfileFile, eclipsePrefFile, sourceLevelOptions ++ inlineOptions.getOrElse(Map.empty))

    val files = sourceDirectories.descendantsExcept(includeFilter, excludeFilter).get.toSet
    def format(file: File) = {
      streams.log.debug(s"$logTag Formatting file: ${file.getAbsolutePath}")
      val contents = IO.read(file)
      val formatted = EclipseJavaFormatter(options).format(contents)
      if (formatted != contents) {
        IO.write(file, formatted)
        streams.log.debug(s"$logTag Formatted file: ${file.getAbsolutePath}")
        Some(file)
      } else None

    }
    val formattedFiles: List[File] = files.filter(_.exists()).flatMap(format).toList
    streams.log.info(s"$logTag Total: ${files.size}, Formatted: ${formattedFiles.size} ")
    formattedFiles
  }

}
