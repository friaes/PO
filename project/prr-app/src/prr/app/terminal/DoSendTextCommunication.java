package prr.app.terminal;

import prr.Network;
import prr.app.exceptions.UnknownTerminalKeyException;
import prr.exceptions.UnknownTerminalException;
import prr.exceptions.TerminalExistsException;
import prr.terminals.Terminal;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.CommandException;
//FIXME add more imports if needed

/**
 * Command for sending a text communication.
 */
class DoSendTextCommunication extends TerminalCommand {
 
        DoSendTextCommunication(Network context, Terminal terminal) {
                super(Label.SEND_TEXT_COMMUNICATION, context, terminal, receiver -> receiver.canStartCommunication());
        }

        @Override
        protected final void execute() throws CommandException {
			try {
				Form request = new Form();
				request.addStringField("key", Prompt.terminalKey());
				request.addStringField("text", Prompt.textMessage());
				request.parse();
				String destinationKey = request.stringField("key");

				switch (_receiver.commReceiverState(destinationKey, _network)){
					case "OFF" : _display.popup(Message.destinationIsOff(destinationKey));
								_receiver.addMissedCommToReceiver(new String[] {"TEXT",
									request.stringField("key")}, _network);
									break;

					default : _receiver.sendTextCommunication(new String[] {
						request.stringField("key"), 
						request.stringField("text")}, _network);
						break;
				}

				 
			} catch (UnknownTerminalException e) {
				throw new UnknownTerminalKeyException(e.getKey());
			}
		}
} 
