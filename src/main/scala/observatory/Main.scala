package observatory

object Main extends App {

  // Get current size of heap in bytes
  println("heapSize:" + Runtime.getRuntime().totalMemory())

  // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
  println( Runtime.getRuntime().maxMemory())

  val temps1975 = Extraction.locateTemperatures(1, "/testStations.csv","/test1975.csv")
  val temps1975Avg = Extraction.locationYearlyAverageRecords(temps1975)

  val temps1976 = Extraction.locateTemperatures(1976, "/testStations.csv","/test1976.csv")
  val temps1976Avg = Extraction.locationYearlyAverageRecords(temps1976)

  val temps1977 = Extraction.locateTemperatures(1977, "/testStations.csv","/test1977.csv")
  val temps1977Avg = Extraction.locationYearlyAverageRecords(temps1977)

  //1330
  println("tempsAvg: "+temps1975Avg.size)
//  tempsAvg.foreach(p => println(p._1 +", " + p._2))

//  Location(-180.0,90.0)

    println(Manipulation.average(Seq(temps1976Avg, temps1977Avg))(GridLocation(-160,90)))
//    deviation


  val colors = List[(Temperature, Color)]((60, Color(255, 255, 255)), (32, Color(255, 0, 0)),
    (12, Color(255, 255, 0)), (0, Color(0, 255, 255)), (-15, Color(0, 0, 255)), (-27, Color(255, 0, 255)),
    (-50, Color(33, 0, 107)), (-60, Color(0, 0, 0)))

  /*println("visualizing: ")
  val myImage = Visualization.visualize(tempsAvg, colors)
  println("heapSize:" + Runtime.getRuntime().totalMemory())
  println("outputting image...")
  myImage.output(new java.io.File("target/some-image.png"))*/


//  val image = Interaction.tile(tempsAvg, colors, Tile(0,0,2))
//  image.output(new java.io.File("target/zoom-image.png"))

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
