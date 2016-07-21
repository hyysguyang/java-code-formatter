package com.lifecosys.sbt

import java.io._
import java.nio.charset.Charset
import java.util.Properties

import org.eclipse.jdt.core.formatter.CodeFormatter
import org.eclipse.jdt.core.{ JavaCore, ToolFactory }
import org.eclipse.jface.text.Document

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.immutable.Seq
import scala.io.Source
import scala.xml.{ Node, NodeSeq, XML }

/**
 *
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 * @author <a href="mailto:guyang@lansent.com">Young Gu</a>
 */
object EclipseJavaFormatter {
  def loadProperties(eclipseFormatProperties: InputStream) = {
    import scala.collection.JavaConverters._
    val options = new Properties()
    options.load(eclipseFormatProperties)
    options.asScala.toMap
  }

  def extractProfileOptions(profile: Option[File]): Option[Map[String, String]] = {
    profile.filter(_.getName.endsWith(".xml")).map {
      p =>
        def extractOption(setting: Node) = {
          val id = (setting \ "@id").text
          val value = (setting \ "@value").text
          id -> value
        }
        val settings: NodeSeq = XML.load(new FileInputStream(p)) \\ "setting"
        val map: Seq[(String, String)] = settings.map(extractOption)
        map.toMap
    }
  }

  /**
   * Build format options, the later override the previous. That's say: profile <- eclipse preference <- options
   *
   * @param profile
   * @param prefProperties
   * @param options
   * @return
   */
  def formatOptions(profile: Option[File], prefProperties: Option[File], options: Map[String, String]): Map[String, String] = {
    val externalOptions = prefProperties.map(o => loadProperties(new FileInputStream(o)))
    val profileOptions = extractProfileOptions(profile)

    profileOptions.getOrElse(Map.empty) ++ externalOptions.getOrElse(Map.empty) ++ options
  }

  def apply(eclipseFormatProperties: File, utf8: String): EclipseJavaFormatter =
    EclipseJavaFormatter(new FileInputStream(eclipseFormatProperties), utf8)

  def apply(eclipseFormatProperties: InputStream, utf8: String): EclipseJavaFormatter = {
    val options = new Properties()
    options.load(eclipseFormatProperties)
    new EclipseJavaFormatter(options.asScala.toMap, utf8)
  }

}

case class EclipseJavaFormatter(options: Map[String, String], utf8: String = "UTF-8") {

  def format(javaCode: String): String = format(new ByteArrayInputStream(javaCode.getBytes(Charset forName utf8)))

  def format(javaCode: InputStream): String = {
    val contents = Source.fromInputStream(javaCode, utf8).mkString
    val document = new Document
    document.set(contents)
    val formatKind = CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS
    val formatOptions = Map(
      JavaCore.COMPILER_SOURCE -> "1.8",
      JavaCore.COMPILER_COMPLIANCE -> "1.8",
      JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM -> "1.8"
    ) ++ options
    val codeFormatter = ToolFactory.createCodeFormatter(formatOptions)
    codeFormatter.format(formatKind, contents, 0, contents.length, 0, null).apply(document)
    javaCode.close()
    document.get
  }

  def format(javaCode: File, utf8: String = "UTF-8"): Unit =
    Option(javaCode).filter(_.exists()).foreach {
      file =>
        val formattedCode = format(new FileInputStream(file))
        new FileWriter(javaCode).write(formattedCode)
    }

}
