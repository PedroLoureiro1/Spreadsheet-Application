package xxl;

import xxl.Storage.CellStorage;

public class Range{

    private Cell _c1;        
    private Cell _c2;
    private Cell _currentCell;
    private Cell _lastCell;
    private boolean _isHorizontal = true;
    
    public Range(String s1, String s2, CellStorage cellStorage){
        _c1 = cellStorage.searchCell(s1);
        _c2 = cellStorage.searchCell(s2);
        if(_c1.getColumn() == _c2.getColumn()){ 
            _isHorizontal = false;
            
            if (_c1.getLine() > _c2.getLine()){
            _currentCell = _c2;
            _lastCell = _c1;
            }

            else{
                _currentCell = _c1;
                _lastCell = _c2;
            }
        }

        if(_c1.getLine() == _c2.getLine()){ 
            _isHorizontal = true;
            
            if (_c1.getColumn() > _c2.getColumn()){
                _currentCell = _c2;
                _lastCell = _c1;
            }

            else{
                _currentCell = _c1;
                _lastCell = _c2;
            }
        }
    }

    public String iterateRange(CellStorage cellStorage){
        if(!_isHorizontal){
            return iterateVerticalRange(cellStorage);
        }

        if(_isHorizontal){
            return iterateHorizontalRange(cellStorage);
        }

        return null;
    }

    public String iterateHorizontalRange(CellStorage cellStorage){
        return horizontalRangeHelper(_currentCell, _lastCell, cellStorage);
    }

    public String iterateVerticalRange(CellStorage cellStorage){
        return verticalRangeHelper(_currentCell, _lastCell, cellStorage);
    }

    public Cell getNextVerticalCell(CellStorage cellStorage){
        return cellStorage.searchCell(_currentCell.nextVerticalCellReference());
    }

    public Cell getNextHorizontalCell(CellStorage cellStorage){
        return cellStorage.searchCell(_currentCell.nextHorizontalCellReference());
    }

    public String horizontalRangeHelper(Cell currentCell, Cell lastCell, CellStorage cellStorage){

        String rangeString = "";
        while (_currentCell != _lastCell){
                if (_currentCell.getContent() == null){
                    rangeString += _currentCell.toStringWithoutContent() + "\n";
                    _currentCell = getNextHorizontalCell(cellStorage);
                }
                else{
                rangeString += _currentCell.toString(cellStorage) + "\n";
                _currentCell = getNextHorizontalCell(cellStorage);
                }
            }

        if (_lastCell.getContent() == null){
            rangeString += _currentCell.toStringWithoutContent();
        }

        else{
            rangeString += _lastCell.toString(cellStorage);
        }
        return rangeString;
    }

    public String verticalRangeHelper(Cell currentCell, Cell lastCell, CellStorage cellStorage){

        String rangeString = "";
        while (_currentCell != _lastCell){
                if (_currentCell.getContent() == null){
                    rangeString += _currentCell.toStringWithoutContent() + "\n";
                    _currentCell = getNextVerticalCell(cellStorage);
                }
                else{
                rangeString += _currentCell.toString(cellStorage) + "\n";
                _currentCell = getNextVerticalCell(cellStorage);
                }
            }

        if (_lastCell.getContent() == null){
            rangeString += _currentCell.toStringWithoutContent();
        }

        else{
            rangeString += _lastCell.toString(cellStorage);
        }
        return rangeString;
    }

    public Boolean getOrientation(){
        return _isHorizontal;
    }

    public void setLastCell(String lastCell, CellStorage cellStorage){
        _lastCell = cellStorage.searchCell(lastCell);
    }

    public void setCurrentCell(String currentCell, CellStorage cellStorage){
        _currentCell = cellStorage.searchCell(currentCell);
    }

    public Cell getLastCell(){
        return _lastCell;
    }

    public Cell getCurrentCell(){
        return _currentCell;
    }

}
