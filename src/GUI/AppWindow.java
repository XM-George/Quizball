package GUI;

import API.Question;
import API.QuizLogic;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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

        boolean gameFinished = (!QuizLogic.categories.isEmpty() && (!Question.hasAvailableQuestion() || QuizLogic.checkIfAllQuestionsArePressed()));

        if (!gameFinished && !QuizLogic.categories.isEmpty()) {
            setMainScreenHelpButtons();
            setCurrentPlayerLabel();
        }

        main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JButton showQ = new JButton();
        showQ.setFocusable(false);
        showQ.setFont(f);
        showQ.setBounds(300, 650, 200, 50);
        if (!QuizLogic.coinFlipDone)
        {
            showQ.setText("Run Coin Flip");
        }
        else if (QuizLogic.categories.isEmpty())
        {
            showQ.setText("Select categories");
        }
        else
        {
            showQ.setText("Select question");
        }
        main.getRootPane().setDefaultButton(showQ);

        if (gameFinished) {
            showQ.setText("Game finished");
            showQ.setEnabled(false);
            showQ.setVisible(false);
            showGameFinishedScreen();
        }

        showQ.addActionListener(e -> {
            if (!QuizLogic.coinFlipDone) {
                showCoinFlipDialog();
                main.dispose();
                start();
                return;
            }

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

            main.dispose();
            if (selected) {
                showQuestionAnswerDialog("Q");
            } else {
                start();
            }
        });

        main.add(showQ);

        main.setVisible(true);
    }

    public void showGameFinishedScreen()
    {
        JLabel finished = new JLabel("Game finished");
        finished.setFont(f);
        finished.setBounds(300, 450, 300, 40);

        String winnerText;

        if (QuizLogic.scores[0] > QuizLogic.scores[1]) {
            winnerText = "Winner: " + QuizLogic.playerNames[0];
        } else if (QuizLogic.scores[1] > QuizLogic.scores[0]) {
            winnerText = "Winner: " + QuizLogic.playerNames[1];
        } else {
            winnerText = "Draw";
        }

        JLabel winner = new JLabel(winnerText);
        winner.setFont(f);
        winner.setBounds(300, 500, 300, 40);

        main.add(finished);
        main.add(winner);

        showRestartButton();
    }

    public void showRestartButton()
    {
        JButton restart = new JButton("Restart");
        ImageIcon playAgain = new ImageIcon(Objects.requireNonNull(getClass().getResource("/ICONS/play-again.png")));
        Image scaledImage = playAgain.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        restart.setIcon(new ImageIcon(scaledImage));
        restart.setBounds(300, 650, 200, 50);
        restart.setFocusable(false);
        main.getRootPane().setDefaultButton(restart);

        restart.addActionListener(e -> {
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
        JMenu names = new JMenu("Set names");
        JMenuItem setNames = new JMenuItem("Set names");
        setNames.addActionListener(e -> {
            getNames();
            main.dispose();
            start();
        });

        JMenu questions = new JMenu("See questions");
        JMenuItem questionsLeft = new JMenuItem("See questions left and questions played");
        questionsLeft.addActionListener(e ->
            showQuestionsPlayedAndLeft()
        );

        names.add(setNames);

        questions.add(questionsLeft);

        menuBar.add(names);
        menuBar.add(questions);

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

            JLabel label = new JLabel(Question.getCategoryDisplayName(c));
            label.setBounds(width, height, 250,30);
            label.setFont(f);

            JButton button = new JButton();
            button.setIcon(new ImageIcon(categoryImage));
            button.setBounds(width, height + 30, 100, 50);
            button.setFocusable(false);
            buttons[categorySum] = button;
            button.addActionListener(e -> {
                QuizLogic.categories.add(c);
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
        confirmButton.setBounds(50, 420, 200, 50);
        confirmButton.setFocusable(false);
        confirmButton.setFont(f);
        confirmButton.addActionListener(e -> {
            if (!QuizLogic.categories.isEmpty()) {
                categoryDialog.dispose();
            }
        });
        categoryDialog.getRootPane().setDefaultButton(confirmButton);

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(300, 420, 200, 50);
        resetButton.setFocusable(false);
        resetButton.setFont(f);
        resetButton.addActionListener(e -> {
            QuizLogic.categories.clear();
            for (JButton b : buttons)
            {
                b.setEnabled(true);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(550, 420, 200, 50);
        cancelButton.setFocusable(false);
        cancelButton.setFont(f);
        cancelButton.addActionListener(e -> {
            QuizLogic.categories.clear();
            categoryDialog.dispose();
        });

        categoryDialog.add(cancelButton);
        categoryDialog.add(confirmButton);
        categoryDialog.add(resetButton);

        categoryDialog.setVisible(true);

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
            JLabel label = new JLabel(Question.getCategoryDisplayName(c));
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

                button.addActionListener(e -> {
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

        height+=10;

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(100, height, 200, 50);
        cancelButton.setFocusable(false);
        cancelButton.setFont(f);
        cancelButton.addActionListener(e -> questionDialog.dispose());

        questionDialog.add(cancelButton);

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
        setNamesButton.addActionListener(e -> {
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
        cancelButton.addActionListener(e -> nameDialog.dispose());

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
        addScoreButton1.addActionListener(e -> {
            if (QuizLogic.scores[0] + 1 <=100)
            {
                QuizLogic.scores[0]++;
            }
            updateScores();
        });

        JButton subtractScoreButton1 = new JButton("-1");
        subtractScoreButton1.setFont(f);
        subtractScoreButton1.setFocusable(false);
        subtractScoreButton1.addActionListener(e -> {
           if(QuizLogic.scores[0] - 1 >= 0)
           {
               QuizLogic.scores[0]--;
           }
           updateScores();
        });

        JButton addScoreButton2 = new JButton("+1");
        addScoreButton2.setFont(f);
        addScoreButton2.setFocusable(false);
        addScoreButton2.addActionListener(e -> {
            if (QuizLogic.scores[1] + 1 <=100)
            {
                QuizLogic.scores[1]++;
            }
            updateScores();
        });

        JButton subtractScoreButton2 = new JButton("-1");
        subtractScoreButton2.setFont(f);
        subtractScoreButton2.setFocusable(false);
        subtractScoreButton2.addActionListener(e -> {
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

    public void setMainScreenHelpButtons()
    {
        ImageIcon x2HelpIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("ICONS/x2Help.png")));
        Image scaledx2HelpIconImage = x2HelpIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);

        JButton doubleP1 = new JButton();
        doubleP1.setIcon(new ImageIcon(scaledx2HelpIconImage));
        doubleP1.setFont(f);
        doubleP1.setBounds(150, 180, 90, 50);
        doubleP1.setFocusable(false);
        doubleP1.addActionListener(e -> {
            QuizLogic.doublePointsUsed[0] = true;
            QuizLogic.doublePointsActive = true;
            //JOptionPane.showMessageDialog(main, QuizLogic.playerNames[0] + " activated x2 points for the next question!");
            doubleP1.setEnabled(false);
        });

        JButton doubleP2 = new JButton();
        doubleP2.setIcon(new ImageIcon(scaledx2HelpIconImage));
        doubleP2.setFont(f);
        doubleP2.setBounds(540, 180, 100, 50);
        doubleP2.setFocusable(false);
        doubleP2.addActionListener(e -> {
            QuizLogic.doublePointsUsed[1] = true;
            QuizLogic.doublePointsActive = true;
            //JOptionPane.showMessageDialog(main, QuizLogic.playerNames[1] + " activated x2 points for the next question!");
            doubleP2.setEnabled(false);
        });

        doubleP1.setToolTipText("x2 βαθμοί ερώτησης");
        doubleP2.setToolTipText("χ2 βαθμοί ερώτησης");

        doubleP1.setEnabled(QuizLogic.activePlayer == 0 && !QuizLogic.doublePointsUsed[0]);
        doubleP2.setEnabled(QuizLogic.activePlayer == 1 && !QuizLogic.doublePointsUsed[1]);

        main.add(doubleP1);
        main.add(doubleP2);
    }

    public void setCurrentPlayerLabel()
    {
        String text = "Παίζει ο " + QuizLogic.playerNames[QuizLogic.activePlayer];

        if (QuizLogic.doublePointsActive) {
            text += " | x2 ACTIVE";
        }

        JLabel currentPlayerLabel = new JLabel(text);
        currentPlayerLabel.setFont(f);
        currentPlayerLabel.setBounds(300, 250, 300, 30);

        main.add(currentPlayerLabel);
    }

    public void updateScores() {
        scoreLabel1.setText("Score: " + QuizLogic.scores[0]);
        scoreLabel2.setText("Score: " + QuizLogic.scores[1]);

        main.repaint();
    }

    public void showQuestionAnswerDialog(String use) {

        if (QuizLogic.currentQuestion == null) {
            JOptionPane.showMessageDialog(null, "No question available.");

            if (main != null) {
                main.dispose();
            }

            start();
            return;
        }

        JFrame questionAnswerFrame = new JFrame();
        questionAnswerFrame.setSize(600, 600);
        questionAnswerFrame.setLayout(null);
        questionAnswerFrame.setLocationRelativeTo(null);
        questionAnswerFrame.setResizable(false);
        questionAnswerFrame.setIconImage(footballImage.getImage());

        questionAnswerFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        JTextArea field = new JTextArea();
        field.setEditable(false);
        field.setFocusable(false);
        field.setLineWrap(true);
        field.setWrapStyleWord(true);
        field.setFont(f);
        field.setMargin(new Insets(10, 10, 10, 10));
        field.setBorder(null);

        JLabel infoLabel = new JLabel(
                "Παίζει: " + QuizLogic.playerNames[QuizLogic.activePlayer]
                        + " | Πόντοι: x" + QuizLogic.getCurrentQuestionPoints()
        );
        infoLabel.setFont(f);
        infoLabel.setBounds(50, 20, 500, 30);
        questionAnswerFrame.add(infoLabel);

        if (use.equals("Q")) {
            field.setText(QuizLogic.currentQuestion.question);
            questionAnswerFrame.setTitle("Question");

            JButton showNext = getShowNextButton(use, questionAnswerFrame);
            questionAnswerFrame.add(showNext);

            JButton helpButton = getHelpMenuButton(questionAnswerFrame);
            questionAnswerFrame.add(helpButton);

        } else if (use.equals("A")) {
            field.setText(QuizLogic.currentQuestion.question + "\n\n" + QuizLogic.currentQuestion.answer);
            if (QuizLogic.stealActive) {
                questionAnswerFrame.setTitle("Answer - Steal Active");
            } else {
                questionAnswerFrame.setTitle("Answer");
            }

            addAutomaticScoringButtons(questionAnswerFrame);
        }

        JScrollPane scrollPane = new JScrollPane(field);
        scrollPane.setBounds(50, 60, 500, 300);
        scrollPane.setBorder(null);
        scrollPane.setViewportBorder(null);

        questionAnswerFrame.add(scrollPane);

        questionAnswerFrame.setVisible(true);
    }

    public void showQuestionsPlayedAndLeft()
    {
        JDialog dialog = new JDialog(main, "Questions statistics", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(main);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        String[] columns = {
                "Category",
                "Questions left",
                "Questions played"
        };

        String[] categoriesToShow = Question.categoryNames;

        ArrayList<Object[]> rows = new ArrayList<>();

        for (String category : categoriesToShow)
        {
            for (int points = 1; points <= 3; points++)
            {
                if ("hiddenQuestion".equals(category) && points != 2) {
                    continue;
                }

                if ("top5".equals(category) && points != 3) {
                    continue;
                }

                rows.add(new Object[]{
                        Question.getCategoryDisplayName(category) + " x" + points,
                        Question.getQuestionsLeft(category, points),
                        Question.getQuestionsPlayed(category, points)
                });
            }
        }

        Object[][] data = rows.toArray(new Object[0][0]);

        JTable questionsTable = new JTable(data, columns);
        questionsTable.setEnabled(false);
        questionsTable.setRowHeight(28);
        questionsTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(questionsTable);

        JButton closeButton = new JButton("Close");
        closeButton.setFocusable(false);
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeButton);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JButton getShowNextButton(String use, JFrame frame)
    {
        JButton showNext = new JButton("Show Answer");
        showNext.setBounds(50, 480, 200, 50);
        showNext.setFocusable(false);
        showNext.setFont(f);

        frame.getRootPane().setDefaultButton(showNext);

        showNext.addActionListener(e -> {
            frame.dispose();

            if (use.equals("Q")) {
                showQuestionAnswerDialog("A");
            }
        });

        return showNext;
    }

    private JButton getHelpMenuButton(JFrame questionAnswerFrame)
    {
        JButton helpButton = new JButton("Βοήθειες");
        helpButton.setBounds(350, 480, 200, 50);
        helpButton.setFocusable(false);
        helpButton.setFont(f);

        helpButton.addActionListener(e -> showHelpMenuDialog(questionAnswerFrame));

        return helpButton;
    }

    public void showHelpMenuDialog(JFrame questionAnswerFrame)
    {
        ImageIcon phoneHelpIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("ICONS/phoneHelp.png")));
        Image scaledPhoneHelpIconImage = phoneHelpIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);

        ImageIcon stealHelpIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("ICONS/stealHelp.png")));
        Image scaledStealHelpIconImage = stealHelpIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);

        JDialog helpDialog = new JDialog(questionAnswerFrame, "Βοήθειες", true);
        helpDialog.getContentPane().setPreferredSize(new Dimension(400, 300));
        helpDialog.pack();
        helpDialog.setLocationRelativeTo(questionAnswerFrame);
        helpDialog.setResizable(false);
        helpDialog.setLayout(null);

        int active = QuizLogic.activePlayer;
        int other = 1 - active;

        JLabel title = new JLabel("Βοήθειες: " + QuizLogic.playerNames[active]);
        title.setFont(f);
        title.setBounds(50, 20, 300, 30);

        JButton phoneHelp = new JButton();
        phoneHelp.setIcon(new ImageIcon(scaledPhoneHelpIconImage));
        phoneHelp.setFont(f);
        phoneHelp.setFocusable(false);
        phoneHelp.setBounds(30, 70, 60, 45);
        phoneHelp.setEnabled(!QuizLogic.phoneHelpUsed[active]);
        phoneHelp.setToolTipText("Τηλέφωνο");

        phoneHelp.addActionListener(e -> {
            QuizLogic.phoneHelpUsed[active] = true;
            phoneHelp.setEnabled(false);


            helpDialog.dispose();
        });

        JButton stealHelp = new JButton();
        stealHelp.setIcon(new ImageIcon(scaledStealHelpIconImage));
        stealHelp.setFont(f);
        stealHelp.setFocusable(false);
        stealHelp.setBounds(120, 70, 60, 45);
        stealHelp.setEnabled(!QuizLogic.stealQuestionUsed[other]);
        stealHelp.setToolTipText("Κλέψιμο");

        stealHelp.addActionListener(e -> {
            QuizLogic.stealQuestionUsed[other] = true;
            QuizLogic.stealActive = true;

            stealHelp.setEnabled(false);
            helpDialog.dispose();
        });

        JButton closeButton = new JButton("Κλείσιμο");
        closeButton.setFont(f);
        closeButton.setFocusable(false);
        closeButton.setBounds(100, 210, 200, 45);
        closeButton.addActionListener(e -> helpDialog.dispose());

        helpDialog.add(title);
        helpDialog.add(phoneHelp);
        helpDialog.add(stealHelp);
        helpDialog.add(closeButton);

        helpDialog.setVisible(true);
    }

    public void showCoinFlipDialog()
    {
        JDialog coinDialog = new JDialog(main, "Coin Flip", true);
        coinDialog.getContentPane().setPreferredSize(new Dimension(500, 350));
        coinDialog.pack();
        coinDialog.setLocationRelativeTo(main);
        coinDialog.setResizable(false);
        coinDialog.setLayout(null);
        coinDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JLabel title = new JLabel("Coin Flip");
        title.setFont(f);
        title.setBounds(190, 20, 200, 40);

        final int playerChoosing = (int) (Math.random() * 2);
        JLabel chooseLabel = new JLabel(QuizLogic.playerNames[playerChoosing] + ", διάλεξε:");
        chooseLabel.setFont(f);
        chooseLabel.setBounds(150, 70, 300, 30);

        JButton headsButton = new JButton("Κορώνα");
        headsButton.setFont(f);
        headsButton.setFocusable(false);
        headsButton.setBounds(80, 120, 150, 50);

        JButton tailsButton = new JButton("Γράμματα");
        tailsButton.setFont(f);
        tailsButton.setFocusable(false);
        tailsButton.setBounds(270, 120, 150, 50);

        JLabel resultLabel = new JLabel("");
        resultLabel.setFont(f);
        resultLabel.setBounds(50, 190, 400, 30);

        JLabel winnerLabel = new JLabel("");
        winnerLabel.setFont(f);
        winnerLabel.setBounds(50, 225, 400, 30);

        JButton player1First = new JButton(QuizLogic.playerNames[0] + " πρώτος");
        player1First.setFont(f);
        player1First.setFocusable(false);
        player1First.setBounds(30, 270, 200, 50);
        player1First.setVisible(false);

        JButton player2First = new JButton(QuizLogic.playerNames[1] + " πρώτος");
        player2First.setFont(f);
        player2First.setFocusable(false);
        player2First.setBounds(270, 270, 200, 50);
        player2First.setVisible(false);

        final int[] winner = {-1};

        headsButton.addActionListener(e -> {
            int coinResult = (int) (Math.random() * 2); // 0 heads, 1 tails

            if (coinResult == 0) {
                winner[0] = playerChoosing;
            } else {
                winner[0] = 1 - playerChoosing;
            }

            resultLabel.setText("Αποτέλεσμα: " + (coinResult == 0 ? "Κορώνα" : "Γράμματα"));
            winnerLabel.setText("Νικητής: " + QuizLogic.playerNames[winner[0]] + ", διάλεξε πρώτο παίκτη");

            headsButton.setEnabled(false);
            tailsButton.setEnabled(false);

            player1First.setVisible(true);
            player2First.setVisible(true);
        });

        tailsButton.addActionListener(e -> {
            int coinResult = (int) (Math.random() * 2); // 0 heads, 1 tails

            if (coinResult == 1) {
                winner[0] = playerChoosing;
            } else {
                winner[0] = 1 -  playerChoosing;
            }

            resultLabel.setText("Αποτέλεσμα: " + (coinResult == 0 ? "Κορώνα" : "Γράμματα"));
            winnerLabel.setText("Νικητής: " + QuizLogic.playerNames[winner[0]] + ", διάλεξε πρώτο παίκτη");

            headsButton.setEnabled(false);
            tailsButton.setEnabled(false);

            player1First.setVisible(true);
            player2First.setVisible(true);
        });

        player1First.addActionListener(e -> {
            QuizLogic.activePlayer = 0;
            QuizLogic.coinFlipDone = true;
            coinDialog.dispose();
        });

        player2First.addActionListener(e -> {
            QuizLogic.activePlayer = 1;
            QuizLogic.coinFlipDone = true;
            coinDialog.dispose();
        });

        coinDialog.add(title);
        coinDialog.add(chooseLabel);
        coinDialog.add(headsButton);
        coinDialog.add(tailsButton);
        coinDialog.add(resultLabel);
        coinDialog.add(winnerLabel);
        coinDialog.add(player1First);
        coinDialog.add(player2First);

        coinDialog.setVisible(true);
    }

    public void addAutomaticScoringButtons(JFrame questionAnswerFrame)
    {
        int active = QuizLogic.activePlayer;
        int other = 1 - active;

        int activePoints = QuizLogic.getCurrentQuestionPoints();
        int otherPoints = QuizLogic.currentQuestion.points;

        if (!QuizLogic.stealActive) {
            JButton correctButton = new JButton(QuizLogic.playerNames[active] + " σωστός");
            correctButton.setBounds(50, 480, 220, 50);
            correctButton.setFocusable(false);
            correctButton.setFont(f);

            correctButton.addActionListener(e -> {
                if (QuizLogic.scores[active] + activePoints <= 100) {
                    QuizLogic.scores[active] += activePoints;
                }

                QuizLogic.switchPlayer();

                questionAnswerFrame.dispose();
                start();
            });

            JButton wrongButton = new JButton("Λάθος");
            wrongButton.setBounds(330, 480, 220, 50);
            wrongButton.setFocusable(false);
            wrongButton.setFont(f);

            wrongButton.addActionListener(e -> {
                QuizLogic.switchPlayer();

                questionAnswerFrame.dispose();
                start();
            });

            questionAnswerFrame.add(correctButton);
            questionAnswerFrame.add(wrongButton);
            return;
        }

        JButton activeCorrect = new JButton(QuizLogic.playerNames[active] + " σωστός");
        activeCorrect.setBounds(30, 400, 250, 50);
        activeCorrect.setFocusable(false);
        activeCorrect.setFont(f);

        activeCorrect.addActionListener(e -> {
            if (QuizLogic.scores[active] + activePoints <= 100) {
                QuizLogic.scores[active] += activePoints;
            }

            QuizLogic.switchPlayer();

            questionAnswerFrame.dispose();
            start();
        });

        JButton otherCorrect = new JButton(QuizLogic.playerNames[other] + " σωστός");
        otherCorrect.setBounds(320, 400, 250, 50);
        otherCorrect.setFocusable(false);
        otherCorrect.setFont(f);

        otherCorrect.addActionListener(e -> {
            if (QuizLogic.scores[other] + otherPoints <= 100) {
                QuizLogic.scores[other] += otherPoints;
            }

            QuizLogic.switchPlayer();

            questionAnswerFrame.dispose();
            start();
        });

        JButton bothCorrect = new JButton("Και οι δύο σωστοί");
        bothCorrect.setBounds(30, 480, 250, 50);
        bothCorrect.setFocusable(false);
        bothCorrect.setFont(f);

        bothCorrect.addActionListener(e -> {
            if (QuizLogic.scores[active] + activePoints <= 100) {
                QuizLogic.scores[active] += activePoints;
            }

            if (QuizLogic.scores[other] + otherPoints <= 100) {
                QuizLogic.scores[other] += otherPoints;
            }

            QuizLogic.switchPlayer();

            questionAnswerFrame.dispose();
            start();
        });

        JButton noneCorrect = new JButton("Κανείς σωστός");
        noneCorrect.setBounds(320, 480, 250, 50);
        noneCorrect.setFocusable(false);
        noneCorrect.setFont(f);

        noneCorrect.addActionListener(e -> {
            QuizLogic.switchPlayer();

            questionAnswerFrame.dispose();
            start();
        });

        questionAnswerFrame.add(activeCorrect);
        questionAnswerFrame.add(otherCorrect);
        questionAnswerFrame.add(bothCorrect);
        questionAnswerFrame.add(noneCorrect);
    }
}