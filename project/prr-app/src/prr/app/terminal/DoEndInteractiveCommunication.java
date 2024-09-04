package prr.app.terminal;

import prr.Network;
import prr.exceptions.NoCommunicationException;
import prr.exceptions.TerminalExistsException;
import prr.exceptions.UnknownTerminalException;
import prr.terminals.Terminal;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.CommandException;
//FIXME add more imports if needed

/**
 * Command for ending communication.
 */
class DoEndInteractiveCommunication extends TerminalCommand {

	DoEndInteractiveCommunication(Network context, Terminal terminal) {
		super(Label.END_INTERACTIVE_COMMUNICATION, context, terminal, receiver -> receiver.canEndCurrentCommunication());
	}

	@Override
	protected final void execute() throws CommandException {
			Form request = new Form();
			request.addStringField("duration", Prompt.duration());
			request.parse();
			_receiver.endCurrentCommunication(
				Long.parseLong(request.stringField("duration")), _network);
			
			_display.popup(Message.communicationCost(_receiver.getLastDebt()));

	}
}
