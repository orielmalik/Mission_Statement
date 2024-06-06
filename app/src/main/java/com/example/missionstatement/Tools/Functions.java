package com.example.missionstatement.Tools;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static Intent sendEmail(String[] arr) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{arr[0], arr[1]});
        intent.putExtra(Intent.EXTRA_SUBJECT, arr[2]);
        intent.putExtra(Intent.EXTRA_TEXT, arr[3]);
        return intent;
    }




}


