import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

public class Lexer {

    private  String rawInput;

    public Lexer(String rawInput) {
        this.rawInput = rawInput;
    }

    public List<Token> getTokens() {
        this.rawInput = this.rawInput.replaceAll("\n","");

        List<Token> listOfTokens = new ArrayList<>();
        int currentIndex = 0;
        int currentIndexFrom = 0;
        boolean _isChecking = false;
        Grammar prevLexem = null;
        Grammar currentLexem = null;

        while (currentIndex < this.rawInput.length()) {
            String substr = this.rawInput.substring(currentIndexFrom, currentIndex + 1);
            currentLexem = null;

            if (substr.equals(" ")) {
                currentIndexFrom++;
                currentIndex++;
                continue;
            }

            for (Grammar lexem : Grammar.values()) {
                Matcher matcher = lexem.getPattern().matcher(substr);

                if (matcher.find()) {
                    currentLexem = lexem;

                    break;
                }
            }
            if (currentLexem != null) {

                prevLexem = currentLexem;
            }
            if (currentLexem != null && !_isChecking) {
                _isChecking = true;
            }

            if (_isChecking && currentLexem == null) {
                Token token = new Token(prevLexem, substr.substring(0, substr.length() - 1));
                listOfTokens.add(token);

                _isChecking = false;
                currentIndexFrom = currentIndex;
            } else {
                currentIndex++;
            }

            if (currentIndex >= this.rawInput.length()) {
                Token token = new Token(prevLexem, substr);
                listOfTokens.add(token);

                _isChecking = false;
                currentIndexFrom = currentIndex;
            }

        }


        return listOfTokens;
    }

    public List<Token> tokens() {
        List<Token> listOfTokens = new ArrayList<>();
        String[] substr = this.rawInput.split(" ");

        for (String str : substr) {
            for (Grammar lexem : Grammar.values()) {
                Matcher matcher = lexem.getPattern().matcher(str);
                boolean flag = false;

                if (matcher.find()) {
                    listOfTokens.add(new Token(lexem, str));
                    flag = true;
                }
                if (flag) break;
            }
        }
        if (listOfTokens.size() == 0)
            return Collections.emptyList();
        else
            return listOfTokens;
    }
}