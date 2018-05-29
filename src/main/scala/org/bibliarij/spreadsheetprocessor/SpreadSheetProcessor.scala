package org.bibliarij.spreadsheetprocessor

import org.bibliarij.spreadsheetprocessor.OperatorFromChar.Operator

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
      try{
        inputCell.toLong
        inputCell
      } catch {
        case nfe: NumberFormatException => "#Error"
      }
    }
  }

  private def processExpression(inputCell: String): String = {
    val expression: String = inputCell.replaceFirst("=", "")
    val operatorsArray: Array[Char] = expression.toCharArray.filter(_.toString.matches(operatorsRegex))
    val operators: mutable.Queue[Char] = mutable.Queue(operatorsArray: _*)
    val operands: mutable.Queue[String] = mutable.Queue(expression.split(operatorsRegex): _*)

    var operand: String = operands.dequeue()
    if(operands.isEmpty && operators.isEmpty){
      if (operand.matches(cellReferenceRegex)){
        return getCellReferenceValue(operand)
      } else {
        try {
          operand.toLong
          return operand
        } catch {
          case nfe: NumberFormatException => return "#Error"
        }
      }
    }

    try {
      while(operands.nonEmpty){
        val nextOperand: String = operands.dequeue()
        val operator: Operator = OperatorFromChar(operators.dequeue())

        val operandLong: Long =
          if (operand.matches(cellReferenceRegex)){
            getCellReferenceValue(operand).toLong
          } else {
            operand.toLong
          }

        val nextOperandLong: Long =
          if (nextOperand.matches(cellReferenceRegex)){
            getCellReferenceValue(nextOperand).toLong
          } else {
            nextOperand.toLong
          }

        operand = operator(operandLong, nextOperandLong).toString
      }
      operand
    } catch {
      case nfe: NumberFormatException => "#Error"
    }
  }

  private def getCellReferenceValue(cellReference: String): String = {
    val i: Int = Integer.valueOf(cellReference(1).toString) - 1
    val j: Int = cellReference(0).toUpper - 65
    if (outputSeq(i)(j).isEmpty){
      outputSeq(i)(j) = processCell(inputSpreadSheet(i)(j))
    }
    outputSeq(i)(j)
  }
}

object OperatorFromChar {
  type Operator = (Long, Long) => Long
  def apply(operator: Char): Operator =
    operator match {
      case '-' => _ - _
      case '+' => _ + _
      case '/' => _ / _
      case '*' => _ * _
    }
}