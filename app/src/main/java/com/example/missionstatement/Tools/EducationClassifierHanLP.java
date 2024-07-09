package com.example.missionstatement.Tools;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EducationClassifierHanLP {

    private static final Set<String> ECONOMIC_KEYWORDS = new HashSet<>(Arrays.asList(
            "economic", "finance", "business", "economy", "market", "trade", "banking", "כלכלה", "פיננסים", "עסקים", "שוק", "מסחר", "בנקאות"
    ));

    private static final Set<String> ENGINEER_KEYWORDS = new HashSet<>(Arrays.asList(
            "engineer", "engineering", "software", "hardware", "programming", "development", "computer", "מהנדס", "הנדסה", "תוכנה", "חומרה", "תכנות", "פיתוח", "מחשב"
    ));

    private static final Set<String> MEDICAL_KEYWORDS = new HashSet<>(Arrays.asList(
            "medical", "medicine", "health", "doctor", "nurse", "hospital", "clinic", "רפואה", "בריאות", "רופא", "אחות", "בית חולים", "מרפאה"
    ));

    private static final Set<String> EDUCATION_KEYWORDS = new HashSet<>(Arrays.asList(
            "education", "study", "school", "university", "college", "learn", "course", "degree", "חינוך", "לימודים", "בית ספר", "אוניברסיטה", "מכללה", "ללמוד", "קורס", "תואר"
    ));

    public static String classifyEducation(String text) {
        text = text.toLowerCase();
        List<Term> terms = HanLP.segment(text);

        int economicScore = 0;
        int engineerScore = 0;
        int medicalScore = 0;
        int educationScore = 0;

        for (Term term : terms) {
            String word = term.word;
            if (ECONOMIC_KEYWORDS.contains(word)) {
                economicScore++;
            }
            if (ENGINEER_KEYWORDS.contains(word)) {
                engineerScore++;
            }
            if (MEDICAL_KEYWORDS.contains(word)) {
                medicalScore++;
            }
            if (EDUCATION_KEYWORDS.contains(word)) {
                educationScore++;
            }
        }

        if (economicScore > engineerScore && economicScore > medicalScore && economicScore > educationScore) {
            return "ECONOMIC";
        } else if (engineerScore > economicScore && engineerScore > medicalScore && engineerScore > educationScore) {
            return "ENGINEER";
        } else if (medicalScore > economicScore && medicalScore > engineerScore && medicalScore > educationScore) {
            return "MEDICAL";
        } else if (educationScore > economicScore && educationScore > engineerScore && educationScore > medicalScore) {
            return "EDUCATION";
        } else {
            return "UNCERTAIN";
        }
    }
}

