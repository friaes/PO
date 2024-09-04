package prr;

import java.io.Serializable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import prr.exceptions.*;
import prr.notifications.*;
import prr.clients.*;
import prr.terminals.*;
import prr.communications.*;
import prr.visits.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collection;
import java.util.Collections;

public class Network implements Serializable {

	/** Serial number for serialization. */
	private static final long serialVersionUID = 202208091753L;

	private boolean _changed = false;

	private Map<String, Client> _clients = new TreeMap<String, Client>();

	private Map<String, Terminal> _terminals = new TreeMap<String, Terminal>();

	private Map<Long, Communication> _communications = new TreeMap<Long, Communication>();

	private long _globalPayments = 0, _globalDebts = 0, _lastCommKey = 1;

	/**
	 * Network constructor, sets the _changed atribute to false.
	 */
	public Network() {
		setChanged(false);
	}

/*--------------------------------General File Related------------------------------------- */
	
	/**
	 * Sets the variable _changed to true, to indicate that changes have been
	 * made to the network.
	 */
	public void changed() {
		setChanged(true);
	}

	/**
	 * @return _changed
	 */
	public boolean hasChanged() {
		return _changed;
	}

	/**
	 * @param changed
	 */
	public void setChanged(boolean changed) {
		_changed = changed;
	}

	/**
	 * Read text input file and create corresponding domain entities.
	 * 
	 * @param filename name of the text input file
	 * @throws UnrecognizedEntryException if some entry is not correct
	 * @throws IOException                if there is an IO erro while processing
	 *                                    the text file
	 * @throws ImportFileException        if there is an error importing a file
	 */
	void importFile(String filename) throws UnrecognizedEntryException, IOException, ImportFileException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split("\\|");
				try {
					registerEntry(fields);
				} catch (InvalidTerminalException | UnrecognizedEntryException | UnknownClientException
						| ClientExistsException | UnknownTerminalException | TerminalExistsException e) {

					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			throw new ImportFileException(filename);
		}
	}

/*------------------------------------Verifiers-----------------------------------------*/
/**
	 * Verifies if there is a client with the given key.
	 * 
	 * @param key
	 * @throws UnknownClientException if the client corresponding to the key
	 *                                does not exist.
	 */
	public void checkClientExists(String key) throws UnknownClientException {
		Client client = _clients.get(key.toLowerCase());
		if (client == null)
			throw new UnknownClientException(key);
	}

	/**
	 * Verifies if there is no client with the given key.
	 * 
	 * @param key
	 * @throws ClientExsistsException if the client corresponding to the key
	 *                                already exists.
	 */
	public void checkNewClient(String key) throws ClientExistsException {
		if (_clients.containsKey(key.toLowerCase()))
			throw new ClientExistsException(key);
	}

	/**
	 * Verifies if there is a terminal with the given key.
	 * 
	 * @param key
	 * @throws UnknownTerminalException if the terminal corresponding to the key
	 *                                  does not exist.
	 */
	public void checkTerminalExists(String key) throws UnknownTerminalException {
		Terminal terminal = _terminals.get(key);
		if (terminal == null)
			throw new UnknownTerminalException(key);
	}

	/**
	 * Verifies if the key is made of 6 digits, and if there is no terminal
	 * with the given key.
	 * 
	 * @param key
	 * @throws InvalidTerminalException if the key is not made of 6 digits.
	 * @throws ClientExsistsException   if the terminal corresponding to the key
	 *                                  already exists.
	 */

	public void checkNewTerminal(String key) throws TerminalExistsException, InvalidTerminalException {
		if (!key.matches("\\d{6}")) {
			throw new InvalidTerminalException(key);
		}

		if (_terminals.containsKey(key)) {
			throw new TerminalExistsException(key);
		}
	}

