package com.example.edward.pacman;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Room {
    protected String name;
    protected int maxPlayers;
    protected int currPlayers;
    protected Game game;
    protected boolean isStarted = false;
    protected BlockingQueue<User> players = new LinkedBlockingQueue<>();
    protected BlockingQueue<User> spectators = new LinkedBlockingQueue<>();
    private Timer timer = new java.util.Timer();
    private Server server;

    private TimerTask task = new TimerTask() {
        public void run() {
            SendState();

        }
    };

    public boolean IsStarted() {
        return isStarted;
    }

    public void Command(int dir, int id) {
        if (User.UP == dir) {
            game.GoUp(id);
        } else if (User.DOWN == dir) {
            game.GoDown(id);
        } else if (User.LEFT == dir) {
            game.GoLeft(id);
        } else if (User.RIGHT == dir) {
            game.GoRight(id);
        }
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

    public Room(Server server, String name, int maxPlayers, User firstPlayer) {

        this.server = server;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.currPlayers = 1;
        players.add(firstPlayer);

        game = new Game(maxPlayers);

        if (currPlayers == maxPlayers)
            StartGame();
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
//        SendBoard();
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

    public void RemovePlayer(User player) {
        players.remove(player);
        if (!IsStarted())
            currPlayers = players.size();
        if (players.size() == 0 && server != null) {
            server.rooms.remove(this);
        }
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
