package com.example.springweb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Document(collection = "questions")
//@RedisHash("questions")
public class Question  implements Serializable {

    @Id
    private String questionId;
    private String question;
    private String testCases;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;
    private String fewTestCases;

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    private String constraints;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTestCases() {
        return testCases;
    }

    public void setTestCases(String testCases) {
        this.testCases = testCases;
    }

    public String getFewTestCases() {
        return fewTestCases;
    }

    public void setFewTestCases(String fewTestCases) {
        this.fewTestCases = fewTestCases;
    }

    public String getOutputs() {
        return outputs;
    }

    public void setOutputs(String outputs) {
        this.outputs = outputs;
    }

    public String getFewOutputs() {
        return fewOutputs;
    }

    public void setFewOutputs(String fewOutputs) {
        this.fewOutputs = fewOutputs;
    }

    private String outputs;

    private String fewOutputs;

}
