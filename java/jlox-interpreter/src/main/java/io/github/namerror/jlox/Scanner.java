package io.github.namerror.jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.namerror.jlox.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>(); // handle reserved keywords
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    // tracking line numbers and characters along scanning
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner (String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // at the beginning of the next lexeme
            start = current;
            scanToken(); // scan the next token
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '/':
                // matching a comment, skip until end of line
                if (match('/')) {
                    while (peek()!='\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                break; // skip whitespaces

            case '\n':
                line++;
                break;

            case '"': string(); break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char advance() {
        return source.charAt(current++); // increment happens after the value is passed in
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current); // do not advance in peek
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current); // get the lexeme string for the token
        this.tokens.add(new Token(type, text, literal, line));
    }

    private void string() {
        while (peek()!='"' && !isAtEnd()) {
            if (peek()=='\n') line++;
            advance(); // within the string quotes ""
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string");
            return;
        }

        // second " closes, since the peek() before didn't advance
        advance();

        // get the string value, trim the " "
        String value = source.substring(start+1, current-1);
        addToken(STRING, value);
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z' ) ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void number() {
        while (isDigit(peek())) advance();

        // looking for floating point
        if (peek()=='.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));

    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance(); // scan entire identifier if it's alphanumeric

        // check if it's a reserved keyword
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type==null) type = IDENTIFIER; // reassign IDENTIFIER if it's not a keyword
        addToken(type);
    }

    private char peekNext() {
        // peek char after the next character
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current+1);
    }
}
