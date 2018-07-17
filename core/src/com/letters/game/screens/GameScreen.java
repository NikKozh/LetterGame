package com.letters.game.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import com.letters.game.LettersGame;
import com.letters.game.objects.Cell;
import com.letters.game.objects.Letter;
import com.letters.game.system.Level;
import com.letters.game.system.MyButton;

import java.util.Random;

// Игровой экран "Игра" (на нём происходит весь основной геймплей)
public class GameScreen implements Screen, InputProcessor {
    // Константы для обозначения состояния уровня:
    final private static int LEVEL_IN_PROGRESS = 0;       // игрок ещё не сложил слово
    final private static int LEVEL_IN_PROGRESS_ERROR = 1; // игрок сложил слово с ошибкой
    final private static int LEVEL_WORD_WIN = 2;          // игрок сложил слово правильно
    final private static int LEVEL_FULL_WIN = 3;          // игрок сложил слово правильно и оно было последним в уровне

    private final LettersGame game;
    private OrthographicCamera camera;
    private Vector3 touchPosition = new Vector3();
    private Letter draggingLetter = null; // ссылка на букву, которую в текущий момент игрок "тащит" по игровому полю

    private Level currentLevel; // ссылка на экземпляр уровня, который сейчас в игре
    private int currentWord = 0; // номер слова, которое сейчас собирает игрок
    private int levelState = LEVEL_IN_PROGRESS; // хранит константу с текущим состоянием уровня

    private Array<Letter> currentLetters = new Array<Letter>();
    private Array<Cell> currentCells = new Array<Cell>();
    private Array<MyButton> buttons = new Array<MyButton>();

    private MyButton buttonBack;
    private MyButton buttonNextWord;

    private final Random random = new Random(); // неизменяемая ссылка для рандомайзера

    private Texture background;

