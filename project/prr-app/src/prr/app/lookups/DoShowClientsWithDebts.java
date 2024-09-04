package prr.app.lookups;

import prr.Network;
import prr.app.visitors.RenderClients;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
//FIXME more imports if needed

/**
 * Show clients with negative balance.
 */
class DoShowClientsWithDebts extends Command<Network> {

	DoShowClientsWithDebts(Network receiver) {
		super(Label.SHOW_CLIENTS_WITH_DEBTS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		RenderClients renderer = new RenderClients();
		_receiver.acceptWithDebts(renderer);
		if (renderer.toString().length() != 0)
			_display.popup(renderer);
	} 
}

