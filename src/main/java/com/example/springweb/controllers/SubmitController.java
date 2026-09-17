package com.example.springweb.controllers;

import com.example.springweb.models.Code;
import com.example.springweb.models.Question;
import com.example.springweb.services.DockerService;
import com.example.springweb.services.FileServices;
import com.example.springweb.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@CrossOrigin
public class SubmitController {

    @Autowired
    private QuestionService qs;

    @Autowired
    private DockerService dockerService;

    @Autowired
    private FileServices fileService;

    @PostMapping(value = "/submit")
    public Code submit(@RequestBody Code code) {

        System.out.println("================================");
        System.out.println("SUBMIT API HIT!");
        System.out.println("================================");

        try {

            // -------------------------------------------------
            // 1. Generate unique code ID
            // -------------------------------------------------

            long generatedLong = new Random().nextLong();
            code.setCode_id(generatedLong);

            String questionId = code.getProblem_id();

            System.out.println("Question ID: " + questionId);


            // -------------------------------------------------
            // 2. Fetch question
            // -------------------------------------------------

            Question question = qs.getQuestionById(questionId);

            if (question == null) {

                System.out.println("QUESTION NOT FOUND!");

                code.setStatus("Question not found");

                return code;
            }


            // -------------------------------------------------
            // 3. Get question data
            // -------------------------------------------------

            String userCode = code.getCode();
            String testCases = question.getTestCases();
            String expectedOutputs = question.getOutputs();

            System.out.println("User code:");
            System.out.println(userCode);


            // -------------------------------------------------
            // 4. Create files
            // -------------------------------------------------

            System.out.println("Creating execution files...");

            fileService.createFile(
                    "code.cpp",
                    userCode
            );

            fileService.createFile(
                    "input.txt",
                    testCases
            );

            fileService.createFile(
                    "exp_output.txt",
                    expectedOutputs
            );

            // Empty files which will be populated by
            // the execution container

            fileService.createFile(
                    "real_output.txt",
                    ""
            );

            fileService.createFile(
                    "verdict.txt",
                    ""
            );

            System.out.println("Execution files created successfully.");


            // -------------------------------------------------
            // 5. Start Docker execution
            // -------------------------------------------------

            System.out.println("Starting Docker execution...");

            String dockerResult = dockerService.createContainer();

            System.out.println(
                    "Docker execution result: "
                            + dockerResult
            );


            // -------------------------------------------------
            // 6. Read verdict
            // -------------------------------------------------

            System.out.println("--------------------------------");
            System.out.println("Reading verdict.txt...");

            String verdict = fileService.readFile(
                    "verdict.txt"
            );

            System.out.println(
                    "VERDICT FILE CONTENT: ["
                            + verdict
                            + "]"
            );

            System.out.println("--------------------------------");


            // -------------------------------------------------
            // 7. Set verdict in response
            // -------------------------------------------------

            if (verdict == null || verdict.trim().isEmpty()) {

                System.out.println(
                        "WARNING: verdict.txt is empty!"
                );

                code.setStatus(
                        "No verdict returned"
                );

            } else {

                code.setStatus(
                        verdict.trim()
                );
            }


            // -------------------------------------------------
            // 8. Return response to React
            // -------------------------------------------------

            System.out.println(
                    "Returning status: ["
                            + code.getStatus()
                            + "]"
            );

            System.out.println("================================");

            return code;

        } catch (Exception e) {

            System.out.println(
                    "ERROR DURING CODE SUBMISSION"
            );

            e.printStackTrace();

            code.setStatus(
                    "Execution error"
            );

            return code;
        }
    }
}