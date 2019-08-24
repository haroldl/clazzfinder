package net.hotelling.harold.clazzfinder

import java.awt._
import javax.swing._
import javax.swing.tree.DefaultMutableTreeNode

/**
  * UI for using ClazzFinder.
  */
class UI(val pathToExplore: String) extends Runnable {
  override def run(): Unit = {
    val frame = new JFrame("ClazzFinder")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    val container = new JPanel()
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS))
    frame.getContentPane.add(container)

    val root = new DefaultMutableTreeNode(pathToExplore)
    val fileUtils = new FileUtils()
    fileUtils.findJavaObjectFiles(pathToExplore).foreach { entity =>
      val childNode = new DefaultMutableTreeNode(entity)
      root.add(childNode)

      entity.contents.foreach { classFileName =>
        val classFileNode = new DefaultMutableTreeNode(classFileName)
        childNode.add(classFileNode)
      }
    }
    val tree = new JTree(root)
    val pane = new JScrollPane(tree)
    container.add(pane)

    frame.pack()
    frame.setLocationByPlatform(true)
    frame.setVisible(true)
  }
}

object UI {
  def run(pathToExlpore: String): Unit = {
    SwingUtilities.invokeLater(new UI(pathToExlpore))
  }
}
