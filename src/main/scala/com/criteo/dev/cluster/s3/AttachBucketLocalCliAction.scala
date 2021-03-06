package com.criteo.dev.cluster.s3

import com.criteo.dev.cluster.config.GlobalConfig
import com.criteo.dev.cluster.{CliAction, NodeFactory, Public}
import com.criteo.dev.cluster.docker.{DockerConstants, DockerRunning, DockerUtilities}
import org.slf4j.LoggerFactory

/**
  * Points a local docker dev cluster to be able to run queries against tables in a given s3 bucket.
  */
@Public object AttachBucketLocalCliAction extends CliAction[Unit] {

  private val logger = LoggerFactory.getLogger(AttachBucketLocalCliAction.getClass)

  override def command: String = "attach-bucket-local"

  override def usageArgs: List[Any] = List("bucket-id", "container.id")

  override def help: String = "Attaches the given local docker cluster to Hive tables located in given S3 bucket.  " +
    "Any existing Hive metadata on cluster is not overriden, be aware to maintain consistency."

  override def applyInternal(args: List[String], config: GlobalConfig): Unit = {
    val bucketId = args(0)
    val dockerContainerId = args(1)
    val conf = config.backCompat

    //check if docker container id matches a running docker instance.
    val dockerMeta = DockerUtilities.getDockerContainerMetadata(
      DockerConstants.localClusterContainerLabel,
      Some(dockerContainerId))
    val runningDockerMeta = dockerMeta.filter(_.dockerState == DockerRunning)
    require(runningDockerMeta.length == 1, s"Cannot find running docker container with id $dockerContainerId")

    //add some conf arguments expected by the SshHive command to construct the target node.
    val target = NodeFactory.getDockerNode(config.target.local, runningDockerMeta(0))
    RunS3DdlAction(target, bucketId, copiedLocally = false, conf)
    DockerUtilities.printClusterDockerContainerInfo(conf, runningDockerMeta)
  }
}
