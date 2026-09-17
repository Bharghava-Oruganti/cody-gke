package com.example.springweb.services;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class KubernetesExecutionService {

    private final KubernetesClient kubernetesClient;

    private static final String NAMESPACE = "default";

    private static final String EXECUTOR_IMAGE =
            "asia-south1-docker.pkg.dev/cody-gke/cody-repo/cpp-executor:latest";

    public KubernetesExecutionService() {
        this.kubernetesClient = new KubernetesClientBuilder().build();
    }

    public String execute(
            String code,
            String input,
            String expectedOutput
    ) {

        String id = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12);

        String configMapName = "submission-" + id;
        String jobName = "executor-" + id;

        try {

            // -------------------------------------------------
            // 1. Create ConfigMap containing submission files
            // -------------------------------------------------

            Map<String, String> files = new HashMap<>();

            files.put("code.cpp", code);
            files.put("input.txt", input);
            files.put("exp_output.txt", expectedOutput);

            ConfigMap configMap =
                    new ConfigMapBuilder()
                            .withNewMetadata()
                            .withName(configMapName)
                            .endMetadata()
                            .withData(files)
                            .build();

            kubernetesClient
                    .configMaps()
                    .inNamespace(NAMESPACE)
                    .resource(configMap)
                    .create();


            // -------------------------------------------------
            // 2. Create Kubernetes Job
            // -------------------------------------------------

            Job job =
                    new JobBuilder()
                            .withNewMetadata()
                            .withName(jobName)
                            .endMetadata()

                            .withNewSpec()
                            .withBackoffLimit(0)
                            .withActiveDeadlineSeconds(30L)

                            .withNewTemplate()
                            .withNewSpec()
                            .withRestartPolicy("Never")

                            .addNewContainer()
                            .withName("executor")
                            .withImage(EXECUTOR_IMAGE)
                            .withImagePullPolicy("Always")

                            .withCommand(
                                    "sh",
                                    "-c",
                                    """
                                    mkdir -p /files

                                    cp /submission/code.cpp /files/code.cpp
                                    cp /submission/input.txt /files/input.txt
                                    cp /submission/exp_output.txt /files/exp_output.txt

                                    exec sh /run.sh
                                    """
                            )

                            .withNewResources()
                            .addToRequests("cpu",
                                    new io.fabric8.kubernetes.api.model.Quantity("250m"))
                            .addToRequests("memory",
                                    new io.fabric8.kubernetes.api.model.Quantity("256Mi"))
                            .addToLimits("cpu",
                                    new io.fabric8.kubernetes.api.model.Quantity("500m"))
                            .addToLimits("memory",
                                    new io.fabric8.kubernetes.api.model.Quantity("512Mi"))
                            .endResources()

                            .addNewVolumeMount()
                            .withName("submission")
                            .withMountPath("/submission")
                            .withReadOnly(true)
                            .endVolumeMount()

                            .endContainer()

                            .addNewVolume()
                            .withName("submission")
                            .withNewConfigMap()
                            .withName(configMapName)
                            .endConfigMap()
                            .endVolume()

                            .endSpec()
                            .endTemplate()

                            .endSpec()

                            .build();


            kubernetesClient
                    .batch()
                    .v1()
                    .jobs()
                    .inNamespace(NAMESPACE)
                    .resource(job)
                    .create();


            // -------------------------------------------------
            // 3. Wait for Job completion
            // -------------------------------------------------

            Job completedJob =
                    kubernetesClient
                            .batch()
                            .v1()
                            .jobs()
                            .inNamespace(NAMESPACE)
                            .withName(jobName)
                            .waitUntilCondition(
                                    j ->
                                            j.getStatus() != null
                                                    &&
                                                    (
                                                            j.getStatus().getSucceeded() != null
                                                                    && j.getStatus().getSucceeded() > 0
                                                                    ||
                                                                    j.getStatus().getFailed() != null
                                                                            && j.getStatus().getFailed() > 0
                                                    ),
                                    35,
                                    java.util.concurrent.TimeUnit.SECONDS
                            );


            // -------------------------------------------------
            // 4. Find Job pod
            // -------------------------------------------------

            Pod pod =
                    kubernetesClient
                            .pods()
                            .inNamespace(NAMESPACE)
                            .withLabel("job-name", jobName)
                            .list()
                            .getItems()
                            .stream()
                            .findFirst()
                            .orElseThrow(
                                    () -> new RuntimeException("Executor pod not found")
                            );


            // -------------------------------------------------
            // 5. Read pod logs
            // -------------------------------------------------

            String logs =
                    kubernetesClient
                            .pods()
                            .inNamespace(NAMESPACE)
                            .withName(pod.getMetadata().getName())
                            .getLog();


            // -------------------------------------------------
            // 6. Extract verdict
            // -------------------------------------------------

            for (String line : logs.split("\\R")) {

                if (line.startsWith("VERDICT:")) {

                    return line
                            .substring("VERDICT:".length())
                            .trim();
                }
            }

            return "No verdict returned";


        } finally {

            // -------------------------------------------------
            // 7. Cleanup
            // -------------------------------------------------

            try {
                kubernetesClient
                        .batch()
                        .v1()
                        .jobs()
                        .inNamespace(NAMESPACE)
                        .withName(jobName)
                        .delete();
            } catch (Exception ignored) {
            }

            try {
                kubernetesClient
                        .configMaps()
                        .inNamespace(NAMESPACE)
                        .withName(configMapName)
                        .delete();
            } catch (Exception ignored) {
            }
        }
    }
}