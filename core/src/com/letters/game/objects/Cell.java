package com.letters.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

// Описывает ячейку, в которую игрок должен поместить букву
public class Cell extends Element {
    private Letter currentLetter; // какая буква в данный момент находится в ячейке
    private char letter;          // какая буква по факту там должна быть

    public Cell(Vector2 pos, char ch) {
        letter = ch;
        currentLetter = null;

        loadTexture();
        setPosition(pos.x, pos.y);
    }

    // Проверка, правильная ли буква находится в ячейке и находится ли вообще:
    public boolean checkLetter() {
        return ((currentLetter != null) && (currentLetter.getLetter() == letter));
    }

    // Устанавливает, какая буква в данный момент находится в ячейке
    public void setCurrentLetter(Letter ltr) {
        currentLetter = ltr;
    }

    public Letter getCurrentLetter() {
        return currentLetter;
    }

    @Override
    public void loadTexture() {
        image = new Texture(Gdx.files.internal("images/cell.png"));
    }

    @Override
    public void setPosition(int x, int y) {
        position.set(x, y);
        setRectangle();
    }
}