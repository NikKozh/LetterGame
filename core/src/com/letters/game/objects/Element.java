package com.letters.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

// Абстрактный класс, описывающий базовые характеристики любого объекта на поле
public abstract class Element {
    protected Vector2 position = new Vector2();
    protected Texture image;
    private Rectangle bounds = new Rectangle(); // прямоугольник нужен для определения границ элемента на поле

    protected void loadTexture() {}

    protected void loadTexture(String imageName) {}

    public Texture getImage() {
        return image;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setPosition(int x, int y) {
        position.set(x, y);
        setRectangle(); // обновление координат у прямоугольников объектов для корректных коллизий (должен вызываться при любом изменении координат)
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        setRectangle();
    }

    public void setRectangle() {
        if (image != null) {
            // В LibGDX начало системы координат для отображения на экране - левый нижний угол, тогда как ввод пользователя и некоторые другие вещи
            // имеют начало с.к. в левом верхнем углу. Поэтому при обработке позиции прямоугольника необходимо вычитать её из высоты экрана,
            // чтобы получить корректные координаты (высота изображения вычитается для того, чтобы у самой картинки начало координат было в верхнем левом углу)
            bounds.set(position.x, Gdx.graphics.getHeight() - position.y - image.getHeight(), image.getWidth(), image.getHeight());
        }
    }
}