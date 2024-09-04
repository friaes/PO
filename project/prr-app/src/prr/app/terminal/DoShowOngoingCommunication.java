package prr.app.terminal;

import prr.Network;
import prr.app.visitors.RenderComms;
import prr.terminals.Terminal;
import pt.tecnico.uilib.menus.CommandException;
//FIXME add more imports if needed

/**
 * Command for showing the ongoing communication.
 */
class DoShowOngoingCommunication extends TerminalCommand {

	DoShowOngoingCommunication(Network context, Terminal terminal) {
		super(Label.SHOW_ONGOING_COMMUNICATION, context, terminal);
	}

	@Override
	protected final void execute() throws CommandException {
		RenderComms renderer = new RenderComms();
		_receiver.acceptComms(renderer);
		if (renderer.toString().length() != 0)
			_display.popup(renderer);	
		else _display.popup(Message.noOngoingCommunication());
	}
}
