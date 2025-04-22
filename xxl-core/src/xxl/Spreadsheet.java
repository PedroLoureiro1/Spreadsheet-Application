package xxl;

import xxl.Storage.CellStorage;
// FIXME import classes
import xxl.exceptions.UnrecognizedEntryException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Class representing a spreadsheet.
 */
public class Spreadsheet implements Serializable {

    private int _nLines;
    private int _nColumns;
    private String _fileName = "";
    private CellStorage _cellStorage;
    private boolean _dirty = true;
    private CutBuffer _cutBuffer;

    @Serial
    private static final long serialVersionUID = 202308312359L;

    // FIXME define attributes
    // FIXME define contructor(s)
    // FIXME define methods

    /**
     * Insert specified content in specified range.
     *
     * @param rangeSpecification
     * @param contentSpecification
     */
    public void insertContents(String rangeSpecification, String contentSpecification) throws UnrecognizedEntryException /* FIXME maybe add exceptions */ {
        Cell c1 = new Cell(0,0);
        CellContent c2 = new CellContent();
        Cell cell = c1.create(rangeSpecification, contentSpecification);
        CellContent c = c2.create(contentSpecification);
        cell.insertCellContent(c);
        _cellStorage.insertCell(cell);
    }

    public Spreadsheet(int nLines, int nColumns){
        _nLines = nLines;
        _nColumns = nColumns;
        _cellStorage = new CellStorage(_nLines, _nColumns);
        _cutBuffer = new CutBuffer(nLines, nColumns);    
    }

    public void insertCell(Cell cell){
        _cellStorage.insertCell(cell);
        _dirty = true;
    }
    /** 
    *  
    * @param cellReference 
    * 
    * @return a string containing the cell reference and the content of the cell 
    */
    public String searchRange(String rangeReference) throws UnrecognizedEntryException{

        String input[] = rangeReference.split(":");


        if(input.length == 1){
            if ((_cellStorage.searchCell(input[0]) == null)){
            throw new UnrecognizedEntryException(input[0]);
            }
            Cell c1 = _cellStorage.searchCell(rangeReference);
            if (c1.getContent() == null)
                return c1.toStringWithoutContent();
            
            return c1.toString(_cellStorage);
        }

        if (input.length == 2){
            if ((_cellStorage.searchCell(input[0]) == null) || (_cellStorage.searchCell(input[1]) == null)){
                throw new UnrecognizedEntryException(rangeReference);
            }

            if(_cellStorage.searchCell(input[0]).getLine() != _cellStorage.searchCell(input[1]).getLine() &&
            _cellStorage.searchCell(input[0]).getColumn() != _cellStorage.searchCell(input[1]).getColumn()){
                throw new UnrecognizedEntryException(rangeReference);
            }

            Range r1 = new Range(input[0], input[1], _cellStorage);
            return r1.iterateRange(_cellStorage);
        }

        return null;

    }
    /**
     * @return the name of the file containing the spreadsheet
     */
    public String getFileName(){
        return _fileName;
    }
    
    public void setFileName (String name){
        _fileName = name;
    }


    /** 
    * verifys if changes were made to the spreadsheet
    * 
    */
    public boolean isDirty(){
        return _dirty;
    }

    public void resetDirty(){
        _dirty = false;
    }

    public void setDirty(){
        _dirty = true;
    }

    public void copy(String rangeReference) throws UnrecognizedEntryException{
        String input[] = rangeReference.split(":");

        if(input.length == 1){
            if ((_cellStorage.searchCell(input[0]) == null)){
                throw new UnrecognizedEntryException(input[0]);
            }
            Cell c1 = _cellStorage.searchCell(rangeReference);
            _cutBuffer.copyCell(c1, rangeReference, _cellStorage);
        }

        if(input.length == 2){
            if ((_cellStorage.searchCell(input[0]) == null) || (_cellStorage.searchCell(input[1]) == null)){
                throw new UnrecognizedEntryException(rangeReference);
            }

            if(_cellStorage.searchCell(input[0]).getLine() != _cellStorage.searchCell(input[1]).getLine() &&
            _cellStorage.searchCell(input[0]).getColumn() != _cellStorage.searchCell(input[1]).getColumn()){
                throw new UnrecognizedEntryException(rangeReference);
            }

            _cutBuffer.copyRange(input[0], input[1], rangeReference, _cellStorage);
        }
    }

    public void delete(String rangeReference) throws UnrecognizedEntryException{
        _dirty = true;
        String input[] = rangeReference.split(":");
        

        if(input.length == 1){
            _cellStorage.deleteCellContent(rangeReference);
        }
        
        if(input.length == 2){
            Range range = new Range(input[0], input[1], _cellStorage);
            if(range.getOrientation()){
                while(range.getCurrentCell() != range.getLastCell()){
                    _cellStorage.deleteCellContent(range.getCurrentCell().toStringReference());
                    range.setCurrentCell(range.getNextHorizontalCell(_cellStorage).toStringReference(), _cellStorage);
                }
                _cellStorage.deleteCellContent(range.getLastCell().toStringReference());
            }
            
            if(!range.getOrientation()){
                while(range.getCurrentCell() != range.getLastCell()){
                    _cellStorage.deleteCellContent(range.getCurrentCell().toStringReference());
                    range.setCurrentCell(range.getNextVerticalCell(_cellStorage).toStringReference(), _cellStorage);
                }
                _cellStorage.deleteCellContent(range.getLastCell().toStringReference());
            }
        }
    }

