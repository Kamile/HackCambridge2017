package uk.ac.cam.km662.hackcambridge2017;

import java.util.ArrayList;

/**
 * Created by kamile on 28/01/2017.
 *
 * Store score of user for analytics
 */

public static class Score {
    private static int correctQuestions = 0;
    private static int incorrectQuestions = 0;
    private static ArrayList weeklyProgress;

    public int getCorrectQuestions(){
        return correctQuestions;
    }

    public int getIncorrectQuestions() {
        return incorrectQuestions;
    }

    public void incrementQuestionScore(){
        correctQuestions += 1;
    }

    public void decrementQuestionScore(){
        incorrectQuestions -= 1;
    }
}
