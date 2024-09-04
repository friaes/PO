package prr.app.lookups;

import prr.Network;
import prr.app.visitors.RenderComms;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
//FIXME more imports if needed

/**
 * Command for showing all communications.
 */
class DoShowAllCommunications extends Command<Network> {

	DoShowAllCommunications(Network receiver) {
		super(Label.SHOW_ALL_COMMUNICATIONS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		RenderComms renderer = new RenderComms();
		_receiver.acceptAllComms(renderer);
		if (renderer.toString().length() != 0)
			_display.popup(renderer);		

	}
}
