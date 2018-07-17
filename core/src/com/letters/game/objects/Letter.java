package com.letters.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

// Описывает букву
public class Letter extends Element {
    private char letter;

    public Letter(Vector2 pos, char ch) {
        letter = ch;

        loadTexture();
        setPosition(pos.x, pos.y);
    }

    public char getLetter() { return letter; }

    // Загружает изображение буквы, которая соответствует полученной в конструкторе
    @Override
    public void loadTexture() {
        try {
            image = new Texture(Gdx.files.internal("images/letters/" + letter + ".png"));
        } catch (Exception e) {
            Gdx.app.log("Texture Error", "Letter's image not found!");
            image = null;
        };
    }
}