package com.example.missionstatement.Objects;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test  implements Serializable {
    private String category;
    private boolean isDone=false;
    private StorageReference fileLocation;
    private List<String>questions;
    private List<List<String>>answers;
    private List<Integer>results;
    public List<Integer>pointsPerAnswer;
    public Test(){
    };

    public void fillContent(String text) {
        answers = new ArrayList<>();
        results = new ArrayList<>();
        questions = new ArrayList<>();
        this.pointsPerAnswer=new ArrayList<>();
        this.pointsPerAnswer=new ArrayList<>();
        questions.add("start");
        answers.add(null);
        int[] pointsPerAnswer=new int[4];

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

            answers.add(Arrays.asList(ans));

            startIndex = answerEndIndex + 1; // עדכן את נקודת ההתחלה לחיפוש הבא

            try {
                int startIndexx = text.indexOf("{");
                int endIndex = text.indexOf("}");

                if (startIndexx != -1 && endIndex != -1 && startIndexx < endIndex) {
                    String points = text.substring(startIndexx + 1, endIndex); // מוציא את התוכן בין הסוגריים
                    String[] split = points.split(",");
                    for (int j = 0; j < split.length; j++) {
                        pointsPerAnswer[j] = Integer.parseInt(split[j].trim());
                        this.pointsPerAnswer.add(pointsPerAnswer[j]);
                    }
                } else {
                    throw new IllegalArgumentException("not contain {}");
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public String generateText() {
        StringBuilder text = new StringBuilder();

        for (int i = 1; i < questions.size(); i++) {
            String question = questions.get(i);
            String[] ans =  answers.get(i).toArray(new String[0]);

            text.append(i).append(".").append(question).append(" (");

            for (int j = 0; j < ans.length; j++) {
                text.append(ans[j]);
                if (j <3&&ans[j]!=null&&!ans[j].isEmpty()) {
                    text.append(", ");
                }
            }

            text.append(")\n");
        }

        text.append("{");

        for (int i = 0; i < pointsPerAnswer.size(); i++) {
            text.append(pointsPerAnswer.get(i).intValue());
            if(i!=pointsPerAnswer.size()-1) {
                text.append(",");
            }
            }


        text.append("}");

        return text.toString();
    }
    public void buildStart()
    {
        answers = new ArrayList<>();
        results = new ArrayList<>();
        questions = new ArrayList<>();


    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("category", category);
        result.put("isDone", isDone);
        result.put("fileLocation", fileLocation);
        List<Integer>integers=new ArrayList<>();
        int a=results.get(0);
        //here i put internal validation for what i want
        if(results.get(0)>10) {
            while (a > 0) {
                integers.add(new Integer(a % 10));
                a = a / 10;
            }
            result.put("results", integers);
        }else {
            result.put("results", results);

        }
        List<Integer>poi=new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            poi.add(pointsPerAnswer.get(i));
        }
        result.put("pointsPerAnswer", poi);
        return result;
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

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public void setPointsPerAnswer(List<Integer> pointsPerAnswer) {
        this.pointsPerAnswer = pointsPerAnswer;
    }

    public List<Integer> getPointsPerAnswer() {
        return pointsPerAnswer;
    }


    public void setAnswers(List<List<String>> answers) {
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

    public List<List<String>> getAnswers() {
        return answers;
    }

    public File buildEnd(String name, Context context) {
        File textFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), name);
        String content=generateText();
        try (FileOutputStream fos = new FileOutputStream(textFile)) {
            fos.write(content.getBytes());
        } catch (IOException e) {
            Log.d("test ",e.getMessage());
            return  null;
        }
        return  textFile;
    }
}
