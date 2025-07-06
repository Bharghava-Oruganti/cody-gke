package com.example.springweb.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

@Service
public class FileServices{
    private final String filesPath = "src/main/java/com/example/springweb/files/"; // Relative path from the working directory
//    private final String filesPath = "C:\\Users\\BHARGHAVA\\Desktop\\springweb\\src\\main\\java\\com\\example\\springweb\\files";
    public void createFile(String fileName, String content) throws IOException {
        Path tempFilePath = Paths.get(filesPath, fileName + ".tmp");
        Path finalFilePath = Paths.get(filesPath, fileName);

        Files.createDirectories(tempFilePath.getParent()); // Ensure the directory exists

        try (FileWriter writer = new FileWriter(tempFilePath.toFile())) {
            writer.write(content);
        }

        // Rename the temp file to final filename
        Files.move(tempFilePath, finalFilePath, StandardCopyOption.REPLACE_EXISTING);
    }

    public File getFile(String fileName) {
        return new File(filesPath + fileName);
    }

    public String readFile(String fileName) throws FileNotFoundException {
        File myObj = new File(filesPath+"verdict.txt");
        Scanner myReader = new Scanner(myObj);
        String status="";
        while (myReader.hasNextLine()) {
             status = myReader.nextLine();
        }
        myReader.close();
        System.out.println(status);
        return status;
    }
}
