package com.example.missionstatement.Tools;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.annotation.RequiresApi;

import com.example.missionstatement.Objects.Test;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Functions {


    private static boolean isEditTextCleared = false;
    private static int counter = 30;//id of editText
    private static ArrayList<Integer> Id;

    public static void RemovePrestentedText(EditText editText) {
        if (editText != null) {
            Id = new ArrayList<>();
// Flag to track whether EditText has been touched before

            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (counter == 30) {
                        Id.add(editText.getId());
                        editText.setText("");
                        counter--;

                    }


                    if (!(Id.contains(editText.getId()))) {
                        Id.add(editText.getId());
                        counter--;
                        editText.setText("");

                    }

                    return false; // Return false to ensure other touch events are also handled (if any)
                }
            });


        }
    }

    public static void erroronEditText(EditText editText, String error, String numericPattern) {
        editText.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {
                                                String text = editable.toString().replace(" ","");

                                                if ((!text.matches(numericPattern) && numericPattern != null && text != null)) {
                                                    editText.setError(error);
                                                } else {
                                                    editText.setError(null); // Remove the error message if the text is valid
                                                }


                                            }
                                        }
        );
    }

    public static boolean isNumeric(String input, String numericPattern) {
        // Regular expression pattern to match numeric values (integers or decimals)

        // Create a Pattern object
        Pattern pattern = Pattern.compile(numericPattern);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(input);

        // Check if the entire input matches the pattern
        return matcher.matches();
    }


    private static String cleanText(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static void CleanEditText(List<EditText> arr, boolean b) {
        if (b) {
            for (int i = 0; i < arr.size(); i++) {
                cleanText(arr.get(i).getText().toString());
            }
        }
    }

    public static boolean checkFirstDigit(long num) {
        long n = num;
        int s = 0;
        while (n > 10) {
            n /= 10;
            s++;
        }
        return n == 0 && s == 9;
    }


    public static boolean checkPhone(String a) {
        if (a.indexOf('0') != 0 || a.length() != 10 || a == null) {
            return false;
        }
        return OnlyDigits(a);

    }

    public static void showErrorDialog(String message, Context c) {
        new AlertDialog.Builder(c)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public static boolean OnlyDigits(String text) {
        for (char c : text.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static Intent sendDeatilsToEmail(String email, String message, String subject) {
        String[] recipients = {email};


        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        return emailIntent;
    }

    public static boolean isValidPattern(String email, String pattern) {
        // Regular expression pattern for a simple email address
        // String pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+";

        return Pattern.matches(pattern, email);
    }

    public static <K, V> boolean areMapsEqual(Map<K, V> map1, Map<K, V> map2) {
        if (map1.size() != map2.size()) {
            return false; // Different number of entries
        }

        for (Map.Entry<K, V> entry : map1.entrySet()) {
            K key = entry.getKey();
            V value1 = entry.getValue();
            V value2 = map2.get(key);

            if (!Objects.equals(value1, value2) || value1 == null || value2 == null) {
                return false; // Values for the same key are different
            }
        }

        return true; // Maps are equal
    }

    //profle

    public static void makeViewsFocusableAndEditable(View view) {
        if (view instanceof ViewGroup) {
            // If the view is a ViewGroup, recursively iterate through its children
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                makeViewsFocusableAndEditable(viewGroup.getChildAt(i));
            }
        } else {
            // If the view is not a ViewGroup, make it focusable and editable
            if (view.isFocusable()) {
                view.setFocusable(true);
            }

            if (view instanceof EditText) {
                // If the view is an EditText, make it editable
                EditText editText = (EditText) view;
                editText.setFocusableInTouchMode(true);
                editText.setClickable(true);
                editText.setCursorVisible(true);
            }
        }
    }
    public static int calculateAgee(String dateString) throws ParseException {

        // Use LocalDate from java.time package (Java 8+)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = dateFormat.parse(dateString);
        LocalDate birthDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            birthDate = LocalDate.parse(dateFormat.format(date));
        }
        LocalDate today = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today = LocalDate.now();
        }

        // Calculate age using Period
        Period period = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            period = Period.between(birthDate, today);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return period.getYears();
        }
        return  0;
    }

    public static Intent sendEmail(String[] arr) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{arr[0], arr[1]});
        intent.putExtra(Intent.EXTRA_SUBJECT, arr[2]);
        intent.putExtra(Intent.EXTRA_TEXT, arr[3]);
        return intent;
    }
    public static int[] convertListToArray(List<Integer> list) {
        // Step 1: Convert List<Integer> to Integer[]
        Integer[] integerArray = list.toArray(new Integer[0]);

        // Step 2: Convert Integer[] to int[]
        int[] intArray = new int[integerArray.length];
        for (int i = 0; i < integerArray.length; i++) {
            intArray[i] = integerArray[i];
        }

        return intArray;
    }
    public static int[] sortAndReturnMin(int[] arr) {
        // Sort the array in ascending order
        Arrays.sort(arr);

        // Return a new array with the minimum value as the first element
        return arr;
    }
