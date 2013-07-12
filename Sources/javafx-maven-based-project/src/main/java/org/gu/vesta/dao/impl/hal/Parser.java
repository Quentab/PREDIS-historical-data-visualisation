package org.gu.vesta.dao.impl.hal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jack
 * 
 */
public class Parser {
    
     /** The logger */
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

    /** The regular expression of the separator */
    private String regexSeparator;

    /** The buffered reader */
    private BufferedReader reader;

    /**
     * Create a new instance of CsvParser
     * @param file the file to parse
     * @param separator the csv separator
     * @param startLineIndex the start line index
     */
    public Parser(File file, char separator, int startLineIndex) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            throw new RuntimeException("Initialization error", e);
        }
        if (startLineIndex < 0) {
            throw new IllegalArgumentException("Start line index is negative");
        }
        switch (separator) {
            case ' ': {
                regexSeparator = "\\s";
                break;
            }
            case ',': {
                regexSeparator = "[,]";
                break;
            }
            case ';': {
                regexSeparator = "[;]";
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported csv separator");
            }
        }
        String line;
        for (int i = 0; i < startLineIndex; i++) {
            try {
                line = reader.readLine();
            } catch (Exception e) {
                throw new RuntimeException("Parsing error...");
            }
            if (line == null) {
                throw new RuntimeException("Start line index inferior to line count");
            }
        }
    }

    /**
     * Get the tokens of the next line
     * @return the tokens of the next line, or null if there is no more line
     */
    public String[] getNextLineTokens() {
        String[] retValue = null;
        try {
            String line = reader.readLine();
            if (line != null) {
                if (line.trim().isEmpty()) {
                    retValue = getNextLineTokens();
                }
                retValue = line.split(regexSeparator);
            }
        } catch (Exception e) {
            throw new RuntimeException("Parsing error...");
        }
        return retValue;
    }

    /**
     * Close the parser
     */
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        }
    }
}
    

