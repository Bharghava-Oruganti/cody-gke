package com.example.springweb.repos;

import com.example.springweb.models.Question;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionRepo extends MongoRepository<Question,String> {
    // repository for questions
}
