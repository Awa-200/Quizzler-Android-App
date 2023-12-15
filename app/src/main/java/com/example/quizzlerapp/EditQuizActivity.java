package com.example.quizzlerapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditQuizActivity extends AppCompatActivity {

    private String quizName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quiz);

        // Get the quiz name from the intent
        quizName = getIntent().getStringExtra("quizName");

        // Initialize the form
        initializeForm();

        Button editAndSaveButton = findViewById(R.id.btnEditAndSave);
        editAndSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                EditText quizNameEditText = findViewById(R.id.quizName);
                String quizName = quizNameEditText.getText().toString();

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
                                Toast.makeText(EditQuizActivity.this, "Quiz Edited Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditQuizActivity.this, WelcomeActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditQuizActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        Button deleteQuizButton = findViewById(R.id.btnDeleteQuiz);
        deleteQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display an alert dialog for confirmation
                showDeleteConfirmationDialog();
            }
        });
    }

    private void initializeForm() {
        // Load the question and options from Firestore
        FirebaseFirestore.getInstance()
                .collection("quizzes")
                .document(quizName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            EditText quizNameEditText = findViewById(R.id.quizName);
                            quizNameEditText.setText(quizName);
                            for (int i = 1; i <= 10; i++) {
                                String questionKey = "question" + i;

                                Map<String, Object> questionData = (Map<String, Object>) document.get(questionKey);
                                if (questionData != null) {
                                    // Set question text
                                    EditText questionEditText = findViewById(getResources().getIdentifier(questionKey, "id", getPackageName()));
                                    questionEditText.setText((String) questionData.get("question"));

                                    // Set options
                                    RadioGroup optionsRadioGroup = findViewById(getResources().getIdentifier(questionKey + "_options", "id", getPackageName()));
                                    Map<String, Object> optionsData = (Map<String, Object>) questionData.get("options");
                                    if (optionsData != null) {
                                        for (char option = 'A'; option <= 'D'; option++) {
                                            String optionKey = "option" + option;
                                            EditText optionEditText = findViewById(getResources().getIdentifier(questionKey + "_" + optionKey + "_edittext", "id", getPackageName()));
                                            optionEditText.setText((String) optionsData.get(optionKey));

                                            RadioButton optionRadioButton = findViewById(getResources().getIdentifier(questionKey + "_" + optionKey + "_radio", "id", getPackageName()));
                                            optionRadioButton.setChecked(optionKey.equals(questionData.get("correctOption")));
                                        }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(EditQuizActivity.this, "Quiz not found", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity if quiz not found
                        }
                    } else {
                        Toast.makeText(EditQuizActivity.this, "Error loading quiz data", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity if there is an error
                    }
                });
    }




    public void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Quiz");
        builder.setMessage("Are you sure you want to delete this quiz?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleDeleteQuiz();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void handleDeleteQuiz() {
        // Delete the quiz from Firestore
        db.collection("quizzes").document(quizName).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditQuizActivity.this, "Quiz Deleted Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditQuizActivity.this, "Error deleting quiz", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
