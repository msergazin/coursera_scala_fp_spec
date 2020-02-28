package observatory

object Main extends App {

//  Extraction.locateTemperatures(1, "/stations.csv","/1975.csv")
  println(Extraction.locateTemperatures(1, "/testStations.csv","/testTemps.csv"))

}
