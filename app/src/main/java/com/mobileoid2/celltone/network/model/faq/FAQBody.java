package com.mobileoid2.celltone.network.model.faq;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FAQBody {
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("answer")
    @Expose
    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
