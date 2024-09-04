package prr;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import prr.exceptions.*;

//FIXME add more import if needed (cannot import from pt.tecnico or prr.app)

/**
 * Manage access to network and implement load/save operations.
 */
public class NetworkManager {

	private String _filename = "";

	/** The network itself. */
	private Network _network = new Network();


	/**
	 * @param filename name of the file containing the serialized application's state
         *        to load.
	 * @throws UnavailableFileException if the specified file does not exist or there is
         *         an error while processing this file.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void load(String filename) throws UnavailableFileException, ClassNotFoundException, IOException {
		_filename = filename;
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))){
			_network = (Network) ois.readObject();
			_network.setChanged(false);
		} catch (FileNotFoundException e) {
			throw new UnavailableFileException(filename);
		}
	}


	/**
         * Saves the serialized application's state into the file associated to the current network.
         *
	 * @throws FileNotFoundException if for some reason the file cannot be created or opened. 
	 * @throws MissingFileAssociationException if the current network does not have a file.
	 * @throws IOException if there is some error while serializing the state of the network to disk.
	 */
	public void save() throws FileNotFoundException, MissingFileAssociationException, IOException {
		if (_filename == null || _filename.equals(""))
			throw new MissingFileAssociationException();
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_filename)))) {
		oos.writeObject(_network);
		_network.setChanged(false);
	  }
	}


	/**
         * Saves the serialized application's state into the specified file. The current network is
         * associated to this file.
         *
	 * @param filename the name of the file.
	 * @throws FileNotFoundException if for some reason the file cannot be created or opened.
	 * @throws MissingFileAssociationException if the current network does not have a file.
	 * @throws IOException if there is some error while serializing the state of the network to disk.
	 */
	public void saveAs(String filename) throws FileNotFoundException, MissingFileAssociationException, IOException {
		_filename = filename;
    	save();
	}

	/**
	 * Read text input file and create domain entities..
	 * 
	 * @param filename name of the text input file
	 * @throws ImportFileException
	 */
	public void importFile(String filename) throws ImportFileException{
		try { _network.importFile(filename);}
		
		catch (IOException | UnrecognizedEntryException e) {
            throw new ImportFileException(filename, e);
    }
	}

	/**
	 * @return filename
	 */
	public String getFilename() {
		return _filename;
	}

	/**
	 * @param filename
	 */
	public void setFilename(String filename) {
		_filename = filename;
	}

	/**
	 * @return changed?
	 */
	public boolean changed() {
		return _network.hasChanged();
	}

	/**
	 * @return network
	 */
	public Network getNetwork() {
		return _network;
	}

	/**
	 * Reset the network.
	 */
	public void reset() {
		_network = new Network();
		_filename = null;
	}

}
