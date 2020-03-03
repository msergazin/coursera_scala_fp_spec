package observatory

object Main extends App {

  val temps = Extraction.locateTemperatures(1, "/stations.csv","/1975.csv")
//  val temps = Extraction.locateTemperatures(1, "/testStations2.csv","/testTemps2.csv")
  val tempsAvg = Extraction.locationYearlyAverageRecords(temps)


  val colors = List[(Temperature, Color)]((60, Color(255, 255, 255)), (32, Color(255, 0, 0)),
    (12, Color(255, 255, 0)), (0, Color(0, 255, 255)), (-15, Color(0, 0, 255)), (-27, Color(255, 0, 255)),
    (-50, Color(33, 0, 107)), (-60, Color(0, 0, 0)))

//  println("visualizing: ")
//  val myImage = Visualization.visualize(tempsAvg, colors)
//  println("outputting image...")
//  myImage.output(new java.io.File("target/some-image.png"))


//  val standardConfig = config(
//    Key.exec.minWarmupRuns -> 20,
//    Key.exec.maxWarmupRuns -> 40,
//    Key.exec.benchRuns -> 80,
//    //    Key.verbose -> true
//  ) withWarmer(new Warmer.Default)
//
//
//  val seqtime = standardConfig measure {
//    Visualization.coordinateToPixel(tempsAvg, colors, (0,0))
//  }
////  println(s"sequential result = $seqResult")
//  println(s" time: $seqtime")
}
