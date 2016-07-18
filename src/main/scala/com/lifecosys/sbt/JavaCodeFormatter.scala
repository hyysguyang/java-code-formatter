package com.lifecosys.sbt

import java.io.{ ByteArrayInputStream, FileInputStream, InputStream }
import java.util.Properties

import sbt.Keys._
import sbt.{ File, FileFilter, _ }

import scala.collection.immutable.Seq

/**
 *
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 * @author <a href="mailto:guyang@lansent.com">Young Gu</a>
 */
private object JavaCodeFormatter {

  def apply(
    eclipseFormatterFile:    Option[File],
    eclipseFormatterOptions: String,
    sourceDirectories:       Seq[File],
    includeFilter:           FileFilter,
    excludeFilter:           FileFilter,
    ref:                     ProjectRef,
    configuration:           Configuration,
    streams:                 TaskStreams
  ): Seq[File] = {

    streams.log.info(s"${Reference.display(ref)}($configuration) Start java code formatter task:")

    def loadProperties(eclipseFormatProperties: InputStream) = {
      import scala.collection.JavaConverters._
      val options = new Properties()
      options.load(eclipseFormatProperties)
      options.asScala.toMap
    }

    val externalOptions = eclipseFormatterFile.map {
      o => loadProperties(new FileInputStream(o))
    }

    val inlineOptions = Option(eclipseFormatterOptions).map {
      o => loadProperties(new ByteArrayInputStream(o.getBytes))
    }

    val options = externalOptions.getOrElse(Map.empty) ++ inlineOptions.getOrElse(Map.empty)

    val files = sourceDirectories.descendantsExcept(includeFilter, excludeFilter).get.toSet
    def format(file: File) = {
      streams.log.debug(s"${Reference.display(ref)}($configuration) Formatting file: ${file.getAbsolutePath}")
      val contents = IO.read(file)
      val formatted = EclipseJavaFormatter(options).format(contents)
      if (formatted != contents) {
        IO.write(file, formatted)
        Some(file)
      } else None

    }
    def logFormattedFile(f: File): Unit = {
      streams.log.debug(s"${Reference.display(ref)}($configuration) Formatted file: ${f.getAbsolutePath}")
    }
    val formattedFiles: List[File] = files.filter(_.exists()).flatMap(format).toList
    formattedFiles foreach logFormattedFile
    formattedFiles
  }

}
