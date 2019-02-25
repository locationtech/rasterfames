/*
 * This software is licensed under the Apache 2 license, quoted below.
 *
 * Copyright 2019 Astraea, Inc.
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

package astraea.spark.rasterframes.expressions.aggstats

import astraea.spark.rasterframes.functions.{dataCells, noDataCells}
import org.apache.spark.sql.catalyst.dsl.expressions._
import org.apache.spark.sql.catalyst.expressions.aggregate.DeclarativeAggregate
import org.apache.spark.sql.catalyst.expressions.{AttributeReference, Expression, _}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.rf.TileUDT
import org.apache.spark.sql.types.{LongType, Metadata}
import org.apache.spark.sql.{Column, TypedColumn}

/**
 * Cell count (data or NoData) aggregate function.
 *
 * @since 10/5/17
 * @param isData true if count should be of non-NoData cells, false if count should be of NoData cells.
 */
case class CellCountAggregate(isData: Boolean, child: Expression) extends DeclarativeAggregate {

  override def prettyName: String =
    if (isData) "agg_data_cells"
    else "agg_no_data_cells"

  private lazy val count =
    AttributeReference("count", LongType, false, Metadata.empty)()

  override lazy val aggBufferAttributes = count :: Nil

  val initialValues = Seq(
    Literal(0L)
  )

  private val cellTest =
    if (isData) udf(dataCells)
    else udf(noDataCells)

  val updateExpressions = Seq(
    If(IsNull(child), count, Add(count, cellTest(new Column(child)).expr))
  )

  val mergeExpressions = Seq(
    count.left + count.right
  )

  val evaluateExpression = count

  def inputTypes = Seq(TileUDT)

  def nullable = true

  def dataType = LongType

  def children = Seq(child)
}

object CellCountAggregate {
  import astraea.spark.rasterframes.encoders.SparkDefaultEncoders._
  def apply(isData: Boolean, tile: Column): TypedColumn[Any, Long] =
    new Column(new CellCountAggregate(isData, tile.expr).toAggregateExpression()).as[Long]
}


