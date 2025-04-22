package xxl;

import java.io.Serializable;

import xxl.Storage.CellStorage;

public class CutBuffer implements Serializable{
    
    private CellStorage _cutBufferStorage;
    private String _rangeReference;
    private Boolean _isHorziontal = false;
    private int _cutBufferSize  = 0;

    public CutBuffer(int nLines, int nColumns) {
        _cutBufferStorage = new CellStorage(nLines, nColumns);
    } 

    public void copyCell(Cell c1, String rangeReference, CellStorage spreadsheetStorage){
        if (_rangeReference != null)
            this.clearCutBuffer();

        _rangeReference =  rangeReference;
        c1.toString(_cutBufferStorage);
        _cutBufferStorage.insertCellContent(1,1,c1);
        _cutBufferSize = 1;
    }

    public void copyRange(String s1, String s2, String rangeReference, CellStorage spreadsheetStorage){
        int line = 1;
        int column = 1;
        if (_rangeReference != null)
            this.clearCutBuffer();
            
        _rangeReference = rangeReference;
        Range range = new Range(s1, s2, spreadsheetStorage);
        if(range.getOrientation()){
            _isHorziontal = true;
            while(range.getCurrentCell() != range.getLastCell()){
                range.getCurrentCell().toString(spreadsheetStorage);
                _cutBufferStorage.insertCellContent(line, column, range.getCurrentCell());
                range.setCurrentCell(range.getNextHorizontalCell(spreadsheetStorage).toStringReference(), spreadsheetStorage);
                column++;
            }
            range.getLastCell().toString(spreadsheetStorage);
            _cutBufferStorage.insertCellContent(line, column, range.getLastCell());
            _cutBufferSize = column;
        }
        if(!range.getOrientation()){
            _isHorziontal = false;
            while(range.getCurrentCell() != range.getLastCell()){
                range.getCurrentCell().toString(spreadsheetStorage);
                _cutBufferStorage.insertCellContent(line, column, range.getCurrentCell());
                range.setCurrentCell(range.getNextVerticalCell(spreadsheetStorage).toStringReference(), spreadsheetStorage);
                line++;
            }
            range.getLastCell().toString(spreadsheetStorage);
            _cutBufferStorage.insertCellContent(line, column, range.getLastCell());
            _cutBufferSize = line;
        }
    }

    public void clearCutBuffer(){
        int line=1;
        int column=1;
        String deleteCellReference = line + ";" + column;
        String clearInput[] = _rangeReference.split(":");

        if(clearInput.length == 1){
            _cutBufferStorage.deleteCell(deleteCellReference);
        }

        if(clearInput.length == 2){
            Range range = new Range(clearInput[0], clearInput[1], _cutBufferStorage);
            if(range.getOrientation()){
                while(range.getCurrentCell() != range.getLastCell()){
                    _cutBufferStorage.deleteCell(deleteCellReference);
                    range.setCurrentCell(range.getNextHorizontalCell(_cutBufferStorage).toStringReference(), _cutBufferStorage);
                    column++;
                }
                _cutBufferStorage.deleteCell(deleteCellReference);
            }

            if(!range.getOrientation()){
                while(range.getCurrentCell() != range.getLastCell()){
                    _cutBufferStorage.deleteCell(deleteCellReference);
                    range.setCurrentCell(range.getNextVerticalCell(_cutBufferStorage).toStringReference(), _cutBufferStorage);
                    line++;
                }
                _cutBufferStorage.deleteCell(deleteCellReference);
            }
        }
    }

    public void pasteCell(String cellReference, CellStorage spreadsheetStorage, int spreadsheetMaxLine, int spreadsheetMaxColumn){
        int line=1;
        int column=1;
        int remainingSpace = remainingSpace(cellReference, spreadsheetMaxLine, spreadsheetMaxColumn);
        String cutBufferCellReference = line + ";" + column;

        
        Cell currentCell = spreadsheetStorage.searchCell(cellReference);
        int ix=0;

        while(ix<_cutBufferSize && remainingSpace >= 0){
            if (_isHorziontal){
                spreadsheetStorage.switchCellContent(_cutBufferStorage.searchCell(cutBufferCellReference), currentCell);
                currentCell = spreadsheetStorage.getNextHorizontalCell(currentCell);
                column++;
                cutBufferCellReference = line + ";" + column;
                ix++;
                remainingSpace--;
            }

            if(!_isHorziontal){
                spreadsheetStorage.switchCellContent(_cutBufferStorage.searchCell(cutBufferCellReference), currentCell);
                currentCell = spreadsheetStorage.getNextVerticalCell(currentCell);
                line++;
                cutBufferCellReference = line + ";" + column;
                ix++;
                remainingSpace--;
            }
        }
    }
    
