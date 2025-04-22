package xxl;

public class StringContent extends CellContent{

    private String _string;

    public StringContent(String s1){
        _string = s1;
    }

    public String getStringContent(){
        return _string;
    }

    public String getStringWithoutPrime(){
        String args[] = _string.split("\'");
        return args[1];
    }
}