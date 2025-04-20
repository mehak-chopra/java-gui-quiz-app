import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class QuizGUI extends JFrame {
    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup optionGroup;
    private JButton submitButton;
    private JLabel statusLabel;

    private java.util.List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    public QuizGUI() {
        setTitle("✨ Java Quiz App");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 250));

        // Load questions from CSV
        try (BufferedReader br = new BufferedReader(new FileReader("questions.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length == 6) {
                    String questionText = parts[0];
                    String[] options = {parts[1], parts[2], parts[3], parts[4]};
                    char correctOption = parts[5].toUpperCase().charAt(0);
                    questions.add(new Question(questionText, options, correctOption));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error loading questions: " + e.getMessage());
        }

        // Fonts
        Font questionFont = new Font("SansSerif", Font.BOLD, 18);
        Font optionFont = new Font("SansSerif", Font.PLAIN, 16);

        questionLabel = new JLabel();
        questionLabel.setFont(questionFont);
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        optionButtons = new JRadioButton[4];
        optionGroup = new ButtonGroup();
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        optionsPanel.setBackground(new Color(245, 245, 250));

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(optionFont);
            optionButtons[i].setBackground(new Color(245, 245, 250));
            optionGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }

        submitButton = new JButton("Submit ✅");
        submitButton.setFocusPainted(false);
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        submitButton.setBackground(new Color(0, 123, 255));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        submitButton.addActionListener(e -> handleSubmit());

        setLayout(new BorderLayout());
        add(questionLabel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.setBackground(new Color(245, 245, 250));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(submitButton);
        bottomPanel.add(statusLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        if (!questions.isEmpty()) {
            loadQuestion();
        } else {
            questionLabel.setText("⚠️ No questions found.");
            submitButton.setEnabled(false);
        }

        setVisible(true);
    }

    private void loadQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            JOptionPane.showMessageDialog(this, "🎉 Quiz Over!\nYour Score: " + score + "/" + questions.size());
            dispose();
            return;
        }

        Question q = questions.get(currentQuestionIndex);
        questionLabel.setText("Q" + (currentQuestionIndex + 1) + ": " + q.questionText);
        char optionChar = 'A';
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(optionChar + ". " + q.options[i]);
            optionButtons[i].setActionCommand(String.valueOf(optionChar));
            optionButtons[i].setSelected(false);
            optionChar++;
        }
        optionGroup.clearSelection();
        statusLabel.setText("");
    }

    private void handleSubmit() {
        if (optionGroup.getSelection() == null) {
            statusLabel.setText("⚠️ Please select an answer!");
            return;
        }

        String selected = optionGroup.getSelection().getActionCommand();
        Question q = questions.get(currentQuestionIndex);
        if (selected.charAt(0) == q.correctOption) {
            score++;
            statusLabel.setText("✅ Correct!");
        } else {
            statusLabel.setText("❌ Wrong! Correct: " + q.correctOption);
        }

        Timer timer = new Timer(1200, e -> {
            currentQuestionIndex++;
            loadQuestion();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QuizGUI::new);
    }
}

class Question {
    String questionText;
    String[] options;
    char correctOption;

    public Question(String questionText, String[] options, char correctOption) {
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
    }
}
