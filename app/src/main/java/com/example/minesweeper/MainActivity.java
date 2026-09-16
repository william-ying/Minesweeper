package com.example.minesweeper;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        GridLayout gameGrid = findViewById(R.id.gameGrid);
        ViewCompat.setOnApplyWindowInsetsListener(gameGrid, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gameGrid.post(() -> {
            int gridWidth = gameGrid.getWidth();
            int padding = gameGrid.getPaddingLeft() + gameGrid.getPaddingRight();
            int availableWidth = gridWidth - padding;

            int gap = 2;
            int cellSize = (availableWidth - (gap * 2 * 10)) / 10;

            for (int i = 0; i < 100; i++) {
                Button cell = (Button) getLayoutInflater()
                        .inflate(R.layout.minesweeper_tile, gameGrid, false);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.setMargins(gap, gap, gap, gap);

                gameGrid.addView(cell, params);
            }
        });
    }
}