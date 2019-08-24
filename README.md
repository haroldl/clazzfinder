clazzfinder
=

**FYI**: This is a hobby project and a work-in-progress.

The idea
-

It can be hard to find the Java class you're looking for. It might be in a deeply nested directory in the filesystem,
inside an archive file, or even inside an archive file inside an archive file. This is a tool for finding and examining
Java class files.

A class file contains the compiled version of a single Java class in the from of Java byte code. Because you may need to
investigate code where you do not have the source code, this tool works with Java byte code instead of source code.

Current features
-

* Show all class files and archive files (JAR, WAR, and ZIP files), and the files inside the archive files.
* Explores the directory given on the command line, or else the current directory.

Planned features
-

* Both a command line and GUI user interface.
* The ability to decompile Java byte code.

Implementation notes
-

Oftentimes working with a Java class file means loading the class in the JVM. This tool does not take that approach
because loading a class in the JVM requires you to already have an the classpath all of the other classes it uses, and
all the classes they us, and so on. This is called the set of all dependencies, including transitive dependencies
(dependencies of dependencies). To pull this off, we look at the Java byte code directly instead of loading the class
into the JVM and using the reflection API.

A key facet of this is that WAR, JAR, and ZIP files are all actually ZIP files. The different between just a ZIP file
and the other two is that WAR and JAR files have well-defined rules about some special files that go inside, e.g. in the
`META-INF` or `WEB-INF` directories.

It is common for a web application written in Java to be packaged as a WAR file which contains the class files with the
code for the web app as well as JAR files for the dependencies. So being able to explore the classfiles inside a JAR
file inside a WAR file is important. This turns out to be easy to do because the standard Java libraries provide a class
java.util.zip.ZipInputStream that combines the functionality of an InputStream for reading the bytes of a file that is
stored inside an archive file as well as listing the contents of that archive file. So to read the bytes of a class file
inside a JAR file inside a WAR file, the code would look something like this:

    InputStream warFile = new FileInputStream("myWarFile.war");
    ZipInputStream warZip = new ZipInputStream(warFile); 

    // Call warZip.getNextEntry() until the entry.getName() is "WEB-INF/lib/myDependency.jar"

    // The following works because warZip is positioned at the beginning of myDependency.jar
    // and will act as an InputStream for the contents of that (nested) file:
    ZipInputStream dependencyJar = new ZipIntputStream(warZip);

    // Call dependencyJar.getNextEntry() until the entry.getName() is "path/to/Some.class"
    // Then treat dependencyJar as an InputStream for reading the bytes of that class flie.
