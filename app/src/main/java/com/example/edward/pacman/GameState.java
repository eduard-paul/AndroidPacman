package com.example.edward.pacman;

import java.util.List;

/**
 * Created by edward on 16.05.15.
 */
public class GameState {
    private static final long serialVersionUID = -1056933580285366915L;
    public List<CharacterState> cs;
    public int[][] board;

    public GameState(List<CharacterState> cs, int[][] board) {
        this.cs = cs;
        this.board = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                this.board[i][j] = board[i][j];
            }
        }
    }
}
