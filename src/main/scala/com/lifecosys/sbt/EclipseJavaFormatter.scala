package com.lifecosys.sbt

import java.io._
import java.nio.charset.Charset
import java.util.Properties

import org.eclipse.jdt.core.{ JavaCore, ToolFactory }
import org.eclipse.jdt.core.formatter.CodeFormatter
import org.eclipse.jface.text.Document

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.io.Source

/**
 *
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 * @author <a href="mailto:guyang@lansent.com">Young Gu</a>
 */
object EclipseJavaFormatter {
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
