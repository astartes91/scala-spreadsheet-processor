package org.bibliarij.spreadsheetprocessor

/**
  * @author Vladimir Nizamutdinov (astartes91@gmail.com)
  */
class SpreadSheet(spreadSheet: Seq[Seq[String]]) {

  override def toString: String =
    spreadSheet.map(row => row.reduce((cell, cell1) => s"$cell\t$cell1")).reduce((row, row1) => s"$row\n$row1")

  def getInternalMap: Seq[Seq[String]] = spreadSheet
}

object SpreadSheet {
  def apply(spreadSheet: Seq[Seq[String]]): SpreadSheet = new SpreadSheet(spreadSheet)
}
