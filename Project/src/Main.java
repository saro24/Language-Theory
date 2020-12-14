import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


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
  public static ParseTree root;
    

    /**
    *Takes as input the file to be read and uses the generated Jflex code 
    * to output all the tokens that appear in the file while ignoring the comments
    * @param args a file of code FORTR-S and options for the execution
    *
    * @throws java.io.IOException if the file given doesn't exist
    */
    public static void main(String[] args) throws java.io.IOException{
      if(args.length > 0){
        boolean execution = false;
        boolean write = false;
        String outputFile = "";
        //Check which options have been given from the terminal
        for(int i = 0; i< args.length-1;i++){
          if(args[i].equals("-exec")){
            execution = true;
          }else if(args[i].equals("-o")){
            if(i < args.length-2){
              outputFile = args[i+1];
              //Verify that the name of the file for the ParseTree is the right type
              if(outputFile.endsWith(".ll")){
                write = true;
              }else{
                System.out.println("Error: The file given for the output is not the proper file type(expected file type: .ll).");
              }
            }else{
              System.out.println("Error: No path given for the output file.");
            }
          }
        }
        //Verify that the name of the file containing the code is the right type
        if(args[args.length-1].endsWith(".fs")){
          File file = new File(".", args[args.length-1]);
          FileReader source = new FileReader(file);
          root = Parser.parse(source);
          LlvmGenerator llvm = new LlvmGenerator(); 
          llvm.PROGRAM(root);
          if(execution){
            System.out.println("Execute");
          }else if(write){
            File output = new File(".", outputFile);
            output.createNewFile();
            FileWriter writer = new FileWriter(output);
            for(int i =0 ; i< llvm.stack.size() ; i ++ ) { 
              writer.write(llvm.stack.get(i).equation) ;
            }
             writer.close();
          }else{
            for(int i =0 ; i< llvm.stack.size() ; i ++ ) { 
              System.out.print(llvm.stack.get(i).equation);
            }
          }
        }else{
          System.out.println("Error: The source file given is not the proper file type(expected file type: .fs).");
        }
      }else{
        System.out.println("Error: No arguments given.");
      }
    }
}