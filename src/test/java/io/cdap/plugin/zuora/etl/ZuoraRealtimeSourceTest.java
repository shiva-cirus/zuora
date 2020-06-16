/*
 *  Copyright © 2020 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package io.cdap.plugin.zuora.etl;

import com.google.common.collect.ImmutableMap;
import io.cdap.cdap.api.artifact.ArtifactSummary;
import io.cdap.cdap.api.data.format.StructuredRecord;
import io.cdap.cdap.api.dataset.table.Table;
import io.cdap.cdap.common.conf.Constants;
import io.cdap.cdap.datastreams.DataStreamsApp;
import io.cdap.cdap.datastreams.DataStreamsSparkLauncher;
import io.cdap.cdap.etl.api.streaming.StreamingSource;
import io.cdap.cdap.etl.mock.batch.MockSink;
import io.cdap.cdap.etl.proto.v2.DataStreamsConfig;
import io.cdap.cdap.etl.proto.v2.ETLPlugin;
import io.cdap.cdap.etl.proto.v2.ETLStage;
import io.cdap.cdap.etl.spark.Compat;
import io.cdap.cdap.proto.ProgramRunStatus;
import io.cdap.cdap.proto.artifact.AppRequest;
import io.cdap.cdap.proto.id.ApplicationId;
import io.cdap.cdap.proto.id.ArtifactId;
import io.cdap.cdap.proto.id.NamespaceId;
import io.cdap.cdap.test.ApplicationManager;
import io.cdap.cdap.test.DataSetManager;
import io.cdap.cdap.test.ProgramManager;
import io.cdap.cdap.test.SparkManager;
import io.cdap.cdap.test.TestConfiguration;
import io.cdap.plugin.zuora.plugin.streaming.source.ZuoraStreamingSource;
import io.cdap.plugin.zuora.plugin.streaming.source.ZuoraStreamingSourceConfig;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ZuoraRealtimeSourceTest extends BaseZuoraSourceTest {
  private static final ArtifactId APP_ARTIFACT_ID = NamespaceId.DEFAULT.artifact("data-streams", "1.0.0");
  private static final ArtifactSummary APP_ARTIFACT = new ArtifactSummary("data-streams", "1.0.0");
  private static final int WAIT_FOR_RECORDS_TIMEOUT_SECONDS = 60;
  private static final long WAIT_FOR_RECORDS_POLLING_INTERVAL_MS = 100;

  @ClassRule
  public static final TestConfiguration CONFIG =
    new TestConfiguration(Constants.Explore.EXPLORE_ENABLED, false,
                          Constants.AppFabric.SPARK_COMPAT, Compat.SPARK_COMPAT);

  @BeforeClass
  public static void setupTest() throws Exception {
    setupStreamingArtifacts(APP_ARTIFACT_ID, DataStreamsApp.class);

    addPluginArtifact(NamespaceId.DEFAULT.artifact("zuora-plugins", "1.0.0"),
                      APP_ARTIFACT_ID,
                      ZuoraStreamingSource.class
    );
  }

  public List<StructuredRecord> getPipelineResults(Map<String, String> properties,
                                                   int expectedRecordsCount) throws Exception {
    ProgramManager programManager = startPipeline(properties);
    return waitForRecords(programManager, expectedRecordsCount);
  }

  private SparkManager deployETL(ETLPlugin sourcePlugin, ETLPlugin sinkPlugin, String appName) throws Exception {
    ETLStage source = new ETLStage("source", sourcePlugin);
    ETLStage sink = new ETLStage("sink", sinkPlugin);
    DataStreamsConfig etlConfig = DataStreamsConfig.builder()
      .addStage(source)
      .addStage(sink)
      .addConnection(source.getName(), sink.getName())
      .setBatchInterval("1s")
      .build();

    AppRequest<DataStreamsConfig> appRequest = new AppRequest<>(APP_ARTIFACT, etlConfig);
    ApplicationId appId = NamespaceId.DEFAULT.app(appName);
    ApplicationManager applicationManager = deployApplication(appId, appRequest);

    return applicationManager.getSparkManager(DataStreamsSparkLauncher.NAME);
  }

  private ProgramManager startPipeline(Map<String, String> properties) throws Exception {
    Map<String, String> sourceProperties = new ImmutableMap.Builder<String, String>()
      .putAll(properties)
      .put(ZuoraStreamingSourceConfig.PULL_FREQUENCY, "15 min")
      .build();

    ETLPlugin sourceConfig = new ETLPlugin("Zuora", StreamingSource.PLUGIN_TYPE, sourceProperties);
    ETLPlugin sinkConfig = MockSink.getPlugin(getOutputDatasetName());

    ProgramManager programManager =
      deployETL(sourceConfig, sinkConfig, "ZuoraStreaming_" + testName.getMethodName());
    programManager.startAndWaitForRun(ProgramRunStatus.RUNNING, 30, TimeUnit.SECONDS);

    return programManager;
  }

  private List<StructuredRecord> waitForRecords(ProgramManager programManager,
                                                int expectedRecordsCount) throws Exception {
    DataSetManager<Table> outputManager = getDataset(getOutputDatasetName());

    Awaitility.await()
      .atMost(WAIT_FOR_RECORDS_TIMEOUT_SECONDS, TimeUnit.SECONDS)
      .pollInterval(WAIT_FOR_RECORDS_POLLING_INTERVAL_MS, TimeUnit.MILLISECONDS)
      .untilAsserted((() -> {
        int recordsCount = MockSink.readOutput(outputManager).size();
        Assert.assertTrue(
          String.format("At least %d records expected, but %d found", expectedRecordsCount, recordsCount),
          recordsCount >= expectedRecordsCount);
      }));

    programManager.stop();
    programManager.waitForStopped(10, TimeUnit.SECONDS);
    programManager.waitForRun(ProgramRunStatus.KILLED, 10, TimeUnit.SECONDS);

    return MockSink.readOutput(outputManager);
  }

  private String getOutputDatasetName() {
    return "output-realtimesourcetest_" + testName.getMethodName();
  }
}
