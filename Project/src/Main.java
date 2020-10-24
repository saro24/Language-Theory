import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

class Main{

	private static ArrayList<String> variables = new ArrayList<String>();
	private static ArrayList<Integer> variablePos = new ArrayList<Integer>();

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
		for(int i=0;i<variables.size();i++){
			System.out.println(variables.get(i)+" "+variablePos.get(i));
		}
    }

    private static void record(String var, int place){
		if(variables.size() == 0){
			variables.add(var);
			variablePos.add(place);
		} else{
			int pos = variables.indexOf(var);
			if(pos == -1){
				for(int i = 0;i < variables.size(); i++){
					if(var.compareTo(variables.get(i)) < 0){
						variables.add(i, var);
						variablePos.add(i, place);
						break;
					}
				}
				if(variables.indexOf(var) == -1){
					variables.add(var);
					variablePos.add(place);
				}
			}
		}
	}
}
