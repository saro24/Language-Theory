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
    * @param args a file of code FORTR-S and options for the execution
    *
    * @throws java.io.IOException if the file given doesn't exist
    */
    public static void main(String[] args) throws java.io.IOException{
      if(args.length > 0){
        boolean verbose = false;
        boolean write = false;
        String treeFile = "";
        for(int i = 0; i< args.length-1;i++){
          if(args[i].equals("-v")){
            verbose = true;
          }else if(args[i].equals("-wt")){
            if(i < args.length-2){
              treeFile = args[i+1];
              if(treeFile.endsWith(".tex")){
                write = true;
              }else{
                System.out.println("Error: The file given for the output of the ParseTree is not the proper file type(expected file type: .tex).");
              }
            }else{
              System.out.println("Error: No path given for the ParseTree file.");
            }
          }
        }
        if(args[args.length-1].endsWith(".fs")){
          File file = new File(".", args[args.length-1]);
          FileReader source = new FileReader(file);
          Parser.parse(source, verbose, write, treeFile);
        }else{
          System.out.println("Error: The source file given is not the proper file type(expected file type: .fs).");
        }
      }else{
        System.out.println("Error: No arguments given.");
      }
    }
}