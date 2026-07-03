package API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("unchecked")
public class Question
{
    public static String[] categoryNames = {"geography" , "gossip" , "hiddenQuestion" , "history" , "top5"};
    public static Map<String, ArrayList<Question>[]> categories = new HashMap<>();
    public static Map<String, int[]> originalQuestionCount = new HashMap<>();

    public String question;
    public String answer;
    public int points;

    static
    {
        for (String category : categoryNames)
        {
            categories.put(category , new ArrayList[]{new ArrayList<Question>(), new ArrayList<Question>(), new ArrayList<Question>()});
        }
    }

    public Question()
    {

    }

    public Question(String question, String answer, String category)
    {
        this(question, answer, 1, category);
    }

    public Question(String question, String answer, int points, String category)
    {
        this.question = question;
        this.answer = answer;
        this.points = points;

        if (!categories.containsKey(category)) {
            throw new IllegalArgumentException("Unknown category: " + category);
        }

        if (points < 1 || points > 3) {
            throw new IllegalArgumentException("Invalid points: " + points + " for question: " + question);
        }

        categories.get(category)[points - 1].add(this);
    }

    public static void getAndThenRemoveQuestion()
    {
        if (QuizLogic.questionCategory == null || QuizLogic.questionPoints <= 0) {
            QuizLogic.currentQuestion = null;
            return;
        }

        ArrayList<Question>[] categoryQuestions = categories.get(QuizLogic.questionCategory);

        if (categoryQuestions == null) {
            QuizLogic.currentQuestion = null;
            return;
        }

        if (QuizLogic.questionPoints > categoryQuestions.length) {
            QuizLogic.currentQuestion = null;
            return;
        }


        ArrayList<Question> list = categoryQuestions[QuizLogic.questionPoints - 1];
        if (list.isEmpty()) {
            QuizLogic.currentQuestion = null;
            return;
        }

        int random = (int) (Math.random() * list.size());
        QuizLogic.currentQuestion = list.remove(random);
    }

    public static boolean hasAvailableQuestion()
    {
        if (QuizLogic.categories.isEmpty()) {
            return false;
        }

        for (String c : QuizLogic.categories) {
            for (int i = 0; i < 3; i++) {
                if (!categories.get(c)[i].isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void saveOriginalQuestionCounts()
    {
        originalQuestionCount.clear();

        for (String category : categoryNames)
        {
            int[] counts = new int[3];

            for (int i = 0; i < 3; i++)
            {
                counts[i] = categories.get(category)[i].size();
            }

            originalQuestionCount.put(category, counts);
        }
    }

    public static int getQuestionsLeft(String category, int points)
    {
        ArrayList<Question>[] questionLists = categories.get(category);

        if (questionLists == null || points < 1 || points > 3) {
            return 0;
        }

        return questionLists[points - 1].size();
    }

    public static int getQuestionsPlayed(String category, int points)
    {
        int[] originalCounts = originalQuestionCount.get(category);

        if (originalCounts == null || points < 1 || points > 3) {
            return 0;
        }

        int original = originalCounts[points - 1];
        int left = getQuestionsLeft(category, points);

        return original - left;
    }

}