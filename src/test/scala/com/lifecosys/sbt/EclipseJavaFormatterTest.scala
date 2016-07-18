package com.lifecosys.sbt

import org.scalatest.FunSuite

/**
 *
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 * @author <a href="mailto:guyang@lansent.com">Young Gu</a>
 */
class EclipseJavaFormatterTest extends FunSuite {

  test("Check simple class") {
    val code =
      """
        |public class Demo{private            String                      name;}
        |
        |
        |
      """.stripMargin
    val formatter: EclipseJavaFormatter = EclipseJavaFormatter(getClass.getResourceAsStream("/java-conversion-with-line140.properties"), "UTF-8")

    val expected =
      """
        |public class Demo {
        |    private String name;
        |}
      """.stripMargin
    assert(expected.trim === formatter.format(code).trim)

  }

  test("Check class with long line...") {
    val code =
      """
        |public class Demo{private            String                      name;
        |public String eclipseJavaFormattergetClasgetResourceAsStreamFormattergetClasgetResourceAsStreamFormattergetClasgetResourceAsStreamFormattergetClasgetResourceAsSt(String message){
        |return "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        |}
        |
        |public String eclipseJavaFormatte(String messagemessagemessagemessagemessage,Long modemodemodemodemodemodemodemodemodemodemodemodemodemode) throws UnsupportedOperationException,  IllegalArgumentException   ,IOException   ,   IllegalAccessError{
        |return "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        |}
        |}
        |
        |
        |
      """.stripMargin
    val formatter: EclipseJavaFormatter = EclipseJavaFormatter(getClass.getResourceAsStream("/java-conversion-with-line140.properties"), "UTF-8")

    val expected =
      """
       public class Demo {
        |    private String name;
        |
        |    public String eclipseJavaFormattergetClasgetResourceAsStreamFormattergetClasgetResourceAsStreamFormattergetClasgetResourceAsStreamFormattergetClasgetResourceAsSt(
        |	    String message) {
        |	return "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        |    }
        |
        |    public String eclipseJavaFormatte(String messagemessagemessagemessagemessage,
        |	    Long modemodemodemodemodemodemodemodemodemodemodemodemodemode) throws UnsupportedOperationException, IllegalArgumentException,
        |	    IOException, IllegalAccessError {
        |	return "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        |    }
        |}
      """.stripMargin
    assert(expected.trim === formatter.format(code).trim)

  }

}
