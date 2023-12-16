package com.example.quizzlerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class QuizMenu2Activity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_menu2);

        LinearLayout layout = findViewById(R.id.layout);

        db.collection("quizzes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Button button = new Button(QuizMenu2Activity.this);
                        button.setText(document.getId());
                        button.setBackgroundResource(R.color.Pallete2);
                        button.setTextColor(getResources().getColor(R.color.Pallete4));
                        button.setTypeface(getResources().getFont(R.font.chewy));
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(QuizMenu2Activity.this, EditQuizActivity.class);
                                intent.putExtra("quizName", document.getId());
                                startActivity(intent);
                            }
                        });
                        layout.addView(button);
                    }
                } else {
                    Log.w("QuizMenu2Activity", "Error getting documents.", task.getException());
                }
            }
        });
    }
}
