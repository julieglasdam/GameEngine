package com.example.julieglasdam.gameengine.Breakout;

import android.graphics.Bitmap;

import com.example.julieglasdam.gameengine.GameEngine;
import com.example.julieglasdam.gameengine.Screen;
import com.example.julieglasdam.gameengine.Sound;
import com.example.julieglasdam.gameengine.TouchEvent;

import java.util.List;

/**
 * Created by julieglasdam on 03/10/2017.
 */

public class GameScreen extends Screen{
    enum State {
        Paused,
        Running,
        GameOver,
        Level2
    }

    World world;
    WorldRenderer renderer;

    Bitmap background = null;
    Bitmap resume = null;
    Bitmap gameOver = null;
    State state = State.Running;
    Sound bounceSound = null;
    Sound blockSound = null;

    public GameScreen(GameEngine gameEngine) {
        super(gameEngine);
        world = new World(gameEngine, new CollisionListener() {
            public void collisionWall() {
                bounceSound.play(1);
            }

            @Override
            public void collisionPaddle() {

            }

            @Override
            public void collisionBlock() {

            }
        }

        );
        renderer = new WorldRenderer(gameEngine, world);
        background = gameEngine.loadBitmap("breakoutassets/background.png");
        resume = gameEngine.loadBitmap("breakoutassets/resume.png");
        gameOver = gameEngine.loadBitmap("breakoutassets/gameover.png");
        bounceSound = gameEngine.loadSound("breakoutassets/bounce.wav");
        blockSound = gameEngine.loadSound("breakoutassets/blocksplosion.wav");
    }

    @Override
    public void update(float deltaTime) {
       if (world.gameOver) {
           state = State.GameOver;
       }
       else if (world.done) {
           state = State.Level2; // In this case update to new screen
       }

       if (state == State.Paused && gameEngine.getTouchEvents().size() > 0) { // Check for paused and if the screen is touched
            state = State.Running;
        }

        if (state == State.GameOver && gameEngine.getTouchEvents().size() > 0) {
            List<TouchEvent> events = gameEngine.getTouchEvents();
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).type == TouchEvent.TouchEventType.Up) {
                    gameEngine.setScreen(new MainMenuScreen(gameEngine));
                    return;
                }
            }

        }

        if (state == State.Running && gameEngine.getTouchY(0) < 38 && gameEngine.getTouchX(0) > 320-38) {
            state = State.Paused;
            return;
        }

        gameEngine.drawBitMap(background, 0, 0);
        if (state == State.Running) {
            world.update(deltaTime, gameEngine.getAccelerometer()[0]);
        }
        renderer.render();


        if (state == State.Paused) {
            gameEngine.drawBitMap(resume, 160 - resume.getWidth()/2, 240 - resume.getHeight()/2);
        }

        if (state == State.GameOver) {
            gameEngine.drawBitMap(gameOver, 160 - gameOver.getWidth()/2, 240 - gameOver.getHeight()/2);
        }
    }

    @Override
    public void pause() {
    if (state == State.Running) state = State.Paused;
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
