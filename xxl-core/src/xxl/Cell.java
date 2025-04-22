package xxl;

import java.util.ArrayList;

import xxl.Storage.CellStorage;

import java.io.Serializable;

public class Cell implements Serializable{

    private Integer _line;
    private Integer _column;
    private CellContent _content;
    private String _contentString;
    private boolean _isStringContent;

    public Cell(Integer line, Integer column){
        _line = line;
        _column = column;
        _content = null;
    }
    /** 
    * receives a line from the imported file and through the reference 
    * in the first half of the line and creates a cell with the content
    * in the second part of the line
    * 
    * @param line
    */
    public Cell create(String pos, String content){
        String sArgs[] = pos.split(";");
        ArrayList<Integer> args = new ArrayList<Integer>(sArgs.length);
        
        for(int ix = 0; ix < sArgs.length; ix++){
            args.add(Integer.valueOf(sArgs[ix]));
        }

        Cell c1 = new Cell(args.get(0), args.get(1));
        c1._contentString = content;
        return c1;
    }

    public void insertCellContent(CellContent content){
        _content = content;
    }

    public void deleteCellContent(){
        _content = null;
    }

    public CellContent showCellContent(){
        return _content;
    }

    
    public String toString(CellStorage cellStorage){
        if(_content instanceof FunctionalContent){
            FunctionalContent functionalContent = (FunctionalContent) _content;
            _contentString = functionalContent.executeFunction(cellStorage);
            return this._line + ";" + this._column + "|" + _contentString;
        }
        
        if(_content instanceof ReferenceContent){
            ReferenceContent referenceContent = (ReferenceContent) _content;
            _contentString = referenceContent.updateValue(cellStorage);
            return this._line + ";" + this._column + "|" + _contentString;
        }

        return this._line + ";" + this._column + "|" + this._contentString;
    }
    /** 
    * @return a string only with the reference of the cell
    */
    public String toStringReference(){
        return this._line + ";" + this._column;
    }

    public String toStringWithoutContent(){
        return this._line + ";" + this._column + "|";
    }

    public String nextHorizontalCellReference(){
        return this._line + ";" + (this._column + 1);
    }

    public String nextVerticalCellReference(){
        return (this._line + 1) + ";" + this._column;
    }

    public int getColumn(){
        return _column;
    }

    public int getLine(){
        return _line;
    }
    
    public CellContent getContent(){
        return _content;
    }

    public String getContentString(){
        return _contentString;
    }

    public int getContentValue(){
        return _content.getContentValue();
    }

    public void setIsStringContent(){
        _isStringContent = true;
    }

    public boolean getIsStringContent(){
        return _isStringContent;
    }

    public void setContent(CellContent cellContent){
        _content = cellContent;
    }

    public void setContentString(String contentString){
        _contentString = contentString;
    }
    public void updateStringContent(CellStorage cellStorage){
        if(_content instanceof FunctionalContent){
            FunctionalContent functionalContent = (FunctionalContent) _content;
            _contentString = functionalContent.executeFunction(cellStorage);
        }
        
        if(_content instanceof ReferenceContent){
            ReferenceContent referenceContent = (ReferenceContent) _content;
            _contentString = referenceContent.updateValue(cellStorage);
        }
    }

    public void updateContentValue(){
        String Args[] = _contentString.split("=");
        this.getContent().setContentValueStr(Args[0]);
    }
}