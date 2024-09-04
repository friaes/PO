package prr.app.terminal;

import prr.Network;
import prr.terminals.Terminal;
import prr.app.exceptions.UnknownTerminalKeyException;
import prr.exceptions.UnknownTerminalException;
import prr.exceptions.TerminalExistsException;
import pt.tecnico.uilib.menus.CommandException;
import pt.tecnico.uilib.forms.Form;


/**
 * Remove friend.
 */
class DoRemoveFriend extends TerminalCommand {

	DoRemoveFriend(Network context, Terminal terminal) {
		super(Label.REMOVE_FRIEND, context, terminal);
		//FIXME add command fields
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			Form request = new Form();
			request.addStringField("key", Prompt.terminalKey());
			request.parse();
			_receiver.removeFriend(request.stringField("key"), _network); 
		} catch (TerminalExistsException e) {
		} catch (UnknownTerminalException e) {
			throw new UnknownTerminalKeyException(e.getKey());
		}
	}
}