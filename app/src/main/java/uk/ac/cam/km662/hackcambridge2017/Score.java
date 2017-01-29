package uk.ac.cam.km662.hackcambridge2017;

import java.util.ArrayList;

/**
 * Created by kamile on 28/01/2017.
 *
 * Store score of user for analytics
 */

public class Score {
    private int correctQuestions = 0;
    private int incorrectQuestions = 0;
    private ArrayList weeklyProgress;
    private int[] topics = new int[5];
    
    //topic 1-2, 3-5
    public void setUp() {
        topics = new int[5];
    }
    
    public int getTopic() {
        return 1;
    }
    
    public void incrementTopic(int topic, int level) {
        //got correct
    }
    
    public void decrementTopic(int topic, int level) {
        //got wrong
    }
    
    //difficulty 1-3
    public int getDifficulty() {
        return 1;
    }

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
