package models

import org.scalatest.{FunSuite, Matchers}
import play.api.libs.json.{JsNumber, Json}

import scala.io.Source

/**
 * Created by Thomas Shippey on 2019-09-26.
 */
class TsdbQueryResultTest extends FunSuite with Matchers {

  test("testExtractResults - integer metrics") {
    val metricResponse = Source.fromFile(s"web/test/data/intMetricResponse.json").mkString
    val expected = List(
      TsdbQueryResult(
        "mapr.cldb.cluster_diskspace_used",
        Map(
          "clusterid" -> "8076356342520649255",
          "fqdn" -> "dev01-mapr01.c.dexda-non-production.internal",
          "clustername" -> "dev01.dexda.net"
        ),
        Seq(
          DataPoint(1579517787, JsNumber(0)),
          DataPoint(1579517777, JsNumber(0)),
          DataPoint(1579517817, JsNumber(0)),
          DataPoint(1579517797, JsNumber(0)),
          DataPoint(1579517827, JsNumber(0))
        ),
        SubQuery("","")
      ),
      TsdbQueryResult(
        "mapr.cldb.cluster_diskspace_used",
        Map(
          "clusterid" -> "8076356342520649255",
          "fqdn" -> "dev01-mapr02.c.dexda-non-production.internal",
          "clustername" -> "dev01.dexda.net"
        ),
        Seq(
          DataPoint(1579517774, JsNumber(0)),
          DataPoint(1579517784, JsNumber(0)),
          DataPoint(1579517804, JsNumber(0)),
          DataPoint(1579517814, JsNumber(0))
        ),
        SubQuery("","")
      ),
      TsdbQueryResult(
        "mapr.cldb.cluster_diskspace_used",
        Map(
          "clusterid" -> "8076356342520649255",
          "fqdn" -> "dev01-mapr03.c.dexda-non-production.internal",
          "clustername" -> "dev01.dexda.net"
        ),
        Seq(
          DataPoint(1579517816, JsNumber(2725))
        ),
        SubQuery("","")
      )
    )
    val actual = parseResponse(metricResponse)
    actual should contain theSameElementsAs expected
  }

  test("testExtractResults - float metrics") {
    val metricResponse = Source.fromFile(s"web/test/data/fpMetricResponse.json").mkString
    val expected = List(
      TsdbQueryResult(
        "mapr.process.cpu_percent",
        Map(
          "clusterid" -> "8076356342520649255",
          "fqdn" -> "dev01-mapr01.c.dexda-non-production.internal",
          "clustername" -> "dev01.dexda.net",
          "process_name" -> "opentsdb"
        ),
        Seq(
          DataPoint(1579517814, JsNumber(1.2328766584396362)),
          DataPoint(1579517774, JsNumber(0.8298755288124084)),
          DataPoint(1579517804, JsNumber(1.0311050415039062)),
          DataPoint(1579517794, JsNumber(0.9180550575256348)),
          DataPoint(1579517784, JsNumber(1.239456057548523))
        ),
        SubQuery("","")
      ),
      TsdbQueryResult(
        "mapr.process.cpu_percent",
        Map(
          "clusterid" -> "8076356342520649255",
          "fqdn" -> "dev01-mapr01.c.dexda-non-production.internal",
          "clustername" -> "dev01.dexda.net",
          "process_name" -> "yarn-mapr-nodemanager"
        ),
        Seq(
          DataPoint(1579517814, JsNumber(0.4109589159488678)),
          DataPoint(1579517774, JsNumber(0.4149377644062042)),
          DataPoint(1579517804, JsNumber(0.3093315064907074)),
          DataPoint(1579517794, JsNumber(0.40802448987960815)),
          DataPoint(1579517784, JsNumber(0.5164400339126587))
        ),
        SubQuery("","")
      )
    )
    val actual = parseResponse(metricResponse)
    actual should contain theSameElementsAs expected
  }

  test("testExtractResults - empty response") {
    val metricResponse = "{}"
    val expected = "JSON parse errors"
    val actual = intercept[Exception] {parseResponse(metricResponse)}
    assert(actual.getMessage == expected)
  }

  def parseResponse(strJsonResponse: String): List[TsdbQueryResult] = {
    Json.parse(strJsonResponse)
      .validate[Seq[TsdbQueryResult]]
      .map(_.toList)
      .getOrElse(throw new Exception(s"JSON parse errors"))
  }

}
