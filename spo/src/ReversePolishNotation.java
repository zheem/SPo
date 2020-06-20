import java.util.*;

public class ReversePolishNotation {
    Map<String, Integer> marksPosiions = new HashMap<String, Integer>();

    private final List<Token> tokens;
    public List<Token> operands = new ArrayList<>();
    public List<Token> variables = new ArrayList<>();
    public List<Token> result = new ArrayList<>();


    Token IfForValue = null;
    private Stack<Token>  ifWhileStack = new Stack<>();
    int isWhileCounter = 0;
    int isIfCounter = 0;
    boolean printFlag = false;

    int markCunter = 1;
    int markPositioinCounter = 0;

    public List<Grammar> op = new ArrayList<>();
    Stack<Token> stack = new Stack<>();

    public ReversePolishNotation(List<Token> tokens) {
        this.tokens = tokens;

        op.add(Grammar.OP);
        op.add(Grammar.ROUND_CLOSE_BRACKET);
        op.add(Grammar.ROUND_OPEN_BRACKET);
        op.add(Grammar.COMPARISION_OP);
        op.add(Grammar.ASSIGN_OP);
    }

    public List<Token> getTokens() {
        return tokens;
    }
    public Map<String, Integer> getmMrksPosiions() {
        return marksPosiions;
    }

    public int GetPriority (Token token) {
        switch (token.getValue()) {
            case "=":
                return 0;

            case "*":
            case "/":
                return 4;

            case ">":
            case "<":
                return 3;

            case "+":
            case "-":
                return 2;

            default:
                return 1;
        }
    }

    public List<Token> translate () {
        for (Token token : tokens) {

            if (token.getType().equals(Grammar.KEY_DATA_TYPE)) {
                continue;
            }

            if (token.getType().equals(Grammar.KEY_PRINTF)) {
                printFlag = true;
                continue;
            }

            if (token.getType().equals(Grammar.SEMICOLON)) {
                while (stack.size() > 0)
                    addToken(stack.pop());

                if (printFlag){
                    addToken(new Token(Grammar.KEY_PRINTF, "printf"));
                    printFlag = false;
                }

                continue;
            }

            if (token.getType().equals(Grammar.KEY_WHILE) || token.getType().equals(Grammar.KEY_IF)) {
                Token tk;

                if (token.getType().equals(Grammar.KEY_WHILE)){
                    tk = new Token(Grammar.KEY_WHILE, "P" + markCunter);
                    isWhileCounter++;
                } else {
                    tk = new Token(Grammar.KEY_IF, "P" + markCunter);
                    isIfCounter++;
                }


                marksPosiions.put("P" + markCunter, markPositioinCounter);
                markCunter++;
                IfForValue = tk;

                ifWhileStack.push(tk);

                continue;
            }

            if (token.getType().equals(Grammar.FIGURE_OPEN_BRACKET)) {

                if ((isWhileCounter > 0) || (isIfCounter > 0)) {
                    addToken(IfForValue);

                    Token m2 = new Token("!F");
                    addToken(m2);
                }

                continue;
            }

            if (token.getType().equals(Grammar.FIGURE_CLOSE_BRACKET)) {
                if (isWhileCounter > 0) {
                    if (ifWhileStack.peek().getType().equals(Grammar.KEY_WHILE)) {
                        Token m1 = new Token(Grammar.KEY_WHILE, "P" + markCunter); //метка РХ с типом KEY_WHILE
                        Token m2 = new Token(Grammar.KEY_WHILE,"!"); //метка ! с типом KEY_WHILE

                        while (stack.size() > 0)
                            addToken(stack.pop());

                        addToken(m1);
                        addToken(m2);

//                        System.out.println("Выгружаем в таблицу: " + "P" + markCunter + ", " + ifWhileStack.peek().getValue());

                        // обязательно идет после addtoken (счетчик сдвигатся)
                        marksPosiions.put("P" + markCunter, marksPosiions.get(ifWhileStack.peek().getValue()));
                        marksPosiions.put(ifWhileStack.peek().getValue(), markPositioinCounter);


                        markCunter++; // сдвиг счетчика меток
                        isWhileCounter--;
                    }
                }

                if (isIfCounter > 0) {
                    if (ifWhileStack.peek().getType().equals(Grammar.KEY_IF)) {
                        while (stack.size() > 0)
                            addToken(stack.pop());
                        // обязательно идет после addtoken (счетчик сдвигатся)
                        marksPosiions.put(ifWhileStack.peek().getValue(), markPositioinCounter);


                        markCunter++; // сдвиг счетчика меток
                        isIfCounter--;
                    }
                }

                ifWhileStack.pop(); // либо так, либо объединить через if-else KEY_IF и KEY_WHILE проверки выше
                continue;
            }




            if (op.contains(token.getType())) {
                if ((stack.size() > 0) && (token.getType() != Grammar.ROUND_OPEN_BRACKET)) {
                    if (token.getType() == Grammar.ROUND_CLOSE_BRACKET) {
                        Token s = stack.pop();
                        while (s.getType() != Grammar.ROUND_OPEN_BRACKET) {
                            addToken(s);
                            s = stack.pop();
                        }
                    } else {
                        if (GetPriority(token) > GetPriority(stack.peek())) {
                            stack.add(token);
                        } else {
                            while (stack.size() > 0 && GetPriority(token) <= GetPriority(stack.peek()))
                                addToken(stack.pop());
                            stack.add(token);
                        }
                    }
                }
                else
                    stack.add(token);
            }
            else
                addToken(token);
        }
        if (stack.size() > 0)
            for (Token token : stack)
                addToken(token);

//        debugMark();

        for (Token token : result) {
            System.out.print(token.getValue()+ " ");
        }

        return result;
    }


    public  void returnAllOperansToResult() {
        for (Token token : operands) {
            addToken(token);
            operands.remove(token);
        }
    }

    public void addToken (Token token) {
        result.add(token);
        markPositioinCounter++;
    }

    private void debugMark() {
        System.out.println("");
        System.out.printf("%-10s%-10s%n", "метка", "значение");
        for (Map.Entry entry : marksPosiions.entrySet()) {
            // Выводим имя поля
            System.out.printf("%-7s", entry.getKey());
            // Выводим значение поля
            System.out.printf("%5s%n", entry.getValue());
        }
        System.out.println();
    }
}