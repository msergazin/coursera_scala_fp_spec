package observatory.domains

object Stations extends Domains {
  def parse(line: String) = {
    val args = line.split(",")
    Stations(
      stn = args(0),
      wban = getIfDefinedAt(args, 1),
      latitude = getIfDefinedAt(args, 2),
      longitude = getIfDefinedAt(args, 3)
    )
  }
}

case class Stations(stn: String, wban: Option[String], latitude: Option[String], longitude: Option[String])