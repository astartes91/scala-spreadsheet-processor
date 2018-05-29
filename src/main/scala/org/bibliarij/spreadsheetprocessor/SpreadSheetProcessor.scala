package org.bibliarij.spreadsheetprocessor

import scala.collection.mutable

class SpreadSheetProcessor(inputSpreadSheet: Seq[Seq[String]]) {

  private val cellReferenceRegex: String = "[A-Za-z][0-9]"
  private val operatorsRegex: String = """\+|-|\*|/"""

  private val outputSeq: mutable.Seq[mutable.Seq[String]] =
    mutable.Seq.fill(inputSpreadSheet.length, inputSpreadSheet(0).length) {""}

  /**
    * @should return correct result
    * @return
    */
  def process(): SpreadSheet = {

    for (i <- inputSpreadSheet.indices){
      for (j <- inputSpreadSheet(i).indices){
        outputSeq(i)(j) = processCell(inputSpreadSheet(i)(j))
      }
    }

    new SpreadSheet(outputSeq)
  }

  private def processCell(inputCell: String, alreadyVisitedCells: Seq[String] = Seq.empty): String = {

    if(inputCell.isEmpty || inputCell.startsWith("#Error")){
      inputCell
    } else if(inputCell.startsWith("'")){
      inputCell.replaceFirst("'", "")
    } else if(inputCell.startsWith("=")) {
      processExpression(inputCell, alreadyVisitedCells)
    } else {
      try {
        inputCell.toLong
        inputCell
      } catch {
        case nfe: NumberFormatException => "#Error: Incorrect cell value"
      }
    }
  }

  private def processExpression(inputCell: String, alreadyVisitedCells: Seq[String]): String = {

    val expression: String = inputCell.replaceFirst("=", "")
    val operatorsArray: Array[Char] = expression.toCharArray.filter(_.toString.matches(operatorsRegex))

    //assumed only binary operators and correct order of operators and operands
    val operators: mutable.Queue[Char] = mutable.Queue(operatorsArray: _*)
    val operands: mutable.Queue[String] = mutable.Queue(expression.split(operatorsRegex).filter(_.nonEmpty): _*)

    try {

      var accumulator: Long = getOperandLongValue(operands.dequeue(), alreadyVisitedCells)

      while(operands.nonEmpty){
        val operator: Operator = getOperator(operators.dequeue())
        val nextOperandLong: Long = getOperandLongValue(operands.dequeue(), alreadyVisitedCells)

        accumulator = operator(accumulator, nextOperandLong)
      }

      if (operators.nonEmpty){
        "#Error: Incorrect expression"
      } else {
        accumulator.toString
      }
    } catch {
      case nfe: NumberFormatException => "#Error: Incorrect operand value in expression"
      case iobe: IndexOutOfBoundsException => "#Error: Incorrect cell reference"
      case ise: IllegalStateException => ise.getMessage
    }
  }

  private def getOperandLongValue(operand: String, alreadyVisitedCells: Seq[String]): Long = {

    if (operand.matches(cellReferenceRegex)) {

      if(alreadyVisitedCells.contains(operand)){
        throw new IllegalStateException("#Error: Expression loop detected")
      }

      val newSeq: Seq[String] = alreadyVisitedCells :+ operand

      val i: Int = Integer.valueOf(operand(1).toString) - 1
      val j: Int = operand(0).toUpper - 65
      if (outputSeq(i)(j).isEmpty){
        outputSeq(i)(j) = processCell(inputSpreadSheet(i)(j), newSeq)
      }
      if(!outputSeq(i)(j).startsWith("#Error")){
        outputSeq(i)(j).toLong
      } else {
        throw new IllegalStateException(outputSeq(i)(j))
      }
    } else {
      operand.toLong
    }
  }

  type Operator = (Long, Long) => Long
  def getOperator(operator: Char): Operator =
    operator match {
      case '-' => _ - _
      case '+' => _ + _
      case '/' => _ / _
      case '*' => _ * _
    }
}