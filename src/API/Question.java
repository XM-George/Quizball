package API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("unchecked")
public class Question
{
    public static String[] categoryNames = {"geography" , "gossip" , "hiddenQuestion" , "history" , "top5"};
    public static Map<String, ArrayList<Question>[]> categories = new HashMap<>();

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
        this.question = question;
        this.answer = answer;
        this.points = 0;
        categories.get(category)[points].add(this);
    }

    public Question(String question, String answer, int points, String category)
    {
        this.question = question;
        this.answer = answer;
        this.points = points;
        categories.get(category)[points-1].add(this);
    }

    public static void getAndThenRemoveQuestion()
    {
        ArrayList<Question> list = categories.get(QuizLogic.questionCategory)[QuizLogic.questionPoints - 1];
        if (list.isEmpty()) {
            QuizLogic.currentQuestion = null;
            return;
        }

        int random = (int) (Math.random() * list.size());
        QuizLogic.currentQuestion = list.get(random);
        list.remove(random);
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
}