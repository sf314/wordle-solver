
/**
 * FrequencyEntry
 * 
 * A simple tuple to associate a given letter like `g`, and it's frequency
 * expressed as a percentage from 0.0 to 1.0.
 */
public class FrequencyEntry {
    private Character letter;
    private Float frequency;

    public FrequencyEntry() {

    }

    public FrequencyEntry(Character l, Float f) {
        this.letter = l;
        this.frequency = f;
    }

    public Character getLetter() {
        return this.letter;
    }

    public Float getFrequency() {
        return this.frequency;
    }
}