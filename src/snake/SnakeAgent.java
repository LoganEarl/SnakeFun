package snake;

public interface SnakeAgent {
    /*
    NEW MODEL============================================
    Input Mappings
    0: -90:-20     OpponentDetector
    1: -20:20       OpponentDetector
    2: 20:110       OpponentDetector
    3: -110:-20     FoodDetector
    4: -20:20       FoodDetector
    5: 20:90       FoodDetector
    6: Starvation level (activation = 1/(foodLevel+1))

    Output Mappings
    0: Right Confidence
    1: Left Confidence
    2: Boost Confidence

    OLD MODEL=============================================
    Input Mappings
    0: -180:-90 OpponentDetector
    1: -90:-60  OpponentDetector
    2: -60:-30  OpponentDetector
    3: -30:-10  OpponentDetector
    4: -10: 10  OpponentDetector
    5: 10:30    OpponentDetector
    6: 30:60    OpponentDetector
    7: 60:90    OpponentDetector
    8: 90:180   OpponentDetector
    9: -180:-90 FoodDetector
    10: -90:-60 FoodDetector
    11: -60:-30 FoodDetector
    12: -30:-10 FoodDetector
    13: -10: 10 FoodDetector
    14: 10:30   FoodDetector
    15: 30:60   FoodDetector
    16: 60:90   FoodDetector
    17: 90:180  FoodDetector
    18: Starvation level (activation = 1/(foodLevel+1))

    Output Mappings
    0: Desired turn angle
    1: Boost
    */



    int NUM_INPUTS = 7;
    int NUM_OUTPUTS = 3;
    void bindSnake(SnakeBody snake);
    SnakeBody getBoundSnake();
    void loadInputs(double[] inputs);
    void processInputs();
    void takeActions();
    void setScore(double score);
    double getScore();
}
