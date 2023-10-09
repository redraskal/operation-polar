package com.frostedmc.fabrication.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Redraskal_2 on 10/23/2016.
 */
public enum Theme {

    VILLAGE("Village"),
    SUPERHERO("Superhero"),
    SCORPIAN("Scorpian"),
    VOLCANO("Volcano"),
    PICNIC("Picnic"),
    STATUE("Statue"),
    HUMAN("Human"),
    MAGIC("Magic"),
    FARM("Farm"),
    HOUSE("House"),
    ISLAND("Island"),
    BUNNY("Bunny"),
    KARATE("Karate"),
    CASTLE("Castle"),
    TRAIN("Train"),
    KITCHEN("Kitchen"),
    DINOSAUR("Dinosaur"),
    STARWARS("StarWars"),
    POKEMON("Pokémon"),
    ROBOT("Robot"),
    HUT("Hut"),
    RIVER("River");

    private String name;

    private Theme(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Selects 9 random themes.
     * @return
     */
    public static Theme[] random() {
        List<Theme> random = new ArrayList<Theme>();
        Collections.addAll(random, Theme.values());
        Collections.shuffle(random);
        random = random.subList(0, 9);
        return random.toArray(new Theme[random.size()]);
    }

    public static Theme from(String string) {
        for(Theme theme : Theme.values()) {
            if(theme.getName().equalsIgnoreCase(string)) {
                return theme;
            }
        }
        return null;
    }
}
