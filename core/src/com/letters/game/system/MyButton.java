package com.letters.game.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import com.letters.game.objects.Element;

// Описывает кнопку в GUI
public class MyButton extends Element {
    private Texture onTouchImg;
    private Texture notActiveImg;
    private int state;
    private Texture currentImg;

    public MyButton(int x, int y, String imgName, int state) {
        image = null;
        onTouchImg = null;
        notActiveImg = null;

        loadTexture(imgName);
        changeState(state);
        setPosition(x, y);
    }

    public MyButton(int x, int y, String imgName) {
        image = null;
        onTouchImg = null;
        notActiveImg = null;

        loadTexture(imgName);
        changeState(1);
        setPosition(x, y);
    }

    // Отдельный метод для изменения состояния элемента, чтобы заодно менялось и изображение:
    public void changeState(int newState) {
        state = newState;

        switch (newState) {
            case 0:
                currentImg = notActiveImg;
                break;
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

    // Загружает несколько изображений для разных состояний кнопки:
    @Override
    public void loadTexture(String imageName) {
        try {
            notActiveImg = new Texture(Gdx.files.internal("images/" + imageName + "0.png"));
            image = new Texture(Gdx.files.internal("images/" + imageName + "1.png"));
            onTouchImg = new Texture(Gdx.files.internal("images/" + imageName + "2.png"));
        } catch (Exception e) {
            Gdx.app.log("Texture Error", "Button's image not found!");
        };
    }

    @Override
    public Texture getImage() {
        return currentImg;
    }
}