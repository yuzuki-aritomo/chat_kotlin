package com.example.chatkotlin.Room;

public class QuestionItem {
    private String question_id;
    private String question_item;

    public String getQuestion_item(int question_id) {
        return this.question_item;
    }

    public void setQuestion_item(String question_item) {
        this.question_item = question_item;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    @Override
    public String toString() {
        return "QuestionItem{" +
                "question_id=" + question_id +
                ", question_item='" + question_item + '\'' +
                '}';
    }
}

