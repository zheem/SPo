import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class LangUI {
    public static void main(String[] args) {
        System.out.println();
        String file = "";
        try {
            File myObj = new File("myProgram.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try(FileReader reader = new FileReader("myProgram.txt"))
        {
            // читаем посимвольно
            int c;
            while((c=reader.read())!=-1){
                    file = file + (char)c;
            }

            file = file.replaceAll("\n","");

            Lexer lexer = new Lexer(file);
            List<Token> tokens = lexer.getTokens();

            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                String a = token.getValue();
            }

            Parser parser = new Parser( lexer.getTokens() );
            if (parser.lang()) {

                if (!parser.checkBrackets()) {
                    System.out.println("ERROR IN BRACKETS!");
                }

                ReversePolishNotation rpNotation = new ReversePolishNotation(lexer.getTokens());
                List<Token> tk = rpNotation.translate();
                StackMachine machine = new StackMachine(tk);
                machine.setVarTable(parser.getVarTable());
                machine.setMarksPosiions(rpNotation.getmMrksPosiions());
                machine.run();
            }

        }

        catch(IOException | LangParseException ex){
            System.out.println("Программа сломалась: "  + ex.getMessage());
        }
    }
}