public static double distance(int x1,int x2,int y1,int y2)
{
    return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y1-y2,2));
}


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isValidLocalDateFormat(String dateString) {
        try {
            // Parse the String with a specific format pattern (replace with your desired format)
            LocalDate.parse(dateString.replace(" ",""), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    public static LocalDate fromStringToLocalDatte(String dateString )
    {
        LocalDate parsedDate =null;
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        }

        // Parse the String into LocalDate object
        //when we call this function we guess string at format

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            parsedDate = LocalDate.parse(dateString, formatter);
        }

        return parsedDate;
    }

    public   static  String sanitizeKey(String key) {
        return key.replaceAll("[/.$#\\[\\]]", "x");
    }

    public static Test fromMap(Map<String, Object> map) {
        Test test = new Test();
        test.setCategory((String) map.get("category"));
        test.setDone((Boolean) map.get("isDone"));
        test.setFileLocation((StorageReference) map.get("fileLocation"));

        test.setQuestions((List<String>) map.get("questions"));
        test.setAnswers((List<List<String>>) map.get("answers"));
        test.setResults((List<Integer>) map.get("results"));
        test.setPointsPerAnswer((List<Integer>) map.get("pointsPerAnswer"));
        return test;
    }public static Test fromMapTest(Map<String, Object> map) {
        Test test = new Test();
        test.setDone((Boolean) map.get("isDone"));

        test.setResults((List<Integer>) map.get("results"));
        test.setPointsPerAnswer((List<Integer>) map.get("pointsPerAnswer"));
        return test;
    }
    public static int calculateAge(String birthdate) throws  ParseException {
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        }

        LocalDate birthDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            birthDate = LocalDate.parse(birthdate, formatter);
        }
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Period.between(birthDate, currentDate).getYears();
        }
return  0;
    }
    public static  String toBirthdateFormat(LocalDate date)
    {
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        }
        String formattedDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDate = date.format(formatter);
        }
        return  (formattedDate);

    }

    public static Integer findMostFrequentNumber(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return null; // Return null or throw an exception if the list is empty or null
        }

        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (Integer number : numbers) {
            frequencyMap.put(number, frequencyMap.getOrDefault(number, 0) + 1);
        }

        int maxFrequency = 0;
        Integer mostFrequentNumber = null;
        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mostFrequentNumber = entry.getKey();
            }
        }

        return mostFrequentNumber;
    }
    public static Map.Entry<Integer, Integer> findMostFrequentNumberShows(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return null; // Return null or throw an exception if the list is empty or null
        }

        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (Integer number : numbers) {
            frequencyMap.put(number, frequencyMap.getOrDefault(number, 0) + 1);
        }

        Map.Entry<Integer, Integer> mostFrequentEntry = null;
        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (mostFrequentEntry == null || entry.getValue() > mostFrequentEntry.getValue()) {
                mostFrequentEntry = entry;
            }
        }

        return mostFrequentEntry;
    }
    public static List<?> convertLongListToIntList(List<?> originalList) {
        if (originalList.isEmpty()) {
            return originalList; // אם הרשימה ריקה, תחזיר את המקורית כמו שהיא
        }

        // בדיקה האם האיבר הראשון ברשימה הוא Long
        boolean isLongList = originalList.get(0) instanceof Long;

        if (isLongList) {
            // אם הרשימה היא מטיפוס Long, המר את כל האיברים ל Integer
            return originalList.stream()
                    .map(o -> ((Long) o).intValue())
                    .collect(Collectors.toList());
        } else {
            // אם הרשימה אינה מטיפוס Long, החזר את הרשימה כפי שהיא
            return originalList;
        }
    }
}


