package com.example.edward.pacman;

import android.graphics.Point;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by edward on 16.05.15.
 */
public class Game extends  Thread{
    private final int[][] defaultBoard = {
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, -1 },
            { -1, 0, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, 0, -1, -1, 0,
                    -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, 0, -1 },
            { -1, 0, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, 0, -1, -1, 0,
                    -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, 0, -1 },
            { -1, 0, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, 0, -1, -1, 0,
                    -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, 0, -1 },
            { -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, -1 },
            { -1, 0, -1, -1, -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1,
                    -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, 0, -1 },
            { -1, 0, -1, -1, -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1,
                    -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, 0, -1 },
            { -1, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0,
                    -1, -1, 0, 0, 0, 0, 0, 0, -1 },
            { -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, 0, -1, -1, 0,
                    -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, 0, -1, -1, 0,
                    -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, -1, -1, 0, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, 0, -1, -1, 0, -1, -1, -1, 0, 0, -1,
                    -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, 0, -1, -1, 0, -1, 0, 0, 0, 0, 0, 0,
                    -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, -1, -1, -1, -1, 0, -1,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { -1, -1, -1, -1, -1, -1, 0, -1, -1, 0, -1, 0, 0, 0, 0, 0, 0,
                    -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, 0, -1, -1, 0, -1, -1, -1, 0, 0, -1,
                    -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, -1, -1, 0, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1,
                    -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1,
                    -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1 },
            { -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, -1 },
            { -1, 0, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, 0, -1, -1, 0,
                    -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, 0, -1 },
            { -1, 0, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, 0, -1, -1, 0,
                    -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, 0, -1 },
            { -1, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, -1, -1, 0, 0, 0, -1 },
            { -1, -1, -1, 0, -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1,
                    -1, -1, 0, -1, -1, 0, -1, -1, 0, -1, -1, -1 },
            { -1, -1, -1, 0, -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1,
                    -1, -1, 0, -1, -1, 0, -1, -1, 0, -1, -1, -1 },
            { -1, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0,
                    -1, -1, 0, 0, 0, 0, 0, 0, -1 },
            { -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, 0,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1 },
            { -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, 0,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1 },
            { -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 } };
    private final Point[] defaultPlayersStartPoints = { new Point(1, 1),
            new Point(1, 26), new Point(29, 26), new Point(29, 1) };
    private final Point[] defaultGhostsStartPoints = { new Point(13, 11),
            new Point(15, 11), new Point(13, 16), new Point(15, 16) };
    private final Point[] playersStartPoints;
    private final Point[] ghostsStartPoints;
    private final int DefaultSpeed = 300;
    private final int TimerPeriod = 30;

    int playersNum, ghostsNum = 4;
    int catchedPlayers = 0;
    int totalFood = 0, catchedFood = 0;
    public Board board;
    List<Character> characters = new LinkedList<Character>();
    Timer timer = new java.util.Timer();
    protected boolean restarting = false;

