package observatory

import org.junit.Test

trait ExtractionTest extends MilestoneSuite {
  private val milestoneTest = namedMilestoneTest("data extraction", 1) _

  // Implement tests for the methods of the `Extraction` object
  @Test def `checking average temps elements`: Unit = {
    val temps = Extraction.locateTemperatures(1, "/testStations.csv","/test1975.csv")
    //  val temps = Extraction.locateTemperatures(1, "/testStations2.csv","/testTemps2.csv")
    val tempsAvg = Extraction.locationYearlyAverageRecords(temps)

    assert(tempsAvg.find(p => p._1 == Location(48.033,-4.733)).get._2 == 15.78)
    assert(tempsAvg.find(p => p._1 == Location(47.117,39.417)).get._2 == 10.07)
    assert(tempsAvg.find(p => p._1 == Location(5.783,0.633)).get._2 == 26.0)
    assert(tempsAvg.find(p => p._1 == Location(55.933,23.317)).get._2 == 7.794)
    assert(tempsAvg.find(p => p._1 == Location(17.033,54.083)).get._2 ==  30.78)
    assert(tempsAvg.find(p => p._1 == Location(52.083,53.633)).get._2 == 17.64)
    assert(tempsAvg.find(p => p._1 == Location(29.1,46.683)).get._2 == 32.5)
    assert(tempsAvg.find(p => p._1 == Location(-5.217,145.8)).get._2 == 26.335)
//    val year = 0
//    val stations = List("")
//    val temperatures = sc.parallelize(List(""))
//
//    var computed = Extraction.locateTemperatures(1, "/testStations.csv","/test1975.csv").collect()
//    val expected = Array[(LocalDate, Location, Temperature)]()
//
//    assert(computed.sameElements(expected))
  }
}
