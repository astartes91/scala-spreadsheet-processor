package org.bibliarij.spreadsheetprocessor

object Io {

  def start(inputReader: InputReader) = {
    val spreadsheetParametersStr: String = inputReader.readLine()
    val spreadSheetParameters: Array[String] = spreadsheetParametersStr.split("\t")
    require(spreadSheetParameters.length == 2)
    val spreadSheetHeight: Long = spreadSheetParameters(0).toLong
    val spreadSheetWeight: Long = spreadSheetParameters(1).toLong

    val spreadSheet: Seq[Seq[String]] =
      for (i <- 1L to spreadSheetHeight) yield {
        val row: String = inputReader.readLine()
        val cells: Seq[String] = row.split("\t").toSeq
        require(cells.length == spreadSheetWeight)
        cells
      }

    val outputSpreadSheet: SpreadSheet = SpreadSheetProcessor.process(SpreadSheet(spreadSheet))
    println(outputSpreadSheet)
  }
}
