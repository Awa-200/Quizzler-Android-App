package com.example.quizzlerapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateQuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        Button createAndSaveButton = findViewById(R.id.btnCreateAndSave);
        createAndSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                EditText quizNameEditText = findViewById(R.id.quizName);
                String quizName = quizNameEditText.getText().toString();

                // Validate the quiz name
                if (TextUtils.isEmpty(quizName)) {
                    quizNameEditText.setError("Please enter Quiz Name");
                    return;
                }

                Map<String, Object> quiz = new HashMap<>();
                for (int i = 1; i <= 10; i++) {
                    EditText questionEditText = findViewById(getResources().getIdentifier("question" + i, "id", getPackageName()));
                    String question = questionEditText.getText().toString();

                    Map<String, Object> options = new HashMap<>();
                    RadioGroup radioGroup = findViewById(getResources().getIdentifier("question" + i + "_options", "id", getPackageName()));
                    int selectedOptionId = radioGroup.getCheckedRadioButtonId();
                    String correctOption = "";
                    for (char option = 'A'; option <= 'D'; option++) {
                        EditText optionEditText = findViewById(getResources().getIdentifier("question" + i + "_option" + option + "_edittext", "id", getPackageName()));
                        String optionText = optionEditText.getText().toString();
                        options.put("option" + option, optionText);

                        RadioButton optionRadioButton = findViewById(getResources().getIdentifier("question" + i + "_option" + option + "_radio", "id", getPackageName()));
                        if (optionRadioButton.isChecked()) {
                            correctOption = "option" + option;
                        }
                    }

                    Map<String, Object> questionRecord = new HashMap<>();
                    questionRecord.put("question", question);
                    questionRecord.put("options", options);
                    questionRecord.put("correctOption", correctOption);

                    quiz.put("question" + i, questionRecord);
                }

                db.collection("quizzes").document(quizName).set(quiz)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CreateQuizActivity.this, "Quiz Created Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateQuizActivity.this, WelcomeActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateQuizActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
