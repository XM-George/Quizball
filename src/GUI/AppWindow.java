package GUI;

import API.Question;
import API.QuizLogic;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class AppWindow {

    Font f = new Font(null, Font.PLAIN, 20);

    ImageIcon footballImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/ICONS/football.png")));

    JFrame main;

    public void start() {
        main = new JFrame();
        main.setPreferredSize(new Dimension(800, 800));
        main.pack();
        main.setLocationRelativeTo(null);
        main.setResizable(false);
        main.setLayout(null);
        main.setIconImage(footballImage.getImage());
        main.setTitle("Quizball");

        setMenuBar();

        setNamesToFrame();

        setScoreForPlayers();

        main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JButton showQ = new JButton();
        showQ.setFocusable(false);
        showQ.setBounds(300, 650, 200, 50);
        showQ.setSize(200, 50);
        if(QuizLogic.categories.isEmpty()) {
            showQ.setText("Select categories");
        }
        else
        {
            showQ.setText("Select question");
        }
        main.getRootPane().setDefaultButton(showQ);

        if (!QuizLogic.categories.isEmpty() && !Question.hasAvailableQuestion()) {
            showQ.setText("Game finished");
            showQ.setEnabled(false);
            showQ.setVisible(false);
            showRestartButton();
        }
        else if (!QuizLogic.categories.isEmpty() && QuizLogic.checkIfAllQuestionsArePressed()) {
            showQ.setText("Game finished");
            showQ.setEnabled(false);
            showQ.setVisible(false);
            showRestartButton();
        }

        showQ.addActionListener(_ -> {
            if (QuizLogic.categories.isEmpty()) {
                categorySelect();

                main.dispose();
                start();
                return;
            }

            if (!Question.hasAvailableQuestion()) {
                main.dispose();
                start();
                return;
            }

            boolean selected = questionSelect();

            if (selected) {
                main.dispose();
                showQuestionAnswerDialog("Q");
            } else {
                main.dispose();
                start();
            }
        });

        main.add(showQ);

        main.setVisible(true);
    }

    public void showRestartButton()
    {
        JButton restart = new JButton("Restart");
        ImageIcon playAgain = new ImageIcon(Objects.requireNonNull(getClass().getResource("/ICONS/play-again.png")));
        Image scaledImage = playAgain.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        restart.setIcon(new ImageIcon(scaledImage));
        restart.setBounds(300, 650, 200, 50);
        restart.setSize(200, 50);
        restart.setFocusable(false);
        main.getRootPane().setDefaultButton(restart);

        restart.addActionListener(_ -> {
            QuizLogic.restartQuiz();
            main.dispose();
            start();
        });

        restart.setVisible(true);

        main.add(restart);
    }

    public void setMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Set names");
        JMenuItem setNames = new JMenuItem("Set names");
        setNames.addActionListener(_ -> {
            getNames();
            main.dispose();
            start();
        });

        menu.add(setNames);

        menuBar.add(menu);

        main.setJMenuBar(menuBar);
    }

    public void categorySelect()
    {
        JDialog categoryDialog = new JDialog();
        categoryDialog.getContentPane().setPreferredSize(new Dimension(800, 500));
        categoryDialog.pack();
        categoryDialog.setTitle("Choose categories");
        ImageIcon questionImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/ICONS/questions.png")));
        categoryDialog.setIconImage(questionImage.getImage());
        categoryDialog.setLocationRelativeTo(null);
        categoryDialog.setResizable(false);
        categoryDialog.setModal(true);
        categoryDialog.setLayout(null);

        int width = 20;
        int height = 20;
        int categorySum = 0;

        JButton[] buttons = new JButton[Question.categories.size()];

        for (String c : Question.categoryNames)
        {
            ImageIcon rawCategoryIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/ICONS/" + c + ".png")));
            Image categoryImage = rawCategoryIcon.getImage().getScaledInstance(42, 42, Image.SCALE_SMOOTH);

            JLabel label = new JLabel(c.toUpperCase());
            label.setBounds(width, height, 250,30);
            label.setFont(f);

            JButton button = new JButton();
            button.setIcon(new ImageIcon(categoryImage));
            button.setBounds(width, height + 30, 100, 50);
            button.setFocusable(false);
            buttons[categorySum] = button;
            button.addActionListener(_ -> {
                String category = new File(rawCategoryIcon.getDescription()).getName().replaceFirst("[.][^.]+$", "");
                QuizLogic.categories.add(category);
                button.setEnabled(false);
            });

            width += 250;
            categorySum += 1;
            if(categorySum % 3 == 0)
            {
                width = 20;
                height += 90;
            }

            categoryDialog.add(label);
            categoryDialog.add(button);
        }

        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBounds(100, 420, 200, 50);
        confirmButton.setFocusable(false);
        confirmButton.setFont(f);
        confirmButton.addActionListener(_ -> categoryDialog.dispose());
        categoryDialog.getRootPane().setDefaultButton(confirmButton);

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(500, 420, 200, 50);
        resetButton.setFocusable(false);
        resetButton.setFont(f);
        resetButton.addActionListener(_ -> {
            QuizLogic.categories.clear();
            for (JButton b : buttons)
            {
                b.setEnabled(true);
            }
        });

        categoryDialog.add(confirmButton);
        categoryDialog.add(resetButton);

        categoryDialog.setVisible(true);

        if (QuizLogic.categories.isEmpty()) {
            QuizLogic.categories.addAll(Arrays.asList(Question.categoryNames));
        }

        QuizLogic.initializeSelectedQuestionsArray();
    }

    public boolean questionSelect()
    {
        QuizLogic.resetValuesAfterQuestion();

        JDialog questionDialog = new JDialog();
        questionDialog.getContentPane().setPreferredSize(new Dimension(400, 100 * (QuizLogic.categories.size() + 1) ));
        questionDialog.pack();
        questionDialog.setTitle("Choose question");
        ImageIcon questionImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/ICONS/questions.png")));
        questionDialog.setIconImage(questionImage.getImage());
        questionDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        questionDialog.setLocationRelativeTo(null);
        questionDialog.setResizable(false);
        questionDialog.setModal(true);
        questionDialog.setLayout(null);

        int categoryNumber = 0;

        int width = 30;
        int height = 20;

        for (String c : QuizLogic.categories)
        {
            JLabel label = new JLabel(c.toUpperCase());
            label.setBounds(width, height, 340,30);
            label.setFont(f);

            height += 40;

            for(int i=0;i<3;i++)
            {
                JButton button = new JButton();

                int points = getPointsForButton(c, i);

                button.setText("x" + points);
                button.setBounds(width, height, 100, 50);
                button.setFocusable(false);
                button.setFont(f);

                if (Question.categories.get(c)[points - 1].isEmpty())
                {
                    button.setEnabled(false);
                    QuizLogic.selectedQuestions[categoryNumber][i] = 1;
                }

                if(QuizLogic.selectedQuestions[categoryNumber][i] == 1)
                {
                    button.setEnabled(false);
                }

                int finalI = i;
                int finalCategoryNumber = categoryNumber;

                button.addActionListener(_ -> {
                    QuizLogic.questionCategory = c;
                    QuizLogic.questionPoints = points;
                    QuizLogic.selectedQuestions[finalCategoryNumber][finalI] = 1;
                    questionDialog.dispose();
                });

                width+=120;

                questionDialog.add(button);
            }

            width = 30;
            height += 60;

            categoryNumber ++;

            questionDialog.add(label);
        }

        questionDialog.setVisible(true);

        if (QuizLogic.questionCategory == null || QuizLogic.questionPoints <= 0) {
            QuizLogic.currentQuestion = null;
            return false;
        }

        Question.getAndThenRemoveQuestion();

        return QuizLogic.currentQuestion != null;
    }

    private int getPointsForButton(String category, int buttonIndex)
    {
        if ("hiddenQuestion".equals(category)) {
            return 2;
        }

        if ("top5".equals(category)) {
            return 3;
        }

        return buttonIndex + 1;
    }

    public void getNames()
    {
        JDialog nameDialog = new JDialog();
        nameDialog.setSize(800, 300);
        nameDialog.setTitle("Set names");
        nameDialog.setLocationRelativeTo(null);
        nameDialog.setResizable(false);
        nameDialog.setModal(true);
        nameDialog.setLayout(null);

        JLabel nameLabel1 = new JLabel("Player 1:");
        nameLabel1.setFont(f);
        JLabel nameLabel2 = new JLabel("Player 2:");
        nameLabel2.setFont(f);

        JTextField nameField1 = new JTextField();
        nameField1.setFont(f);
        JTextField nameField2 = new JTextField();
        nameField2.setFont(f);

        JButton setNamesButton = new JButton("Set names");
        setNamesButton.setFont(f);
        setNamesButton.setFocusable(false);
        nameDialog.getRootPane().setDefaultButton(setNamesButton);
        setNamesButton.addActionListener(_ -> {
            if(!nameField1.getText().isEmpty())
            {
                QuizLogic.playerNames[0] = nameField1.getText();
            }
            if(!nameField2.getText().isEmpty())
            {
                QuizLogic.playerNames[1] = nameField2.getText();
            }
            nameDialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(f);
        cancelButton.setFocusable(false);
        cancelButton.addActionListener(_ -> nameDialog.dispose());

        nameLabel1.setBounds(50, 20, 325, 30);
        nameLabel2.setBounds(425, 20, 325, 30);

        nameField1.setBounds(50, 60, 325, 40);
        nameField2.setBounds(425, 60, 325, 40);

        setNamesButton.setBounds(50, 200, 325, 40);
        cancelButton.setBounds(425, 200, 325, 40);

        nameDialog.add(nameLabel1);
        nameDialog.add(nameLabel2);
        nameDialog.add(nameField1);
        nameDialog.add(nameField2);
        nameDialog.add(setNamesButton);
        nameDialog.add(cancelButton);

        nameDialog.setVisible(true);
    }

    public void setNamesToFrame()
    {
        if(QuizLogic.playerNames == null)
        {
            return;
        }

        JLabel nameLabel1 = new JLabel(QuizLogic.playerNames[0]);
        nameLabel1.setFont(f);

        JLabel nameLabel2 = new JLabel(QuizLogic.playerNames[1]);
        nameLabel2.setFont(f);

        nameLabel1.setBounds(50, 20, 300, 30);
        nameLabel2.setBounds(450, 20, 300, 30);

        main.add(nameLabel1);
        main.add(nameLabel2);
    }


    JLabel scoreLabel1;
    JLabel scoreLabel2;
    public void setScoreForPlayers()
    {
        scoreLabel1 = new JLabel("Score: " + QuizLogic.scores[0]);
        scoreLabel1.setFont(f);

        scoreLabel2 = new JLabel("Score: " + QuizLogic.scores[1]);
        scoreLabel2.setFont(f);

        JButton addScoreButton1 = new JButton("+1");
        addScoreButton1.setFont(f);
        addScoreButton1.setFocusable(false);
        addScoreButton1.addActionListener(_ -> {
            if (QuizLogic.scores[0] + 1 <=100)
            {
                QuizLogic.scores[0]++;
            }
            updateScores();
        });

        JButton subtractScoreButton1 = new JButton("-1");
        subtractScoreButton1.setFont(f);
        subtractScoreButton1.setFocusable(false);
        subtractScoreButton1.addActionListener(_ -> {
           if(QuizLogic.scores[0] - 1 >= 0)
           {
               QuizLogic.scores[0]--;
           }
           updateScores();
        });

        JButton addScoreButton2 = new JButton("+1");
        addScoreButton2.setFont(f);
        addScoreButton2.setFocusable(false);
        addScoreButton2.addActionListener(_ -> {
            if (QuizLogic.scores[1] + 1 <=100)
            {
                QuizLogic.scores[1]++;
            }
            updateScores();
        });

        JButton subtractScoreButton2 = new JButton("-1");
        subtractScoreButton2.setFont(f);
        subtractScoreButton2.setFocusable(false);
        subtractScoreButton2.addActionListener(_ -> {
            if(QuizLogic.scores[1] - 1 >= 0)
            {
                QuizLogic.scores[1]--;
            }
            updateScores();
        });

        scoreLabel1.setBounds(50, 60, 300, 30);
        scoreLabel2.setBounds(450, 60, 300, 30);

        addScoreButton1.setBounds(50, 100, 100, 40);
        subtractScoreButton1.setBounds(200, 100, 100, 40);

        addScoreButton2.setBounds(450, 100, 100, 40);
        subtractScoreButton2.setBounds(600, 100, 100, 40);

        main.add(scoreLabel1);
        main.add(scoreLabel2);
        main.add(addScoreButton1);
        main.add(subtractScoreButton1);
        main.add(addScoreButton2);
        main.add(subtractScoreButton2);
    }

    public void updateScores() {
        scoreLabel1.setText("Score: " + QuizLogic.scores[0]);
        scoreLabel2.setText("Score: " + QuizLogic.scores[1]);

        main.repaint();
    }

    public void showQuestionAnswerDialog(String use) {

        if (QuizLogic.currentQuestion == null) {
            JOptionPane.showMessageDialog(main, "No question available.");

            if (main != null) {
                main.dispose();
            }

            start();
            return;
        }

        JFrame questionAnswerFrame = new JFrame();
        questionAnswerFrame.setSize(600, 500);
        questionAnswerFrame.setLayout(null);
        questionAnswerFrame.setLocationRelativeTo(null);
        questionAnswerFrame.setResizable(false);
        questionAnswerFrame.setIconImage(footballImage.getImage());

        questionAnswerFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        JTextArea field = new JTextArea();
        field.setBounds(50, 50, 500, 300);
        field.setEditable(false);
        field.setFocusable(false);
        field.setLineWrap(true);
        field.setWrapStyleWord(true);
        field.setFont(f);

        if (use.equals("Q")) {
            field.setText(QuizLogic.currentQuestion.question);
            questionAnswerFrame.setTitle("Question");
        } else if (use.equals("A")) {
            field.setText(QuizLogic.currentQuestion.question + "\n\n" + QuizLogic.currentQuestion.answer);
            questionAnswerFrame.setTitle("Answer");
        }

        JButton showNext = getShowNextButton(use, questionAnswerFrame);

        questionAnswerFrame.add(field);
        questionAnswerFrame.add(showNext);

        questionAnswerFrame.setVisible(true);
    }

    private JButton getShowNextButton(String use, JFrame frame) {
        JButton showNext = new JButton();
        showNext.setBounds(200, 380, 200, 50);
        if (use.equals("Q")) {
            showNext.setText("Show Answer");
        } else if (use.equals("A")) {
            showNext.setText("Next Question");
        }
        showNext.setFocusable(false);
        frame.getRootPane().setDefaultButton(showNext);
        showNext.addActionListener(_ -> {
            frame.dispose();
            if (use.equals("Q")) {
                showQuestionAnswerDialog("A");
            } else if (use.equals("A")) {
                start();
            }
        });
        return showNext;
    }
}