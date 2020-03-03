package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 2nd milestone: basic visualization
  */
import scala.math.{abs, sin, cos, sqrt, pow, toRadians, asin}

object Visualization extends VisualizationInterface {
  val WIDTH = 360
  val HEIGHT = 180
  val EARTH_RADIUS = 6371
  val P = 2
  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    val distancesAndTemps = temperatures.map(pair => (distance(location, pair._1),pair._2))
    val closestLocation = distancesAndTemps.reduce((a, b) => if(a._1 < b._1) a else b)
    if(closestLocation._1 < 1) closestLocation._2
    else {
      // interpolate
      val weights = distancesAndTemps.map(pair => (1 / pow(pair._1, P), pair._2))
      weights.map(pair => pair._1 * pair._2).sum  / weights.map(_._1).sum
      /*val distanceTempSum = temperatures
        .foldLeft(0.0)((acc, tuple) => acc + (invertedDistance(location, tuple._1) * tuple._2))
      val distanceSum = temperatures
        .foldLeft(0.0)((acc, tuple) => acc + invertedDistance(location, tuple._1))
      distanceTempSum / distanceSum*/
    }
  }

  def invertedDistance(p1: Location, p2: Location) : Double = 1 / distance(p1, p2)

  private def areAntipodes(a: Location, b: Location): Boolean = (a.lat == -b.lat) && (abs(a.lon - b.lon) == 180)
  private def distance(a: Location, b: Location): Double = {
    if (a == b) 0
    else if (areAntipodes(a, b)) EARTH_RADIUS * math.Pi
    else {
      val delta_lon = toRadians(abs(a.lon - b.lon))
      val alat = toRadians(a.lat)
      val blat = toRadians(b.lat)
      val delta_lat = abs(alat - blat)
      val delta_sigma = 2 * asin(
        sqrt(
          pow(sin(delta_lat/2), 2) + cos(alat) * cos(blat) * (pow(sin(delta_lon/2), 2))
        )
      )
      EARTH_RADIUS * delta_sigma
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    val sameCol = points.find(_._1 == value)
    sameCol match {
      case Some((_, col)) => col
      case _ =>
        val (smaller, bigger) = points.partition(_._1 < value)
        if (smaller.isEmpty) {
          bigger.minBy(_._1)._2
        } else {
          val a = smaller.maxBy(_._1)
          if (bigger.isEmpty) {
            a._2
        } else {
            val b = bigger.minBy(_._1)
            val wa = 1 / abs(a._1 - value)
            val wb = 1 / abs(b._1 - value)
            def interpolate(x: Int, y: Int): Int = ((wa * x + wb * y) / (wa + wb)).round.toInt
            val ca = a._2
            val cb = b._2
            Color(interpolate(ca.red, cb.red), interpolate(ca.green, cb.green), interpolate(ca.blue, cb.blue))
          }
        }
    }
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    val coords = for {
      i <- 0 until HEIGHT
      j <- 0 until WIDTH
    } yield (i, j)
    println("starting par...")


    val pixels = coords.par
      .map(coordinateToPixel(temperatures, colors, _))
      .toArray
    println("done")
    Image(WIDTH, HEIGHT, pixels)
  }

  private def transformCoord(coord: (Int, Int)): Location = {
    val lon = (coord._2 - WIDTH/2) * (360 / WIDTH)
    val lat = -(coord._1 - HEIGHT/2) * (180 / HEIGHT)
    Location(lat, lon)
  }

  def coordinateToPixel(temperatures: Iterable[(Location, Temperature)],
                                colors: Iterable[(Temperature, Color)],
                                coordinates: (Int, Int)) = {
    println("coordinateToPixel: " + coordinates._1 + ", " + coordinates._2)
    val color = interpolateColor(colors, predictTemperature(temperatures, transformCoord(coordinates)))
    Pixel(color.red, color.green, color.blue, 255)
  }

}

