package prr.app.terminal;



import prr.Network;
import prr.terminals.Terminal;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.CommandException;
// Add more imports if needed

/**
 * Perform payment.
 */
class DoPerformPayment extends TerminalCommand {

	DoPerformPayment(Network context, Terminal terminal) {
		super(Label.PERFORM_PAYMENT, context, terminal);
		//FIXME add command fields
	}

	@Override
	protected final void execute() throws CommandException {
		Form request = new Form();
		request.addStringField("key", Prompt.commKey());
		request.parse();
		long commKey = Long.parseLong(request.stringField("key"));
		if (!_receiver.canPerformPayment(commKey))
			_display.popup(Message.invalidCommunication());
		else _receiver.performPayment(commKey,_network);
	}
}