/*---------------------------------Client Related---------------------------------------*/
/**
	 * Adds a client to the network.
	 * 
	 * @param client
	 * @throws ClientExistsException if the client already exists.
	 */
	public void addClient(Client client) throws ClientExistsException {
		String key = client.getKey();
		_clients.put(key.toLowerCase(), client);
	}

	/**
	 * Gets the client from the given key.
	 * 
	 * @param key
	 * @return client with the given key.
	 * @throws UnknownClientException if the client corresponding to the key
	 *                                does not exist.
	 */

	public Client getClient(String key) {
		Client client = _clients.get(key);
		return client;
	}

	public long getClientPayments(String key) {
		return getClient(key).getPayments();
	}

	public long getClientDebts(String key) {
		return getClient(key).getDebts();
	}

	public List<Client> getClientsWithDebts() {
		List<Client> clientsWithDebts = new ArrayList<Client>();
		for (Client client : _clients.values())
			if (client.getDebts() > 0)
				clientsWithDebts.add(client);
		clientsWithDebts.sort(Client.debtComparator);
		return clientsWithDebts;
	}

	public List<Client> getClientsWithoutDebts() {
		List<Client> clientsWithoutDebts = new ArrayList<Client>();
		for (Client client : _clients.values())
			if (client.getDebts() == 0)
				clientsWithoutDebts.add(client);
		return clientsWithoutDebts;
	}

	/**
	 * @param client
	 * @return collection of all the networks clients.
	 */
	public Collection<Client> getClients() {
		return Collections.unmodifiableCollection(_clients.values());
	}

	public void enableClientNotifications(String key) throws UnknownClientException, NotificationsAlreadyOnException {
		checkClientExists(key);
		if (getClient(key).NotificationsToString().equals("YES"))
			throw new NotificationsAlreadyOnException();
		getClient(key).setNotifications(true);
		changed();
	}

	public void disableClientNotifications(String key) throws UnknownClientException, NotificationsAlreadyOffException {
		checkClientExists(key);
		if (getClient(key).NotificationsToString().equals("NO"))
			throw new NotificationsAlreadyOffException();
		getClient(key).setNotifications(false);
		changed();
	}

	public String showNotificationsClient(String clientKey) {
		Client client = getClient(clientKey);
		String renderNot = client.sendNotifications(new DefaultMethod());
		changed();
		return renderNot;
	}

/*--------------------------------Terminal Related--------------------------------------*/
/**
	 * Adds a terminal to the network.
	 * 
	 * @param terminal
	 * @throws TerminalExistsException if the terminal already exists.
	 */
	public void addTerminal(Terminal terminal) throws TerminalExistsException, InvalidTerminalException {
		String key = terminal.getKey();
		_terminals.put(key, terminal);
	}

	/**
	 * Gets the terminal from the given key.
	 * 
	 * @param key
	 * @return terminal with the given key.
	 * @throws UnknownTerminalException if the terminal corresponding to the key
	 *                                  does not exist.
	 */
	public Terminal getTerminal(String key) {
		Terminal terminal = _terminals.get(key);
		return terminal;
	}

	/**
	 * @param terminal
	 * @return collection of all the networks terminal.
	 */
	public Collection<Terminal> getTerminals() {
		return Collections.unmodifiableCollection(_terminals.values());
	}

	/**
	 * Makes a list of unused terminals, meaning all the terminals in IDLE
	 * state.
	 * 
	 * @return array of unused terminals
	 */
	public List<Terminal> getUnusedTerminals() {
		List<Terminal> unusedTerminals = new ArrayList<Terminal>();
		for (Terminal terminal : _terminals.values())
			if (terminal.getStateString().equals("IDLE"))
				unusedTerminals.add(terminal);
		return unusedTerminals;
	}

	public List<Terminal> getTerminalsPositiveBalance() {
		List<Terminal> positiveBalance = new ArrayList<Terminal>();
		for (Terminal terminal : _terminals.values())
			if (terminal.getBalance() > 0)
				positiveBalance.add(terminal);
		return positiveBalance;
	}

	public String getTerminalStateString(String key) throws UnknownTerminalException {
		checkTerminalExists(key);
		return getTerminal(key).getStateString();
	}