    public void pasteRange(String s1, String rangereference, CellStorage spreadsheetStorage, int spreadsheetMaxLine, int spreadsheetMaxColumn){
        if(_cutBufferSize != rangeReferenceSize(rangereference)){
            return;
        }
        pasteCell(s1, spreadsheetStorage, spreadsheetMaxLine, spreadsheetMaxColumn);     
    }

    public int remainingSpace(String cellReference, int spreadsheetMaxLine, int spreadsheetMaxColumn){
        String splitedCoordinates[] = cellReference.split(";");
        
        if (_isHorziontal){
            return (spreadsheetMaxColumn - Integer.valueOf(splitedCoordinates[1]));
        }

        return (spreadsheetMaxLine - Integer.valueOf(splitedCoordinates[0]));
    }

    public String visualize(){
        int line=1;
        int column=1;
        String cutBufferCellReference = line + ";" + column;
        String visualizeString = "";

        if (_cutBufferSize == 0)
            return visualizeString;
        
        if (_cutBufferSize == 1){
            if(_cutBufferStorage.searchCell(cutBufferCellReference).getContentString() == null)
                    visualizeString += cutBufferCellReference + "|";
            else
                visualizeString += cutBufferCellReference + "|" +  _cutBufferStorage.searchCell(cutBufferCellReference).getContentString();

            return visualizeString;
        }

        else{
            String splitedCoordinates[] = _rangeReference.split(":");
            Range range = new Range(splitedCoordinates[0], splitedCoordinates[1], _cutBufferStorage);

            if(range.getOrientation()){
                while(range.getCurrentCell() != range.getLastCell()){
                    if(_cutBufferStorage.searchCell(cutBufferCellReference).getContentString() == null)
                        visualizeString += cutBufferCellReference + "|" + "\n";
                    else
                        visualizeString += cutBufferCellReference + "|" +  _cutBufferStorage.searchCell(cutBufferCellReference).getContentString() + "\n";

                    range.setCurrentCell(range.getNextHorizontalCell(_cutBufferStorage).toStringReference(), _cutBufferStorage);
                    column++;
                    cutBufferCellReference = line + ";" + column;

                }
                if(_cutBufferStorage.searchCell(cutBufferCellReference).getContentString() == null)
                    visualizeString += cutBufferCellReference + "|";
                else
                    visualizeString += cutBufferCellReference + "|" +  _cutBufferStorage.searchCell(cutBufferCellReference).getContentString();
            }
            
            if(!range.getOrientation()){
                while(range.getCurrentCell() != range.getLastCell()){
                    if(_cutBufferStorage.searchCell(cutBufferCellReference).getContentString() == null)
                        visualizeString += cutBufferCellReference + "|" + "\n";
                    else
                        visualizeString += cutBufferCellReference + "|" +  _cutBufferStorage.searchCell(cutBufferCellReference).getContentString() +"\n";
                    range.setCurrentCell(range.getNextVerticalCell(_cutBufferStorage).toStringReference(), _cutBufferStorage);

                    line++;
                    cutBufferCellReference = line + ";" + column;
                }
                if(_cutBufferStorage.searchCell(cutBufferCellReference).getContentString() == null)
                    visualizeString += cutBufferCellReference + "|";
                else
                    visualizeString += cutBufferCellReference + "|" +  _cutBufferStorage.searchCell(cutBufferCellReference).getContentString();
            }
            
            return visualizeString;
        }
    }
    public int rangeReferenceSize(String rangeReference){
        String cells[] = rangeReference.split("[:;]");
        if(cells[0].equals(cells[2]))       
            return Math.abs(Integer.valueOf(cells[1]) - Integer.valueOf(cells[3])) + 1;
        return Math.abs(Integer.valueOf(cells[0]) - Integer.valueOf(cells[2])) + 1;
    }
}   