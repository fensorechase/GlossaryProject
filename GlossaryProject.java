import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 *
 *
 * @author Chase Fensore
 */
public final class GlossaryProject {

    /**
     * Default constructor--private to prevent instantiation.
     */
    private GlossaryProject() {
        // no code needed here
    }

    /**
     * Compare {@code String}s in lexicographic order.
     */
    private static class StringLT implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    /**
     * Generates the .HTML code in a file for an ordered list of terms and their
     * definitions.
     *
     * @param wordMap
     *            the {@code Map} of terms and definitions
     *
     * @param outFile
     *            the {@code SimpleWriter} to write to html file
     * @param termList
     *            the {@code Queue} of terms to be put in glossary
     * @param destination
     *            the {@code String} containing the name of index file path
     * @requires {@code outFile} stream is open
     *
     * @ensures outFile contains appropriate HTML formatting, and hyperlinks to
     *          the definitions of all terms in {@code termList} and
     *          {@code wordMap}
     *
     **/
    private static void generateGlossary(Map<String, String> wordMap,
            Queue<String> termList, SimpleWriter outFile, String destination) {

        //HTML Output begins
        outFile.println("<html>");
        outFile.println("<head>");
        outFile.println("<title>Glossary</title>");
        outFile.println("</head>");
        outFile.println("<body>");
        outFile.println("<h2>Glossary</h2>");
        outFile.println("<hr>");
        outFile.println("<h3>Index</h3>");
        outFile.println("<ul>"); //Start of term index
        for (String s : termList) {
            outFile.println("<li>");
            outFile.println("<a href=\"" + s + ".html\">" + s + "</a>"); //for-each ordered? hyperlink
            outFile.println("</li>");
            //Create, write to term file
            SimpleWriter termFile = new SimpleWriter1L(s + ".html");
            termFile.println("<html>");
            termFile.println("<head>");
            termFile.println("<title>" + s + "</title>");
            termFile.println("</head>");
            termFile.println("<body>");
            termFile.println("<h2>");
            termFile.println("<b>");
            termFile.println("<i>");
            termFile.println("<font color=\"red\">" + s + "</font>");
            termFile.println("</i>");
            termFile.println("</b>");
            termFile.println("</h2>");

            String def = wordMap.value(s); //*def of current term, s
            termFile.println("<blockquote>");

            //START Definition generation
            String term = s;
            generateDefinition(termList, term, wordMap, termFile);
            //END Definition

            termFile.println("</blockquote>");
            termFile.println("<hr>");
            termFile.println("<p>");
            termFile.println("Return to ");
            //Return to Index page
            termFile.println("<a href=\"" + destination + "\">"
                    + destination.substring(0, 5) + "</a>");
            termFile.println(".");
            //Closing tags
            termFile.println("</p>");
            termFile.println("</body>");
            termFile.println("</html>");

            termFile.close(); //close to be re-used
        }
        //Index closing tags
        outFile.println("</ul>");
        outFile.println("</body>");
        outFile.println("</html>");
    }

    /**
     * Generates and prints to {@code termFile} the definition and embedded
     * hyperlinks to a given term.
     *
     * @param termList
     *            the {@code Queue} of terms to be put in glossary
     * @param term
     *            the {@code String} term to be defined in termFile
     * @param wordMap
     *            the {@code Map} of terms and definitions
     *
     * @param termFile
     *            the {@code SimpleWriter} to write to given term's html file
     *
     *
     * @requires termFile stream is open
     *
     * @ensures termFile contains appropriate HTML formatting the input
     *          {@code term} definition: if the term definition contains another
     *          term, links to other term's file in definition
     *
     *          else, the raw definition is printed to {@code termFile}
     *
     **/
    private static void generateDefinition(Queue<String> termList, String term,
            Map<String, String> wordMap, SimpleWriter termFile) {

        final String separatorStr = " \t,";
        Set<Character> separatorSet = new Set1L<>();
        generateElements(separatorStr, separatorSet);

        String def = wordMap.value(term); //given term, get definition
        int pos = 0;
        while (pos < def.length()) {
            String token = nextWordOrSeparator(def, pos, separatorSet);

            boolean containsTerm = false;
            for (String test : termList) {
                if (token.equals(test)) {
                    containsTerm = true;
                }
            }
            if (!containsTerm) {
                termFile.print(token);
            }
            if (containsTerm) {
                termFile.print("<a href=" + '"' + token + ".html" + '"' + '>'
                        + token + "</a>");
            }
            pos = pos + token.length();
        }

    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param strSet
     *            the {@code Set} to be replaced
     * @replaces strSet
     * @ensures strSet = entries(str)
     */
    private static void generateElements(String str, Set<Character> strSet) {
        assert str != null : "Violation of: str is not null";
        assert strSet != null : "Violation of: strSet is not null";

        strSet.clear(); //can we assume strSet is empty?
        Set<Character> tempSet = strSet.newInstance(); //same type
        for (int i = 0; i < str.length(); i++) {
            char sub = str.charAt(i);

            if (!tempSet.contains(sub)) { //only adds unique characters
                tempSet.add(sub);
            }

        }
        strSet.transferFrom(tempSet); //replace strSet

    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        //check "text" at position
        boolean isSep = false;
        //check 0th char

        isSep = separators.contains(text.charAt(position));

        //now we know if text starts with: word, or sep
        int strLen = position; //***to be changed
        if (isSep) { //sep cont forward

            while (strLen < text.length()
                    && separators.contains(text.charAt(strLen))) {
                strLen++; //index

            }
        } else { //word cont forward
            while (strLen < text.length()
                    && !separators.contains(text.charAt(strLen))) {
                strLen++; //index

            }
        }
        String result = text.substring(position, strLen);
        //-1: will iterate through first "cont=false"

        return result;

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        SimpleWriter out = new SimpleWriter1L();
        SimpleReader in = new SimpleReader1L();
        out.print("Insert terms file name: ");
        String termsFile = in.nextLine(); //must be in form "___.html"
        SimpleReader inFile = new SimpleReader1L(termsFile); //read from file
        out.print("Insert output file name: ");
        String destination = in.nextLine();
        SimpleWriter outFile = new SimpleWriter1L(destination); //write to this file

        //CREATE MAP
        Map<String, String> wordMap = new Map1L<>();

        //CREATE QUEUE of terms
        Queue<String> termList = new Queue1L<>();

        String token = "";
        String term = "";
        String definition = "";

        while (!inFile.atEOS()) {
            term = inFile.nextLine(); //line of input
            termList.enqueue(term); //adds term to term Queue
            while (!inFile.atEOS() && !(token = inFile.nextLine()).equals("")) {
                definition = definition + token + " "; //full token

            }
            //Put current term, definition into wordMap
            wordMap.add(term, definition);
            definition = "";

        }

        //Alphabetically order termList
        Comparator<String> cs = new StringLT(); //instantiates lexiographic comparator
        termList.sort(cs); //alphabetically sorts termList
        out.print(termList.toString());

        //generate glossary, outputs the glossary HTML code to outFile
        generateGlossary(wordMap, termList, outFile, destination);

        out.close();
        in.close();
        inFile.close();
    }
}
