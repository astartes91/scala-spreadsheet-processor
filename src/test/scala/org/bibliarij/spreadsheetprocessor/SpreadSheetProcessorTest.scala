package org.bibliarij.spreadsheetprocessor

import org.assertj.core.api.Assertions
import org.junit.Test

class SpreadSheetProcessorTest {

    /**
      * @verifies return correct result
      */
    @Test
    def process_shouldReturnCorrectResult(): Unit = {
      val inputSeq: Seq[Seq[String]] = Seq(
        Seq("1", "'Test"),
        Seq("Error", "=1+5-2*B3/3"),
        Seq("", "4"),
        Seq("=B3", "=2"),
        Seq("=-2", "=-2+2"),
        Seq("=B1", "=Z0")
      )
      val outputSpreadSheet: SpreadSheet = new SpreadSheetProcessor(inputSeq).process()
      val outputSeq: Seq[Seq[String]] = outputSpreadSheet.getInternalSeq

      val row1: Seq[String] = outputSeq(0)
      Assertions.assertThat(row1(0)).isEqualTo("1")
      Assertions.assertThat(row1(1)).isEqualTo("Test")

      val row2: Seq[String] = outputSeq(1)
      Assertions.assertThat(row2(0)).isEqualTo("#Error: Incorrect cell value")
      Assertions.assertThat(row2(1)).isEqualTo("5")

      val row3: Seq[String] = outputSeq(2)
      Assertions.assertThat(row3(0)).isEqualTo("")

      val row4: Seq[String] = outputSeq(3)
      Assertions.assertThat(row4(0)).isEqualTo("4")
      Assertions.assertThat(row4(1)).isEqualTo("2")

      val row5: Seq[String] = outputSeq(4)
      Assertions.assertThat(row5(0)).isEqualTo("#Error: Incorrect expression")
      Assertions.assertThat(row5(1)).isEqualTo("#Error: Incorrect expression")

      val row6: Seq[String] = outputSeq(5)
      Assertions.assertThat(row6(0)).isEqualTo("#Error: Incorrect operand value in expression")
      Assertions.assertThat(row6(1)).isEqualTo("#Error: Incorrect cell reference")
    }
}