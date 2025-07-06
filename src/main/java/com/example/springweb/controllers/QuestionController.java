package com.example.springweb.controllers;


import com.example.springweb.models.Question;
import com.example.springweb.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = "https://cody-one.vercel.app")
@CrossOrigin
@RequestMapping("/question")
public class QuestionController {

    // D.I the service class
    // Handle exceptions in incoming post requests
    @Autowired
    private QuestionService question;

    @PostMapping("/create")
    @CachePut(value = "questions",key = "#q.questionId")
    public Question createQuestion(@RequestBody Question q){
        return question.createQuestion(q);
    }

    @GetMapping("/")
    public List<Question> getAllQuestions(){
        return question.getAllQuestions();
    }

    // get Specific Question
    @GetMapping("/{questionId}")
    @Cacheable(value = "questions",key = "#questionId")
//    @CacheEvict(value = question)
    public Question getQuestionByID(@PathVariable String questionId){
        System.out.println("HIT BY ID"+questionId);
        return question.getQuestionById(questionId);
    }

    // write all the other necessary endpoints for question handling in future
}
