package net.sf.ardengine.input;

/**
 *  Sent from renderer, contains info about typed character.
 */
public class CharInput {
    /**Typed char*/
    final char input;

    /**
     * @param input Typed char
     */
    public CharInput(char input) {
        this.input = input;
    }
}
