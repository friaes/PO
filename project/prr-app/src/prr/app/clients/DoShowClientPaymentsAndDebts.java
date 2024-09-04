package prr.app.clients;

import prr.Network;

import prr.app.exceptions.UnknownClientKeyException;
import prr.exceptions.UnknownClientException;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
//FIXME add more imports if needed

/**
 * Show the payments and debts of a client.
 */
class DoShowClientPaymentsAndDebts extends Command<Network> {

	DoShowClientPaymentsAndDebts(Network receiver) {
		super(Label.SHOW_CLIENT_BALANCE, receiver);
		//FIXME add command fields
	}

	@Override
	protected final void execute() throws CommandException {
		try{
			Form request = new Form();
			request.addStringField("key", Prompt.key());
			request.parse();
			
			_receiver.checkClientExists(request.stringField("key"));
			_display.popup(Message.clientPaymentsAndDebts(request.stringField("key"),
			_receiver.getClientPayments("key"),
			 _receiver.getClientDebts("key")));
		
			} catch (UnknownClientException e){
			throw new UnknownClientKeyException(e.getKey());
		}
	}
}