/*------------------------------Communication Related-----------------------------------*/

	public long nextCommKey() {
		return _lastCommKey++;
	}

	/**
	 * Adds a communication to the network.
	 * 
	 * @param communication
	 */
	public void addCommunication(Communication communication) {
		_communications.put(communication.getKey(), communication);
	}

/*---------------------------------Payment & Debts------------------------------------- */
	
	public long getGlobalPayments() {
		return _globalPayments;
	}

	public long getGlobalDebts() {

		return _globalDebts;
	}

	public void addDebt(long price) {
		_globalDebts += price;
	}

	public void addPayment(long price) {
		_globalPayments += price;
	}

	public void removeDebt(long debt) {
		_globalDebts -= debt;
	}
	
/*------------------------------------Registers---------------------------------------- */
	
	

	/**
	 * Depending on the first element of the parameter Array, this method will
	 * register a certain entity.
	 * 
	 * @param fields
	 * @throws UnrecognizedEntryException if the first parameter of the array
	 *                                    can't be associated to any entity type.
	 * @throws ClientExistsException      if the client already exists
	 *                                    (registerClient).
	 * @throws UnknownClientException     if the client corresponding to the key
	 *                                    does not exist (registerTerminal).
	 * @throws TerminalExistsException    if the terminal already exists
	 *                                    (registerTerminal).
	 * @throws InvalidTerminalException   if the key is not made of 6 digits
	 *                                    (registerTerminal)
	 * @throws UnknownTerminalException   if the terminal corresponding to the key
	 *                                    does not exist (registerFriend).
	 */
	public void registerEntry(String[] fields) throws UnrecognizedEntryException,
			UnknownClientException, ClientExistsException, UnknownTerminalException,
			TerminalExistsException, InvalidTerminalException {
		switch (fields[0]) {
			case "CLIENT" -> registerClient(fields);
			case "BASIC", "FANCY" -> registerTerminal(fields);
			case "FRIENDS" -> registerFriend(fields);
			default -> throw new UnrecognizedEntryException(fields[0]);
		}
		changed();
	}

	/**
	 * Registers a client in the newtwork.
	 * 
	 * @param fields (Array of Strings containing the information of the client)
	 * @throws ClientExistsException if the client already exists.
	 */
	public void registerClient(String[] fields) throws ClientExistsException {
		int taxId = Integer.parseInt(fields[3]);
		checkNewClient(fields[1]);
		Client client = new Client(fields[1], fields[2], taxId);
		addClient(client);
		changed();
	}

	/**
	 * Registers a terminal in the newtwork.
	 * 
	 * @param fields (Array of Strings containing the information of the terminal)
	 * @throws UnknownClientException  if the client corresponding to the key
	 *                                 does not exist.
	 * @throws TerminalExistsException if the temrinal already exists.
	 */
	public void registerTerminal(String[] fields) throws UnknownClientException, TerminalExistsException,
			InvalidTerminalException {
		Terminal terminal;
		checkNewTerminal(fields[1]);
		checkClientExists(fields[2]);
		Client client = getClient((fields[2]));
		if (fields[0].equals("BASIC"))
			terminal = new BasicTerminal(fields[1], client, fields[3]);
		else
			terminal = new FancyTerminal(fields[1], client, fields[3]);

		addTerminal(terminal);
		client.addTerminal(terminal);
		changed();
	}

	/**
	 * Registers a terminal in the newtwork.
	 * 
	 * @param fields (Array of Strings containing the information of the terminal)
	 * @throws UnknownTerminalException if the terminal corresponding to the key
	 *                                  does not exist.
	 * @throws TerminalExistsException
	 */
	public void registerFriend(String[] fields) throws UnknownTerminalException, TerminalExistsException {
		Terminal terminal = getTerminal(fields[1]);
		for (String terminalKey : fields[2].split(",")) {
			terminal.addFriend(terminalKey, this);
		}
		changed();
	}

