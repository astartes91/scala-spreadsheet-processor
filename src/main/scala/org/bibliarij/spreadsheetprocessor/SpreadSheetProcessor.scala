package org.bibliarij.spreadsheetprocessor

import org.bibliarij.spreadsheetprocessor.OperatorFromChar.Operator

import scala.collection.mutable

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
    SpreadSheet(seq)
  }

  private def processExpression(inputCell: String): String = {
    val expression: String = inputCell.replaceFirst("=", "")
    val operators: mutable.Queue[Character] = mutable.Queue()
    val operands: mutable.Queue[String] = mutable.Queue()
    var operand: String = ""
    expression.foreach(
      character => {
        val isOperator: Boolean =
          character.equals('+') || character.equals('-') || character.equals('/') || character.equals('*')
        if (isOperator){
          operators.enqueue(character)
          operands.enqueue(operand)
          operand = ""
        } else {
          operand += character
        }
      }
    )

    operands.enqueue(operand)
    operand = operands.dequeue()

    try {
      while(operands.nonEmpty){
        val nextOperand: String = operands.dequeue()
        val operator: Operator = OperatorFromChar(operators.dequeue())
        operand = operator(operand.toLong, nextOperand.toLong).toString
      }
      operand
    } catch {
      case nfe: NumberFormatException => "#Error"
    }
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