    public GameScreen(final LettersGame game, Level level) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.GAME_WIDTH, game.GAME_HEIGHT);

        buttonBack = new MyButton(40, 100, "btnBack", 1);
        buttons.add(buttonBack);
        buttonNextWord = new MyButton(game.GAME_WIDTH - 360 - 40, 100, "btnNextWord", 0);
        buttons.add(buttonNextWord);

        currentLevel = level;
        currentLevel.loadWords();
        setLevel();

        background = new Texture(Gdx.files.internal("images/background.png"));

        Gdx.input.setInputProcessor(this);
    }

    // Подготавливает уровень при старте или переключении между словами:
    private void setLevel() {
        currentLetters.clear();         // очищаем все старые буквы
        currentCells.clear();           // и ячейки
        buttonNextWord.changeState(0);  // "выключаем" кнопку следующего слова
        levelState = LEVEL_IN_PROGRESS;

        String word = currentLevel.getWords().get(currentWord); // получаем нужную строку из массива слов по номеру текущего слова
        setCells(word);   // создаём ячейки по слову
        setLetters(word); // создаём буквы по слову
    }

    // Согласно принятому слову создаёт нужное количество ячеек под него, расположенных рановмерно по игровому полю^
    private void setCells(String word) {
        int interval = (game.GAME_WIDTH - LettersGame.CELL_WIDTH * word.length()) / (word.length() + 1);
        for (int i = 0, newX = interval; i < word.length(); i++, newX += LettersGame.CELL_WIDTH + interval) {
            currentCells.add(new Cell(new Vector2(newX, 300), word.charAt(i)));
        }
    }

    // Создаёт из принятого слова нужное количество букв и располагает их случайным образом по полю
    private void setLetters(String word) {
        for (int i = 0; i < word.length(); i++) {
            currentLetters.add(new Letter(new Vector2(random.nextInt(game.GAME_WIDTH - 40 - LettersGame.LETTER_WIDTH) + 40,
                    random.nextInt(game.GAME_HEIGHT - LettersGame.LETTER_HEIGHT - 40 - 640) + 640), word.charAt(i)));
        }
    }

    // Проверяет все ячейки на наличие в них правильной буквы, возвращает истину если всё правильно, в противном случае ложь
    private boolean checkCells() {
        for (Cell cell : currentCells) {
            if (!cell.checkLetter()) return false;
        }
        return true;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(background, 0, 0);
        game.mainFont.draw(game.batch, "УРОВЕНЬ 1: " + currentLevel.getName() + " (" + (currentWord + 1) + "/" + currentLevel.getWords().size + ")", 80, game.GAME_HEIGHT - 100);

        // Отображаем надпись на экране в зависимости от состояния уровня (если игрок не завершил слово, надписи нет):
        switch (levelState) {
            case LEVEL_IN_PROGRESS_ERROR:
                game.mainFont.draw(game.batch, "НЕПРАВИЛЬНО!\nПОПРОБУЙ ЕЩЕ РАЗ", 80, game.GAME_HEIGHT - 200);
                break;

            case LEVEL_WORD_WIN:
                game.mainFont.draw(game.batch, "ПРАВИЛЬНО!\nПЕРЕХОДИ К СЛЕДУЮЩЕМУ СЛОВУ", 80, game.GAME_HEIGHT - 200);
                break;

            case LEVEL_FULL_WIN:
                game.mainFont.draw(game.batch, "ПРАВИЛЬНО!\nМОЛОДЕЦ, УРОВЕНЬ ЗАВЕРШЕН", 80, game.GAME_HEIGHT - 200);
                break;
        }

        // Отрисовка происходит тремя почти одинаковыми цилками вместо одного для требуемой очерёдности - чтобы одни объекты никогда не перекрывали другие:
        for (Cell cell : currentCells) {
            game.batch.draw(cell.getImage(), cell.getPosition().x, cell.getPosition().y);
        }
        for (MyButton button : buttons) {
            game.batch.draw(button.getImage(), button.getPosition().x, button.getPosition().y);
        }
        for (Letter letter : currentLetters) {
            game.batch.draw(letter.getImage(), letter.getPosition().x, letter.getPosition().y);
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

        // Перебираем все буквы, если игрок попал пальцем в одну из них - передаём ссылку на неё
        // в draggingLetter, чтобы другие методы "знали", какую букву игрок тащит по полю:
        for (Letter letter : currentLetters) {
            if (letter.getBounds().contains(screenX, screenY)) {
                draggingLetter = letter;
                break;
            }
        }

        for (MyButton _button : buttons) {
            if (_button.getBounds().contains(screenX, screenY) && _button.getState() != 0) {
                _button.changeState(2);
            }
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (draggingLetter == null) return false; // если игрок не тащит никакой буквы, то сразу выходим из метода

        camera.unproject(touchPosition.set(screenX, screenY, 0));
        draggingLetter.setPosition(screenX, game.GAME_HEIGHT - screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        camera.unproject(touchPosition.set(screenX, screenY, 0));

        // Если игрок тащит какую-либо букву:
        if (draggingLetter != null) {
            boolean needCheckLetters; // нужно ли проверять буквы на правильность

            for (Cell cell : currentCells) {
                // Если границы буквы пересеклись с границами одной из ячеек и при этом ячейка пустая
                if ((draggingLetter.getBounds().overlaps(cell.getBounds())) && (cell.getCurrentLetter() == null)) {
                    cell.setCurrentLetter(draggingLetter); // даём этой ячейке ссылку на букву, которую она теперь должна содержать
                    draggingLetter.setPosition(cell.getPosition().x + 10, cell.getPosition().y + 10); // переставляем букву в ячейку, чтобы она была по центру

                    needCheckLetters = true;
                    // Перебираем все ячейки, если хотя бы одна из них пустая - проверки на правильность слова не будет:
                    for (Cell cell2 : currentCells) {
                        if (cell2.getCurrentLetter() == null) {
                            needCheckLetters = false;
                        }
                    }
                    // Если всё-таки все ячейки с буквами, проводим проверку на правильность составленного слова:
                    if (needCheckLetters && checkCells()) {
                        // Если текущее слово в уровне последнее, то засчитываем игроку полную победу (прохождение уровня):
                        if ((currentWord + 1) == currentLevel.getWords().size) {
                            levelState = LEVEL_FULL_WIN;
                        } else {
                            levelState = LEVEL_WORD_WIN;
                            buttonNextWord.changeState(1); // если нет, то просто разблокируем кнопку "далее"
                        }
                    } else if (needCheckLetters) {
                        levelState = LEVEL_IN_PROGRESS_ERROR; // если слово составлено неправильно, отображаем ошибку и больше ничего не делаем
                    }
                    break;
                    // Если границы буквы не совпали с границами ячейки, но в ячейке есть ссылка на ту же самую букву,
                    // значит, игрок вытащил букву из ячейки и её надо очистить, а кнопку "далее" - сделать неактивной
                } else if ((!draggingLetter.getBounds().overlaps(cell.getBounds())) && (cell.getCurrentLetter() == draggingLetter)) {
                    cell.setCurrentLetter(null);
                    levelState = LEVEL_IN_PROGRESS;
                    buttonNextWord.changeState(0);
                    break;
                }


            }
            // Если игрок не тащит какую-либо букву, а просто отпустил палец:
        } else {
            if (buttonBack.getBounds().contains(screenX, screenY) && buttonBack.getState() == 2) {
                game.setScreen(new LevelScreen(game)); // если нажата кнопка "назад", то возвращаемся на игровой экран "Выбор уровня"
                dispose();
            }

            if (buttonNextWord.getBounds().contains(screenX, screenY) && buttonNextWord.getState() == 2) {
                currentWord++; // если нажата кнопка "далее", то увеличиваем текущее слово на единицу
                setLevel();    // и формируем уровень по новой
            }

            // Стандартная обработка всех кнопок - если палец отпустили, то сбрасываем состояние кнопки
            for (MyButton _button : buttons) {
                if (_button.getState() != 0) {
                    _button.changeState(1);
                }
            }
        }

        draggingLetter = null;
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