import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.SortedMap;

class Main{

	private static SortedMap<String, Integer> variables = new TreeMap<>();

    public static void main(String args[]) throws java.io.IOException{
    	System.out.println("Initialising: ");
    	File file = new File(".", args[0]);
        FileReader source = new FileReader(file);
        final LexicalAnalyzer analyzer = new LexicalAnalyzer(source);
        Symbol token = analyzer.nextToken();
        while(token.getType() != LexicalUnit.EOS){
        	if(token.getType() == LexicalUnit.VARNAME){
        		record(token.getValue().toString(), token.getLine());
        	}
        	System.out.println(token);
        	token = analyzer.nextToken();
        }
        System.out.println("");
		System.out.println("Variables");
		for (Map.Entry<String, Integer> entry : variables.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
    }

    private static void record(String var, int place){
		if(! variables.containsKey(var)){
			variables.put(var, place);
		}
	}
}