package com.example.springweb.controllers;

import com.example.springweb.models.Add;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddController {
    public static class StdioAdapter extends ResultCallback.Adapter<Frame> {

        @Override
        public void onNext(Frame object) {
            this.onLineGot(new String(object.getPayload()), object.getStreamType().equals(StreamType.STDERR));
        }

        private void onLineGot(String line, boolean stderr) {
            System.out.println((stderr ? "(err) " : "") + line);
        }
    }

    private static void attachStdio(DockerClient dockerClient, CreateContainerResponse container, ResultCallback<Frame> callback) {
        dockerClient.logContainerCmd(container.getId())
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(true)
                .exec(callback);
    }



//    DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
//    final DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
   @Autowired
   private DockerClient dockerClient ;

    @PostMapping("/add")
    public String add(@RequestBody Add a){
        try{
            // creation of container
           // String[] command = { "bash", "-c", "echo $((5 + 10))" };
            CreateContainerResponse container = dockerClient.createContainerCmd("test")
                    .withHostConfig(new HostConfig()
                            .withNetworkMode("bridge"))
                    .exec();

            // starting of container with data
            dockerClient.startContainerCmd(container.getId()).exec();
            // wait
            WaitContainerResultCallback resultCallback = new WaitContainerResultCallback();
            dockerClient.waitContainerCmd(container.getId()).exec(resultCallback);
            resultCallback.awaitCompletion();

            attachStdio(dockerClient, container, new StdioAdapter());
//            dockerClient.removeContainerCmd(container.getId()).exec();
        }catch(Exception e){
            e.printStackTrace();
            return "Error processing request.";
        }

        return "Success";
    }

}
