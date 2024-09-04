package prr.app.terminals;

import javax.sound.midi.Receiver;

import prr.Network;
import prr.NetworkManager;
import pt.tecnico.uilib.forms.Form;
import prr.exceptions.UnrecognizedEntryException;
import prr.exceptions.UnknownTerminalException;
import prr.app.exceptions.UnknownTerminalKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
//FIXME add mode import if needed

/**
 * Open a specific terminal's menu.
 */
class DoOpenMenuTerminalConsole extends Command<Network> {

	DoOpenMenuTerminalConsole(Network receiver) {
		super(Label.OPEN_MENU_TERMINAL, receiver);
		//FIXME add command fields
	}

	@Override
	protected final void execute() throws CommandException {
		try{	
			Form request = new Form();
			request.addStringField("terminalKey", Prompt.terminalKey());
			request.parse();
			_receiver.checkTerminalExists(request.stringField("terminalKey"));
			(new prr.app.terminal.Menu(_receiver, _receiver.getTerminal(request.stringField("terminalKey")))).open();
		} catch(UnknownTerminalException e) {
			throw new UnknownTerminalKeyException(e.getKey());
		}
	}
}
