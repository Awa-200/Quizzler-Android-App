package com.example.quizzlerapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class QuizGameActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView txtQuestion, txtTimer;
    private Button btnOptionA, btnOptionB, btnOptionC, btnOptionD;

    private String quizName;
    private int currentQuestion = 1;
    private int userScore = 0;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_game);

        // Initialize UI components
        txtQuestion = findViewById(R.id.txtQuestion);
        txtTimer = findViewById(R.id.txtTimer);
        btnOptionA = findViewById(R.id.btnOptionA);
        btnOptionB = findViewById(R.id.btnOptionB);
        btnOptionC = findViewById(R.id.btnOptionC);
        btnOptionD = findViewById(R.id.btnOptionD);


        //  quiz name from Intent
        quizName = getIntent().getStringExtra("quizName");

        // Load the first question
        loadQuestion(currentQuestion);

        // Set up click listeners for answer buttons
        btnOptionA.setOnClickListener(view -> checkAnswer("optionA"));
        btnOptionB.setOnClickListener(view -> checkAnswer("optionB"));
        btnOptionC.setOnClickListener(view -> checkAnswer("optionC"));
        btnOptionD.setOnClickListener(view -> checkAnswer("optionD "));

    }

    private void loadQuestion(int questionNumber) {
        // Cancel the previous timer if it exists
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Load the question and options from Firestore
        db.collection("quizzes")
                .document(quizName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> questionData = (Map<String, Object>) document.get("question" + questionNumber);
                            if (questionData != null) {
                                String questionText = (String) questionData.get("question");
                                Map<String, Object> options = (Map<String, Object>) questionData.get("options");

                                // Display question and options
                                txtQuestion.setText(questionText);
                                btnOptionA.setText((String) options.get("optionA"));
                                btnOptionB.setText((String) options.get("optionB"));
                                btnOptionC.setText((String) options.get("optionC"));
                                btnOptionD.setText((String) options.get("optionD"));

                                // Clear the selected answer from the previous question
                                clearSelectedAnswer();

                                // Enable option buttons
                                enableOptionButtons(true);

                                // Start the countdown timer
                                startTimer();

                            } else {
                                // No more questions, navigate to ViewScoreActivity
                                navigateToViewScore();
                            }
                        }
                    } else {
                        Toast.makeText(QuizGameActivity.this, "Error loading question", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearSelectedAnswer() {
        // Reset the selected state of the answer buttons
        btnOptionA.setSelected(false);
        btnOptionB.setSelected(false);
        btnOptionC.setSelected(false);
        btnOptionD.setSelected(false);
    }

    private void enableOptionButtons(boolean enable) {
        // Enable or disable the option buttons
        btnOptionA.setEnabled(enable);
        btnOptionB.setEnabled(enable);
        btnOptionC.setEnabled(enable);
        btnOptionD.setEnabled(enable);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                txtTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                // Timer finished, move to the next question
                currentQuestion++;
                loadQuestion(currentQuestion);
            }
        }.start();
    }

    private void checkAnswer(String selectedOption) {
        // Retrieve the correct option from Firestore
        db.collection("quizzes")
                .document(quizName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> questionData = (Map<String, Object>) document.get("question" + currentQuestion);
                            if (questionData != null) {
                                String correctOption = (String) questionData.get("correctOption");

                                // Check if the selected option is correct
                                boolean isCorrect = selectedOption.equals(correctOption);

                                // Debugging information
                                Log.d("QuizGameActivity", "Selected Option: " + selectedOption);
                                Log.d("QuizGameActivity", "Correct Option: " + correctOption);
                                Log.d("QuizGameActivity", "Is Correct: " + isCorrect);


                                // Update score only if the selected option is correct
                                if (isCorrect) {
                                    userScore++;
                                    Toast.makeText(this, "Your Score: " + userScore, Toast.LENGTH_SHORT).show();
                                }

                                // Move to the next question
                                currentQuestion++;
                                if (currentQuestion <= 10) {
                                    loadQuestion(currentQuestion);
                                } else {
                                    // All questions finished, navigate to ViewScoreActivity
                                    navigateToViewScore();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(QuizGameActivity.this, "Error checking answer", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void navigateToViewScore() {
        // Intent to ViewScoreActivity with the user's total score
        Intent intent = new Intent(QuizGameActivity.this, ViewScoreActivity.class);
        intent.putExtra("userScore", userScore);
        startActivity(intent);
        finish(); // Finish current activity to prevent going back to the quiz
    }

}
