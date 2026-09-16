package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ResultPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        boolean win = intent.getBooleanExtra("WIN", false);
        int time = intent.getIntExtra("TIME", 0);

        TextView statusTV = findViewById(R.id.resultStatus);
        TextView msgTV = findViewById(R.id.resultMsg);
        TextView timeTV = findViewById(R.id.resultTime);

        if (win) {
            statusTV.setText("You Won.");
            msgTV.setText("Good Job!");
        } else {
            statusTV.setText("Game Over.");
            msgTV.setText("Better luck next time.");
        }

        timeTV.setText("Used " + time + " seconds.");
    }

    public void backToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}