/*----------------------------Client visitor accept methods---------------------------- */

	/**
	 * Visit selected client, passing the second parameter of the client accept
	 * method as true, meaning it's the last client, showing only the one
	 * client asked from the app.
	 * 
	 * @param selector
	 * @param visitor
	 * @param key
	 * @throws UnknownClientException if the client corresponding to the key
	 *                                does not exist.
	 */
	public void acceptClient(Selector<Client> selector, ClientVisitor visitor, String key)
			throws UnknownClientException {
		checkClientExists(key);
		Client client = getClient(key);
		if (selector.ok(_clients.get(key)))
			client.accept(visitor, true);
	}

	/**
	 * Visit selected client, forcing the second parameter of the client accept
	 * method to true, meaning it's the last client, showing only the one
	 * client asked from the app. Uses the default selector.
	 * 
	 * @param visitor
	 * @param key
	 * @throws UnknownClientException
	 */
	public void acceptClient(ClientVisitor visitor, String key) throws UnknownClientException {
		acceptClient(new Selector<Client>() {
		}, visitor, key);
	}

	/**
	 * Visit selected clients, all of them in this case.
	 * 
	 * @param selector
	 * @param visitor
	 */
	public void acceptAllClients(Selector<Client> selector, ClientVisitor visitor) {
		long clientCount = 0;
		long numberOfClients = _clients.keySet().size();
		for (String clientKey : _clients.keySet()) {
			++clientCount;
			Client client = _clients.get(clientKey);
			if (selector.ok(client))
				client.accept(visitor, clientCount == numberOfClients);
		}
	}

	/**
	 * Visits selected clients, all of them in this case.
	 * Uses the default selector.
	 * 
	 * @param visitor
	 */
	public void acceptAllClients(ClientVisitor visitor) {
		acceptAllClients(new Selector<Client>() {
		}, visitor);
	}

	public void acceptWithDebts(Selector<Client> selector, ClientVisitor visitor) {
		long clientCount = 0;
		List<Client> clientsWithDebts = getClientsWithDebts();

		long numberOfClients = clientsWithDebts.size();

		for (Client client : clientsWithDebts) {
			++clientCount;
			if (selector.ok(client))
				client.accept(visitor, clientCount == numberOfClients);
		}
	}

	public void acceptWithDebts(ClientVisitor visitor) {
		acceptWithDebts(new Selector<Client>() {
		}, visitor);
	}

	public void acceptWithoutDebts(Selector<Client> selector, ClientVisitor visitor) {
		long clientCount = 0;
		List<Client> clientsWithoutDebts = getClientsWithoutDebts();
		long numberOfClients = clientsWithoutDebts.size();

		for (Client client : clientsWithoutDebts) {
			++clientCount;
			if (selector.ok(client))
				client.accept(visitor, clientCount == numberOfClients);
		}
	}

	public void acceptWithoutDebts(ClientVisitor visitor) {
		acceptWithoutDebts(new Selector<Client>() {
		}, visitor);
	}
