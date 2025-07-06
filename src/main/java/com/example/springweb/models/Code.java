package com.example.springweb.models;


import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "code")
public class Code {

    private String problem_id;
    private String code;
    private String sysIn;
    private String sysOut;

    private String status;

    public Long getCode_id() {
        return code_id;
    }

    public void setCode_id(Long code_id) {
        this.code_id = code_id;
    }

    private Long code_id;




    public String getProblem_id() {
        return problem_id;
    }

    public void setProblem_id(String problem_id) {
        this.problem_id = problem_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSysIn() {
        return sysIn;
    }

    public void setSysIn(String sysIn) {
        this.sysIn = sysIn;
    }

    public String getSysOut() {
        return sysOut;
    }

    public void setSysOut(String sysOut) {
        this.sysOut = sysOut;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String ss) {
        this.status = ss;
    }
// private String lang;
}
