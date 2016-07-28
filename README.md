# How to use it

Add plugin dependent to project/plugins.sbt

```scala
resolvers += Resolver.url("hyysguyang-sbt-plugins", url("https://dl.bintray.com/hyysguyang/sbt-plugins/"))(Resolver.ivyStylePatterns)
addSbtPlugin("com.lifecosys.sbt" % "java-code-formatter" % "0.2")

```

This will add `formatJava` to your project.

The default will use the eclipse default Java Conventions format with project javacOptions source level, default is 1.8.
To customize it just:

```scala
import com.lifecosys.sbt.JavaCodeFormatterPlugin.JavaCodeFormatterKeys._
val formattingSettings = List(
eclipseProfileFile in javaCodeFormatter := Some(file("docs/develop/coding-style/JavaConventions-variant.xml"))
)
```
You can use all configuration such as:


```scala
import com.lifecosys.sbt.JavaCodeFormatterPlugin.JavaCodeFormatterKeys._
val formattingSettings = List(
eclipseProfileFile in javaCodeFormatter := Some(file("docs/develop/coding-style/JavaConventions-variant.xml")),
eclipsePrefFile in javaCodeFormatter := Some(file("docs/develop/coding-style/JavaConventions-variant.xml")),
eclipseFormatterOptions in javaCodeFormatter :=
      """
        |org.eclipse.jdt.core.formatter.alignment_for_binary_expression=16
        |org.eclipse.jdt.core.formatter.alignment_for_compact_if=16
        |org.eclipse.jdt.core.formatter.alignment_for_conditional_expression=80
        |org.eclipse.jdt.core.formatter.alignment_for_enum_constants=0
      """.stripMargin
)

```

Please note that the sequence `eclipseProfileFile <- eclipsePrefFile <- eclipseFormatterOptions`, the later will override the previous.

# How to Build it

Just checkout the source code and run `sbt test` to run all the test case.

# License

[Apache 2.0](https://www.apache.ogirg/licenses/LICENSE-2.0.html)