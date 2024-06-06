package com.example.missionstatement.Tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MissionClassifier {

    private static final Set<String> LOVE_KEYWORDS = new HashSet<>(Arrays.asList(
            "love", "relationship", "partner", "dating", "romance", "marriage"
    ));

    private static final Set<String> EDUCATION_KEYWORDS = new HashSet<>(Arrays.asList(
            "education", "study", "school", "university", "college", "learn", "course", "degree"
    ));

    public static String classifyMission(String text) {
        text = text.toLowerCase();
        if (containsKeywords(text, LOVE_KEYWORDS)) {
            return "Love";
        } else if (containsKeywords(text, EDUCATION_KEYWORDS)) {
            return "Education";
        } else {
            return "Uncertain";
        }
    }

    private static boolean containsKeywords(String text, Set<String> keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }


}
