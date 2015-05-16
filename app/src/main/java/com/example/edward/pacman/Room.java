package com.example.edward.pacman;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by edward on 16.05.15.
 */
public class Room {
    private String name;
    private int maxPlayers;
    private int currPlayers;
    private Game game;
    private boolean isStarted = false;
    private List<User> players = new LinkedList<User>();
    private List<User> spectators = new LinkedList<User>();
    private Timer timer = new java.util.Timer();

    private TimerTask task = new TimerTask() {
        public void run() {
            SendState();

        }
    };

    public boolean IsStarted() {
        return isStarted;
    }

    public void Command(String line, int id) {
        if ("Up".equals(line)) {
            game.GoUp(id);
        } else if ("Down".equals(line)) {
            game.GoDown(id);
        } else if ("Left".equals(line)) {
            game.GoLeft(id);
        } else if ("Right".equals(line)) {
            game.GoRight(id);
        }
    }

    public Room(String name, int maxPlayers, User firstPlayer) {

        this.name = name;
        this.maxPlayers = maxPlayers;
        this.currPlayers = 1;
        players.add(firstPlayer);

        game = new Game(maxPlayers);

        if (currPlayers == maxPlayers)
            StartGame();
    }

    public Room(String name, CustomBoard cb, User firstPlayer) {

        this.name = name;
        this.maxPlayers = cb.playersStartPoints.size();
        this.currPlayers = 1;
        players.add(firstPlayer);

        game = new Game(cb);

        if (currPlayers == maxPlayers)
            StartGame();
    }

    public void AddPlayer(User firstPlayer) {
        if (currPlayers < maxPlayers && !isStarted) {
            players.add(firstPlayer);
            currPlayers = players.size();
            if (currPlayers == maxPlayers) {
                StartGame();
            }
        }
    }

    public void AddSpectator(User spectator) {
        if (IsStarted()) {
            spectator.SendStart();
            spectator.SendBoard(game.board.board);
        }
        spectators.add(spectator);
    }

    private void StartGame() {
        int i = 1;
        isStarted = true;
        for (User player : players) {
            player.playerId = i++;
            player.SendStart();
        }
        for (User spectator : spectators) {
            spectator.SendStart();
        }
        SendBoard();
        timer.schedule(task, 0, 50);

        game.start();
        try {
            game.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void SendState() {
        GameState gs = game.getGameState();
        for (User player : players) {
            player.SendState(gs);
        }
        for (User spectator : spectators) {
            spectator.SendState(gs);
        }
    }

    private void SendBoard() {
        for (User player : players) {
            player.SendBoard(game.board.board);
        }
        for (User spectator : spectators) {
            spectator.SendBoard(game.board.board);
        }
    }

    public void RemovePlayer(User player) {
        players.remove(player);
        if (!IsStarted())
            currPlayers = players.size();
    }

    public void RemoveSpectator(User spectator) {
        spectators.remove(spectator);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        timer.cancel();
    }
}