    TimerTask task = new TimerTask() {
        public void run() {
            for (Character character : characters) {
                if (character.speed != 0)
                    character.move();
            }
            if (restarting) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                Restart();
            }
        }
    };

    public void GoUp(int id) {
        for (Character ch : characters) {
            if (ch.id == id) {
                ch.setDesiredDirection(-2);
            }
        }
    }

    public void GoDown(int id) {
        for (Character ch : characters) {
            if (ch.id == id) {
                ch.setDesiredDirection(2);
            }
        }
    }

    public void GoLeft(int id) {
        for (Character ch : characters) {
            if (ch.id == id) {
                ch.setDesiredDirection(-1);
            }
        }
    }

    public void GoRight(int id) {
        for (Character ch : characters) {
            if (ch.id == id) {
                ch.setDesiredDirection(1);
            }
        }
    }

    public Game(int playersNum) {

        this.playersStartPoints = defaultPlayersStartPoints;
        this.ghostsStartPoints = defaultGhostsStartPoints;
        this.playersNum = playersNum;
        board = new Board();
        for (int i = 0; i < board.board.length; i++) {
            for (int j = 0; j < board.board[0].length; j++) {
                if (board.board[i][j] == 0) {
                    totalFood++;
                    board.board[i][j] = 5;
                }
            }
        }
        totalFood *= 0.1;
    }

    public Game(CustomBoard cb) {
        this.playersNum = cb.playersStartPoints.size();
        this.playersStartPoints = cb.playersStartPoints.toArray(new Point[0]);
        this.ghostsStartPoints = cb.ghostsStartPoints.toArray(new Point[0]);
        board = new Board(cb);
        for (int i = 0; i < board.board.length; i++) {
            for (int j = 0; j < board.board[0].length; j++) {
                if (board.board[i][j] != -1) {
                    totalFood++;
                    board.board[i][j] = 5;
                }
            }
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < playersNum; i++) {
            int direction = 1;
            if (i % 2 == 1)
                direction = -1;
            characters.add(new Player(playersStartPoints[i],
                    direction, DefaultSpeed, i + 1));
            board.setCellState(playersStartPoints[i], 1);
        }

        for (int i = 0; i < ghostsNum; i++) {
            int direction = 1;
            if (i % 2 == 1)
                direction = -1;
            characters.add(new Ghost(ghostsStartPoints[i],
                    direction, DefaultSpeed, -1 - i, characters.get(i
                    % playersNum)));
            board.setCellState(ghostsStartPoints[i], -2);
        }
        timer.schedule(task, 0, TimerPeriod);
    }

    private void Restart() {
        board.Clean();
        for (int i = 0; i < playersNum; i++) {
            int direction = 1;
            if (i % 2 == 1)
                direction = -1;
            Character player = characters.get(i);
            player.Setter(playersStartPoints[i], direction,
                    DefaultSpeed);
            player.winnerId = 0;
        }
        for (int i = playersNum; i < playersNum + ghostsNum; i++) {
            int direction = 1;
            if (i % 2 == 1)
                direction = -1;
            Character ghost = characters.get(i);
            ghost.Setter(ghostsStartPoints[i - playersNum],
                    direction, DefaultSpeed);
        }
        catchedPlayers = 0;
        catchedFood = 0;
        restarting = false;
    }

    public class Board {
        public int board[][];
        protected int cleanBoard[][];

        public Board(CustomBoard cb) {
            board = new int[cb.board.length][cb.board[0].length];
            cleanBoard = new int[cb.board.length][cb.board[0].length];
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    board[i][j] = cb.board[i][j];
                    cleanBoard[i][j] = cb.board[i][j];
                }
            }
        }
        /**
         * Creates the default board
         */
        public Board() {
            board = new int[defaultBoard.length][defaultBoard[0].length];
            cleanBoard = new int[defaultBoard.length][defaultBoard[0].length];
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    board[i][j] = defaultBoard[i][j];
                    cleanBoard[i][j] = defaultBoard[i][j];
                }
            }
        }

        public void Clean() {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    board[i][j] = cleanBoard[i][j];
                    if (board[i][j] == 0)
                        board[i][j] = 5;
                }
            }
        }

        /**
         * @param state
         *            '0' - empty, '1' - hero, '-1' - wall, '-2' - ghost
         */
        public void setCellState(Point cell, int state) {
            board[cell.x][cell.y] = state;
        }

        /**
         * @return '0' - empty, '1' - hero, '-1' - wall, '-2' - ghost
         */
        public int getCellState(Point cell) {
            return board[cell.x][cell.y];
        }
    }

    protected GameState getGameState() {
        List<CharacterState> cs = new LinkedList<CharacterState>();
        for (Character character : characters) {
            cs.add(character.getCharState());
        }
        GameState gs = new GameState(cs, board.board);
        return gs;
    }

    abstract class Character {
        protected int id;
        protected Point cell;
        protected double dist = 0;
        protected int direction;
        protected int speed;
        protected int desiredDirection;
        protected int myFood = -1;
        protected int winnerId = 0;

        public CharacterState getCharState() {
            CharacterState s = new CharacterState();
            s.id = id;
            s.x = cell.x;
            s.y = cell.y;
            s.dist = dist;
            s.direction = direction;
            s.speed = speed;
            s.winnerId = winnerId;
            return s;
        }

        /**
         * @param direction
         *            "1" - right, "-1" - left, "2" - down, "-2" - up
         * @param speed
         *            Time in Ms needed to reach next cell
         */
        public Character(Point cell, int direction, int speed, int id) {
            this.cell = new Point(cell);
            this.direction = direction;
            this.desiredDirection = direction;
            this.speed = speed;
            this.id = id;
        }

        public void Setter(Point cell, int direction, int speed) {
            this.cell = new Point(cell);
            this.direction = direction;
            this.desiredDirection = direction;
            this.speed = speed;
            dist = 0;
        }

        public void setDesiredDirection(int dd) {
            desiredDirection = dd;
        }

        public abstract void move();

        protected Point DesiredCell() {
            Point result = new Point(cell);

            switch (desiredDirection) {
                case 1:
                    result.y++;
                    if (result.y == 28)
                        result.y = 0;
                    break;
                case -1:
                    result.y--;
                    if (result.y == -1)
                        result.y = 27;
                    break;
                case 2:
                    result.x++;
                    break;
                case -2:
                    result.x--;
                    break;

                default:
                    break;
            }

            return result;
        }

        protected Point NextCell() {
            Point result = new Point(cell);

            switch (direction) {
                case 1:
                    result.y++;
                    if (result.y == 28)
                        result.y = 0;
                    break;
                case -1:
                    result.y--;
                    if (result.y == -1)
                        result.y = 27;
                    break;
                case 2:
                    result.x++;
                    break;
                case -2:
                    result.x--;
                    break;

                default:
                    break;
            }

            return result;
        }
    }

    private class Player extends Character {
        /**
         * @param direction
         *            "1" - right, "-1" - left, "2" - down, "-2" - up
         * @param speed
         *            Time in ms needed to reach next cell
         */
        public Player(Point cell, int direction, int speed, int id) {
            super(cell, direction, speed, id);
            myFood = 0;
        }

        @Override
        public void Setter(Point cell, int direction, int speed) {
            super.Setter(cell, direction, speed);
            this.myFood = 0;
        }

        @Override
        public void move() {
            if (Math.abs(desiredDirection) == Math.abs(direction))
                direction = desiredDirection;

            if (board.getCellState(NextCell()) != -1
                    || (Math.abs(dist) > 1.1 * TimerPeriod / speed)) {
                dist += 2 * Math.signum(direction) * TimerPeriod / speed;
                if (Math.abs(dist) >= 1) {
                    if (board.getCellState(NextCell()) < -1) {
                        this.speed = 0;
                        this.dist = 0;
                        this.cell = new Point(0, playersNum
                                - catchedPlayers);
                        catchedPlayers++;
                        if (catchedPlayers == playersNum) {
                            restarting = true;
                            characters.get(0).winnerId = -1;
                        }
                    } else {
                        board.setCellState(cell, 0);
                        cell = NextCell();
                        dist -= 2 * Math.signum(dist);
                        if (board.getCellState(cell) == 5) {
                            myFood++;
                            catchedFood++;
                            if (catchedFood == totalFood) {
                                ShowResults();
                            }
                        }
                    }
                }
            }

            if (this.speed != 0) {
                board.setCellState(cell, this.id);

                if (board.getCellState(DesiredCell()) != -1
                        && (Math.abs(dist) < 1.1 * TimerPeriod / speed)) {
                    direction = desiredDirection;
                }
            }
        }

    }

    private class Ghost extends Character {
        private Character aim;
        private boolean foodFlag = true; // If the ghost picked up food

        /**
         * @param direction
         *            "1" - right, "-1" - left, "2" - down, "-2" - up
         * @param speed
         *            Time in ms needed to reach next cell
         */
        public Ghost(Point cell, int direction, int speed, int id,
                     Character character) {
            super(cell, direction, speed, id);
            this.aim = character;
        }

        @Override
        public void Setter(Point cell, int direction, int speed) {
            super.Setter(cell, direction, speed);
            this.foodFlag = true;
        }

        double distance(Point a, Point b) {
            return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
        }

        @Override
        public void move() {

            Point aimCell = null;

            Point right = RightCell();
            Point left = LeftCell();
            Point next = NextCell();

            double minDistToAim = Double.MAX_VALUE;

            Random rand = new Random();

            int offsetX = (rand.nextInt(3) - 1) * 4;
            int offsetY = (rand.nextInt(3) - 1) * 4;

            aimCell = new Point(aim.cell.x + offsetX, aim.cell.y + offsetY);

            if (board.getCellState(next) != -1
                    && distance(next,aimCell) < minDistToAim) {
                desiredDirection = direction;
                minDistToAim = distance(next,aimCell);
            }
            if (board.getCellState(right) != -1
                    && distance(right,aimCell) < minDistToAim) {
                TurnRight();
                minDistToAim = distance(right,aimCell);
            }
            if (board.getCellState(left) != -1
                    && distance(left,aimCell) < minDistToAim) {
                TurnLeft();
                minDistToAim = distance(left,aimCell);
            }

            if (board.getCellState(NextCell()) != -1
                    || (Math.abs(dist) > 1.1 * TimerPeriod / speed)) {
                dist += 2 * Math.signum(direction) * TimerPeriod / speed;
                if (Math.abs(dist) >= 1) {
                    if (board.getCellState(NextCell()) > 0
                            && board.getCellState(NextCell()) != 5) {
                        Character player = null;
                        for (Character character : characters) {
                            if (character.id == board
                                    .getCellState(NextCell())) {
                                player = character;
                            }
                        }
                        player.speed = 0;
                        player.dist = 0;
                        player.cell = new Point(0, playersNum
                                - catchedPlayers);
                        catchedPlayers++;
                        if (catchedPlayers == playersNum) {
                            restarting = true;
                            characters.get(0).winnerId = -1;
                        }
                    }
                    if (foodFlag)
                        board.setCellState(cell, 5);
                    else
                        board.setCellState(cell, 0);

                    cell = NextCell();

                    if (board.getCellState(cell) == 5)
                        foodFlag = true;
                    else
                        foodFlag = false;

                    dist -= 2 * Math.signum(dist);
                }
            }

            board.setCellState(cell, this.id - 1);

            if (board.getCellState(DesiredCell()) != -1
                    && (Math.abs(dist) < 1.1 * TimerPeriod / speed)) {
                direction = desiredDirection;
            }
        }

        private void TurnRight() {
            switch (direction) {
                case 1:
                    desiredDirection = 2;
                    break;
                case -1:
                    desiredDirection = -2;
                    break;
                case 2:
                    desiredDirection = -1;
                    break;
                case -2:
                    desiredDirection = 1;
                    break;

                default:
                    break;
            }
        }

        private void TurnLeft() {
            switch (direction) {
                case 1:
                    desiredDirection = -2;
                    break;
                case -1:
                    desiredDirection = 2;
                    break;
                case 2:
                    desiredDirection = 1;
                    break;
                case -2:
                    desiredDirection = -1;
                    break;

                default:
                    break;
            }
        }

        private Point RightCell() {
            Point result = new Point(cell);

            switch (direction) {
                case 1:
                    result.x++;
                    break;
                case -1:
                    result.x--;
                    break;
                case 2:
                    result.y--;
                    break;
                case -2:
                    result.y++;
                    break;

                default:
                    break;
            }
            return result;
        }

        private Point LeftCell() {
            Point result = new Point(cell);

            switch (direction) {
                case 1:
                    result.x--;
                    break;
                case -1:
                    result.x++;
                    break;
                case 2:
                    result.y++;
                    break;
                case -2:
                    result.y--;
                    break;

                default:
                    break;
            }

            return result;
        }

    }

    public void ShowResults() {
        int maxId = -1;
        for (int i = 0; i < playersNum - catchedPlayers; i++) {
            Character max = characters.get(0);
            for (Character character : characters) {
                if (max.myFood <= character.myFood && character.speed != 0)
                    max = character;
            }
            if (i==0) maxId = max.id;
            max.myFood = 0;
            max.speed = 0;
            max.dist = 0;
            max.cell = new Point(0, i);
            max.winnerId = maxId;
        }
        restarting = true;
    }
}
