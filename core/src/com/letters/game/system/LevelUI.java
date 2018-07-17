package com.letters.game.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import com.letters.game.objects.Element;

// Описывает элемент GUI, отвечающий за уровень
public class LevelUI extends Element {
    private Level level;
    private boolean isActive; // активна ли кнопка

    private int state; // состояние элемента GUI: 0 - отключен (нельзя нажать); 1 - активен; 2 - нажат
    private Texture onTouchImg;
    private Texture currentImg;

    public LevelUI (int x, int y, Level lvl) {
        level = lvl;

        image = null;
        loadTexture();
        changeState(1);

        setPosition(x, y);
    }

    public void changeState(int newState) {
        state = newState;

        switch (newState) {
            /*case 0:
                currentImg = notActiveImg;
                break;*/
            case 1:
                currentImg = image;
                break;
            case 2:
                currentImg = onTouchImg;
                break;
        }
    }

    public int getState() {
        return state;
    }

    public Level getLevel() {
        return level;
    }

    public boolean isActive() {
        return isActive;
    }

    // Здесь имеется ввиду установка состояния для объекта в смысле активен (выбран) уровень или нет:
    public void setState(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public void loadTexture() {
        try {
            image = new Texture(Gdx.files.internal("images/level1.png"));
            onTouchImg = new Texture(Gdx.files.internal("images/level2.png"));
        } catch (Exception e) {
            Gdx.app.log("Texture Error", "LevelsUI's image not found!");
            image = null;
        };
    }

    @Override
    public Texture getImage() {
        return currentImg;
    }
}