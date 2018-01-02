package edu.uoc.iartal.trekkingchallenge.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Functionality used by different activities
public class CommonFunctionality {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]{2,}+)*$";

    /**
     *  Validate mail format
     * @param email
     * @return if mail is ok
     */
    public boolean validateEmail(String email) {
        Pattern pattern;

        // Define mail pattern
        pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Validate password length
     * @param password
     * @return if password is ok
     */
    public boolean validatePassword(String password) {
        return password.length() > 5;
    }

    /**
     * Sum two hours in double format
     * @param firstHour
     * @param secondHour
     * @return
     */
    public double sumHours (Double firstHour, Double secondHour){
        String [] firstNum = (Double.toString(firstHour)).split("\\.");
        String [] secondNum = (Double.toString(secondHour)).split("\\.");
        Integer firstInt = Integer.parseInt(firstNum[0]);
        Integer secondInt = Integer.parseInt(secondNum[0]);
        double totalDecimal = (firstHour - (double)firstInt) + (secondHour - (double)secondInt);

        if (totalDecimal > ConstantsUtils.MINUTES){
            totalDecimal = totalDecimal / ConstantsUtils.MINUTES;
        }
        return round((double)firstInt + (double)secondInt + totalDecimal,ConstantsUtils.NUM_OF_DECIMALS);
    }

    public ArrayList<Integer> sumTime (int historyHour, int hour, int historyMin, int min){
        ArrayList<Integer> time = new ArrayList<>();
        int totalMin = historyMin + min;
        int countHour = 0;

        while (totalMin >= 60){
            totalMin = totalMin - 60;
            countHour ++;
        }

        time.add(historyHour + hour + countHour);
        time.add(totalMin);

        return time;
    }

    /**
     * Round double number decimals
     * @param value
     * @param places
     * @return
     */
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
