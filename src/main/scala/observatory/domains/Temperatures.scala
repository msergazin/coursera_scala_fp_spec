package observatory.domains

object Temperatures extends Domains {

  private val MAX_TEMP = "9999.9"

  def parse(line: String) = {
    val args = line.split(",")
    Temperatures(
      stn = args(0),
      wban = getIfDefinedAt(args, 1),
      month = getIfDefinedAt(args, 2).map(_.toInt),
      day = getIfDefinedAt(args, 3).map(_.toInt),
      temperature = mapTemperature(getIfDefinedAt(args, 4))
    )
  }

  private def mapTemperature(temp: Option[String]): Option[Double] = temp match {
    case None => None
    case Some(t) => if (t == MAX_TEMP) None else temp.map(_.toDouble).map(toCelsius)
  }

  private def toCelsius(temp: Double) : Double = BigDecimal((temp - 32) / 1.8).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
}

case class Temperatures(stn: String, wban: Option[String], month: Option[Int], day: Option[Int], temperature: Option[Double])
