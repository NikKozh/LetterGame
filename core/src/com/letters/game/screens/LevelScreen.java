package com.letters.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import com.letters.game.LettersGame;
import com.letters.game.system.Level;
import com.letters.game.system.LevelUI;
import com.letters.game.system.MyButton;

// Игровой экран "Выбор уровня"
public class LevelScreen implements Screen, InputProcessor {
    private final LettersGame game;
    private OrthographicCamera camera;

    private Vector3 touchPosition = new Vector3();

    private Array<Level> allLevels = new Array<Level>();
    private Array<LevelUI> levelsUI = new Array<LevelUI>();

    private MyButton buttonBack;
    private int selectedLevel; // номер выбранного в текущий момент уровня
    private Texture background;

    public LevelScreen(final LettersGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.GAME_WIDTH, game.GAME_HEIGHT);

        buttonBack = new MyButton(game.GAME_WIDTH / 2 - 180, 100, "btnBack", 1);

        addLevels();

        background = new Texture(Gdx.files.internal("images/mainBackground.png"));

        Gdx.input.setInputProcessor(this);
    }

    // Считывает уровни в соответствующей папке, создаёт необходимые экземпляры типа Level и LevelUI и заполняет соответствующие массивы
    private void addLevels() {
        FileHandle[] levels = Gdx.files.internal("levels/").list(); // метод LibGDX, который возвращает список файлов в указанной директории

        if (levels == null) {
            Gdx.app.log("File Error", "Levels not found!");
        } else {
            Level tmpLvl;
            int tmpX = game.GAME_WIDTH / 2 - 200;
            int tmpY = game.GAME_HEIGHT / 2 - 200;

            // обработка полученного массива файлов:
            for(FileHandle file : levels) {
                tmpLvl = new Level(file.name().replace(".txt", "")); // у каждого файла берём его название, убираем расширение в конце
                allLevels.add(tmpLvl);                               // и добавляем в общий список уровней
                levelsUI.add(new LevelUI(tmpX, tmpY, tmpLvl));       // а также сразу создаём элемент GUI и добавляем его в соответствующий список
                tmpX += game.GAME_WIDTH / 2;                         // следующий элемент будет смещён вправо на половину ширины экрана
            }
            selectedLevel = 0;              // устанавливаем первый уровень как выбранный
            levelsUI.get(0).setState(true); // и устанавливаем его состояние как "активирован"
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(background, 0, 0);
        game.batch.draw(buttonBack.getImage(), buttonBack.getPosition().x, buttonBack.getPosition().y);
        game.captionsFont.draw(game.batch, "ВЫБЕРИ УРОВЕНЬ", game.GAME_WIDTH / 2 - 300, game.GAME_HEIGHT - 250);

        for (LevelUI lvl : levelsUI) {
            game.batch.draw(lvl.getImage(), lvl.getPosition().x, lvl.getPosition().y);
            game.mainFont.draw(game.batch, lvl.getLevel().getName(), lvl.getPosition().x, lvl.getPosition().y - 30);
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

        camera.unproject(touchPosition.set(screenX, screenY, 0));

        if (buttonBack.getBounds().contains(Gdx.input.getX(), Gdx.input.getY())) {
            buttonBack.changeState(2);
        }

        // если нажатие совпало с координатами GUI-элемента и при этом соотвествтующий уровкень активен, то устанавливаем ему активное состояние:
        for (LevelUI lvl : levelsUI) {
            if (lvl.getBounds().contains(Gdx.input.getX(), Gdx.input.getY()) && (lvl.isActive())) {
                lvl.changeState(2);
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

        for (LevelUI lvl : levelsUI) {
            if (lvl.getBounds().contains(Gdx.input.getX(), Gdx.input.getY()) && (lvl.getState() == 2)) {
                game.setScreen(new GameScreen(game, lvl.getLevel())); // по нажатию на уровень, если он активный, переходим на игровой экран "Игра"
                dispose();
                break; // после dispose() метод продолжает выполняться, поэтому досрочно выходим из цикла
            }
            lvl.changeState(1); // в этом цикле мы перебираем все уровни тогда, когда игрок отпустил палец, поэтому сбрасываем состояние всех GUI-элементов уровней
        }

        // Переключение между уровнями - если игрок нажал на правую часть экрана, то сдвигаем все GUI-элементы уровней влево и наоборот:
        if ((Gdx.input.getX() > game.GAME_WIDTH / 2 + 200) && ((selectedLevel + 1) < levelsUI.size)) {
            levelsUI.get(selectedLevel).setState(false); // одновременно со смещением делаем старый уровень "не выбранным"
            selectedLevel++;                             // увеличиваем активный уровень на единицу
            levelsUI.get(selectedLevel).setState(true);  // и устанавливаем новый уровень как "выбранный"

            for (LevelUI lvl : levelsUI) {
                lvl.setPosition(lvl.getPosition().x - (game.GAME_WIDTH / 2), lvl.getPosition().y);
            }
        } else if ((Gdx.input.getX() < game.GAME_WIDTH / 2 - 200) && ((selectedLevel - 1) >= 0)) {
            levelsUI.get(selectedLevel).setState(false);
            selectedLevel--;
            levelsUI.get(selectedLevel).setState(true);

            for (LevelUI lvl : levelsUI) {
                lvl.setPosition(lvl.getPosition().x + (game.GAME_WIDTH / 2), lvl.getPosition().y);
            }
        }

        if (buttonBack.getBounds().contains(Gdx.input.getX(), Gdx.input.getY()) && (buttonBack.getState() == 2)) {
            game.setScreen(new MainMenuScreen(game)); // по нажатию кнопки "Назад" - возврат на игровой экран "Главное меню"
            dispose();
        }

        buttonBack.changeState(1);

        return true;
    }

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