package net.hotelling.harold.clazzfinder

object ClazzFinder {
  val fileUtils = new FileUtils()

  def main(args: Array[String]): Unit = {
    val pathToExplore = if (args.size > 0) args(0) else fileUtils.currentDirectory
    UI.run(pathToExplore)
  }

}

