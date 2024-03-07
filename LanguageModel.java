import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		// Your code goes here
        String window = "";
        char chr;

        In in = new In(fileName);
        for(int j = 0; j < windowLength(); j++) {
                window += in.readChar();
        }
        while (!in.isEmpty()){
            chr= in.readChar();

            List probabilities = CharDataMap.get(window);
            if (probabilities == null) {
                probabilities = new List();
                CharDataMap.put(window, probabilities);
            }
            probabilities.update(chr);
            window = window + chr;
            window = window.substring(1);
        }
        for (List probabilities: CharDataMap.values()) {
            calculateProbabilities(probabilities);
        }
    }

    // Computes and sets the probabilities (p and cp fields) of all the
    // characters in the given list. */
    public void calculateProbabilities(List probabilities) {                
        // Your code goes here
        int charTotal = 0;
        for(int j = 0; j < probabilities.getSize(); j++) {
           charData current = probabilities.get(j);
           charTotal += current.count;
        }

        double totalProbabilities = 0;
        for(int j = 0; j < probabilities.getSize(); j++) {
           charData current = probabilities.get(j);
           current.p += (double) current.count / charTotal;
           totalProbabilities += current.p;
           current.cp = totalProbabilities;
        }

    }

    // Returns a random character from the given probabilities list.
    public char getRandomChar(List probabilities) {
        // Your code goes here
    double r = randomGenerator.nextDouble();
    ListIterator iterator = probabilities.ListIterator(0);
    while (iterator.hasNext()) {
         CharData current = iterator.next();
         if (r <=current.cp) {
            return current.char;
         }
    }
    return probabilities.get(probabilities.getSize()- 1).char;

    }

    /**
     * Generates a random text, based on the probabilities that were learned during training. 
     * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
     * doesn't appear as a key in Map, we generate no text and return only the initial text. 
     * @param numberOfLetters - the size of text to generate
     * @return the generated text
     */
    public String generate(String initialText, int textLength) {
        // Your code goes here
        if (initialText.length() < windowLength){
            return initialText;
        }
        String windows = initialText.substring(initialText.length() - windowLength);
        StringBuilder genText = new StringBuilder(window);
        for (int j = 0; j < textLength; j++) {
            List probabilities = CharDataMap.get(window);
            if(probabilities = null){
               return genText.toString();
            }
            else {   
                char chr = getRandomChar(probabilities);
                genText.append(chr);
                window = genText.substring(genText.length() - windowLength);

            }
        }
        return genText.toString();
    }

    /** Returns a string representing the map of this language model. */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String key : CharDataMap.keySet()) {
            List keyProbabilities = CharDataMap.get(key);
            str.append(key + " : " + keyProbabilities + "\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        // Your code goes here
    }
}
