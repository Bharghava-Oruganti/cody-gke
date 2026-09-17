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
public class FileServices {

    // This is the path INSIDE the Spring Boot Docker container.
    // docker-compose.yml mounts:
    //
    // ./files:/app/files
    //
    // Therefore the application should use /app/files.
    private final String filesPath = "/app/files/";

    /**
     * Creates a file atomically using a temporary file first.
     */
    public void createFile(String fileName, String content) throws IOException {

        Path directory = Paths.get(filesPath);

        // Make sure /app/files exists
        Files.createDirectories(directory);

        Path tempFilePath = directory.resolve(fileName + ".tmp");
        Path finalFilePath = directory.resolve(fileName);

        // Write content to temporary file
        try (FileWriter writer = new FileWriter(tempFilePath.toFile())) {
            writer.write(content);
        }

        // Replace old file with new file
        Files.move(
                tempFilePath,
                finalFilePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        System.out.println(
                "Created file: " + finalFilePath.toAbsolutePath()
        );
    }

    /**
     * Returns a File object for the requested file.
     */
    public File getFile(String fileName) {

        return Paths
                .get(filesPath, fileName)
                .toFile();
    }

    /**
     * Reads the requested file.
     */
    public String readFile(String fileName)
            throws FileNotFoundException {

        File file = getFile(fileName);

        System.out.println(
                "Reading file: "
                        + file.getAbsolutePath()
        );

        System.out.println(
                "File exists: "
                        + file.exists()
        );

        System.out.println(
                "File size: "
                        + file.length()
        );

        if (!file.exists()) {
            throw new FileNotFoundException(
                    "File not found: "
                            + file.getAbsolutePath()
            );
        }

        StringBuilder content = new StringBuilder();

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {

                if (!content.isEmpty()) {
                    content.append("\n");
                }

                content.append(scanner.nextLine());
            }
        }

        String result = content.toString();

        System.out.println(
                "File content: [" + result + "]"
        );

        return result;
    }
}