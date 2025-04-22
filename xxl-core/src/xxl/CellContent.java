package xxl;

import java.io.Serializable;

public class CellContent implements Serializable{

    private int _contentValue = 0;

    /** receives a line read from the imported file and distiguishes the types of cell contents
    * creating one accordingly 
    * 
    * @param line read from the input
    * 
    * @return the cell content passed on the imported line as a specific type of cell content
    */

    public CellContent create(String line){
   
            String args[] = line.split("=");
            int arrayLength = args.length;

            if(arrayLength == 1){
                String literalArgs[] = args[0].split("'");
                arrayLength = literalArgs.length;

                if (line.charAt(0) == '\''){
                    CellContent c2 = new StringContent(line);
                    return c2;
                }

                else{
                    CellContent c1 = new IntContent(line);
                    _contentValue = Integer.valueOf(line);
                    return c1;
                }
            }

            if(arrayLength == 2){
                String nonLiteralArgs[] = args[1].split("[(,:)]");
                switch(nonLiteralArgs.length){
                    case 1:
                        CellContent c1 = new ReferenceContent(args[1]);
                        return c1;
                    case 3:
                        CellContent c2 = new FunctionalContent(nonLiteralArgs[0], nonLiteralArgs[1], nonLiteralArgs[2]);
                        return c2;
                }
            }
            
        return null;
    }
    public boolean verifyFunctionalContent(Cell cell){
        return (cell.showCellContent() instanceof FunctionalContent);
    }

    public boolean verifyStringContent(Cell cell){
        return (cell.showCellContent() instanceof StringContent);
    }

    public void setContentValueStr(String contentValue){
        _contentValue = Integer.valueOf(contentValue);
    }

    public void setContentValueInt(Integer contentValue){
        _contentValue = contentValue;
    }

    public int getContentValue(){
        return _contentValue;
    }
}

