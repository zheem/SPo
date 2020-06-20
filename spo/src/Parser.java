

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Parser {

    private final List<Token> tokens;
    private int counter = 0;
    private VariablesTable vTable;
    HashMap<String, Integer> varTable = new HashMap<String, Integer>();

    boolean openRoundBracketFound = false;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        vTable = new VariablesTable();
    }

    public HashMap<String, Integer> getVarTable () {
        return varTable;
    }

    public boolean lang() throws LangParseException {

        try {
            while(tokens.size() > counter) {
                expr();
            }

            System.out.println("****** Успешно! ******");
            return true;
        } catch (LangParseException e) {
            System.out.println("****** В коде ошибка ******");
            System.out.println(e);
            System.out.println(tokens.size());
            System.out.println(counter);
            return false;
        }
    }

    private void expr() throws LangParseException {
        value_expr();
    }

    private  void variableCreation() throws LangParseException {
        KEY_DATA_TYPE();
        VAR();
        ASSIGN_OP();

        int step = counter;
        try {
            variableValue();
            SEMICOLON();
        } catch (LangParseException e) {
            counter = step;
            listGet();
        }
    }
    private  void variableAssigment() throws LangParseException {
        VAR();
        ASSIGN_OP();
        int step = counter;
        try {
            variableValue();
            SEMICOLON();
        } catch (LangParseException e) {
            counter = step;
            listGet();
        }
    }

    private void listCreation () throws LangParseException {
        KEY_LIST();
        VAR();
        SEMICOLON();
    }

    private  void listAdd () throws LangParseException {
        VAR();
        KEY_LIST_ADD();
        value();
        SEMICOLON();
    }

    private  void listGet () throws LangParseException {
        VAR();
        KEY_LIST_GET();
        value();
        SEMICOLON();
    }

    private  void whileExpression () throws LangParseException {
        KEY_WHILE();
        ROUND_OPEN_BRACKET();
        value();
        COMPARISION_OP();
        value();
        ROUND_CLOSE_BRACKET();
        FIGURE_OPEN_BRACKET();
        int step2 = counter;

        try {
            while (!(tokens.get(counter).getType().equals(Grammar.FIGURE_CLOSE_BRACKET)))
                value_expr();

            FIGURE_CLOSE_BRACKET();
        } catch (LangParseException e) {
            counter = step2;
            FIGURE_CLOSE_BRACKET();
        }
    }

    private void printF() throws LangParseException {
        KEY_PRINTF();
        ROUND_OPEN_BRACKET();
        value();
        ROUND_CLOSE_BRACKET();
        SEMICOLON();
    }

    private void functionCreation () throws LangParseException {
        KEY_DATA_TYPE();
        VAR();
        ROUND_OPEN_BRACKET();

        int step2 = counter;
        try {
            while (!(tokens.get(counter).getType().equals(Grammar.ROUND_CLOSE_BRACKET))){
                KEY_DATA_TYPE();
                variableValue();
                // проеврка на создание параметров
            }

            ROUND_CLOSE_BRACKET();
        } catch (LangParseException e) {
            counter = step2;
            ROUND_CLOSE_BRACKET();
        }
    }

    private void forLoop () throws LangParseException {
        KEY_FOR();
        ROUND_OPEN_BRACKET();

        variableCreation();

        VAR();
        COMPARISION_OP();
        value();
        SEMICOLON();

        //вот тут поправить... все же хочу i = i + 1;
        VAR();
        ASSIGN_OP();
        value();
        ROUND_CLOSE_BRACKET();

        FIGURE_OPEN_BRACKET();
        int step2 = counter;
        try {
            while (!(tokens.get(counter).getType().equals(Grammar.FIGURE_CLOSE_BRACKET)))
                value_expr();

            FIGURE_CLOSE_BRACKET();
        } catch (LangParseException e) {
            counter = step2;
            FIGURE_CLOSE_BRACKET();
        }
    }

    private void ifCondition() throws LangParseException {
        KEY_IF();
        ROUND_OPEN_BRACKET();
        value();
        int step1 = counter;

        // сделать поддержку `>=` и `<=`
        try {
            COMPARISION_OP();
        } catch (LangParseException e) {
            counter = step1;
            ASSIGN_OP();
            ASSIGN_OP();
        }
        value();
        ROUND_CLOSE_BRACKET();
        FIGURE_OPEN_BRACKET();
        int step2 = counter;

        try {
            while (!(tokens.get(counter).getType().equals(Grammar.FIGURE_CLOSE_BRACKET)))
                value_expr();

            FIGURE_CLOSE_BRACKET();
        } catch (LangParseException e) {
            counter = step2;
            FIGURE_CLOSE_BRACKET();
        }
    }

    private  void value_expr () throws LangParseException {
        int step = counter;

        try {
            variableCreation();

            //System.out.println("trying to create: " + tokens.get(step).getValue() +  " " + tokens.get(step+1).getValue());
            boolean create = vTable.addVariable(new VariablesTable.tVariable(tokens.get(step).getValue(), tokens.get(step+1).getValue()));
            varTable.put(tokens.get(step+1).getValue(), 0); // новый вариант (упрощенный)

            if (!create) {
                System.out.println("VARIABLE ALREADY EXIST!");
                throw new LangParseException("VARIABLE ALREADY EXIST!");
            }
        } catch (LangParseException e) {
            counter = step;
            try {
                variableAssigment();

                boolean assign = vTable.checkIfValueExist(new VariablesTable.tVariable(tokens.get(step).getValue()));

                if (!assign) {
                    System.out.println("VARIABLE doesn't EXIST!");
                    throw new LangParseException("VARIABLE doesn't EXIST!");
                }

            } catch (LangParseException e2) {
                counter = step;
                try {
                    ifCondition();
                } catch (LangParseException e3) {
                    counter = step;
                    try {
                        forLoop();
                    } catch (LangParseException e4){
                        counter = step;

                        try {
                            printF();
                        } catch (LangParseException e5) {
                            counter = step;

                            try {
                                whileExpression();
                            } catch (LangParseException e6) {
                                counter = step;

                                try {
                                    listCreation();
                                } catch (LangParseException e7) {
                                    counter = step;

                                    listAdd();
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private Token match() throws LangParseException {
        Token token = null;

        if (counter < tokens.size()) {
//            while (tokens.get(counter).getType().equals(LexemType.ROUND_OPEN_BRACKET) || tokens.get(counter).getType().equals(LexemType.ROUND_CLOSE_BRACKET))
//                counter++;

            token = tokens.get(counter);
            counter++;
        } else throw new LangParseException("FATAL ERROR: Закончились токены проверки...");

        return token;
    }

    private  void varValue () throws LangParseException {
        int tmpCounter = counter;

        try {
            value();
            SEMICOLON();
            System.out.println("Trying to exit");
        } catch (LangParseException e) {
            counter = tmpCounter;
            try {
                value();
            } catch (LangParseException e2) {
                counter = tmpCounter;
                variableValue();
                OP();
                variableValue();
            }
        }
    }

    private  void brackets () throws LangParseException {
        ROUND_OPEN_BRACKET();
        variableValue();
        ROUND_CLOSE_BRACKET();
    }

    private void skipBracket () {
        while (tokens.get(counter).getType().equals(Grammar.ROUND_OPEN_BRACKET) || tokens.get(counter).getType().equals(Grammar.ROUND_CLOSE_BRACKET))
            counter++;
    }

    private void variableValue() throws LangParseException {

        skipBracket();
        value();
        while (tokens.size() > counter) {
            skipBracket();
            if (tokens.get(counter).getType().equals(Grammar.SEMICOLON))
                break;
            skipBracket();
            OP();
            skipBracket();
            value();
        }



    }


    public boolean checkBrackets () {
        Stack<Token> stack = new Stack<>();
        Token prevToken = tokens.get(0);
        for (Token token : tokens) {
            Grammar type = token.getType();

            if (type.equals(Grammar.ROUND_OPEN_BRACKET))
            {
                stack.push(token);
                if ((prevToken.getType().equals(Grammar.DIGIT)) || (prevToken.getType().equals(Grammar.VAR))) {
                    return false;
                }
            }
            if (type.equals(Grammar.ROUND_CLOSE_BRACKET)) {
                if (stack.size() <= 0)
                    return false;
                if (stack.peek().getType().equals(Grammar.ROUND_OPEN_BRACKET)) {
                    stack.pop();
                }
            }
            if (type.equals(Grammar.ROUND_CLOSE_BRACKET))
            {
                if (prevToken.getType().equals(Grammar.OP)) {
                    return false;
                }
            }
            prevToken = token;
        }

        if (stack.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private void value() throws LangParseException {
        int newCount = counter;

        try {
            VAR();
        } catch (LangParseException e){
            counter = newCount;
            DIGIT();
        }
    }

    private void matchToken(Token token, Grammar type) throws LangParseException {
        if (!token.getType().equals(type)) {
            throw new LangParseException("ERROR: " + token.getType()
                    + " expected but "
                    + token.getType().name() + ": '" + token.getValue()
                    + "' found");
        }
    }

    private void VAR() throws LangParseException {
        matchToken(match(), Grammar.VAR);
    }
    private void KEY_DATA_TYPE() throws LangParseException {
        matchToken(match(), Grammar.KEY_DATA_TYPE);
    }
    private void KEY_IF() throws LangParseException {
        matchToken(match(), Grammar.KEY_IF);
    }
    private void ROUND_OPEN_BRACKET() throws LangParseException {
        matchToken(match(), Grammar.ROUND_OPEN_BRACKET);
    }
    private void ROUND_CLOSE_BRACKET() throws LangParseException {
        matchToken(match(), Grammar.ROUND_CLOSE_BRACKET);
    }
    private void FIGURE_OPEN_BRACKET() throws LangParseException {
        matchToken(match(), Grammar.FIGURE_OPEN_BRACKET);
    }
    private void FIGURE_CLOSE_BRACKET() throws LangParseException {
        matchToken(match(), Grammar.FIGURE_CLOSE_BRACKET);
    }
    private void COMPARISION_OP() throws LangParseException {
        matchToken(match(), Grammar.COMPARISION_OP);
    }
    private void SEMICOLON() throws LangParseException {
        matchToken(match(), Grammar.SEMICOLON);
    }
    private void KEY_FOR() throws LangParseException {
        matchToken(match(), Grammar.KEY_FOR);
    }
    private void KEY_WHILE() throws LangParseException {
        matchToken(match(), Grammar.KEY_WHILE);
    }
    private void KEY_ELSE() throws LangParseException {
        matchToken(match(), Grammar.KEY_FOR);
    }
    private void DIGIT() throws LangParseException {
        matchToken(match(), Grammar.DIGIT);
    }
    private void OP() throws LangParseException {
        matchToken(match(), Grammar.OP);
    }
    private void ASSIGN_OP() throws LangParseException {
        matchToken(match(), Grammar.ASSIGN_OP);
    }
    private void KEY_PRINTF() throws LangParseException {
        matchToken(match(), Grammar.KEY_PRINTF);
    }
    private void DOUBLE_QUOTES() throws LangParseException {
        matchToken(match(), Grammar.DOUBLE_QUOTES);
    }
    private  void KEY_LIST () throws LangParseException {
        matchToken(match(), Grammar.KEY_LIST);
    }
    private  void KEY_LIST_ADD () throws LangParseException {
        matchToken(match(), Grammar.KEY_LIST_ADD);
    }

    private  void KEY_LIST_GET () throws LangParseException {
        matchToken(match(), Grammar.KEY_LIST_GET);
    }

}
