package API;

import java.util.ArrayList;

public class QuizLogic
{
    public static String[] playerNames = {"Player 1", "Player 2"};
    public static int[] scores = {0,0};

    public static String questionCategory;
    public static int questionPoints;

    public static Question currentQuestion;

    public static ArrayList<String> categories = new ArrayList<>();

    public static int[][] selectedQuestions;

    public static boolean[] phoneHelpUsed = {false, false};
    public static boolean[] doublePointsUsed = {false, false};
    public static boolean[] stealQuestionUsed = {false, false};

    public static int activePlayer = 0;
    public static boolean doublePointsActive = false;

    public static void initializeSelectedQuestionsArray()
    {
        selectedQuestions = new int[categories.size()][3];
        for(int i = 0; i < categories.size(); i++)
        {
            for (int j = 0; j < 3; j++)
            {
                selectedQuestions[i][j] = 0;
            }
        }
    }

    public static boolean checkIfAllQuestionsArePressed()
    {
        if (selectedQuestions == null) {
            return false;
        }

        for (int[] selectedQuestion : selectedQuestions) {
            for (int i : selectedQuestion) {
                if (i == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void restartQuiz()
    {
        scores = new int[]{0,0};
        QuizLogic.categories.clear();

        selectedQuestions = null;
        questionCategory = null;
        questionPoints = 0;
        currentQuestion = null;

        phoneHelpUsed = new boolean[]{false, false};
        doublePointsUsed = new boolean[]{false, false};
        stealQuestionUsed = new boolean[]{false, false};

        activePlayer = 0;
        doublePointsActive = false;
    }

    public static void resetValuesAfterQuestion()
    {
        QuizLogic.questionCategory = null;
        QuizLogic.questionPoints = 0;
        QuizLogic.currentQuestion = null;
    }

    public static void switchPlayer()
    {
        activePlayer = 1 - activePlayer;
        doublePointsActive = false;
    }
}