    public void cut(String rangeReference) throws UnrecognizedEntryException{
        copy(rangeReference);
        delete(rangeReference);
    }

    public void paste(String rangeReference) throws UnrecognizedEntryException{
        _dirty = true;
        String input[] = rangeReference.split(":");

        if(input.length == 1){
            _cutBuffer.pasteCell(input[0], _cellStorage, _nLines, _nColumns);
        }

        if(input.length == 2){
            _cutBuffer.pasteRange(input[0], rangeReference, _cellStorage, _nLines, _nColumns);
        }
    }

    public String visualizeCutBuffer(){
        return _cutBuffer.visualize();
    }

    public void insert(String rangeReference, String content) throws UnrecognizedEntryException{
        _dirty = true;
        String input[] = rangeReference.split(":");
        Cell auxCell = new Cell(2,2);
        CellContent auxCellContent = new CellContent();
        auxCell.setContent(auxCellContent.create(content));
        auxCell.setContentString(content);
        
        if(input.length == 1){
            _cellStorage.switchCellContent(auxCell, _cellStorage.searchCell(rangeReference));
        }

        if(input.length == 2){
            Range range = new Range(input[0], input[1], _cellStorage);
            if(range.getOrientation()){
                while(range.getCurrentCell() != range.getLastCell()){
                    _cellStorage.switchCellContent(auxCell, range.getCurrentCell()); 
                    range.setCurrentCell(range.getNextHorizontalCell(_cellStorage).toStringReference(), _cellStorage);
                }
                _cellStorage.switchCellContent(auxCell, range.getLastCell());
            }
            
            if(!range.getOrientation()){
                while(range.getCurrentCell() != range.getLastCell()){
                    _cellStorage.switchCellContent(auxCell, range.getCurrentCell()); 
                    range.setCurrentCell(range.getNextVerticalCell(_cellStorage).toStringReference(), _cellStorage);
                }
                _cellStorage.switchCellContent(auxCell, range.getLastCell());          
            }    
        }
    }

    public String searchValue(String value){
        String cellContentString;
        String cellReference;
        String searchResult = "";
        for(int ixLine = 1; ixLine <= _nLines; ixLine++){
            for(int ixColumn = 1; ixColumn <= _nColumns; ixColumn++){
                cellReference = ixLine + ";" + ixColumn;
                cellContentString = _cellStorage.searchCell(cellReference).toString(_cellStorage);
                String args[] = cellContentString.split("[|=]");

                if(args[1].equals(value)){
                    searchResult += cellContentString + "\n";
                }
            }
        }
        if (searchResult.length() > 0 && searchResult.charAt(searchResult.length() - 1) == '\n') 
            searchResult = searchResult.substring(0, (searchResult.length() - 1));
        return searchResult;
    }

    public String searchFunction(String functionName){
        String cellReference;
        String searchResult = "";
        for(int ixLine = 1; ixLine <= _nLines; ixLine++){
            for(int ixColumn = 1; ixColumn <= _nColumns; ixColumn++){
                cellReference = ixLine + ";" + ixColumn;
                if(_cellStorage.searchCell(cellReference).getContent() instanceof FunctionalContent){
                    FunctionalContent functionalContent = (FunctionalContent) _cellStorage.searchCell(cellReference).getContent();
                    if(functionalContent.getFunctionName().contains(functionName)){
                        searchResult += _cellStorage.searchCell(cellReference).toString(_cellStorage) + "\n";
                    }
                }
            }
        }
        if (searchResult.length() > 0 && searchResult.charAt(searchResult.length() - 1) == '\n') 
            searchResult = searchResult.substring(0, (searchResult.length() - 1));
        return sortLine(searchResult);
    }


    
    public String sortLine(String input){ 
        String[] lines = input.split("\n");

            // Define a custom comparator
            Comparator<String> lineComparator = new Comparator<String>() {
                @Override
                public int compare(String line1, String line2) {
                    // Split each line into its components
                    String[] parts1 = line1.split("[;|]");
                    String[] parts2 = line2.split("[;|]");

                    // Compare function names
                    int functionNameComparison = parts1[2].compareTo(parts2[2]);
                    if (functionNameComparison != 0) {
                        return functionNameComparison;
                    }

                    // Compare line numbers
                    int lineComparison = Integer.compare(Integer.parseInt(parts1[0]), Integer.parseInt(parts2[0]));
                    if (lineComparison != 0) {
                        return lineComparison;
                    }

                    // Compare column numbers
                    return Integer.compare(Integer.parseInt(parts1[1]), Integer.parseInt(parts2[1]));
                }
            };

            // Sort the lines using the custom comparator
            Arrays.sort(lines, lineComparator);

            // Join the sorted lines back into a single string
            String sortedString = String.join("\n", lines);
            return sortedString;
        }
}
