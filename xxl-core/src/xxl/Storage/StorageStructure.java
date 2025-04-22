package xxl.Storage;

import java.util.Map;
import java.util.TreeMap;

import xxl.Spreadsheet;

public class StorageStructure{
    
    private Map <String, Spreadsheet> _spreadSheetMap = new TreeMap<String, Spreadsheet>();

    public void insertSpreadsheet(Spreadsheet s1){
            _spreadSheetMap.put(s1.getFileName(), s1);
    }

    public Spreadsheet searchSpreadsheet(String name){
        return _spreadSheetMap.get(name);
    }

    public void deleteSpreadsheet(String name){
        _spreadSheetMap.remove(name);
    }
}