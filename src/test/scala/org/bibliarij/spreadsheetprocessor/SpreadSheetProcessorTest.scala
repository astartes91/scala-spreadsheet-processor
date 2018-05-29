package org.bibliarij.spreadsheetprocessor

import org.assertj.core.api.Assertions
import org.junit.Test

class SpreadSheetProcessorTest {

    /**
      * @verifies return correct result
      */
    @Test
    def process_shouldReturnCorrectResult(): Unit = {
      val inputSeq: Seq[Seq[String]] = Seq(Seq("1", "'Test"), Seq("Error", "=1+2"), Seq("", ""))
      val outputSpreadSheet: SpreadSheet = new SpreadSheetProcessor(inputSeq).process()
      val outputSeq: Seq[Seq[String]] = outputSpreadSheet.getInternalSeq
      val row1: Seq[String] = outputSeq(0)
      val row2: Seq[String] = outputSeq(1)
      val row3: Seq[String] = outputSeq(2)
      Assertions.assertThat(row1(0)).isEqualTo("1")
      Assertions.assertThat(row1(1)).isEqualTo("Test")
      Assertions.assertThat(row2(0)).isEqualTo("#Error")
      Assertions.assertThat(row2(1)).isEqualTo("3")
      Assertions.assertThat(row3(0)).isEqualTo("")
    }
}