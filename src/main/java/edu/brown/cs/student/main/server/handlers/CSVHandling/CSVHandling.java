package edu.brown.cs.student.main.server.handlers.CSVHandling;

import java.util.List;

public class CSVHandling {

    private List<List<String>> parsedData;

    public CSVHandling(List<List<String>> parsedData) {
        this.parsedData = parsedData;
    }

    public boolean isCSVLoaded() {
        return this.parsedData != null;
    }

    public void setParsedData(List<List<String>> parsedData) {
        this.parsedData = parsedData;
    }

    public List<List<String>> getParsedData() {
        List<List<String>> parsedDataCopy = this.parsedData;
        return parsedDataCopy;
    }
}
