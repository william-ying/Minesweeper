package com.example.minesweeper;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private GridLayout gameGrid;
    private TextView flagCountTV;
    private TextView timerTV;
    private boolean isFlagMode = false;
    private boolean gameOver = false;
    private boolean win = false;
    private boolean timerRunning = false;
    private int seconds = 0;
    private Cell[][] board;
    private int revealed = 0;
    private int flagsPlaced = 0;
    private final int NUM_MINES = 6;

    private static class Cell {

        boolean isMine = false;
        boolean isRevealed = false;
        boolean isFlagged = false;
        int adjacentMines = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        gameGrid = findViewById(R.id.gameGrid);
        flagCountTV = findViewById(R.id.flagcount);
        timerTV = findViewById(R.id.timer);
        Button toggleButton = findViewById(R.id.toggleButton);

        updateFlagCount();
        runTimer();

        toggleButton.setOnClickListener(v -> {
            isFlagMode = !isFlagMode;
            toggleButton.setText(isFlagMode ? R.string.flag : R.string.pick);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        board = new Cell[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = new Cell();
            }
        }
        Random random = new Random();
        int mines = 0;
        while (mines < NUM_MINES) {
            int r = random.nextInt(10);
            int c = random.nextInt(10);
            if (!board[r][c].isMine) {
                board[r][c].isMine = true;
                mines++;
            }
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (!board[i][j].isMine) {
                    int count = 0;
                    for (int r = max(0, i - 1); r <= min(9, i + 1); r++) {
                        for (int c = max(0, j - 1); c <= min(9, j + 1); c++) {
                            if (board[r][c].isMine) count++;
                        }
                    }
                    board[i][j].adjacentMines = count;
                }
            }
        }

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
                final int row = i / 10;
                final int col = i % 10;
                cell.setOnClickListener(view -> {
                    if (!timerRunning && !gameOver) {
                        timerRunning = true;
                    }
                    if (!isFlagMode) {
                        revealCell(row, col, cell);
                    } else {
                        flagCell(row, col, cell);
                    }
                });
            }
        });
    }

    private void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (timerRunning && !gameOver) {
                    seconds++;
                    if (timerTV != null) {
                        timerTV.setText(String.valueOf(seconds));
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void flagCell(int r, int c, Button b) {
        if (gameOver) return;
        Cell cell = board[r][c];
        if (cell.isRevealed) return;

        cell.isFlagged = !cell.isFlagged;
        if (cell.isFlagged) {
            b.setText(R.string.flag);
            flagsPlaced++;
        } else {
            b.setText("");
            flagsPlaced--;
        }
        updateFlagCount();
    }

    private void updateFlagCount() {
        if (flagCountTV != null) {
            flagCountTV.setText(String.valueOf(NUM_MINES - flagsPlaced));
        }
    }

    private void revealCell(int r, int c, Button b) {
        if (gameOver) return;

        Cell cell = board[r][c];
        if (cell.isRevealed || cell.isFlagged) return;

        cell.isRevealed = true;
        revealed++;
        b.setEnabled(false);
        b.setBackgroundColor(Color.LTGRAY); // Changed to LTGRAY for better contrast

        if (cell.isMine) {
            gameOver = true;
            b.setText(R.string.mine);
            b.setBackgroundColor(Color.RED);
            // Reveal all mines
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (board[i][j].isMine) {
                        Button mineButton = (Button) gameGrid.getChildAt(i * 10 + j);
                        mineButton.setText(R.string.mine);
                    }
                }
            }
            goToResultPage();
            return;
        }

        if (cell.adjacentMines > 0) {
            b.setText(String.valueOf(cell.adjacentMines));
        } else {
            // Recursive reveal for 8 neighbors
            for (int i = max(0, r - 1); i <= min(9, r + 1); i++) {
                for (int j = max(0, c - 1); j <= min(9, c + 1); j++) {
                    if (i == r && j == c) continue;
                    revealCell(i, j, (Button) gameGrid.getChildAt(i * 10 + j));
                }
            }
        }

        if (revealed == (100 - NUM_MINES)) {
            gameOver = true;
            win = true;
            goToResultPage();
        }
    }

    private void goToResultPage() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, ResultPage.class);
            intent.putExtra("WIN", win);
            intent.putExtra("TIME", seconds);
            startActivity(intent);
            finish();
        }, 2000); // 2 second delay
    }
}