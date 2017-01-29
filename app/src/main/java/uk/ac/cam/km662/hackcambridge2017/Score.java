package uk.ac.cam.km662.hackcambridge2017;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by kamile on 28/01/2017.
 *
 * Store score of user for analytics
 */

public class Score {
    private int correctQuestions = 0;
    private int incorrectQuestions = 0;
    private ArrayList weeklyProgress;
    private int topicRange[] = new int[5];
    private int topicScores[] = new int[5];
    int min, max;
    
    //topic 1-2, 3-5
    public Score() {
        for (int i=0; i<topicRange.length; i++) {
            topicRange[i] = i*3;
            topicScores[i] = 0;
        }
        min=0;
        max = 15;
    }
    
    public int getTopic() {
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        int sum=0, i=0;
        //recheck this
        while (sum < randomNum) {
            sum += topicRange[i];
            i++;
        }
        return i;
    }
    
    public void incrementTopic(int topic, int level) {
        //got correct
        int i=topic;
        while (i<topicRange.length) {
            if (topicScores[i]-level < 0) {
                topicScores[i] = 0;
                topicRange[i] = 0;
            } else {
                adjustRest(topic, level);
            }
        }
        incrementQuestionScore();
    }

    public void decrementTopic(int topic, int level) {
        //got wrong
        int i = topic;
        while (i < topicRange.length) {
            if (topicScores[i] - level < 0) {
                topicScores[i] = 0;
                topicRange[i] = 0;
            } else {
                adjustRest(topic, level);
            }
        }
        decrementQuestionScore();
    }
    
    //difficulty 1-3
    public int getDifficulty() {
        int randomNum = ThreadLocalRandom.current().nextInt(1, 3 + 1);
        return randomNum;
    }
        
    private void adjustRest(int pos, int amt) {
        while (pos < topicRange.length) {
            topicRange[pos] += amt;
            topicScores[pos] += amt;
        }
        max += amt;
    }
    
    public int[] getTopicScores() { return topicScores;}

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
