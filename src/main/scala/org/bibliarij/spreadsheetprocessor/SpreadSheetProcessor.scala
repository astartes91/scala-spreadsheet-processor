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

  private def processCell(inputCell: String): String = {

    if(inputCell.isEmpty){
      inputCell
    } else if(inputCell.startsWith("'")){
      inputCell.replaceFirst("'", "")
    } else if(inputCell.startsWith("=")) {
      processExpression(inputCell)
    } else {
      try {
        inputCell.toLong
        inputCell
      } catch {
        case nfe: NumberFormatException => "#Error: Incorrect cell value"
      }
    }
  }

  private def processExpression(inputCell: String): String = {

    val expression: String = inputCell.replaceFirst("=", "")
    val operatorsArray: Array[Char] = expression.toCharArray.filter(_.toString.matches(operatorsRegex))

    //assumed only binary operators and correct order of operators and operands
    val operators: mutable.Queue[Char] = mutable.Queue(operatorsArray: _*)
    val operands: mutable.Queue[String] = mutable.Queue(expression.split(operatorsRegex).filter(_.nonEmpty): _*)

    try {

      var accumulator: Long = getOperandLongValue(operands.dequeue())

      while(operands.nonEmpty){
        val operator: Operator = getOperator(operators.dequeue())
        val nextOperandLong: Long = getOperandLongValue(operands.dequeue())

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
    }
  }

  private def getOperandLongValue(operand: String): Long = {

    if (operand.matches(cellReferenceRegex)) {
      val i: Int = Integer.valueOf(operand(1).toString) - 1
      val j: Int = operand(0).toUpper - 65
      if (outputSeq(i)(j).isEmpty){
        outputSeq(i)(j) = processCell(inputSpreadSheet(i)(j))
      }
      outputSeq(i)(j).toLong
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