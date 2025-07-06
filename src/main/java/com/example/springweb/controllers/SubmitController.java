package com.example.springweb.controllers;

import com.example.springweb.models.Code;
import com.example.springweb.models.Question;
import com.example.springweb.services.DockerService;
import com.example.springweb.services.FileServices;
import com.example.springweb.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;


@RestController
//@CrossOrigin(origins = "https://cody-one.vercel.app")
@CrossOrigin
public class SubmitController {
    private String question_api_route = "localhost:8080/question/";

    @Autowired
    private QuestionService qs;

    @Autowired
    private DockerService dockerService;

    @Autowired
    private FileServices fileService;

    String s = "";
    @PostMapping(value = "/submit")
    public Code submit(@RequestBody Code code){
        System.out.println("API HIT!");
        try{
            // fetch the test_data from cache and bind it with incoming data
            long generatedLong = new Random().nextLong();
            code.setCode_id(generatedLong);
            String question_id = code.getProblem_id();
            System.out.println(question_id);

            question_api_route += question_id;
            // fetch in springboot and create a testcases expectedoutput and code and realoutput and verdict files.
            Question question = qs.getQuestionById(question_id);
            String usercode = code.getCode();
            String testCases = question.getTestCases();
            String expectedOutputs = question.getOutputs();
            // create additional verdict and useroutput files that are blank

            System.out.println(usercode);
//            String files_path = "../files/";
//            File userOutput = new File(files_path + "real_output.txt");
//            File verdict = new File(files_path + "verdict.txt");
//            userOutput.createNewFile();
//            verdict.createNewFile();
//
//            // file operations in a service and also optimize this part in future
//            FileWriter codeWriter = new FileWriter(files_path + "code.cpp");
//            codeWriter.write(usercode);
//            codeWriter.close();
//
//            FileWriter tcWriter = new FileWriter(files_path + "input.txt");
//            tcWriter.write(testCases);
//            tcWriter.close();
//
//            FileWriter eOWriter = new FileWriter(files_path + "exp_output.txt");
//            eOWriter.write(expectedOutputs);
//            eOWriter.close();

            fileService.createFile("code.cpp", usercode);
            fileService.createFile("input.txt", testCases);
            fileService.createFile("exp_output.txt", expectedOutputs);
            fileService.createFile("real_output.txt", "");
            fileService.createFile("verdict.txt", "");

            // get call gives a test file.txt with all the test cases

            // create a object of binded code

            // spawn a container and excetute code
            String ok = dockerService.createContainer();
            System.out.println(ok);

            // ok finally docker spawning..

//            File myObj = new File(files_path+"verdict.txt");
//            Scanner myReader = new Scanner(myObj);
//            String status="";
//            while (myReader.hasNextLine()) {
//                 status = myReader.nextLine();
//            }
//            myReader.close();
//            code.setStatus(status);
            code.setStatus(fileService.readFile("verdict.txt"));

            return code;

        }catch (Exception e){
            e.printStackTrace();
        }

        return code;
    }


}
