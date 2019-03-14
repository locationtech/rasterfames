/*
 * This software is licensed under the Apache 2 license, quoted below.
 *
 * Copyright 2018 Astraea, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     [http://www.apache.org/licenses/LICENSE-2.0]
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package astraea.spark.rasterframes.ref

import java.net.URI

import astraea.spark.rasterframes.TestEnvironment.ReadMonitor
import astraea.spark.rasterframes.ref.RasterSource.{FileGeoTiffRasterSource, GDALRasterSource}
import astraea.spark.rasterframes.{TestData, TestEnvironment}
import geotrellis.raster.io.geotiff.GeoTiff
import geotrellis.vector.Extent
import org.apache.spark.sql.rf.RasterSourceUDT

import scala.language.reflectiveCalls

/**
 *
 *
 * @since 8/22/18
 */
class RasterSourceSpec extends TestEnvironment with TestData {
  def sub(e: Extent) = {
    val c = e.center
    val w = e.width
    val h = e.height
    Extent(c.x, c.y, c.x + w * 0.1, c.y + h * 0.1)
  }

  describe("General RasterSource") {
    it("should identify as UDT") {
      assert(new RasterSourceUDT() === new RasterSourceUDT())
    }
  }

  describe("HTTP RasterSource") {
    it("should support metadata querying over HTTP") {
      withClue("remoteCOGSingleband") {
        val src = RasterSource(remoteCOGSingleband1)
        assert(!src.extent.isEmpty)
      }
      withClue("remoteCOGMultiband") {
        val src = RasterSource(remoteCOGMultiband)
        assert(!src.extent.isEmpty)
      }
    }
    it("should read sub-tile") {
      withClue("remoteCOGSingleband") {
        val src = RasterSource(remoteCOGSingleband1)
        val Left(raster) = src.read(sub(src.extent))
        assert(raster.size > 0 && raster.size < src.size)
      }
      withClue("remoteCOGMultiband") {
        val src = RasterSource(remoteCOGMultiband)
        //println("CoG size", src.size, src.dimensions)
        val Right(raster) = src.read(sub(src.extent))
        //println("Subtile size", raster.size, raster.dimensions)
        assert(raster.size > 0 && raster.size < src.size)
      }
    }
    it("should Java serialize") {
      import java.io._
      val src = RasterSource(remoteCOGSingleband1)
      val buf = new java.io.ByteArrayOutputStream()
      val out = new ObjectOutputStream(buf)
      out.writeObject(src)
      out.close()

      val data = buf.toByteArray
      val in = new ObjectInputStream(new ByteArrayInputStream(data))
      val recovered = in.readObject().asInstanceOf[RasterSource]
      assert(src.toString === recovered.toString)
    }
  }
  describe("File RasterSource") {
    it("should support metadata querying of file") {
      val localSrc = geotiffDir.resolve("LC08_B7_Memphis_COG.tiff").toUri
      val src = RasterSource(localSrc)
      assert(!src.extent.isEmpty)
    }
  }

  describe("Caching") {
    val localSrc = geotiffDir.resolve("LC08_B7_Memphis_COG.tiff").toUri

    trait Fixture {
      val counter = ReadMonitor(false)
      val src = RasterSource(localSrc, Some(counter))
    }

    it("should cache headers")(new Fixture {
      val e = src.extent
      val startReads = counter.reads

      val c = src.crs
      val e2 = src.extent
      val ct = src.cellType
      counter.reads should be(startReads)
    })

    it("should Spark serialize caching")(new Fixture {

      import spark.implicits._

      val origType = src.getClass

      val e = src.extent

      val df = Seq(src, src, src).toDS.repartition(3)
      val src2 = df.collect()(1)

      val e2 = src2.extent
      val ct = src2.cellType

      src2 match {
        case _: GDALRasterSource => ()
        case s if s.getClass == origType ⇒
          val t = s.asInstanceOf[RasterSource { def callback: Option[ReadMonitor] }]
          t.callback match {
            case Some(cb: ReadMonitor) ⇒ assert(cb.reads === 1)
            case o ⇒ fail(s"Expected '$o' to be a ReadMonitor")
          }
        case o ⇒ fail(s"Expected '$o' to be FileGeoTiffRasterSource")
      }
    })
  }

  describe("RasterSourceToTiles Expression") {
    it("should read all tiles") {
      val src = RasterSource(remoteMODIS)

      val subrasters = src.readAll().left.get

      val collected = subrasters.map(_.extent).reduceLeft(_.combine(_))

      assert(src.extent.xmin === collected.xmin +- 0.01)
      assert(src.extent.ymin === collected.ymin +- 0.01)
      assert(src.extent.xmax === collected.xmax +- 0.01)
      assert(src.extent.ymax === collected.ymax +- 0.01)

      val totalCells = subrasters.map(_.size).sum

      assert(totalCells === src.size)

      subrasters.zipWithIndex.foreach{case (r, i) ⇒
        // TODO: how to test?
        GeoTiff(r, src.crs).write(s"target/$i.tiff")
      }
    }
  }
}
