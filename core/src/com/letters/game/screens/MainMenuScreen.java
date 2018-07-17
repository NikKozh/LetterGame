package com.letters.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import com.letters.game.LettersGame;
import com.letters.game.system.MyButton;

// Игровой экран "Главное меню"
public class MainMenuScreen implements Screen, InputProcessor {
    private final LettersGame game;
    private OrthographicCamera camera;

    private Vector3 touchPosition = new Vector3(); // трёхмерный вектор, который хранит координаты касания игрока (Z всегда 0)

    private Array<MyButton> allButtons = new Array<MyButton>();
    private MyButton buttonExit;
    private MyButton buttonNewGame;

    private Texture background;

    public MainMenuScreen(final LettersGame game) {
        this.game = game;

        camera = new OrthographicCamera(); // получаем 2D-камеру
        camera.setToOrtho(false, game.GAME_WIDTH, game.GAME_HEIGHT); // устанавливаем параметры 2D-камеры

        buttonExit = new MyButton(game.GAME_WIDTH / 2 - 180, game.GAME_HEIGHT / 2 - 300 - 40, "btnExit");
        allButtons.add(buttonExit);
        buttonNewGame = new MyButton(game.GAME_WIDTH / 2 - 180, game.GAME_HEIGHT / 2 - 40, "btnPlay");
        allButtons.add(buttonNewGame);

        background = new Texture(Gdx.files.internal("images/mainBackground.png"));

        Gdx.input.setInputProcessor(this); // сообщаем LibGDX, что любой ввод будет обрабатывать этот экземпляр
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);          // очищаем экран
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // определённым цветом

        camera.update();
        game.batch.setProjectionMatrix(camera.combined); // устанавливаем необходимый тип камеры для корректной отрисовки

        game.batch.begin();
        game.batch.draw(background, 0, 0);

        for (MyButton btn : allButtons) {
            game.batch.draw(btn.getImage(), btn.getPosition().x, btn.getPosition().y);
        }
        game.batch.end();
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Если нажали вторым пальцем, сразу выходим из метода:
        if (pointer > 0) {
            return false;
        }

        camera.unproject(touchPosition.set(screenX, screenY, 0)); // преобразует 2D-координаты касания в 3D-коориданты пространства, с которым работает камера (Z = 0)

        for (MyButton btn : allButtons) {
            if (btn.getBounds().contains(screenX, screenY)) {
                btn.changeState(2); // когда игрок консулся пальцем экрана в месте определённой кнопки, назначаем ей состояние 2, чтобы поменять её изображение
            }
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        camera.unproject(touchPosition.set(screenX, screenY, 0));

        if (buttonNewGame.getBounds().contains(screenX, screenY) && (buttonNewGame.getState() == 2)) {
            game.setScreen(new LevelScreen(game)); // переходим на игровой экран выбора уровня
            dispose();
        }

        if (buttonExit.getBounds().contains(screenX, screenY) && (buttonExit.getState() == 2)) {
            Gdx.app.exit(); // выходим из приложения
        }

        for (MyButton btn : allButtons) {
            btn.changeState(1); // когда игрок поднял палец, перебираем все кнопки и назначаем им состояние 1, т.е. меняем изображение на неактивное
        }

        return true;
    }

    // Далее идут все остальные методы из интерфейсов Screen и InputProcessor, которые необходимо переопределить, но которые в проекте не используются
    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean scrolled (int amount) {
        return false;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}