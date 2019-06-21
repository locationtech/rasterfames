# Release&nbsp;Notes

## 0.8.x

### 0.8.0

* Upgraded to the following core dependencies: Spark 2.3.2, GeoTrellis 2.3.0, GeoMesa 2.2.1, JTS 1.16.0.
* Added new tile functions `rf_round`, `rf_abs`, `rf_log`, `rf_log10`, `rf_log2`, `rf_log1p`, `rf_exp`, `rf_exp10`, `rf_exp2`, `rf_expm1`, `rf_resample`.
* Support python-side [Shapely](https://pypi.org/project/Shapely/) geometry User-Defined Type.
* SQL API support for: `rf_assemble_tile`, `rf_array_to_tile`.
* Introduced at the source level the concept of a `RasterSource` and `RasterRef`, enabling lazy/delayed read of sub-scene tiles.
* Added `withKryoSerialization` extension methods on `SparkSession.Builder` and `SparkConf`.
* Added `rf_render_matrix` debugging function.
* Added `RasterFrame.withExtent` extension method.
* Added `SinglebandGeoTiff.toDF` extension method.
* Added `DataFrame.rasterJoin` extension method for merging two dataframes with tiles in disparate CRSs.
* Added `rf_crs` for `ProjectedRasterTile` columns.
* Added `st_extent` (for `Geometry` types) and `rf_extent` (for `ProjectedRasterTile` and `RasterSource` columns).
* Added `st_geometry` (for `Extent` types) and `rf_geometry` (for `ProjectedRasterTile` and `RasterSource` columns).
* _Breaking_: Root package changed from `org.locationtech.rasterframes` to `org.locationtech.rasterframes`.
* _Breaking_: Removed `envelope`, in lieu of `st_extent`, `rf_extent` or `st_envelope` 
* _Breaking_: Renamed `rf_extent_geometry` to `st_geometry`
* _Breaking_: Renamed `rf_tile_dimensions` to `rf_dimensions`
* _Breaking_: Renamed `rf_reproject_geometry` to `st_reproject`
* _Breaking_: With the upgrade to JTS 1.16.0, all imports of `com.vividsolutions.jts` need to be changed to `org.locationtech.jts`.
* _Deprecation_: Tile column functions (in `RasterFunctions`) and SQL registered names have all been renamed to follow `snake_case` conventions, with an `rf_` prefix, matching SQL and Python. A temporary compatibility shim is included so that code built against 0.7.1 and earlier still work. These will be marked as deprecated.
* _Breaking_: In Scala and SQL, `..._scalar` functions (e.g. `local_add_scalar`) have been removed. Non-scalar forms now dynamically detect type of right hand side.
* _Breaking_: `tileToArray` has been replaced with `_tile_to_array_double` and `_tile_to_array_int`.
* _Breaking_: Renamed `bounds_geometry` to `rf_extent_geometry`.
* _Breaking_: renamed `agg_histogram` to `rf_agg_approx_histogram`, `local_agg_stats` to `rf_agg_local_stats`, `local_agg_max` to `rf_agg_local_max`, `local_agg_min` to `rf_agg_local_min`, `local_agg_mean` to `rf_agg_local_mean`, `local_agg_data_cells` to `rf_agg_local_data_cells`, `local_agg_no_data_cells` to `rf_agg_local_no_data_cells`.
* _Breaking_: `CellHistogram` no longer carries along approximate statistics, due to confusing behavior. Use `rf_agg_stats` instead.
* Introduced `LocalCellStatistics` class to wrap together results from `LocalStatsAggregate`.
* _Breaking_: `TileDimensions` moved from `astraea.spark.rasterframes` to `org.locationtech.rasterframes.model`.
* _Breaking_: Renamed `RasterFrame.withBounds` to `RasterFrame.withGeometry` for consistency with DataSource schemas.
* Reworked build scripts for RasterFrames Jupyter Notebook. 
   
## 0.7.x

### 0.7.1

* Fixed ColorRamp pipeline in MultibandRender
* Fixed Python wrapper for `explodeTiles`

### 0.7.0

* Now an incubating project under Eclipse Foundation LocationTech! GitHub repo moved to [locationtech/rasterframes](https://github.com/locationtech/rasterframes).
* PySpark support! See [`pyrasterframes/python/README.rst`](https://github.com/locationtech/rasterframes/blob/develop/pyrasterframes/python/README.rst) to get started.
* Exposed Spark JTS spatial operations in Python.
* Added RasterFrames-enabled Jupyter Notebook Docker Container package. See [`deployment/README.md`](https://github.com/locationtech/rasterframes/blob/develop/deployment/README.md) for details.
* Updated to GeoMesa version 2.0.1.
* Added `convertCellType`, `normalizedDifference` `mask` and `inverseMask` operations on tile columns.
* Added tile column + scalar operations: `localAddScalar`, `localSubtractScalar`, `localMultiplyScalar`, `localDivideScalar`  
* Added `rasterize` and `reprojectGeometry` operations on geometry columns.
* Added for for writing GeoTIFFs from RasterFrames via `DataFrameWriter`.
* Added `spark.read.geotrellis.withNumPartitions(Int)` for setting the initial number of partitions to use when reading a layer.
* Added `spark.read.geotrellis.withTileSubdivisions(Int)` for evenly subdividing tiles before they become rows in a RasterFrame.
* Added `experimental` package for sandboxing new feature ideas.
* Added experimental GeoJSON DataSource with schema inferfence on feature properties.
* Added Scala, SQL, and Python tile-scalar arithmetic operations: `localAddScalar`, `localSubtractScalar`, `localMultipyScalar`, `localDivideScalar`.
* Added Scala, SQL, and Python tile functions for logical comparisons both tile-tile and tile-scalar variants: `localLess`, `localLessEqual`, `localGreater`, `localGreaterEqual`, `localEqual`, and `localUnequal`. 
* Added [`SlippyExport`](https://github.com/locationtech/rasterframes/blob/develop/experimental/src/main/scala/astraea/spark/rasterframes/experimental/slippy/SlippyExport.scala) 
  experimental feature for exporting the contents of a RasterFrame as a [SlippyMap](https://wiki.openstreetmap.org/wiki/Slippy_Map)
  tile image directory structure and Leaflet/OpenMaps-enabled HTML file. 
* Added [experimental DataSource implementations](https://github.com/locationtech/rasterframes/tree/develop/experimental/src/main/scala/astraea/spark/rasterframes/experimental/datasource/awspds) for [MODIS](https://registry.opendata.aws/modis/) and [Landsat 8](https://registry.opendata.aws/landsat-8/) catalogs on AWS PDS.   
* _Change_: Default interpoation for `toRaster` and `toMultibandRaster` has been changed from `Bilinear` to `NearestNeighbor`.
* _Breaking_: Renamed/moved `astraea.spark.rasterframes.functions.CellStatsAggregateFunction.Statistics` to
`astraea.spark.rasterframes.stats.CellStatistics`.
* _Breaking_: `HistogramAggregateFunction` now generates the new type `astraea.spark.rasterframes.stats.CellHistogram`.
* _Breaking_: `box2D` renamed `envelope`.

## 0.6.x
  
### 0.6.1

* Added support for reading striped GeoTiffs (#64).
* Moved extension methods associated with querying tagged columns to `DataFrameMethods` for supporting
  temporal and spatial columns on non-RasterFrame DataFrames.
* GeoTIFF and GeoTrellis DataSources automatically initialize RasterFrames.
* Added `RasterFrame.toMultibandRaster`.
* Added utility for rendering multiband tile as RGB composite PNG.
* Added `RasterFrame.withRFColumnRenamed` to lessen boilerplate in maintaining `RasterFrame` type tag.  

### 0.6.0

* Upgraded to Spark 2.2.1. Added `VersionShims` to allow for Spark 2.1.x backwards compatibility.
* Introduced separate `rasterframes-datasource` library for hosting sources from which to read RasterFrames.
* Implemented basic (but sufficient) temporal and spatial filter predicate push-down feature for the GeoTrellis layer datasource.
* Added Catalyst expressions specifically for spatial relations, allowing for some polymorphism over JTS types.
* Added a GeoTrellis Catalog `DataSource` for inspecting available layers and associated metadata at a URI 
* Added GeoTrellis Layer DataSource for reading GeoTrellis layers from any SPI-registered GeoTrellis backend (which includes HDFS, S3, Accumulo, HBase, Cassandra, etc.).
* Ability to save a RasterFrame as a GeoTrellis layer to any SPI-registered GeoTrellis backends. Multi-column RasterFrames are written as Multiband tiles.
* Addd a GeoTiff DataSource for directly loading a (preferably Cloud Optimized) GeoTiff as a RasterFrame, each row containing tiles as they are internally organized.
* Fleshed out support for `MultibandTile` and `TileFeature` support in datasource.
* Added typeclass for specifying merge operations on `TileFeature` data payload.
* Added `withTemporalComponent` convenince method for creating appending a temporal key column with constant value.
* _Breaking_: Renamed `withExtent` to `withBounds`, and now returns a JTS `Polygon`.
* Added `EnvelopeEncoder` for encoding JTS `Envelope` type. 
* Refactored build into separate `core` and `docs`, paving way for `pyrasterframes` polyglot module.
* Added utility extension method `withPrefixedColumnNames` to `DataFrame`.

#### Known Issues
* Writing multi-column RasterFrames to GeoTrellis layers requires all tiles to be of the same cell type.

## 0.5.x

### 0.5.12

* Added `withSpatialIndex` to introduce a column assigning a z-curve index value based on the tile's centroid in EPSG:4326. 
* Added column-appending convenience methods: `withExtent`, `withCenter`,  `withCenterLatLng`
* Documented example of creating a GeoTrellis layer from a RasterFrame.
* Added Spark 2.2.0 forward-compatibility.
* Upgraded to GeoTrellis 1.2.0-RC2.

### 0.5.11

* Significant performance improvement in `explodeTiles` (1-2 orders of magnitude). See [#38](https://github.com/s22s/raster-frames/issues/38)
* Fixed bugs in `NoData` handling when converting to `Double` tiles.

### 0.5.10

* Upgraded to shapeless 2.3.2
* Fixed [#36](https://github.com/s22s/raster-frames/issues/36), [#37](https://github.com/s22s/raster-frames/issues/37)

### 0.5.9

* Ported to sbt 1.0.3
* Added sbt-generated `astraea.spark.rasterframes.RFBuildInfo`
* Fixed bug in computing `aggMean` when one or more tiles are `null` 
* Deprecated `rfIinit` in favor of `SparkSession.withRasterFrames` or `SQLContext.withRasterFrames` extension methods

### 0.5.8

* Upgraded to GeoTrellis 1.2.0-RC1
* Added [`REPLsent`-based](https://github.com/marconilanna/REPLesent) tour of RasterFrames
* Moved Giter8 template to separate repository `s22s/raster-frames.g8` due to sbt limitations
* Updated _Getting Started_ to reference new Giter8 repo
* Changed SQL function name `rf_stats` and `rf_histogram` to `rf_aggStats` and `rf_aggHistogram` 
  for consistency with DataFrames API

### 0.5.7

* Created faster implementation of aggregate statistics.
* Fixed bug in deserialization of `TileUDT`s originating from `ConstantTile`s
* Fixed bug in serialization of `NoDataFilter` within SparkML pipeline
* Refactoring of UDF organization
* Various documentation tweaks and updates
* Added Giter8 template

### 0.5.6

* `TileUDF`s are encoded using directly into Catalyst--without Kryo--resulting in an insane
 decrease in serialization time for small tiles (`int8`, <= 128²), and pretty awesome speedup for
 all other cell types other than `float32` (marginal slowing). While not measured, memory 
 footprint is expected to have gone down.


### 0.5.5

* `aggStats` and `tileMean` functions rewritten to compute simple statistics directly rather than using `StreamingHistogram`
* `tileHistogramDouble` and `tileStatsDouble` were replaced by `tileHistogram` and `tileStats`
* Added `tileSum`, `tileMin` and `tileMax` functions 
* Added `aggMean`, `aggDataCells` and `aggNoDataCells` aggregate functions.
* Added `localAggDataCells` and `localAggNoDataCells` cell-local (tile generating) fuctions
* Added `tileToArray` and `arrayToTile`
* Overflow fix in `LocalStatsAggregateFunction`