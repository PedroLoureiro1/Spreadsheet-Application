package xxl.app.edit;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Spreadsheet;
// FIXME import classes
import xxl.exceptions.UnrecognizedEntryException;

/**
 * Class for inserting data.
 */
class DoInsert extends Command<Spreadsheet> {

    DoInsert(Spreadsheet receiver) {
        super(Label.INSERT, receiver);
        addStringField("Gama", Prompt.address());
        addStringField("Content",Prompt.content());
        // FIXME add fields
    }

    @Override
    protected final void execute() throws CommandException {
        // FIXME implement command
        try{
            _receiver.insert(stringField("Gama"), stringField("Content"));
        }
        
        catch(UnrecognizedEntryException e){
            throw new InvalidCellRangeException(e.getEntrySpecification());
        }
    }

}
