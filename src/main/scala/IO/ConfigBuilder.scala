package IO
import logic.{RoomConfig, Vector2d}

import java.io.FileNotFoundException
import scala.collection.mutable.{Buffer, Map}
import scala.io.Source

class ConfigBuilder(filepath: String) {
  private def extractDouble(data: String, errorMessage: String): Double = {
    data.toDoubleOption match {
      case Some(d: Double) => d
      case None => throw new CorruptedConfigFileException(errorMessage)
    }
  }

  def build(): RoomConfig = {
    try {
      val source = Source.fromFile(filepath)
      val coords = Buffer[(Double, Double)]()
      val params = Map[String, Option[Double]](
        "room_width" -> None,
        "room_height" -> None,
        "exit_size" -> None,
        "exit_location_proportion" -> None,
        "max_speed" -> None,
        "max_acc" -> None,
        "search_radius" -> None,
        "seeking_weight" -> None,
        "separation_weight" -> None,
        "containment_weight" -> None
      )
      val lines = source.getLines().toArray
      var i = 0
      while (i < lines.length) {
        val header = lines(i).drop(1)
        if (lines(i)(0) != '#') throw new CorruptedConfigFileException("The config file is corrupted.")
        header match {
          case s if params.keySet.contains(s) =>
            i += 1
            val value = extractDouble(lines(i), s"$header data is corrupted.")
            params += ((header, Some(value)))
            i += 1
          case "coords" =>
            i += 1
            while(i < lines.length && !lines(i).startsWith("#")) {
              val data = lines(i).split(',')
              if (data.length != 2) throw new CorruptedConfigFileException("The initial coordinates datas is corrupted.")
              coords += ((extractDouble(data(0), "The initial coordinates datas is corrupted."), extractDouble(data(1), "The initial coordinates datas is corrupted.")))
              i += 1
            }
        }
      }
      source.close()
      if (params.exists( _._2.isEmpty )) throw new CorruptedConfigFileException("Some of the parameters were not specified in the config file.")
      RoomConfig(
        coords.toVector,
        params("room_width").get,
        params("room_height").get,
        params("exit_size").get,
        Vector2d(params("room_width").get, params("room_height").get * (1 - params("exit_size").get) * params("exit_location_proportion").get),
        params("max_speed").get,
        params("max_acc").get,
        params("search_radius").get,
        params("seeking_weight").get,
        params("separation_weight").get,
        params("containment_weight").get
      )
    } catch {
      case e: FileNotFoundException =>
        val configException = new CorruptedConfigFileException("The specified file could not be found.")
        configException.initCause(e)
        throw configException
      case e: ArrayIndexOutOfBoundsException =>
        val configException = new CorruptedConfigFileException("The config file is corrupted.")
        configException.initCause(e)
        throw configException
    }
  }
}
