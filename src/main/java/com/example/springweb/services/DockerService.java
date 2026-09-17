package com.example.springweb.services;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DockerService {

    @Autowired
    private DockerClient dockerClient;

    /*
     * IMPORTANT:
     *
     * This is the path on the HOST machine that Docker Desktop
     * can access.
     *
     * Do NOT use /app/files here.
     *
     * /app/files is only the path inside the Spring Boot container.
     */
    private final String hostFilesPath =
            "C:/Users/BHARGHAVA/Desktop/springweb/files";

    /*
     * This is the path inside the code execution container.
     */
    private final String containerFilesPath = "/files";


    public String createContainer() {

        try {

            System.out.println(
                    "Host files path: " + hostFilesPath
            );

            System.out.println(
                    "Container files path: " + containerFilesPath
            );


            // --------------------------------------------------
            // 1. Check compiler image
            // --------------------------------------------------

            boolean imageExists = dockerClient
                    .listImagesCmd()
                    .exec()
                    .stream()
                    .flatMap(image ->
                            image.getRepoTags() != null
                                    ? Arrays.stream(image.getRepoTags())
                                    : java.util.stream.Stream.empty()
                    )
                    .anyMatch(tag ->
                            tag.equals(
                                    "angryclawz/cpp_comp_image:latest"
                            )
                    );

            System.out.println(
                    "Compiler image exists: " + imageExists
            );


            // --------------------------------------------------
            // 2. Pull compiler image if necessary
            // --------------------------------------------------

            if (!imageExists) {

                System.out.println(
                        "IMAGE DOESN'T EXIST! PULLING..."
                );

                dockerClient
                        .pullImageCmd(
                                "angryclawz/cpp_comp_image"
                        )
                        .withTag("latest")
                        .start()
                        .awaitCompletion();

                System.out.println(
                        "Compiler image pulled successfully."
                );
            }


            // --------------------------------------------------
            // 3. Create execution container
            // --------------------------------------------------

            System.out.println(
                    "Creating execution container..."
            );

            CreateContainerResponse container =
                    dockerClient
                            .createContainerCmd(
                                    "angryclawz/cpp_comp_image:latest"
                            )
                            .withHostConfig(
                                    new HostConfig()
                                            .withNetworkMode("bridge")
                                            .withBinds(
                                                    new Bind(
                                                            hostFilesPath,
                                                            new Volume(
                                                                    containerFilesPath
                                                            )
                                                    )
                                            )
                            )
                            .exec();


            System.out.println(
                    "Created container: "
                            + container.getId()
            );


            // --------------------------------------------------
            // 4. Start execution container
            // --------------------------------------------------

            System.out.println(
                    "Starting execution container..."
            );

            dockerClient
                    .startContainerCmd(container.getId())
                    .exec();


            // --------------------------------------------------
            // 5. Wait for execution to finish
            // --------------------------------------------------

            System.out.println(
                    "Waiting for execution container..."
            );

            WaitContainerResultCallback resultCallback =
                    new WaitContainerResultCallback();

            dockerClient
                    .waitContainerCmd(container.getId())
                    .exec(resultCallback);

            resultCallback.awaitCompletion();


            System.out.println(
                    "Container execution completed."
            );


            // --------------------------------------------------
            // 6. Remove execution container
            // --------------------------------------------------

            dockerClient
                    .removeContainerCmd(container.getId())
                    .withForce(true)
                    .exec();

            System.out.println(
                    "Execution container removed."
            );

            return "Success";


        } catch (Exception e) {

            System.out.println(
                    "ERROR WHILE CREATING/EXECUTING CONTAINER"
            );

            e.printStackTrace();

            return "Failure in container creation";
        }
    }
}