package com.example.missionstatement.Tools;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MissionClassifierHanLP {

    private static final Set<String> LOVE_KEYWORDS = new HashSet<>(Arrays.asList(
            "love", "relationship", "partner", "dating", "romance", "marriage", "אהבה", "זוגיות", "שותף", "דייטינג", "רומנטיקה", "נישואין"
    ));
    private static final Set<String> FITNESS_KEYWORDS = new HashSet<>(Arrays.asList(
            "love", "relationship", "partner", "dating", "romance", "marriage", "תוכנית אמונים", "תזונה", "כושר"
    ));

    private static final Set<String> EDUCATION_KEYWORDS = new HashSet<>(Arrays.asList(
            "education", "study", "school", "university", "college", "learn", "course", "degree", "חינוך", "לימודים", "בית ספר", "אוניברסיטה", "מכללה", "ללמוד", "קורס", "תואר"
    ));

    public static String classifyMission(String text) {
        text = text.toLowerCase();
        List<Term> terms = HanLP.segment(text);

        int loveScore = 0;
        int educationScore = 0;
        int ft=0;

        for (Term term : terms) {
            String word = term.word;
            if (LOVE_KEYWORDS.contains(word)) {
                loveScore++;
            }
            if (EDUCATION_KEYWORDS.contains(word)) {
                educationScore++;
            }  if (FITNESS_KEYWORDS.contains(word)) {
                ft++;
            }
        }

        if (ft > educationScore) {
            return "FITNESS";
        } else if (educationScore > ft) {
            return "EDUCATION";
        } else {
            return "BOTH";
        }
    }


}

