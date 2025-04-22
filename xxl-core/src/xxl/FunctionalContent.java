package xxl;

import xxl.Storage.CellStorage;

public class FunctionalContent extends CellContent{
    private String _functionName;
    private String _c1;
    private String _c2;
    private String _functional;
    private Integer _functionArg1;
    private Integer _functionArg2;
    private Integer _functionValue;
    
    
    public FunctionalContent(String s1, String s2, String s3){
        _functionName = s1;
        _c1 = s2;
        _c2 = s3;
        if(s1.length() > 3){
            _functional = "=" + s1 + "(" + s2 + ":" + s3 +")";
            return;
        }

        _functional = "=" + s1 + "(" + s2 + "," + s3 +")";

    }
    
    public String getFunctionName(){
        return _functionName;
    }

    public String executeFunction(CellStorage cellStorage){
        switch(_functionName){
            
            case "ADD":
                updateArgs(cellStorage);
                add();
                return _functional;

            case "SUB":
                updateArgs(cellStorage);
                sub();
                return _functional;

            case "MUL":
                updateArgs(cellStorage);
                mul();
                return _functional;

            case "DIV":
                updateArgs(cellStorage);
                div();
                return _functional;

            case "AVERAGE":
                average(_c1, _c2, cellStorage);
                return _functional;

            case "PRODUCT":
                product(_c1, _c2, cellStorage);
                return _functional;
            
            case "COALESCE":
                coalesce(_c1, _c2, cellStorage);
                return _functional;

            case "CONCAT":
                concat(_c1, _c2, cellStorage);
                return _functional;

            default:    
                return null;
        } 
    }
    
    public void updateArgs(CellStorage cellStorage) throws NumberFormatException{ 
        String s1 = _c1;
        Cell c1 = cellStorage.searchCell(s1);
        verifyFunctional();
        while(c1 != null){
            if(verifyFunctionalContent(c1)){
                FunctionalContent functionalContent = (FunctionalContent) c1.getContent();
                s1 = functionalContent.executeFunction(cellStorage);
                String args[] = s1.split("=");
                s1 = args[0];
                break;
            }

            if(c1.getContent() == null){
                _functional = "#VALUE" + _functional;
                return;
            }

            if(verifyStringContent(c1)){
                c1.setIsStringContent();
                _functional = "#VALUE" + _functional;
                return;
            }

            s1 = c1.getContentString();

            String args[] = s1.split("=");
            if(args.length == 2)
                c1 = cellStorage.searchCell(args[1]);
            else
                c1 = cellStorage.searchCell(s1);
        }
        try{
            _functionArg1 = Integer.valueOf(s1);
        }
        catch(NumberFormatException e){
            _functional = "#VALUE" + _functional;
        }

        String s2 = _c2;
        Cell c2 = cellStorage.searchCell(s2);

        while(c2 != null){
            if(verifyFunctionalContent(c2)){
                FunctionalContent functionalContent = (FunctionalContent) c2.getContent();
                s2 = functionalContent.executeFunction(cellStorage);
                String args[] = s2.split("=");
                s2 = args[0];
                break;
            }

            if(c2.getContent() == null){
                _functional = "#VALUE" + _functional;
                return;
            }

            if(verifyStringContent(c2)){
                c2.setIsStringContent();
                _functional = "#VALUE" + _functional;
                return;
            }

            s2 = c2.getContentString();

            String args[] = s2.split("=");
            if(args.length == 2)
                c2 = cellStorage.searchCell(args[1]);
            else
                c2 = cellStorage.searchCell(s2);
        }

        try{
            _functionArg2 = Integer.valueOf(s2);
        }
        catch(NumberFormatException e){
            _functional = "#VALUE" + _functional;
        }
    }

    public void add() throws NumberFormatException{
        if (_functionArg1 == null || _functionArg2 == null)
            ;
        else{
            if(_functionValue == null){
                try{
                    _functionValue = _functionArg1 + _functionArg2;
                    _functional =  _functionValue + _functional;
                }
                catch(NumberFormatException e){
                    _functional = "#VALUE" + _functional;
                }
            }
        }
    }

    public void sub(){
        if (_functionArg1 == null || _functionArg2 == null){
            ;
        }

        else{
            if(_functionValue == null){
                try{
                    _functionValue = _functionArg1 - _functionArg2;
                    _functional =  _functionValue + _functional;
                }
                catch(NumberFormatException e){
                    _functional = "#VALUE" + _functional;
                }
            }
        }
    }

