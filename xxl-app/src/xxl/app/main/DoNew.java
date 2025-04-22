package xxl.app.main;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Calculator;
import xxl.Spreadsheet;

/**
 * Open a new file.
 */
class DoNew extends Command<Calculator> {

    DoNew(Calculator receiver) {
        super(Label.NEW, receiver);
    }

    @Override
    protected final void execute() throws CommandException {
        Spreadsheet currentSpreadsheet = _receiver.getSpreadsheet();
        if(currentSpreadsheet != null && currentSpreadsheet.isDirty()){
            Boolean confirm = Form.confirm(Prompt.saveBeforeExit());
            if (confirm){
                DoSave doSave = new DoSave(_receiver);
                doSave.execute();
            }
        }
        _receiver.createSpreadsheet(Form.requestInteger(Prompt.lines()), Form.requestInteger(Prompt.columns()));        
    }
}
