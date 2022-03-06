
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