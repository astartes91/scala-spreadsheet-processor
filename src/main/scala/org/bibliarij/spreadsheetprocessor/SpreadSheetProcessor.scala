package org.bibliarij.spreadsheetprocessor

object SpreadSheetProcessor {

  def process(inputSpreadSheet: SpreadSheet): SpreadSheet = {
    SpreadSheet(inputSpreadSheet.getInternalMap.map(inputRow => inputRow.map(inputCell => inputCell)))
  }
}
