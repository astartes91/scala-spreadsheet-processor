package org.bibliarij.spreadsheetprocessor

import scala.io.StdIn

object ConsoleInputReader extends InputReader {
  
  override def readLine(): String = StdIn.readLine()
}
