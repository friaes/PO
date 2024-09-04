package prr.app.terminals;

import prr.Network;
import prr.exceptions.TerminalExistsException;
import prr.exceptions.InvalidTerminalException;
import prr.exceptions.UnknownClientException;

import prr.app.exceptions.InvalidTerminalKeyException;
import prr.app.exceptions.UnknownClientKeyException;
import prr.app.exceptions.DuplicateTerminalKeyException;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Register terminal.
 */
class DoRegisterTerminal extends Command<Network> {

	DoRegisterTerminal(Network receiver) {
		super(Label.REGISTER_TERMINAL, receiver);
		//FIXME add command fields
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			String type;
			Form request1 = new Form();
			Form request2 = new Form();
			request1.addStringField("terminalKey", Prompt.terminalKey());
			request1.parse();

			do {
				type = Form.requestString(Prompt.terminalType());
			}while (!type.equals("BASIC") && !type.equals("FANCY"));

			request2.addStringField("clientKey", Prompt.clientKey());
			request2.parse();


			_receiver.registerTerminal(new String[] { //
				type,
				request1.stringField("terminalKey"), //
				request2.stringField("clientKey"), "ON"
				});
			
			} catch (TerminalExistsException e) {
				throw new DuplicateTerminalKeyException(e.getKey());
			} catch(UnknownClientException e) {
					throw new UnknownClientKeyException(e.getKey());
			} catch(InvalidTerminalException e) {
				throw new InvalidTerminalKeyException(e.getKey());
			}
			
	}
}
