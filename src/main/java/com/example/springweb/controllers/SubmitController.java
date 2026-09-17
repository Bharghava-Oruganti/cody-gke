package com.example.springweb.controllers;

import com.example.springweb.models.Code;
import com.example.springweb.models.Question;
import com.example.springweb.services.KubernetesExecutionService;
import com.example.springweb.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@CrossOrigin
public class SubmitController {

    @Autowired
    private KubernetesExecutionService kubernetesExecutionService;

    @Autowired
    private QuestionService qs;

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

            System.out.println("Test cases:");
            System.out.println(testCases);

            System.out.println("Expected outputs:");
            System.out.println(expectedOutputs);


            // -------------------------------------------------
            // 4. Execute code using Kubernetes
            // -------------------------------------------------

            System.out.println("--------------------------------");
            System.out.println("Starting Kubernetes execution...");
            System.out.println("--------------------------------");

            String verdict = kubernetesExecutionService.execute(
                    userCode,
                    testCases,
                    expectedOutputs
            );


            // -------------------------------------------------
            // 5. Set verdict
            // -------------------------------------------------

            System.out.println("--------------------------------");
            System.out.println("KUBERNETES VERDICT: [" + verdict + "]");
            System.out.println("--------------------------------");

            if (verdict == null || verdict.trim().isEmpty()) {

                code.setStatus("No verdict returned");

            } else {

                code.setStatus(verdict.trim());
            }


            // -------------------------------------------------
            // 6. Return response to React
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

            code.setStatus("Execution error");

            return code;
        }
    }
}