package com.example.lancelot.fullscreensnakegame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine {
    private static final int GameWidth = 28;
    private static final int GameHeight = 42;

    private final List<Coordinate> walls = new ArrayList<>();
    private final List<Coordinate> snake = new ArrayList<>();
    private final List<Coordinate> apples = new ArrayList<>();

    private final Random random = new Random();
    private boolean increaseTail = false;

    public static boolean wallsExist = false;

    private Direction currentDirection = Direction.East;
    private GameState currentGameState = GameState.Running;

    private int appleEaten = 0;

    private Coordinate getSnakeHead() {
        return snake.get(0);
    }

    public GameEngine() {
    }

    public void initGame() {
        AddSnake();
        AddWalls();
        AddApples();
    }

    public void UpdateDirection(Direction newDirection) {
        if (Math.abs(newDirection.ordinal() - currentDirection.ordinal()) % 2  == 1){
            currentDirection = newDirection;
        }
    }

    public void Update(){
        //Update the snake
        switch (currentDirection) {
            case North:
                UpdateSnake(0,-1);
                break;
            case East:
                UpdateSnake(1,0);
                break;
            case South:
                UpdateSnake(0,1);
                break;
            case West:
                UpdateSnake(-1, 0);
                break;
        }

        if (wallsExist == true) {
            // Check wall collision
            for (Coordinate w : walls) {
                if (snake.get(0).equals(w)) {
                    currentGameState = GameState.Lost;
                }
            }
        } else {
            // Implements no wall collision
            if (getSnakeHead().getX() == GameWidth) {
                snake.get(0).setX(getSnakeHead().getX() - GameWidth + 2);
                snake.get(0).setY(getSnakeHead().getY());
            } else if (getSnakeHead().getX() == -1) {
                snake.get(0).setX(getSnakeHead().getX() + GameWidth - 2);
                snake.get(0).setY(getSnakeHead().getY());
            } else if (getSnakeHead().getY() == GameHeight) {
                snake.get(0).setX(getSnakeHead().getX());
                snake.get(0).setY(getSnakeHead().getY() - GameHeight + 2);
            } else if (getSnakeHead().getY() == -1) {
                snake.get(0).setX(getSnakeHead().getX());
                snake.get(0).setY(getSnakeHead().getY() + GameHeight - 2);
            }
        }


        // Check self collision
        for (int i = 1; i < snake.size(); i++) {
            if (getSnakeHead().equals(snake.get(i))) {
                currentGameState = GameState.Lost;
                return;
            }
        }

        // Check apples
        Coordinate appleToRemove = null;
        for (Coordinate apple : apples) {
            if (getSnakeHead().equals(apple)) {
                appleToRemove = apple;
                increaseTail = true;
                getAppleEaten();
            }
        }
        if (appleToRemove != null) {
            apples.remove(appleToRemove);
            AddApples();
        }
    }

    public TileType[][] getMap() {
        TileType[][] map = new TileType[GameWidth][GameHeight];

        for (int x = 0; x < GameWidth; x++) {
            for (int y = 0; y < GameHeight; y++) {
                map[x][y] = TileType.Nothing;
            }
        }

        for (Coordinate s : snake) {
            map[s.getX()][s.getY()] = TileType.SnakeTail;
        }

        for (Coordinate a : apples) {
            map[a.getX()][a.getY()] = TileType.Apple;
        }

        map[snake.get(0).getX()][snake.get(0).getY()] = TileType.SnakeHead;

        for (Coordinate walls : walls) {
            map[walls.getX()][walls.getY()] = TileType.Wall;
        }
        return map;
    }

    private void getAppleEaten() {
        appleEaten++;
        FullscreenActivity.score.setText("Score: " + String.valueOf(appleEaten));
    }

    private void UpdateSnake(int x, int y) {
        int newX  = snake.get(snake.size() - 1).getX();
        int newY = snake.get(snake.size() - 1).getY();

        for (int i = snake.size() -1; i > 0; i--) {
            snake.get(i).setX(snake.get(i-1).getX());
            snake.get(i).setY(snake.get(i-1).getY());
        }

        if (increaseTail) {
            snake.add(new Coordinate(newX,newY));
            increaseTail = false;
        }
        snake.get(0).setX(snake.get(0).getX() + x);
        snake.get(0).setY(snake.get(0).getY() + y);

    }

    private void AddSnake() {
        snake.clear();

        int x = 1 + random.nextInt(GameWidth - 10);
        int y = 1 + random.nextInt(GameHeight - 2);

        snake.add(new Coordinate(x,y));
        snake.add(new Coordinate(x,y+1));
    }

    private void AddWalls() {
        //Top and Bottom walls
        for (int x = 0; x < GameWidth; x++) {
            walls.add(new Coordinate(x,0));
            walls.add(new Coordinate(x, GameHeight-1));
        }

        //Left and Right Walls
        for (int y = 1; y < GameHeight; y++) {
            walls.add(new Coordinate(0, y));
            walls.add(new Coordinate(GameWidth-1, y));
        }
    }

    private void AddApples() {
        Coordinate coordinate = null;

        boolean added = false;

        while (!added) {
            int x = 1 + random.nextInt(GameWidth - 2);
            int y = 1 + random.nextInt(GameHeight - 2);

            coordinate = new Coordinate(x, y);
            boolean collision = false;
            for (Coordinate s : snake) {
                if (s.equals(coordinate)) {
                    collision = true;
                    break;
                }
            }
            for (Coordinate a : apples) {
                if (a.equals(coordinate)) {
                    collision = true;
                    break;
                }
            }
            added = !collision;
        }
        apples.add(coordinate);
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }
}
