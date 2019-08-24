package net.hotelling.harold.clazzfinder

import java.io.File

import org.junit.Test
import org.junit.Assert._

/**
  * Test cases for FileUtils.
  */
class FileUtilsTest {

  @Test
  def testZipFilenames(): Unit = {
    assertTrue("foo.zip is an archive",
      new FileUtils().isArchiveFile(new File("foo.zip")))
    assertTrue("foo.jar is an archive",
      new FileUtils().isArchiveFile(new File("foo.jar")))
    assertTrue("foo.war is an archive",
      new FileUtils().isArchiveFile(new File("foo.war")))
    assertFalse("foo is not an archive",
      new FileUtils().isArchiveFile(new File("foo")))
    assertFalse("foo.txt is not an archive",
      new FileUtils().isArchiveFile(new File("foo.txt")))
  }

}
