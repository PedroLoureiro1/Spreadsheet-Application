package xxl;

import java.util.ArrayList; 

public class User{

    private String _name = "root";
    private ArrayList<Spreadsheet> _SpreadsheetList = new ArrayList<Spreadsheet>(); 

    public User(String name){
        _name = name;
    }

    public void insertSpreadsheet(Spreadsheet spreadsheet){
        _SpreadsheetList.add(spreadsheet);
    }
}