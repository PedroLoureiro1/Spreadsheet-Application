package xxl;

import xxl.Storage.CellStorage;

public class ReferenceContent extends CellContent{

    String _reference;
    String _refContent;
    String _referenceValue;

    public ReferenceContent(String s1){
        _refContent = "=" + s1;
        _reference = s1;
    }


    public String updateValue(CellStorage cellStorage){ 
            String s1 = _reference;
            Cell c1 = cellStorage.searchCell(s1);

            verifyRefContent();
            while(c1 != null){
                if(verifyFunctionalContent(c1)){
                    FunctionalContent functionalContent = (FunctionalContent) c1.getContent();
                    s1 = functionalContent.executeFunction(cellStorage);
                    String args[] = s1.split("=");
                    s1 = args[0];
                    break;
                }
                s1 = c1.getContentString();
                if (s1 == null){
                    _referenceValue = "#VALUE";
                    break;
                }
                
                else{
                    String args[] = s1.split("=");
                    if(args.length == 2){
                        c1 = cellStorage.searchCell(args[1]);
                    }
                    else
                        c1 = cellStorage.searchCell(s1);
                }
            }
            if (s1 == null){
                _referenceValue = "#VALUE";
            }
            else{
                _referenceValue = s1;
            }
            _refContent =  _referenceValue + _refContent;
            return _refContent;
    }
    public String getReferenceValue(){
        return _referenceValue;
    }

    public void verifyRefContent(){
        String Args[] = _refContent.split("=");
        if(Args[0] != "")
            _refContent = "=" + Args[1];
    }
}
