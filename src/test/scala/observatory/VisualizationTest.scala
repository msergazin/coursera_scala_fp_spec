package observatory

trait VisualizationTest extends MilestoneSuite {
  private val milestoneTest = namedMilestoneTest("raw data display", 2) _

  // Implement tests for the methods of the `Visualization` object
//  @Test def `checking average temps elements`: Unit = {

//    val temps = List[(Location, Temperature)]((Location(5, 5), 3),(Location(-5, 5), 5))
//    var location = Location(5, 5)
//
//    var computed = Visualization.predictTemperature(temps, location)
//    var expected = 3
//    assert(computed == expected)
//
//    location = Location(0, 3)
//    computed = Visualization.predictTemperature(temps, location)
//    expected = 4
//    assert(computed == expected)
//
//    location = Location(4, 4)
//    computed = Visualization.predictTemperature(temps, location)
//    assert(computed < 4)
//  }

}
