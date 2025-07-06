package com.example.springweb.services;

import com.example.springweb.models.Question;
import com.example.springweb.repos.QuestionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepo repo;


    // Try the exceptional handling in this service methods for failure from mongo : )

    // C.R.U.D

    // create

    public Question createQuestion (Question question){
//        question.setQuestionId(UUID.randomUUID().toString().split("-")[0]);
        return repo.save(question);
    }

    // READ
    public List<Question> getAllQuestions(){
        return repo.findAll();
    }

    // get quetion by id
    public Question getQuestionById(String id){
        System.out.println("HIT BY "+id);
        return repo.findById(id).get();
    }

    // Update

    public Question updateQuestionById(String id){
        Question updated_question = repo.findById(id).get();

        updated_question.setQuestion("updated bay");
        return updated_question;
    }

    // delete

    public String deleteQuestionById(String id){
        repo.deleteById(id);
        return "Deleted Successfully";
    }
}
