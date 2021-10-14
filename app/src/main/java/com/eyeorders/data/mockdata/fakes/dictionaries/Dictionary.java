package com.eyeorders.data.mockdata.fakes.dictionaries;


import com.eyeorders.data.mockdata.fakes.helpers.TextualHelper;

public class Dictionary {

    /**
     * @param   amountWords
     * @param   amountSentences
     * @return  Random line/s of text, consisting from the given amount of words if 1 sentence / sentences
     */
    String getRandomLine(Integer amountWords, Integer amountSentences) {
        return "";
    }

    /**
     * @param   amountWords
     * @return  One random line of text, consisting from roughly the given amount of words
     */
    public String getRandomLine(Integer amountWords) {
        Integer amountSentences  = 1;

        return getRandomLine(amountWords, amountSentences).trim();
    }

    /**
     * @param   max     Maximum
     * @return  Random  number within min to max
     */
    private static Integer getRandomNumber(Integer max) {
        return (int)(Math.random() * ((max) + 1));
    }

    /**
     * @param   words    Array of strings
     * @return  One random item out of the given array, optionally one that contains the given amount of words
     */
    static String pickRandomString(String[] words, Integer amountWords)  {
        if (null != amountWords && amountWords > 0) {
                // Reduce sentences to items with the closest to the given amount of words
            words = TextualHelper.filterByWordCount(words, amountWords, 2);
        }

        return words[getRandomNumber(words.length -1)];
    }

    /**
     * @param   words
     * @return  One random item of the given array of words
     */
    static String pickRandomString(String[] words) {
        return pickRandomString(words, null);
    }

    /**
     * @param   str
     * @return  Given string with first character made uppercase
     */
    static String ucFirst(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
