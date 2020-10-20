import java.io.File;
import java.io.FileReader;

class Main{

    public static void main(String args[]) throws java.io.IOException{
    	System.out.print("Initialising: ");
    	String current = new File( "." ).getCanonicalPath();
    	File file = new File(current, args[0]);
    	System.out.println(file);
        FileReader source = new FileReader(file);
        final LexicalAnalyzer analyzer = new LexicalAnalyzer(source);
		analyzer.nextToken();
    }

}