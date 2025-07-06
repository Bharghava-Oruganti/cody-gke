//package com.example.springweb.services;
//
//import com.example.springweb.models.Code;
//import com.example.springweb.repos.CodeRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//public class CodeService {
//    // D.I the repository
//    @Autowired
//    private CodeRepo repo;
//
//    // C.R.U.D Methods
//
//    // create
//
//    public Code createQuestion(Code question){
//        question.setProblem_id(UUID.randomUUID().toString().split("-")[0]);
//        return repo.save(question);
//    }
//
//    // read
//
//    public List<Code> getAllCodes(){
//        return repo.findAll();
//    }
//
//    public Code getCodeById
//}
