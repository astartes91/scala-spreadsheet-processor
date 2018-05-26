package org.bibliarij.spreadsheetprocessor

object SpreadSheetProcessor {

  def process(inputSpreadSheet: SpreadSheet): SpreadSheet = {
    val seq: Seq[Seq[String]] = inputSpreadSheet.getInternalMap
      .map(
        inputRow => {
          inputRow.map(
            inputCell => {
              if(inputCell.startsWith("'")){
                inputCell.replaceFirst("'", "")
              } else if(inputCell.startsWith("=")) {
                inputCell
              } else {
                try{
                  inputCell.toLong
                  inputCell
                } catch {
                  case nfe: NumberFormatException => "#Error"
                }
              }
            }
          )
        }
      )
    SpreadSheet(seq)
  }
}
