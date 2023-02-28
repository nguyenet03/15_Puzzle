package com.example.a15squares;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameActivity implements View.OnClickListener {

    private ArrayList<Button> buttons;
    private ArrayList<Integer> squares;
    private Button emptyButton;
    private Button clickedButton;
    private TextView displayWin;
    private Button restartButton;

    // constructor
    public GameActivity(ArrayList<Button> button, Button restart) {
        buttons = button;
        squares = new ArrayList<>(16);
        emptyButton = null;
        clickedButton = null;
        restartButton = restart;
        
        // add numbers to squares ArrayList
        for (int i = 0; i < 15; i++) {
            squares.add(i + 1);
        }
        
        //randomly shuffle board
        Collections.shuffle(squares);
        
        //only have solvable boards
        while (!solvable()) {
            Collections.shuffle(squares);
        }
    }

    public void setButtons() {
        
        //randomize buttons
        Random rand = new Random();
        squares.trimToSize();
        buttons.trimToSize();
        int random = rand.nextInt(buttons.size());
        int squareIndex = 0;
        
        //instantiate 15 numbered buttons with a blank button
        for (int i = 0; i < buttons.size(); i++) {
            if (i == random) {
                buttons.get(i).setText(" ");
            }
            else {
                buttons.get(i).setText("" + squares.get(squareIndex));
                squareIndex++;
            }
        }
        //check button positioning upon first initialization
        for (int i = 0; i < buttons.size(); i++) {
            
            //if not in correct spot, remain black
            if (!correctPosition(i)) {
                buttons.get(i).setBackgroundColor(Color.BLACK);
            } 
            
            //if in correct spot, set color to red to indicate correct spot
            else {
                buttons.get(i).setBackgroundColor(Color.RED);
            }
        }
    }

    @Override
    public void onClick(View v) {
        
        //restart game
        if (v.getId() == restartButton.getId()) {
            
            //hide win message if restarting from solved
            displayWin.setVisibility(View.INVISIBLE);
            
            //scramble the board
            Collections.shuffle(squares);
            
            //scramble until board is solvable
            while (!solvable()) {
                Collections.shuffle(squares);
            }
        }

        if (!isEmpty(v)) {
            return;
        } 
        else {
            //Swap the buttons
            String temp = String.valueOf(clickedButton.getText());
            clickedButton.setText(" ");
            emptyButton.setText(temp);
        }

        //check correct positions after swap
        for (int i = 0; i < buttons.size(); i++) {

            //if incorrect, square remains black
            if (!correctPosition(i)) {
                buttons.get(i).setBackgroundColor(Color.BLACK);
            }

            //if correct position, set to RED
            else {
                buttons.get(i).setBackgroundColor(Color.RED);
            }
        }

        //check for win
        if (hasWinner()) {
            displayWin.setVisibility(View.VISIBLE);
        }
        else {
            displayWin.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @return return true if board is solved
     */
    public boolean hasWinner() {
        for (int i = 0; i < buttons.size() - 1; i++) {
            if (!correctPosition(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param index the index of the button being looked at
     * @return true if the button is in the correct position
     */
    public boolean correctPosition(int index) {
        if (String.valueOf(buttons.get(index).getText()).equals(String.valueOf(index + 1))) {
            return true;
        }
        return false;
    }

    /**
     * @param view
     * @return returns true if any neighboring buttons
     * are empty
     */
    public boolean isEmpty(View view) {
        
        // create 4x4 array for ease of navigation
        Button[][] buttonsArray = new Button[4][4];
        int index = 0;
        for (int i = 0; i < buttonsArray.length; i++) {
            for (int j = 0; j < buttonsArray[i].length; j++) {
                buttonsArray[i][j] = buttons.get(index);
                index++;
            }
        }

        // find button that was clicked
        int clickedButtonId = view.getId();
        clickedButton = null;
        for (int clicked = 0; clicked < buttons.size(); clicked++) {
            if (buttons.get(clicked).getId() == clickedButtonId) {
                clickedButton = buttons.get(clicked);
                break;
            }
        }

        //check if the clicked button has an empty neighbors
        for (int i = 0; i < buttonsArray.length; i++) {
            for (int j = 0; j < buttonsArray[i].length; j++) {
                if (buttonsArray[i][j].equals(clickedButton)) {
                    
                    //check to the right
                    if (isValid(i + 1, j, buttonsArray.length, buttonsArray[i].length)) {
                        if (String.valueOf(buttonsArray[i + 1][j].getText()).equals(" ")) {
                            emptyButton = buttonsArray[i + 1][j];
                            return true;
                        }
                    }
                    
                    //check to the left
                    if (isValid(i - 1, j, buttonsArray.length, buttonsArray[i].length)) {
                        if (String.valueOf(buttonsArray[i - 1][j].getText()).equals(" ")) {
                            emptyButton = buttonsArray[i - 1][j];
                            return true;
                        }
                    }
                    
                    //check above
                    if (isValid(i, j + 1, buttonsArray.length, buttonsArray[i].length)) {
                        if (String.valueOf(buttonsArray[i][j + 1].getText()).equals(" ")) {
                            emptyButton = buttonsArray[i][j + 1];
                            return true;
                        }
                    }
                    
                    //check below
                    if (isValid(i, j - 1, buttonsArray.length, buttonsArray[i].length)) {
                        if (String.valueOf(buttonsArray[i][j - 1].getText()).equals(" ")) {
                            emptyButton = buttonsArray[i][j - 1];
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return return true if board is solvable
     * 
     * The link below was used to reference how to check if a board is solvable
     * https://www.geeksforgeeks.org/check-instance-15-puzzle-solvable/
     */
    public boolean solvable() {
        setButtons();
        int[] values = new int[buttons.size()];
        for (int i = 0; i < values.length; i++) {
            if (String.valueOf(buttons.get(i).getText()).equals(" ")) {
                continue;
            }
            values[i] = Integer.parseInt(String.valueOf(buttons.get(i).getText()));
        }

        //calculate sum of number of inversions
        int inversions = 0;
        for (int i = 0; i < values.length; i++) {
            int currNum = values[i];
            for (int j = i; j < values.length; j++) {
                if (currNum > values[j] && values[j] != 0) {
                    inversions++;
                }
            }
        }
        Log.d("inversions", "" + inversions);

        //return true if inversions is even, else return false
        return inversions % 2 == 0;
    }

    /**
     * @param row, row index
     * @param col, column index
     * @param rowLength, max rows
     * @param colLength, max columns
     * @return return true if the input parameters are within the bounds of the board
     */
    public boolean isValid(int row, int col, int rowLength, int colLength) {
        if (row < 0 || row >= rowLength || col < 0 || col >= colLength) {
            return false;
        }
        return true;
    }

    /**
     * @param view the text to be displayed
     *
     *             Displays win message on solve
     */
    public void setDisplayWin(TextView view) {
        displayWin = view;
    }
}
