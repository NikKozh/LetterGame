package com.letters.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import com.letters.game.screens.MainMenuScreen;

// Главный класс, с которого начинается выполнение приложения
public class LettersGame extends Game {
	private static final String RUSSIAN_CHARACTERS = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
			+ "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
			+ "1234567890.,:;_¡!¿?\"'+-*/()[]={}";

	// Задаём глобальные константы для последующего использования в других классах (размеры текстуры буквы и ячейки под неё)
	public static final int CELL_WIDTH = 120;
	public static final int CELL_HEIGHT = 120;
	public static final int LETTER_WIDTH = 120;
	public static final int LETTER_HEIGHT = 120;

	public SpriteBatch batch; // хранит, если вкратце, пространство для более оптимизированной отрисовки изображений
	public BitmapFont mainFont;
	public BitmapFont captionsFont;
	public int GAME_WIDTH;  // Размеры игрового экрана
	public int GAME_HEIGHT; // (позже используюстя как глобальные переменные)
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		mainFont = new BitmapFont();
		captionsFont = new BitmapFont();

		// Преобразуем шрифты из формата ttf в формат, используемый LibGDX - BitmapFont
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.characters = RUSSIAN_CHARACTERS + FreeTypeFontGenerator.DEFAULT_CHARS; // устанавливаем набор символов для шрифта - кириллица + латиница
		parameter.size = 48;
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Main Text.ttf"));
		mainFont = generator.generateFont(parameter);

		parameter.size = 70;
		parameter.color.add(Color.BLUE);
		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Captions.ttf"));
		captionsFont = generator.generateFont(parameter);
		generator.dispose();

		GAME_WIDTH = Gdx.graphics.getWidth();
		GAME_HEIGHT = Gdx.graphics.getHeight();

		this.setScreen(new MainMenuScreen(this)); // таким образом переходим на другой игровой экран
	}

	// Вызывается каждый тик, это единственный метод для отрисовки изображений:
	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		mainFont.dispose();
		captionsFont.dispose();
	}
}