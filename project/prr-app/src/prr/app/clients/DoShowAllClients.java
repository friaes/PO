package prr.app.clients;

import prr.Network;
import prr.app.visitors.RenderClients;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show all clients.
 */
class DoShowAllClients extends Command<Network> {

	DoShowAllClients(Network receiver) {
		super(Label.SHOW_ALL_CLIENTS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		RenderClients renderer = new RenderClients();
		_receiver.acceptAllClients(renderer);
		if (renderer.toString().length() != 0)
			_display.popup(renderer);
	}
}