    public void mul(){
        if (_functionArg1 == null || _functionArg2 == null)
            ;
        else{
            if(_functionValue == null){
                try{
                    _functionValue = _functionArg1 * _functionArg2;
                    _functional =  _functionValue + _functional;
                }
                catch(NumberFormatException e){
                    _functional = "#VALUE" + _functional;
                }
            }
        }
    }

    public void div(){
        if (_functionArg1 == null || _functionArg2 == null)
            ;
        
        if (_functionArg2 == 0){
            _functional = "#VALUE" + _functional;
        }
        
        else{
            if(_functionValue == null){
                try{
                    _functionValue = _functionArg1 / _functionArg2;
                    _functional =  _functionValue + _functional;
                }
                catch(NumberFormatException e){
                    _functional = "#VALUE" + _functional;
                }
            }
        }
    }

    public void average(String c1, String c2, CellStorage cellStorage){
        int total = 0;
        int counter = 0;
        Range range = new Range(c1, c2, cellStorage);
        if(range.getOrientation()){
            while(range.getCurrentCell() != range.getLastCell()){
                verifyTypeContent(range.getCurrentCell(), cellStorage);
                if(range.getCurrentCell().getContent() == null || range.getCurrentCell().getContentString().charAt(0) == '\''){ 
                    _functional = "#VALUE" + _functional;
                    return;
                }
                total += range.getCurrentCell().getContentValue();
                counter++;
                range.setCurrentCell(range.getNextHorizontalCell(cellStorage).toStringReference(), cellStorage);
            }
        }

        else {
            while(range.getCurrentCell() != range.getLastCell()){
                verifyTypeContent(range.getCurrentCell(), cellStorage);
                if(range.getCurrentCell().getContent() == null || range.getCurrentCell().getContentString().charAt(0) == '\''){
                    _functional = "#VALUE" + _functional;
                    return; 
                }
                total += range.getCurrentCell().getContentValue();
                counter++;
                range.setCurrentCell(range.getNextVerticalCell(cellStorage).toStringReference(), cellStorage);
            }
        }

        verifyTypeContent(range.getLastCell(), cellStorage);
        if(range.getCurrentCell().getContent() == null || range.getCurrentCell().getContentString().charAt(0) == '\''){
            _functional = "#VALUE" + _functional;
            return; 
        }
        total += range.getLastCell().getContentValue();
        counter++;
        _functionValue = (total/counter);
        setContentValueInt(_functionValue);
        _functional = _functionValue + _functional;

    }

    public void product(String c1, String c2, CellStorage cellStorage){
        int total = 1;
        Range range = new Range(c1, c2, cellStorage);
        if(range.getOrientation()){
            while(range.getCurrentCell() != range.getLastCell()){
                verifyTypeContent(range.getCurrentCell(), cellStorage);
                if(range.getCurrentCell().getContent() == null || range.getCurrentCell().getContentString().charAt(0) == '\''){
                    _functional = "#VALUE" + _functional;
                    return; 
                }
                verifyFunctional();
                
                total *= range.getCurrentCell().getContentValue();
                range.setCurrentCell(range.getNextHorizontalCell(cellStorage).toStringReference(), cellStorage);
            }
        }

        else {
            while(range.getCurrentCell() != range.getLastCell()){
                verifyTypeContent(range.getCurrentCell(), cellStorage);
                if(range.getCurrentCell().getContent() == null || range.getCurrentCell().getContentString().charAt(0) == '\''){
                    _functional = "#VALUE" + _functional;
                    return; 
                }
                verifyFunctional();
                total *= range.getCurrentCell().getContentValue();
                range.setCurrentCell(range.getNextVerticalCell(cellStorage).toStringReference(), cellStorage);
            }
        }

        verifyTypeContent(range.getLastCell(), cellStorage);
        if(range.getCurrentCell().getContent() == null || range.getCurrentCell().getContentString().charAt(0) == '\''){
            _functional = "#VALUE" + _functional;
            return; 
        }
        total *= range.getLastCell().getContentValue();
        _functionValue = total;
        setContentValueInt(_functionValue);
        _functional = _functionValue + _functional;

    }

