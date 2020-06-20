import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class StackMachine {
    private final List<Token> tokens;
    private Stack<String> buffer = new Stack<>();
    private int counter = 0;

    int a,b,c;


    HashMap<String, Integer> varTable;
    HashMap<String, LinkedList> listTable = new HashMap<String, LinkedList>();
    Map<String, Integer> marksPosiions = new HashMap<String, Integer>();

    public StackMachine(List<Token> tokens) {
        this.tokens = tokens;
    }


    public void setVarTable (HashMap<String, Integer> table) {
        this.varTable = table;
    }
    public void setMarksPosiions (Map<String, Integer> marks) {
        this.marksPosiions = marks;
    }


    public int run () {
        System.out.println("\n\n****** Стек-Машина ******\n");
        Token token;

//        debugTable();
//        debugMark();

        while (counter < tokens.size()) {
            token = tokens.get(counter);

            if (token.getType() == Grammar.VAR) {
                buffer.push(token.getValue());
            } else if (token.getType() == Grammar.DIGIT) {
                buffer.push(token.getValue());
            } else if (token.getType() == Grammar.OP) {
                OPERATION(token.getValue());
            } else if (token.getType() == Grammar.ASSIGN_OP) {
                ASSIGN_OP();
            } else if (token.getType() == Grammar.COMPARISION_OP) {
                LOGIC_OPERATION(token.getValue());
            } else if (token.getValue() == "!F") {
                int pointValue = marksPosiions.get(tokens.get(counter-1).getValue());
                boolean fl = buffer.pop().equals("true");
                counter = fl ? counter : pointValue - 1;
            } else if (token.getValue() == "!") {
                int pointValue = marksPosiions.get(tokens.get(counter-1).getValue());
                counter = pointValue;
                counter--;
            } else if (token.getType() == Grammar.KEY_PRINTF) {
                System.out.println("ParLang >  " + getVarFromTable(buffer.pop()));
            } else if (token.getType() == Grammar.KEY_LIST) {
                LinkedList list = new LinkedList();
                counter++;
                listTable.put(tokens.get(counter).getValue(), list);
            } else if (token.getType() == Grammar.KEY_LIST_ADD) {
                String variable = buffer.pop();             // название списка
                LinkedList list = listTable.get(variable);  // этот список из таблиц
                counter++;
                int value = getVarFromTable(tokens.get(counter).getValue());    // значение переменной для записи в список
                list.add(value);    // помещаю в список
                listTable.put(variable, list); //возвращаю список обратно в таблицу
            } else if (token.getType() == Grammar.KEY_LIST_GET) {
                counter++;
                int id = getVarFromTable(tokens.get(counter).getValue());

                String variable = buffer.pop();
                LinkedList list = listTable.get(variable);  // этот список из таблиц
                int value = list.getByIndex(id);
                buffer.push(String.valueOf(value));
            }
            counter++;
        }

        debugTable();
        return 0;
    }


    private void ASSIGN_OP(){
        a = getVarFromTable(buffer.pop());
        varTable.put(buffer.pop(), a);
    }

    private void OPERATION(String op) {
        b = getVarFromTable(buffer.pop());
        a = getVarFromTable(buffer.pop());

        switch (op) {
            case "+":
                c = a + b;
                break;
            case "-":
                c = a - b;
                break;
            case "/":
                c = a / b;
                break;
            case "*":
                c = a * b;
                break;
        }
        buffer.push(String.valueOf(c));
    }

    private void LOGIC_OPERATION(String op) {
        boolean flag = false;
        b = getVarFromTable(buffer.pop());
        a = getVarFromTable(buffer.pop());

        switch (op) {
            case "<":
                flag = a < b;
                break;
            case ">":
                flag = a > b;
                break;
            case "==":
                flag = a == b;
                break;
            case "!=":
                flag = a != b;
                break;
            case "<=":
                flag = a <= b;
                break;
            case ">=":
                flag = a >= b;
                break;
        }
        buffer.push(String.valueOf(flag));
    }

    private int getVarFromTable (String value) {
        if (isDigit(value)) {
            return Integer.valueOf(value);
        } else {
            return varTable.get(value);
        }
    }


    private static boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void debugTable() {
        System.out.println("");
        System.out.printf("%-15s%-10s%n", "Переменная:", "Значение переменной:");
        for (Map.Entry entry : varTable.entrySet()) {
            // Выводим имя поля
            System.out.printf("%-15s", entry.getKey());
            // Выводим значение поля
            System.out.printf("%5s%n", entry.getValue());
        }
        System.out.println();
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
