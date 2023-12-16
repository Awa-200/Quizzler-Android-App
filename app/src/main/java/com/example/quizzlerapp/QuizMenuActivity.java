package com.example.quizzlerapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
public class QuizMenuActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_menu);

        LinearLayout layout = findViewById(R.id.layout);

        db.collection("quizzes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Button button = new Button(QuizMenuActivity.this);
                        button.setText(document.getId());

                        button.setBackgroundResource(R.color.Pallete2);
                        button.setTextColor(getResources().getColor(R.color.Pallete4));
                        button.setTypeface(getResources().getFont(R.font.chewy));

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(QuizMenuActivity.this, QuizGameActivity.class);
                                intent.putExtra("quizName", document.getId());
                                startActivity(intent);
                            }
                        });
                        layout.addView(button);
                    }
                } else {
                    Log.w("QuizMenuActivity", "Error getting documents.", task.getException());
                }
            }
        });
    }
}