    public void concat(String c1, String c2, CellStorage cellStorage){
        String concatResult = "";
        Range range = new Range(c1, c2, cellStorage);
        if(range.getOrientation()){
            while(range.getCurrentCell() != range.getLastCell()){
                verifyTypeContent(range.getCurrentCell(), cellStorage);
                if(range.getCurrentCell().showCellContent() instanceof StringContent){
                    StringContent stringContent = (StringContent) range.getCurrentCell().showCellContent();
                    concatResult += stringContent.getStringWithoutPrime();
                }
                range.setCurrentCell(range.getNextHorizontalCell(cellStorage).toStringReference(), cellStorage);
            }
        }

        if(!range.getOrientation()){
            while(range.getCurrentCell() != range.getLastCell()){
                verifyTypeContent(range.getCurrentCell(), cellStorage);
                if(range.getCurrentCell().showCellContent() instanceof StringContent){
                    StringContent stringContent = (StringContent) range.getCurrentCell().showCellContent();
                    concatResult += stringContent.getStringWithoutPrime();
                }
                range.setCurrentCell(range.getNextVerticalCell(cellStorage).toStringReference(), cellStorage);
            }
        }

        if(range.getLastCell().showCellContent() instanceof StringContent){
            verifyTypeContent(range.getLastCell(), cellStorage);
            if(range.getCurrentCell().showCellContent() instanceof StringContent){
                StringContent stringContent = (StringContent) range.getCurrentCell().showCellContent();
                concatResult += stringContent.getStringWithoutPrime();
            }
        }

        String args[] = _functional.split("['=]");
        if(args.length == 2)
            _functional = "'" + concatResult + _functional;
    }

    public void coalesce(String c1, String c2, CellStorage cellStorage){
        Range range = new Range(c1, c2, cellStorage);
        String coalesceResult = "";
        if(range.getOrientation()){ 
            while(range.getCurrentCell() != range.getLastCell()){
                if(range.getCurrentCell().showCellContent() instanceof ReferenceContent){
                    ReferenceContent referenceContent = (ReferenceContent) range.getCurrentCell().showCellContent();
                    referenceContent.updateValue(cellStorage);
                    if(range.getCurrentCell().getIsStringContent()){
                        coalesceResult = referenceContent.getReferenceValue();
                        _functional = coalesceResult + _functional;
                        return;
                    }
                }
                if(range.getCurrentCell().showCellContent() instanceof StringContent){
                    StringContent stringContent = (StringContent) range.getCurrentCell().showCellContent();
                    coalesceResult = stringContent.getStringContent();
                    _functional = coalesceResult + _functional;
                    return;
                }
                range.setCurrentCell(range.getNextHorizontalCell(cellStorage).toStringReference(), cellStorage);
            }
        }

        if(!range.getOrientation()){ 
            while(range.getCurrentCell() != range.getLastCell()){
                if(range.getCurrentCell().showCellContent() instanceof ReferenceContent){
                    ReferenceContent referenceContent = (ReferenceContent) range.getCurrentCell().showCellContent();
                    referenceContent.updateValue(cellStorage);
                    if(range.getCurrentCell().getIsStringContent()){
                        coalesceResult = referenceContent.getReferenceValue();
                        _functional = coalesceResult + _functional;
                        return;
                    }
                }
                if(range.getCurrentCell().showCellContent() instanceof StringContent){
                    StringContent stringContent = (StringContent) range.getCurrentCell().showCellContent();
                    coalesceResult = stringContent.getStringContent();
                    _functional = coalesceResult + _functional;
                    return;
                }
                range.setCurrentCell(range.getNextHorizontalCell(cellStorage).toStringReference(), cellStorage);
            }
        }

        String args[] = _functional.split("['=]");
        if(args.length == 2)
            _functional = "'" + coalesceResult + _functional;     
    }


    public void verifyTypeContent(Cell c1, CellStorage cellStorage){
        if(c1.showCellContent() instanceof FunctionalContent){
            FunctionalContent functionalContent = (FunctionalContent) c1.showCellContent();
            c1.setContentString(functionalContent.executeFunction(cellStorage));
            c1.updateContentValue();
        }

        if(c1.showCellContent() instanceof ReferenceContent){
            ReferenceContent referenceContent = (ReferenceContent) c1.showCellContent();
            c1.setContentString(referenceContent.updateValue(cellStorage));
            c1.updateContentValue();
        }

        if(c1.showCellContent() instanceof IntContent){
            if(c1.showCellContent() != null){ 
                int contentValue = Integer.valueOf(c1.getContentString());
                c1.showCellContent().setContentValueInt(contentValue);
            }
        }
    }
    public void verifyFunctional(){
        String Args[] = _functional.split("=");
        if(Args[0] != "")
            _functional = "=" + Args[1];
        _functionValue = null;
    }
}
