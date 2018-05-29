package org.bibliarij.spreadsheetprocessor

import org.bibliarij.spreadsheetprocessor.OperatorFromChar.Operator

import scala.collection.mutable

class SpreadSheetProcessor(inputSpreadSheet: Seq[Seq[String]]) {

  private val cellReferenceRegex: String = "[A-Za-z][0-9]"
  private val operatorsRegex: String = """\+|-|\*|/"""

  private val outputSeq: Seq[Seq[String]] = Seq.empty

  /**
    * @should return correct result
    * @return
    */
  def process(): SpreadSheet = {
    val seq: Seq[Seq[String]] = inputSpreadSheet
      .map(
        inputRow => {
          inputRow.map(
            inputCell => {
              if(inputCell.startsWith("'")){
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
          )
        }
      )
    new SpreadSheet(seq)
  }

  private def processExpression(inputCell: String): String = {
    val expression: String = inputCell.replaceFirst("=", "")
    val operatorsArray: Array[Char] = expression.toCharArray.filter(_.toString.matches(operatorsRegex))
    val operators: mutable.Queue[Char] = mutable.Queue(operatorsArray: _*)
    val operands: mutable.Queue[String] = mutable.Queue(expression.split(operatorsRegex): _*)
    var operand: String = operands.dequeue()

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
    "#Error"
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