/*----------------------------Terminal visitor accept methods---------------------------- */
	
	/**
	 * Visit selected terminals, in this case all of the terminals.
	 * 
	 * @param visitor
	 * @param key
	 */
	public void acceptAllTerminals(Selector<Terminal> selector, TerminalVisitor visitor) {
		long terminalCount = 0;
		long numberOfTerminals = _terminals.keySet().size();
		for (String terminalKey : _terminals.keySet()) {
			++terminalCount;
			Terminal terminal = _terminals.get(terminalKey);
			if (selector.ok(terminal))
				terminal.accept(visitor, terminalCount == numberOfTerminals);
		}
	}

	/**
	 * Visit selected terminals, in this case all of the terminals.
	 * Uses the defautl selector.
	 * 
	 * @param visitor
	 */
	public void acceptAllTerminals(TerminalVisitor visitor) {
		acceptAllTerminals(new Selector<Terminal>() {
		}, visitor);
	}

	/**
	 * Visit selected terminals. In this case all unused Terminals, i.e. ,
	 * all terminals in IDLE state.
	 * 
	 * @param slector
	 * @param visitor
	 */
	public void acceptUnused(Selector<Terminal> selector, TerminalVisitor visitor) {
		long terminalCount = 0;
		List<Terminal> unusedTerminals = getUnusedTerminals();
		long numberOfTerminals = unusedTerminals.size();
		for (Terminal terminal : unusedTerminals) {
			++terminalCount;
			if (selector.ok(terminal))
				terminal.accept(visitor, terminalCount == numberOfTerminals);
		}
	}

	/**
	 * Visit selected terminals. In this case all unused Terminals, i.e. ,
	 * all terminals in IDLE state. Uses the defautl selector.
	 * 
	 * @param visitor
	 */
	public void acceptUnused(TerminalVisitor visitor) {
		acceptUnused(new Selector<Terminal>() {
		}, visitor);
	}

	public void acceptWithPositiveBalance(Selector<Terminal> selector, TerminalVisitor visitor) {
		long terminalCount = 0;
		List<Terminal> unusedTerminals = getUnusedTerminals();
		long numberOfTerminals = unusedTerminals.size();
		for (Terminal terminal : unusedTerminals) {
			++terminalCount;
			if (selector.ok(terminal))
				terminal.accept(visitor, terminalCount == numberOfTerminals);
		}
	}

	public void acceptWithPositiveBalance(TerminalVisitor visitor) {
		acceptWithPositiveBalance(new Selector<Terminal>() {
		}, visitor);
	}

/*------------------------Communication visitor acceot methods-------------------------*/

	public void acceptAllComms(Selector<Communication> selector, CommVisitor visitor) {
		long commsCount = 0;
		long numberOfComms = _communications.keySet().size();
		for (long commKey : _communications.keySet()) {
			++commsCount;
			Communication communication = _communications.get(commKey);
			if (selector.ok(communication))
				communication.accept(visitor, commsCount == numberOfComms);
		}
	}

	public void acceptAllComms(CommVisitor visitor) {
		acceptAllComms(new Selector<Communication>() {
		}, visitor);
	}

	public void acceptCommsFromClient(Selector<Communication> selector, CommVisitor visitor, String key)
			throws UnknownClientException {
		checkClientExists(key);
		Client client = getClient(key);
		Map<Long, Communication> comms = client.getCommsFromThis();
		long commsCount = 0;
		long numberOfComms = comms.keySet().size();
		for (long commKey : comms.keySet()) {
			++commsCount;
			Communication communication = _communications.get(commKey);
			if (selector.ok(communication))
				communication.accept(visitor, commsCount == numberOfComms);
		}
	}

	public void acceptCommsFromClient(CommVisitor visitor, String key) throws UnknownClientException {
		acceptCommsFromClient(new Selector<Communication>() {
		}, visitor, key);
	}

	public void acceptCommsToClient(Selector<Communication> selector, CommVisitor visitor, String key)
			throws UnknownClientException {
		checkClientExists(key);
		Client client = getClient(key);
		Map<Long, Communication> comms = client.getCommsToThis();
		long commsCount = 0;
		long numberOfComms = comms.keySet().size();
		for (long commKey : comms.keySet()) {
			++commsCount;
			Communication communication = _communications.get(commKey);
			if (selector.ok(communication))
				communication.accept(visitor, commsCount == numberOfComms);
		}
	}

	public void acceptCommsToClient(CommVisitor visitor, String key) throws UnknownClientException {
		acceptCommsToClient(new Selector<Communication>() {
		}, visitor, key);
	}
}
