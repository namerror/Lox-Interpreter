package io.github.namerror.jlox;

/* What type of lexemes they represent */
public enum TokenType {
    // Single character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, STAR, SLASH,

    // One or two character tokens
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals: lexemes for literal values like number and string
    IDENTIFIER, STRING, NUMBER,

    // Keywords
    AND, OR, TRUE, FALSE,
    CLASS, FUN, RETURN, SUPER, THIS, VAR,
    IF, ELSE, WHILE, NIL, PRINT, FOR,

    EOF
}
