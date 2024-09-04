package prr.app.lookups;

import prr.Network;
import prr.app.visitors.RenderClients;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
//FIXME more imports if needed

/**
 * Show clients with positive balance.
 */
class DoShowClientsWithoutDebts extends Command<Network> {

	DoShowClientsWithoutDebts(Network receiver) {
		super(Label.SHOW_CLIENTS_WITHOUT_DEBTS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		RenderClients renderer = new RenderClients();
		_receiver.acceptWithoutDebts(renderer);
		if (renderer.toString().length() != 0)
			_display.popup(renderer);
	} 
}

