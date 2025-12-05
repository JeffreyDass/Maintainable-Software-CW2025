package com.comp2042.service;

public class LevelService {

    public static int calculateLevel(int totalLines, int linesPerLevel) {
        return 1 + (totalLines / linesPerLevel);
    }
}
