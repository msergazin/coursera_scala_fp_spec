package observatory

import java.io.File
import java.time.LocalDate

import observatory.domains.{Stations, Temperatures}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD

import scala.io.Source

/**
  * 1st milestone: data extraction
  */
object Extraction extends ExtractionInterface {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  val conf: SparkConf = new SparkConf()
    .setMaster("local[*]")
    .setAppName("extraction")
    .set("spark.driver.host", "localhost")
  val sc: SparkContext = new SparkContext(conf)


  def getRDDFromResource(resource: String): RDD[String] = {
    val fileStream = Source.getClass.getResourceAsStream(resource)
    sc.makeRDD(Source.fromInputStream(fileStream).getLines().toList)
  }
  private def filePath(path: String) =
    new File(this.getClass.getClassLoader.getResource("." + path).toURI).getPath

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {
    val stations = getRDDFromResource(stationsFile).map(Stations.parse).filter {
      case Stations(_, _, None, _) => false
      case Stations(_, _, _, None) => false
      case _ => true
    }
    println("stations.getNumPartitions: " + stations.getNumPartitions)
    /*Stations(007005,None,None,None)*/
//    println("stations: " + stations.count() + ", " + stations.first())
    /*007070,,09,25,87.8*/
//    val temperatures =  sc.textFile(filePath(temperaturesFile)).map(Temperatures.parse)
    val temperatures =  getRDDFromResource(temperaturesFile).map(Temperatures.parse)
    println("temperatures.getNumPartitions: " + temperatures.getNumPartitions)
//    println("temperatures: " + temperatures.count() + ", " + temperatures.first())

    def extractLocateTemps(stationsList: Iterable[Stations], tempList: Iterable[Temperatures]) : Iterable[(LocalDate, Location, Double)] =
      tempList.map(temp => (
        LocalDate.of(year, temp.month.get, temp.day.get),
        Location(stationsList.head.latitude.map(_.toDouble).get, stationsList.head.longitude.map(_.toDouble).get),
        temp.temperature.get))

    //((,14610),CompactBuffer(Stations(,Some(14610),Some(+45.648),Some(-068.693))))
    val groupedStation = stations.groupBy(stations => (stations.stn, stations.wban.getOrElse("")))

    //((387630,),CompactBuffer(Temperatures(387630,None,Some(1),Some(1),Some(7.666666666666665)), Temperatures(387630,None,Some(1),Some(2),Some(6.61111111111111)), Temperatures(387630,None,Some(1),Some(3),Some(2.1666666666666656)), Temperatures(387630,None,Some(1),Some(4),Some(3.8333333333333326)), Temperatures(387630,None,Some(1),Some(5),Some(5.833333333333333)), Temperatures(387630,None,Some(1),Some(6),Some(7.833333333333334)), Temperatures(387630,None,Some(1),Some(7),Some(7.5)), Temperatures(387630,None,Some(1),Some(8),Some(7.222222222222222)), Temperatures(387630,None,Some(1),Some(9),Some(10.166666666666664)), Temperatures(387630,None,Some(1),Some(10),Some(12.0)), Temperatures(387630,None,Some(1),Some(11),Some(7.000000000000001)), Temperatures(387630,None,Some(1),Some(12),Some(3.2222222222222205)), Temperatures(387630,None,Some(1),Some(13),Some(1.555555555555554)), Temperatures(387630,None,Some(1),Some(14),Some(-3.722222222222222)), Temperatures(387630,None,Some(1),Some(15),Some(-5.277777777777778)), Temperatures(387630,None,Some(1),Some(16),Some(-0.777777777777777)), Temperatures(387630,None,Some(1),Some(17),Some(2.000000000000001)), Temperatures(387630,None,Some(1),Some(18),Some(0.8333333333333333)), Temperatures(387630,None,Some(1),Some(19),Some(-1.7777777777777772)), Temperatures(387630,None,Some(1),Some(20),Some(-2.999999999999999)), Temperatures(387630,None,Some(1),Some(21),Some(-1.0000000000000004)), Temperatures(387630,None,Some(1),Some(22),Some(0.0)), Temperatures(387630,None,Some(1),Some(23),Some(0.0)), Temperatures(387630,None,Some(1),Some(24),Some(0.3333333333333341)), Temperatures(387630,None,Some(1),Some(25),Some(0.9999999999999984)), Temperatures(387630,None,Some(1),Some(26),Some(2.6111111111111125)), Temperatures(387630,None,Some(1),Some(27),Some(1.7777777777777792)), Temperatures(387630,None,Some(1),Some(28),Some(2.4444444444444438)), Temperatures(387630,None,Some(1),Some(29),Some(3.7222222222222237)), Temperatures(387630,None,Some(1),Some(30),Some(2.7777777777777777)), Temperatures(387630,None,Some(1),Some(31),Some(0.8333333333333333)), Temperatures(387630,None,Some(2),Some(1),Some(2.388888888888887)), Temperatures(387630,None,Some(2),Some(2),Some(-3.8333333333333326)), Temperatures(387630,None,Some(2),Some(3),Some(-4.333333333333334)), Temperatures(387630,None,Some(2),Some(4),Some(-2.833333333333334)), Temperatures(387630,None,Some(2),Some(5),Some(1.1111111111111112)), Temperatures(387630,None,Some(2),Some(6),Some(1.3333333333333326)), Temperatures(387630,None,Some(2),Some(7),Some(7.000000000000001)), Temperatures(387630,None,Some(2),Some(8),Some(8.833333333333332)), Temperatures(387630,None,Some(2),Some(9),Some(9.611111111111109)), Temperatures(387630,None,Some(2),Some(10),Some(11.444444444444445)), Temperatures(387630,None,Some(2),Some(11),Some(2.333333333333335)), Temperatures(387630,None,Some(2),Some(12),Some(1.4444444444444453)), Temperatures(387630,None,Some(2),Some(13),Some(5.999999999999998)), Temperatures(387630,None,Some(2),Some(14),Some(3.444444444444446)), Temperatures(387630,None,Some(2),Some(15),Some(3.4999999999999982)), Temperatures(387630,None,Some(2),Some(16),Some(6.8888888888888875)), Temperatures(387630,None,Some(2),Some(17),Some(8.277777777777777)), Temperatures(387630,None,Some(2),Some(18),Some(4.388888888888888)), Temperatures(387630,None,Some(2),Some(19),Some(-0.6666666666666663)), Temperatures(387630,None,Some(2),Some(20),Some(0.0)), Temperatures(387630,None,Some(2),Some(21),Some(1.555555555555554)), Temperatures(387630,None,Some(2),Some(22),Some(0.7222222222222207)), Temperatures(387630,None,Some(2),Some(23),Some(-0.22222222222222143)), Temperatures(387630,None,Some(2),Some(24),Some(2.000000000000001)), Temperatures(387630,None,Some(2),Some(25),Some(1.3333333333333326)), Temperatures(387630,None,Some(2),Some(26),Some(2.7777777777777777)), Temperatures(387630,None,Some(2),Some(27),Some(3.5555555555555545)), Temperatures(387630,None,Some(2),Some(28),Some(7.333333333333335)), Temperatures(387630,None,Some(3),Some(1),Some(11.222222222222223)), Temperatures(387630,None,Some(3),Some(2),Some(6.444444444444445)), Temperatures(387630,None,Some(3),Some(3),Some(3.5555555555555545)), Temperatures(387630,None,Some(3),Some(4),Some(1.8333333333333317)), Temperatures(387630,None,Some(3),Some(5),Some(2.000000000000001)), Temperatures(387630,None,Some(3),Some(6),Some(2.833333333333334)), Temperatures(387630,None,Some(3),Some(7),Some(0.3333333333333341)), Temperatures(387630,None,Some(3),Some(8),Some(2.7222222222222214)), Temperatures(387630,None,Some(3),Some(9),Some(2.833333333333334)), Temperatures(387630,None,Some(3),Some(10),Some(6.333333333333332)), Temperatures(387630,None,Some(3),Some(11),Some(7.000000000000001)), Temperatures(387630,None,Some(3),Some(12),Some(10.499999999999998)), Temperatures(387630,None,Some(3),Some(13),Some(10.499999999999998)), Temperatures(387630,None,Some(3),Some(14),Some(9.833333333333334)), Temperatures(387630,None,Some(3),Some(15),Some(7.166666666666666)), Temperatures(387630,None,Some(3),Some(16),Some(4.500000000000001)), Temperatures(387630,None,Some(3),Some(17),Some(6.7777777777777795)), Temperatures(387630,None,Some(3),Some(18),Some(11.833333333333332)), Temperatures(387630,None,Some(3),Some(19),Some(10.222222222222221)), Temperatures(387630,None,Some(3),Some(20),Some(8.333333333333334)), Temperatures(387630,None,Some(3),Some(21),Some(10.833333333333334)), Temperatures(387630,None,Some(3),Some(22),Some(12.388888888888888)), Temperatures(387630,None,Some(3),Some(23),Some(19.499999999999996)), Temperatures(387630,None,Some(3),Some(24),Some(14.000000000000002)), Temperatures(387630,None,Some(3),Some(25),Some(17.27777777777778)), Temperatures(387630,None,Some(3),Some(26),Some(13.666666666666668)), Temperatures(387630,None,Some(3),Some(27),Some(19.0)), Temperatures(387630,None,Some(3),Some(28),Some(13.722222222222223)), Temperatures(387630,None,Some(3),Some(29),Some(10.277777777777777)), Temperatures(387630,None,Some(3),Some(30),Some(10.166666666666664)), Temperatures(387630,None,Some(3),Some(31),Some(11.555555555555554)), Temperatures(387630,None,Some(4),Some(1),Some(12.0)), Temperatures(387630,None,Some(4),Some(2),Some(14.166666666666666)), Temperatures(387630,None,Some(4),Some(3),Some(11.444444444444445)), Temperatures(387630,None,Some(4),Some(4),Some(13.444444444444446)), Temperatures(387630,None,Some(4),Some(5),Some(14.555555555555557)), Temperatures(387630,None,Some(4),Some(6),Some(14.5)), Temperatures(387630,None,Some(4),Some(7),Some(20.0)), Temperatures(387630,None,Some(4),Some(8),Some(18.666666666666664)), Temperatures(387630,None,Some(4),Some(9),Some(19.27777777777778)), Temperatures(387630,None,Some(4),Some(10),Some(18.166666666666668)), Temperatures(387630,None,Some(4),Some(11),Some(18.777777777777775)), Temperatures(387630,None,Some(4),Some(12),Some(20.444444444444443)), Temperatures(387630,None,Some(4),Some(13),Some(24.166666666666664)), Temperatures(387630,None,Some(4),Some(14),Some(21.999999999999996)), Temperatures(387630,None,Some(4),Some(15),Some(18.833333333333336)), Temperatures(387630,None,Some(4),Some(16),Some(13.22222222222222)), Temperatures(387630,None,Some(4),Some(17),Some(14.388888888888888)), Temperatures(387630,None,Some(4),Some(18),Some(14.555555555555557)), Temperatures(387630,None,Some(4),Some(19),Some(17.444444444444443)), Temperatures(387630,None,Some(4),Some(20),Some(19.333333333333332)), Temperatures(387630,None,Some(4),Some(21),Some(21.888888888888893)), Temperatures(387630,None,Some(4),Some(22),Some(20.0)), Temperatures(387630,None,Some(4),Some(23),Some(17.83333333333333)), Temperatures(387630,None,Some(4),Some(24),Some(16.222222222222225)), Temperatures(387630,None,Some(4),Some(25),Some(17.666666666666664)), Temperatures(387630,None,Some(4),Some(26),Some(19.166666666666668)), Temperatures(387630,None,Some(4),Some(27),Some(24.777777777777775)), Temperatures(387630,None,Some(4),Some(28),Some(21.77777777777778)), Temperatures(387630,None,Some(4),Some(29),Some(23.833333333333336)), Temperatures(387630,None,Some(4),Some(30),Some(25.611111111111107)), Temperatures(387630,None,Some(5),Some(1),Some(25.77777777777778)), Temperatures(387630,None,Some(5),Some(2),Some(20.500000000000004)), Temperatures(387630,None,Some(5),Some(3),Some(19.61111111111111)), Temperatures(387630,None,Some(5),Some(4),Some(20.72222222222222)), Temperatures(387630,None,Some(5),Some(5),Some(22.722222222222225)), Temperatures(387630,None,Some(5),Some(6),Some(22.5)), Temperatures(387630,None,Some(5),Some(7),Some(20.33333333333333)), Temperatures(387630,None,Some(5),Some(8),Some(21.5)), Temperatures(387630,None,Some(5),Some(9),Some(22.166666666666668)), Temperatures(387630,None,Some(5),Some(10),Some(22.22222222222222)), Temperatures(387630,None,Some(5),Some(11),Some(24.611111111111107)), Temperatures(387630,None,Some(5),Some(12),Some(24.499999999999996)), Temperatures(387630,None,Some(5),Some(13),Some(24.444444444444443)), Temperatures(387630,None,Some(5),Some(14),Some(24.27777777777778)), Temperatures(387630,None,Some(5),Some(15),Some(23.000000000000004)), Temperatures(387630,None,Some(5),Some(16),Some(25.666666666666668)), Temperatures(387630,None,Some(5),Some(17),Some(25.833333333333332)), Temperatures(387630,None,Some(5),Some(18),Some(25.77777777777778)), Temperatures(387630,None,Some(5),Some(19),Some(25.611111111111107)), Temperatures(387630,None,Some(5),Some(20),Some(28.000000000000004)), Temperatures(387630,None,Some(5),Some(21),Some(30.999999999999996)), Temperatures(387630,None,Some(5),Some(22),Some(28.666666666666664)), Temperatures(387630,None,Some(5),Some(23),Some(25.611111111111107)), Temperatures(387630,None,Some(5),Some(24),Some(27.22222222222222)), Temperatures(387630,None,Some(5),Some(25),Some(28.444444444444446)), Temperatures(387630,None,Some(5),Some(26),Some(28.000000000000004)), Temperatures(387630,None,Some(5),Some(27),Some(29.499999999999996)), Temperatures(387630,None,Some(5),Some(28),Some(27.666666666666664)), Temperatures(387630,None,Some(5),Some(29),Some(29.27777777777778)), Temperatures(387630,None,Some(5),Some(30),Some(31.22222222222222)), Temperatures(387630,None,Some(6),Some(1),Some(27.333333333333336)), Temperatures(387630,None,Some(6),Some(2),Some(25.500000000000004)), Temperatures(387630,None,Some(6),Some(3),Some(26.166666666666664)), Temperatures(387630,None,Some(6),Some(4),Some(27.166666666666668)), Temperatures(387630,None,Some(6),Some(5),Some(30.38888888888889)), Temperatures(387630,None,Some(6),Some(6),Some(31.833333333333332)), Temperatures(387630,None,Some(6),Some(7),Some(31.5)), Temperatures(387630,None,Some(6),Some(8),Some(29.777777777777775)), Temperatures(387630,None,Some(6),Some(9),Some(26.555555555555554)), Temperatures(387630,None,Some(6),Some(10),Some(30.222222222222225)), Temperatures(387630,None,Some(6),Some(11),Some(31.722222222222218)), Temperatures(387630,None,Some(6),Some(12),Some(30.72222222222222)), Temperatures(387630,None,Some(6),Some(13),Some(29.833333333333336)), Temperatures(387630,None,Some(6),Some(14),Some(27.77777777777778)), Temperatures(387630,None,Some(6),Some(15),Some(28.499999999999996)), Temperatures(387630,None,Some(6),Some(16),Some(30.833333333333332)), Temperatures(387630,None,Some(6),Some(17),Some(29.222222222222218)), Temperatures(387630,None,Some(6),Some(18),Some(31.833333333333332)), Temperatures(387630,None,Some(6),Some(19),Some(29.777777777777775)), Temperatures(387630,None,Some(6),Some(20),Some(30.166666666666664)), Temperatures(387630,None,Some(6),Some(21),Some(30.666666666666668)), Temperatures(387630,None,Some(6),Some(22),Some(32.5)), Temperatures(387630,None,Some(6),Some(23),Some(30.72222222222222)), Temperatures(387630,None,Some(6),Some(24),Some(33.333333333333336)), Temperatures(387630,None,Some(6),Some(25),Some(35.33333333333333)), Temperatures(387630,None,Some(6),Some(26),Some(34.44444444444444)), Temperatures(387630,None,Some(6),Some(27),Some(33.833333333333336)), Temperatures(387630,None,Some(6),Some(28),Some(33.833333333333336)), Temperatures(387630,None,Some(6),Some(29),Some(33.61111111111111)), Temperatures(387630,None,Some(6),Some(30),Some(33.0)), Temperatures(387630,None,Some(7),Some(1),Some(33.833333333333336)), Temperatures(387630,None,Some(7),Some(2),Some(33.0)), Temperatures(387630,None,Some(7),Some(3),Some(31.999999999999996)), Temperatures(387630,None,Some(7),Some(4),Some(34.55555555555556)), Temperatures(387630,None,Some(7),Some(5),Some(36.83333333333333)), Temperatures(387630,None,Some(7),Some(6),Some(36.5)), Temperatures(387630,None,Some(7),Some(7),Some(34.0)), Temperatures(387630,None,Some(7),Some(8),Some(34.33333333333333)), Temperatures(387630,None,Some(7),Some(9),Some(35.72222222222222)), Temperatures(387630,None,Some(7),Some(10),Some(36.333333333333336)), Temperatures(387630,None,Some(7),Some(11),Some(33.72222222222222)), Temperatures(387630,None,Some(7),Some(12),Some(32.388888888888886)), Temperatures(387630,None,Some(7),Some(13),Some(31.38888888888889)), Temperatures(387630,None,Some(7),Some(14),Some(28.833333333333336)), Temperatures(387630,None,Some(7),Some(15),Some(27.444444444444446)), Temperatures(387630,None,Some(7),Some(16),Some(29.833333333333336)), Temperatures(387630,None,Some(7),Some(17),Some(33.77777777777778)), Temperatures(387630,None,Some(7),Some(18),Some(32.666666666666664)), Temperatures(387630,None,Some(7),Some(19),Some(32.72222222222222)), Temperatures(387630,None,Some(7),Some(20),Some(32.16666666666667)), Temperatures(387630,None,Some(7),Some(21),Some(33.0)), Temperatures(387630,None,Some(7),Some(22),Some(32.666666666666664)), Temperatures(387630,None,Some(7),Some(23),Some(32.55555555555555)), Temperatures(387630,None,Some(7),Some(24),Some(30.833333333333332)), Temperatures(387630,None,Some(7),Some(25),Some(31.999999999999996)), Temperatures(387630,None,Some(7),Some(26),Some(33.72222222222222)), Temperatures(387630,None,Some(7),Some(27),Some(31.999999999999996)), Temperatures(387630,None,Some(7),Some(28),Some(31.44444444444444)), Temperatures(387630,None,Some(7),Some(29),Some(31.722222222222218)), Temperatures(387630,None,Some(7),Some(30),Some(33.27777777777778)), Temperatures(387630,None,Some(7),Some(31),Some(33.333333333333336)), Temperatures(387630,None,Some(8),Some(1),Some(34.0)), Temperatures(387630,None,Some(8),Some(2),Some(33.166666666666664)), Temperatures(387630,None,Some(8),Some(3),Some(34.833333333333336)), Temperatures(387630,None,Some(8),Some(4),Some(35.27777777777778)), Temperatures(387630,None,Some(8),Some(5),Some(35.72222222222222)), Temperatures(387630,None,Some(8),Some(6),Some(36.99999999999999)), Temperatures(387630,None,Some(8),Some(7),Some(33.166666666666664)), Temperatures(387630,None,Some(8),Some(8),Some(33.388888888888886)), Temperatures(387630,None,Some(8),Some(9),Some(33.166666666666664)), Temperatures(387630,None,Some(8),Some(10),Some(34.49999999999999)), Temperatures(387630,None,Some(8),Some(11),Some(34.0)), Temperatures(387630,None,Some(8),Some(12),Some(31.166666666666664)), Temperatures(387630,None,Some(8),Some(13),Some(29.666666666666668)), Temperatures(387630,None,Some(8),Some(14),Some(30.999999999999996)), Temperatures(387630,None,Some(8),Some(15),Some(32.388888888888886)), Temperatures(387630,None,Some(8),Some(16),Some(25.500000000000004)), Temperatures(387630,None,Some(8),Some(17),Some(25.77777777777778)), Temperatures(387630,None,Some(8),Some(18),Some(21.999999999999996)), Temperatures(387630,None,Some(8),Some(19),Some(24.27777777777778)), Temperatures(387630,None,Some(8),Some(20),Some(25.0)), Temperatures(387630,None,Some(8),Some(21),Some(29.0)), Temperatures(387630,None,Some(8),Some(22),Some(25.222222222222225)), Temperatures(387630,None,Some(8),Some(23),Some(27.22222222222222)), Temperatures(387630,None,Some(8),Some(24),Some(30.999999999999996)), Temperatures(387630,None,Some(8),Some(25),Some(27.444444444444446)), Temperatures(387630,None,Some(8),Some(26),Some(25.999999999999996)), Temperatures(387630,None,Some(8),Some(27),Some(25.611111111111107)), Temperatures(387630,None,Some(8),Some(28),Some(25.33333333333333)), Temperatures(387630,None,Some(8),Some(29),Some(29.777777777777775)), Temperatures(387630,None,Some(8),Some(30),Some(29.0)), Temperatures(387630,None,Some(8),Some(31),Some(28.27777777777778)), Temperatures(387630,None,Some(9),Some(1),Some(26.77777777777778)), Temperatures(387630,None,Some(9),Some(2),Some(28.000000000000004)), Temperatures(387630,None,Some(9),Some(3),Some(26.999999999999996)), Temperatures(387630,None,Some(9),Some(4),Some(29.27777777777778)), Temperatures(387630,None,Some(9),Some(5),Some(29.0)), Temperatures(387630,None,Some(9),Some(6),Some(25.833333333333332)), Temperatures(387630,None,Some(9),Some(7),Some(25.555555555555554)), Temperatures(387630,None,Some(9),Some(8),Some(27.277777777777775)), Temperatures(387630,None,Some(9),Some(9),Some(28.72222222222222)), Temperatures(387630,None,Some(9),Some(10),Some(28.27777777777778)), Temperatures(387630,None,Some(9),Some(11),Some(25.38888888888889)), Temperatures(387630,None,Some(9),Some(12),Some(27.5)), Temperatures(387630,None,Some(9),Some(13),Some(26.77777777777778)), Temperatures(387630,None,Some(9),Some(14),Some(24.444444444444443)), Temperatures(387630,None,Some(9),Some(15),Some(21.166666666666664)), Temperatures(387630,None,Some(9),Some(16),Some(23.277777777777782)), Temperatures(387630,None,Some(9),Some(17),Some(24.222222222222218)), Temperatures(387630,None,Some(9),Some(18),Some(24.777777777777775)), Temperatures(387630,None,Some(9),Some(19),Some(25.999999999999996)), Temperatures(387630,None,Some(9),Some(20),Some(23.000000000000004)), Temperatures(387630,None,Some(9),Some(21),Some(22.83333333333333)), Temperatures(387630,None,Some(9),Some(22),Some(24.611111111111107)), Temperatures(387630,None,Some(9),Some(23),Some(25.166666666666664)), Temperatures(387630,None,Some(9),Some(24),Some(21.277777777777775)), Temperatures(387630,None,Some(9),Some(25),Some(21.999999999999996)), Temperatures(387630,None,Some(9),Some(26),Some(20.555555555555554)), Temperatures(387630,None,Some(9),Some(27),Some(23.000000000000004)), Temperatures(387630,None,Some(9),Some(28),Some(22.722222222222225)), Temperatures(387630,None,Some(9),Some(29),Some(20.833333333333332)), Temperatures(387630,None,Some(9),Some(30),Some(23.499999999999996)), Temperatures(387630,None,Some(10),Some(1),Some(21.222222222222225)), Temperatures(387630,None,Some(10),Some(2),Some(19.499999999999996)), Temperatures(387630,None,Some(10),Some(3),Some(20.833333333333332)), Temperatures(387630,None,Some(10),Some(4),Some(20.999999999999996)), Temperatures(387630,None,Some(10),Some(5),Some(20.833333333333332)), Temperatures(387630,None,Some(10),Some(6),Some(19.833333333333336)), Temperatures(387630,None,Some(10),Some(7),Some(18.777777777777775)), Temperatures(387630,None,Some(10),Some(8),Some(18.777777777777775)), Temperatures(387630,None,Some(10),Some(9),Some(19.72222222222222)), Temperatures(387630,None,Some(10),Some(10),Some(18.833333333333336)), Temperatures(387630,None,Some(10),Some(11),Some(19.555555555555557)), Temperatures(387630,None,Some(10),Some(12),Some(21.38888888888889)), Temperatures(387630,None,Some(10),Some(13),Some(16.72222222222222)), Temperatures(387630,None,Some(10),Some(14),Some(15.333333333333334)), Temperatures(387630,None,Some(10),Some(15),Some(13.166666666666668)), Temperatures(387630,None,Some(10),Some(16),Some(10.777777777777777)), Temperatures(387630,None,Some(10),Some(17),Some(7.999999999999999)), Temperatures(387630,None,Some(10),Some(18),Some(7.833333333333334)), Temperatures(387630,None,Some(10),Some(19),Some(6.833333333333331)), Temperatures(387630,None,Some(10),Some(20),Some(7.166666666666666)), Temperatures(387630,None,Some(10),Some(21),Some(8.277777777777777)), Temperatures(387630,None,Some(10),Some(22),Some(10.111111111111112)), Temperatures(387630,None,Some(10),Some(23),Some(11.833333333333332)), Temperatures(387630,None,Some(10),Some(24),Some(9.611111111111109)), Temperatures(387630,None,Some(10),Some(25),Some(10.166666666666664)), Temperatures(387630,None,Some(10),Some(26),Some(15.999999999999998)), Temperatures(387630,None,Some(10),Some(27),Some(16.61111111111111)), Temperatures(387630,None,Some(10),Some(28),Some(17.0)), Temperatures(387630,None,Some(10),Some(29),Some(15.277777777777777)), Temperatures(387630,None,Some(10),Some(30),Some(10.999999999999998)), Temperatures(387630,None,Some(10),Some(31),Some(11.555555555555554)), Temperatures(387630,None,Some(11),Some(1),Some(13.333333333333332)), Temperatures(387630,None,Some(11),Some(2),Some(5.833333333333333)), Temperatures(387630,None,Some(11),Some(3),Some(8.22222222222222)), Temperatures(387630,None,Some(11),Some(4),Some(6.833333333333331)), Temperatures(387630,None,Some(11),Some(5),Some(1.8333333333333317)), Temperatures(387630,None,Some(11),Some(6),Some(-0.3888888888888885)), Temperatures(387630,None,Some(11),Some(7),Some(2.4444444444444438)), Temperatures(387630,None,Some(11),Some(8),Some(1.4444444444444453)), Temperatures(387630,None,Some(11),Some(9),Some(5.166666666666665)), Temperatures(387630,None,Some(11),Some(10),Some(3.4999999999999982)), Temperatures(387630,None,Some(11),Some(11),Some(6.666666666666666)), Temperatures(387630,None,Some(11),Some(12),Some(10.999999999999998)), Temperatures(387630,None,Some(11),Some(13),Some(9.833333333333334)), Temperatures(387630,None,Some(11),Some(14),Some(-2.1666666666666656)), Temperatures(387630,None,Some(11),Some(15),Some(-2.000000000000001)), Temperatures(387630,None,Some(11),Some(16),Some(-0.777777777777777)), Temperatures(387630,None,Some(11),Some(17),Some(1.5000000000000016)), Temperatures(387630,None,Some(11),Some(18),Some(9.333333333333332)), Temperatures(387630,None,Some(11),Some(19),Some(8.666666666666668)), Temperatures(387630,None,Some(11),Some(20),Some(5.999999999999998)), Temperatures(387630,None,Some(11),Some(21),Some(9.611111111111109)), Temperatures(387630,None,Some(11),Some(22),Some(9.333333333333332)), Temperatures(387630,None,Some(11),Some(23),Some(14.666666666666666)), Temperatures(387630,None,Some(11),Some(24),Some(11.777777777777779)), Temperatures(387630,None,Some(11),Some(25),Some(11.444444444444445)), Temperatures(387630,None,Some(11),Some(26),Some(10.166666666666664)), Temperatures(387630,None,Some(11),Some(27),Some(8.555555555555555)), Temperatures(387630,None,Some(11),Some(28),Some(7.722222222222221)), Temperatures(387630,None,Some(11),Some(29),Some(4.388888888888888)), Temperatures(387630,None,Some(11),Some(30),Some(3.6666666666666674)), Temperatures(387630,None,Some(12),Some(1),Some(1.2777777777777761)), Temperatures(387630,None,Some(12),Some(2),Some(-1.2777777777777781)), Temperatures(387630,None,Some(12),Some(3),Some(-0.777777777777777)), Temperatures(387630,None,Some(12),Some(4),Some(-0.8888888888888896)), Temperatures(387630,None,Some(12),Some(5),Some(-1.1111111111111112)), Temperatures(387630,None,Some(12),Some(6),Some(-2.4444444444444438)), Temperatures(387630,None,Some(12),Some(7),Some(-0.16666666666666705)), Temperatures(387630,None,Some(12),Some(8),Some(0.4999999999999992)), Temperatures(387630,None,Some(12),Some(9),Some(1.722222222222223)), Temperatures(387630,None,Some(12),Some(10),Some(3.444444444444446)), Temperatures(387630,None,Some(12),Some(11),Some(6.444444444444445)), Temperatures(387630,None,Some(12),Some(12),Some(5.611111111111112)), Temperatures(387630,None,Some(12),Some(13),Some(3.444444444444446)), Temperatures(387630,None,Some(12),Some(14),Some(1.1666666666666674)), Temperatures(387630,None,Some(12),Some(15),Some(-1.4444444444444453)), Temperatures(387630,None,Some(12),Some(16),Some(-2.1666666666666656)), Temperatures(387630,None,Some(12),Some(17),Some(-1.8333333333333337)), Temperatures(387630,None,Some(12),Some(18),Some(-1.5555555555555558)), Temperatures(387630,None,Some(12),Some(19),Some(2.000000000000001)), Temperatures(387630,None,Some(12),Some(20),Some(7.666666666666665)), Temperatures(387630,None,Some(12),Some(21),Some(7.222222222222222)), Temperatures(387630,None,Some(12),Some(22),Some(5.611111111111112)), Temperatures(387630,None,Some(12),Some(23),Some(12.5)), Temperatures(387630,None,Some(12),Some(24),Some(3.5555555555555545)), Temperatures(387630,None,Some(12),Some(25),Some(2.4444444444444438)), Temperatures(387630,None,Some(12),Some(26),Some(-0.8888888888888896)), Temperatures(387630,None,Some(12),Some(27),Some(1.5000000000000016)), Temperatures(387630,None,Some(12),Some(28),Some(4.61111111111111)), Temperatures(387630,None,Some(12),Some(30),Some(2.000000000000001)), Temperatures(387630,None,Some(12),Some(31),Some(1.1666666666666674))))
    val groupedTemperatures = temperatures.groupBy(temperatures => (temperatures.stn, temperatures.wban.getOrElse("")))


    groupedStation
      .join(groupedTemperatures)
      .flatMapValues(group => extractLocateTemps(group._1, group._2))
      .values
      .collect()
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] =
    records.groupBy(_._2).mapValues(getAverageTemperature).values
  def getAverageTemperature(stationTemps: Iterable[(LocalDate, Location, Double)]): (Location, Double) = {
    val avTemp = stationTemps.foldLeft(0.0)((acc, actual) => acc + actual._3) / stationTemps.size
    (stationTemps.head._2, avTemp)
  }

}
