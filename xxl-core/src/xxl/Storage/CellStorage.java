package xxl.Storage;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import xxl.Cell;

public class CellStorage implements Serializable{
    
    private Map <String , Cell> _map;//= new TreeMap<String, Cell>();

    public CellStorage(int line, int column){
        _map = new TreeMap<String,Cell>();
        for(int ixLine = 1; ixLine <= line; ixLine++){
            for(int ixColumn = 1; ixColumn <= line; ixColumn++){
                insertCell(new Cell(ixLine, ixColumn));
            }
        }
    }


    /** 
    * receieves the cell and insrts it on the structure(map)
    * 
    * @param cell 
    */
    public void insertCell(Cell cell){
        _map.put(cell.toStringReference(), cell);
    }
    
    /** 
    * @param string with the reference of the cell 
    */
    public Cell searchCell(String string){
        return _map.get(string);
    }

    /** 
    * @param string with the reference of the cell 
    */    
    public void deleteCell(String string){
        _map.put(string, null);
    }

    public void insertCellContent(int line, int column, Cell cell){
        String cutBufferCellReference = line + ";" + column;
        _map.put(cutBufferCellReference, cell);
    }

    public void switchCellContent(Cell c1, Cell c2){
        c2.setContent(c1.showCellContent());
        c2.setContentString(c1.getContentString());
    }

    public Cell getNextHorizontalCell(Cell c1){
        return searchCell(c1.nextHorizontalCellReference());
    }

    public Cell getNextVerticalCell(Cell c1){
        return searchCell(c1.nextVerticalCellReference());
    }

    public void deleteCellContent(String string){
        if(searchCell(string) != null){
            searchCell(string).setContent(null);
        }
    }
}