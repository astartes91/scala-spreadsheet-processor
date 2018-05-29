package org.bibliarij.spreadsheetprocessor

import org.assertj.core.api.Assertions
import org.junit.Test

class SpreadSheetProcessorTest {

    /**
      * @verifies return correct result
      */
    @Test
    def process_shouldReturnCorrectResult(): Unit = {
      val inputSeq: Seq[Seq[String]] = Seq(Seq("1", "'Test", "Error", "=1+2"))
      val outputSpreadSheet: SpreadSheet = new SpreadSheetProcessor(inputSeq).process()
      val outputSeq: Seq[Seq[String]] = outputSpreadSheet.getInternalSeq
      val row: Seq[String] = outputSeq(0)
      Assertions.assertThat(row(0)).isEqualTo("1")
      Assertions.assertThat(row(1)).isEqualTo("Test")
      Assertions.assertThat(row(2)).isEqualTo("#Error")
      Assertions.assertThat(row(3)).isEqualTo("3")
    }
}