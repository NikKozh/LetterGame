package com.letters.game.system;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.files.FileHandle;
        import com.badlogic.gdx.utils.Array;

// Описывает уровень
public class Level {
    private String name;
    private Array<String> words = new Array<String>(); // уровень в контексте данной игры - это массив слов

    public Level(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Array<String> getWords() {
        return words;
    }

    // Загружает уровень из файла и разделяет его на слова, помещая их в массив
    public void loadWords() {
        try {
            FileHandle file = Gdx.files.internal("levels/" + name + ".txt");
            String[] wordsArray = file.readString().split("\\r?\\n");
            for (String word : wordsArray) {
                if (word.length() <= 10) {
                    words.add(word);
                }
            }
        } catch (Exception e) {
            Gdx.app.log("File Error", "Level not found!");
            words = null;
        }
    }
}