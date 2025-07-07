package com.example.springweb.services;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DockerService {

    @Autowired
    private DockerClient dockerClient;
   // private String local_path = "/home/claws6206/cody/backend/src/main/java/com/example/springweb/files";
//     private String local_path = "/app/files";
  //  private String local_path = "C:\\Users\\BHARGHAVA\\Desktop\\springweb\\src\\main\\java\\com\\example\\springweb\\files";
    private String local_path = "/home/claws6206/cody/backend/src/main/java/com/example/springweb/files";
    Path filePath = Paths.get(local_path, "");

    private String container_path = "/files";
    Volume volume = new Volume(container_path);
    Volume volume2 = new Volume(local_path);
    public String createContainer(){
        try{
            Path currentPath = Paths.get("").toAbsolutePath();
            Path filePath = currentPath.resolve(local_path).normalize();

            // Convert the path to a format that Docker understands
            String hostPath = filePath.toString().replace("\\", "/");
            System.out.println("Final Local Volume Bind Path is : " + hostPath);

            boolean imageExists = dockerClient.listImagesCmd()
                    .exec()
                    .stream()
                    .flatMap(image -> image.getRepoTags() != null ?
                            java.util.Arrays.stream(image.getRepoTags()) :
                            java.util.stream.Stream.empty())
                    .anyMatch(tag -> tag.equals("angryclawz/cpp_comp_image:latest"));

//            List<Image> imageList = dockerClient.listImagesCmd().exec();
//            for(Image img : imageList){
//                System.out.println(img);
//            }
//            DockerClient dockerClient2 = DockerClientBuilder.getInstance().build();
            System.out.println(imageExists);
            if(!imageExists){
                System.out.println("IMAGE DOESN'T EXIST ! PULLING...");
                dockerClient.pullImageCmd("angryclawz/cpp_comp_image")
                        .withTag("latest")
                        .start()
                        .awaitCompletion();
            }


            CreateContainerResponse container = dockerClient.createContainerCmd("angryclawz/cpp_comp_image")
//                    .withUser("1000:1000")
                    .withHostConfig(new HostConfig()
                            .withNetworkMode("bridge")
                            .withBinds(new Bind(local_path,volume)

                    ))
                    .exec();

            // starting of container with data
            dockerClient.startContainerCmd(container.getId())
                    .exec();
            // wait
            WaitContainerResultCallback resultCallback = new WaitContainerResultCallback();
            dockerClient.waitContainerCmd(container.getId()).exec(resultCallback);
            resultCallback.awaitCompletion();

            return "Success";
        }catch(Exception e){
            e.printStackTrace();
        }

        return "Failure in container creation";
    }
}
