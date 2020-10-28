import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.SortedMap;

/** 
* 
* @author Orestis Tranganidas
* @author Sara Bouglam
*/

/**
*The main file of the compiler for the programming language FORTR-S
*/
public class Main{
    
    /**
    * Saves the variables that appear in the file in alphabetical order alongside the line
    * they first appear in
    */
	private static SortedMap<String, Integer> variables = new TreeMap<>();
    

    /**
    *Takes as input the file to be read and uses the generated Jflex code 
    * to output all the tokens that appear in the file while ignoring the comments
    * @param args a file of code FORTR-S
    *
    * @throws java.io.IOException if the file given doesn't exist
    */
    public static void main(String args[]) throws java.io.IOException{
    	File file = new File(".", args[0]);
        FileReader source = new FileReader(file);
        final LexicalAnalyzer analyzer = new LexicalAnalyzer(source);
        Symbol token = analyzer.nextToken();
        //Read through all the tokens that appear in the file until an EOS token appears 
        while(token.getType() != LexicalUnit.EOS){
        	if(token.getType() == LexicalUnit.VARNAME){
        		record(token.getValue().toString(), token.getLine());
        	}
        	System.out.println(token);
        	token = analyzer.nextToken();
        }
        /*Output the variables that appear in the file in alphabetical order and the line
        * they first appear in
        */
        System.out.println("");
		System.out.println("Variables");
		for (Map.Entry<String, Integer> entry : variables.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
    }

    /**
    *Saves the first time a variable appears in the code file given and ignores duplicates
    */
    private static void record(String var, int place){
		if(! variables.containsKey(var)){
			variables.put(var, place);
		}
	}
}