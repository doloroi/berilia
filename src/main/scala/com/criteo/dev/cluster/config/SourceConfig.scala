package com.criteo.dev.cluster.config

case class SourceConfig(
                         address: String,
                         tables: List[TableConfig],
                         copyConfig: CopyConfig,
                         s3HDFSScheme: String,
                         defaultSampleProb: Double,
                         defaultPartitionCount: Int,
                         defaultSampleSize: Option[Long],
                         sampleDatabase: String,
                         gateway: GatewayConfig
                       )

/**
  * Table config
  *
  * @param name           the name of the table
  * @param sampleProb     the sampling probability
  * @param sampleSize     the sampling size, which overrides the sample prob
  * @param partitions     the list of partitions
  * @param partitionCount the number of partitions to be copied
  */
case class TableConfig(
                        name: String,
                        sampleProb: Option[Double],
                        sampleSize: Option[Long],
                        partitions: List[List[String]],
                        partitionCount: Option[Int]
                      )

case class CopyConfig(
                       scheme: String,
                       sampleThreshold: Long,
                       listeners: List[String],
                       sampleListeners: List[String]
                     )

case class GatewayConfig(
                          dockerPorts: List[String],
                          dockerFiles: List[String],
                          conf: Map[String, String]
                        )
