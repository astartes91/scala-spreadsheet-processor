package org.bibliarij.spreadsheetprocessor

import org.junit.Test

import scala.io.Source

class FileInputReader(name: String) extends InputReader {

  private val lineIterator: Iterator[String] = Source.fromResource(name).getLines()

  override def readLine(): String = lineIterator.next()
}

object CorrectFileInputReader extends FileInputReader("correct_input.txt")

class IoTest {

  @Test
  def test: Unit = {
    Io.start(CorrectFileInputReader)
  }
}