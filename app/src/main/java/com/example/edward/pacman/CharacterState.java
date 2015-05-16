package com.example.edward.pacman;

import android.graphics.Point;

/**
 * Created by edward on 16.05.15.
 */
public class CharacterState {
    private static final long serialVersionUID = 7237905012931057864L;
    public int id;
    public Point cell;
    public double dist;
    public int direction, speed;
}
