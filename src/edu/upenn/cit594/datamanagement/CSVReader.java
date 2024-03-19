package edu.upenn.cit594.datamanagement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 5130409650040L;
    private final CharacterReader reader;

    public CSVReader(CharacterReader reader) {
        this.reader = reader;}
    characterType Char_Type;
    enum State {Initial, Unquoted, Escaped, DoubleQuoted, CRLF}
    enum characterType {LF, CR, DoubleQuote, Comma, Text, EOF}
    

    private characterType detectcharacterType(int newChar){
        return switch (newChar) {
            default -> characterType.Text;
            case 10 -> characterType.LF;
            case 13 -> characterType.CR;
            case 34 -> characterType.DoubleQuote;
            case 44 -> characterType.Comma;
            case -1 -> characterType.EOF;};}

    public String[] readRow() throws IOException{
        List<String> stringList = new ArrayList<>();
        StringBuilder stringField = new StringBuilder();
        State currentState = State.Initial;
        boolean delimitComma = false;

        
        while (true){
            int newChar = reader.read();
            Char_Type = detectcharacterType(newChar);
            
            switch(currentState){
                case Initial:
                    switch (Char_Type){ 
                        case EOF:
                            if (delimitComma){
                                stringList.add("");
                                return stringList.toArray(new String[0]);
                            }else return null;
                        case LF:
                            stringList.add("");
                            return stringList.toArray(new String[0]);
                        case CR:
                            currentState = State.CRLF;
                            break;
                        case DoubleQuote:
                            currentState = State.Escaped;
                            break;
                        case Comma:
                            delimitComma = true;
                            stringList.add("");
                            break;
                        case Text:
                            currentState = State.Unquoted;
                            stringField.append((char) newChar);
                            break;}
                    break;

                case Unquoted:
                    switch (Char_Type){
                        case EOF, LF:
                            stringList.add(stringField.toString());
                            return stringList.toArray(new String[0]);
                        case CR:
                            currentState = State.CRLF;
                            break;
                        case Comma:
                            currentState= State.Initial;
                            delimitComma = true;
                            stringList.add(stringField.toString());
                            stringField.setLength(0);
                            break;
                        case Text:
                            stringField.append((char) newChar);
                            break;
                    }
                    break;

                case Escaped:
                    switch (Char_Type){
                        case Text,Comma,LF,CR:
                            stringField.append((char) newChar);
                            break;
                        case DoubleQuote:
                            currentState = State.DoubleQuoted;
                            break;}
                    break;

                case DoubleQuoted:
                    switch (Char_Type){
                        case EOF:
                            stringList.add(stringField.toString());
                            return stringList.toArray(new String[0]);
                        case LF:
                            if(stringField.charAt(stringField.length() -1) == '\r'){
                                stringField.deleteCharAt(stringField.length() -1);
                            }
                            stringList.add(stringField.toString());
                            return stringList.toArray(new String[0]);
                        case CR:
                            stringField.append((char) newChar);
                            break;
                        case DoubleQuote:
                            currentState = State.Escaped;
                            stringField.append((char) newChar);
                            break;
                        case Comma:
                            currentState=State.Initial;
                            stringList.add(stringField.toString());
                            stringField.setLength(0);
                            break;}
                    break;
                case CRLF:
                    switch (Char_Type) {
                        case LF:
                            stringList.add(stringField.toString());
                            return stringList.toArray(new String[0]);}
                    break;
            }}}}