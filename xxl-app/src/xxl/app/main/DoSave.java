package xxl.app.main;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import xxl.Calculator;
// FIXME import classes
import xxl.Spreadsheet;
import java.io.FileNotFoundException;

import java.io.IOException;
import xxl.exceptions.MissingFileAssociationException;

/**
 * Save to file under current name (if unnamed, query for name).
 */
class DoSave extends Command<Calculator> {

    DoSave(Calculator receiver) {
        super(Label.SAVE, receiver, xxl -> xxl.getSpreadsheet() != null);
    }

    @Override
    protected final void execute() {
        // FIXME implement command and create a local Form

        Spreadsheet currSpreadsheet = _receiver.getSpreadsheet();

        try{
            if (currSpreadsheet.getFileName() == ""){
                _receiver.saveAs(Form.requestString(Prompt.newSaveAs()));
            }

            else{
                _receiver.save();
            }
        } 
        catch (MissingFileAssociationException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
