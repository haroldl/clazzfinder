package net.hotelling.harold.clazzfinder

import java.io.{File, FileInputStream, InputStream}
import java.util.zip.{ZipEntry, ZipInputStream}

/**
  * Methods for working with the filesystem and jar/war/zip files.
  */
class FileUtils {

  def currentDirectory = new File(".").getCanonicalPath

  private val classFileRegex = """.*\.class$""".r
  private val archiveFileRegex = """.*\.(?:jar|war|zip)$""".r

  def isArchiveFile(path: File): Boolean = {
    val filenameMatches = path.getName match {
      case archiveFileRegex() => true
      case _ => false
    }
    (!path.isDirectory) && filenameMatches
  }

  def isClassFile(path: File): Boolean = {
    val filenameMatches = path.getName match {
      case classFileRegex() => true
      case _ => false
    }
    (!path.isDirectory) && filenameMatches
  }

  /**
    * Find any class or jar/war/zip archive files under the given path.
    *
    * @param path is the location to search.
    * @return If path is a file then the result will be just that
    *         file if it is an archive, or else an empty list. If
    *         path is a directory then the result will be any archive
    *         files found anywhere under that directory.
    */
  def findJavaObjectFiles(path: String): List[Entity] = {
    findJavaObjectFiles(new File(path))
  }

  /**
    * Find any class or jar/war/zip archive files under the given path.
    *
    * @param path is the location to search.
    * @return If path is a file then the result will be just that
    *         file if it is an archive, or else an empty list. If
    *         path is a directory then the result will be any archive
    *         files found anywhere under that directory.
    */
  def findJavaObjectFiles(path: File): List[Entity] = {
    if (path.isDirectory) {
      path.listFiles.toList.flatMap(findJavaObjectFiles)
    } else  if (isArchiveFile(path)) {
      List(ArchiveFile(path))
    } else  if (isClassFile(path)) {
      List(ClassFile(path))
    } else {
      List.empty
    }
  }
}

class ZipInputStreamIterator(val in: ZipInputStream) extends Iterator[ZipEntry] {
  private var peekedEntry: ZipEntry = in.getNextEntry

  override def hasNext: Boolean = peekedEntry != null

  override def next(): ZipEntry = {
    val prior = peekedEntry
    peekedEntry = in.getNextEntry
    prior
  }
}

trait Container {

}

abstract sealed class Entity {
  def contents: List[String]
  def inputStream: InputStream

  protected def listArchiveContents(path: String): List[String] = listArchiveContents(new File(path))
  protected def listArchiveContents(file: File): List[String] = listArchiveContents(new FileInputStream(file))
  protected def listArchiveContents(in: InputStream): List[String] = listArchiveContents(new ZipInputStream(in))
  protected def listArchiveContents(in: ZipInputStream): List[String] = new ZipInputStreamIterator(in)
    .filter(_.isDirectory == false).map(_.getName).toList
}

case class ClassFile(val file: File) extends Entity {
  override lazy val contents = List.empty
  override def inputStream = new FileInputStream(file)
}

case class ArchiveFile(val file: File) extends Entity {
  override lazy val contents = listArchiveContents(file)
  override def inputStream = new FileInputStream(file)
}

case class NestedArchiveFile(val filepath: String, val parent: Entity) extends Entity {
  override def contents = ???
  override def inputStream: InputStream = ???
}

case class NestedClassFile(val filepath: String, val parent: Entity) extends Entity {
  override lazy val contents = List.empty
  override def inputStream: InputStream = ???
}
