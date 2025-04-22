package xxl.app.main;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Calculator;
import xxl.Spreadsheet;
// FIXME import classes
import xxl.exceptions.UnavailableFileException;

/**
 * Open existing file.
 */
class DoOpen extends Command<Calculator> {

    DoOpen(Calculator receiver) {
        super(Label.OPEN, receiver);
    }

    @Override
    protected final void execute() throws CommandException {
        try {
            Spreadsheet currentSpreadsheet = _receiver.getSpreadsheet();
            if(currentSpreadsheet != null && currentSpreadsheet.isDirty()){
                Boolean confirm = Form.confirm(Prompt.saveBeforeExit());
                if (confirm){
                    DoSave doSave = new DoSave(_receiver);
                    doSave.execute();
                }
            }

            _receiver.load(Form.requestString(Prompt.openFile()));
            } catch (UnavailableFileException e) {
                    throw new FileOpenFailedException(e);
            }
    }

}
