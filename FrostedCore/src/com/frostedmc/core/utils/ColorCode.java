package com.frostedmc.core.utils;

/**
 * Created by Redraskal_2 on 8/25/2016.
 */
public enum ColorCode {

    AQUA('b'),
    BLACK('0'),
    BLUE('9'),
    BOLD('l'),
    DARK_AQUA('3'),
    DARK_BLUE('1'),
    DARK_GRAY('8'),
    DARK_PURPLE('5'),
    DARK_RED('4'),
    DARK_GREEN('2'),
    GOLD('6'),
    GRAY('7'),
    GREEN('a'),
    ITALIC('o'),
    LIGHT_PURPLE('d'),
    MAGIC('k'),
    RED('c'),
    RESET('r'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    WHITE('f'),
    YELLOW('e');

    private char character;
    private char mc;

    private ColorCode(char character) {
        this.character = character;
        this.mc = '§';
    }

    public String convert() {
        return new String(this.mc + "" + this.character);
    }
}