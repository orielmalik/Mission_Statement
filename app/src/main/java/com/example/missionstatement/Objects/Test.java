package com.example.missionstatement.Objects;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    private String category;
    private boolean isDone=false;
    private StorageReference fileLocation;
    private List<String>questions;
    private List<String[]>answers;
    private List<Integer>results;
    private int[]pointsPerAnswer;
    public Test(){
    };

    public void fillContent(String text) {
        answers = new ArrayList<>();
        results = new ArrayList<>();
        questions = new ArrayList<>();

        int startIndex = 0;

        for (int i = 1; i <= 8; i++) {
            int questionStartIndex = text.indexOf(i + ".", startIndex);
            if (questionStartIndex == -1) {
                return; // צא אם אין עוד שאלות
            }

            int questionEndIndex = text.indexOf("?", questionStartIndex);
            if (questionEndIndex == -1) {
                return; // צא אם אין עוד סימן שאלה
            }

            questionEndIndex++;

            String question = text.substring(questionStartIndex, questionEndIndex).trim();
            questions.add(question);

            int answerStartIndex = questionEndIndex + 1; // התשובות מתחילות אחרי סימן השאלה
            int answerEndIndex = text.indexOf(")", answerStartIndex);
            if (answerEndIndex == -1) {
                return; // צא אם אין סוגר סוגר
            }

            String answersText = text.substring(answerStartIndex + 1, answerEndIndex).trim(); // מתחיל אחרי '(' ומסתיים לפני ')'
            String[] ans = answersText.split(", ");
            Log.d(null, "fillContent: "+Arrays.toString(ans));
           /* if (ans.length != 4) {
                throw new IllegalArgumentException("Each question must have exactly 4 answers");
            }*/

            answers.add(ans);

            startIndex = answerEndIndex + 1; // עדכן את נקודת ההתחלה לחיפוש הבא
        }
    }

public void buildStart()
{
    answers = new ArrayList<>();
    results = new ArrayList<>();
    questions = new ArrayList<>();


}
    @NonNull
    @Override
    public String toString() {

        return  " Category "+getCategory()
                + "\n"+ "fileLocation"+getFileLocation().getPath();
    }













    public String getCategory() {
        return category;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }



    public List<String[]> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String[]> answers) {
        this.answers = answers;
    }

    public List<Integer> getResults() {
        return results;
    }

    public void setResults(List<Integer> results) {
        this.results = results;
    }

    public StorageReference getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(StorageReference fileLocation) {
        this.fileLocation = fileLocation;
    }
}
