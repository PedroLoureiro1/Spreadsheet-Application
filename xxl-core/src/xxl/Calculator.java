package xxl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import xxl.Storage.StorageStructure;
import xxl.exceptions.ImportFileException;
import xxl.exceptions.MissingFileAssociationException;
import xxl.exceptions.UnavailableFileException;
import xxl.exceptions.UnrecognizedEntryException;

import java.io.FileReader;

// FIXME import classes

/**
 * Class representing a spreadsheet application.
 */
public class Calculator {

    /** The current spreadsheet. */
    private Spreadsheet _spreadsheet = null;
    private StorageStructure _storageStructure = new StorageStructure();

    // FIXME add more fields if needed

    /**
     * Saves the serialized application's state into the file associated to the current network.
     *
     * @throws FileNotFoundException if for some reason the file cannot be created or opened. 
     * @throws MissingFileAssociationException if the current network does not have a file.
     * @throws IOException if there is some error while serializing the state of the network to disk.
     */
    public void save() throws FileNotFoundException, MissingFileAssociationException, IOException {
        // FIXME implement serialization method

		if (_spreadsheet.getFileName() == "") {
			throw new MissingFileAssociationException();
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_spreadsheet.getFileName())))) {
			oos.writeObject(_spreadsheet);
            oos.close();
		}
        _spreadsheet.resetDirty();
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
        _spreadsheet.setFileName(filename);
        _storageStructure.insertSpreadsheet(_spreadsheet); 
        save();
        // FIXME implement serialization method
    }

    /**
     * @param filename name of the file containing the serialized application's state
     *        to load.
     * @throws UnavailableFileException if the specified file does not exist or there is
     *         an error while processing this file.
     */
    public void load(String filename) throws UnavailableFileException {
        // FIXME implement serialization method
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
			_spreadsheet = (Spreadsheet) ois.readObject();
		} 
        catch (IOException | ClassNotFoundException e) {
			throw new UnavailableFileException(filename);
		}
    }

    /**
     * Read text input file and create domain entities..
     *
     * @param filename name of the text input file
     * @throws ImportFileException
     */
    public void importFile(String filename) throws ImportFileException {
    
        try(BufferedReader r = new BufferedReader(new FileReader(filename))){
            String currentLine;
            String[] splitLine;
            int linhas;
            int colunas;
            
            currentLine = r.readLine();
            splitLine = currentLine.split("=");
            linhas=Integer.valueOf(splitLine[1]);
            currentLine = r.readLine();
            splitLine = currentLine.split("=");
            colunas=Integer.valueOf(splitLine[1]);
            
            _spreadsheet = new Spreadsheet(linhas, colunas);
            
            while ((currentLine = r.readLine()) != null) {
                String[] line = currentLine.split("\\|");
                if (line.length == 2)
                    _spreadsheet.insertContents(line[0], line[1]);          
                }
        }

        catch(UnrecognizedEntryException e){
            throw new ImportFileException(filename, e);
        }
        catch (IOException  e){
            e.printStackTrace();
        } 
    }
    /**
   * Creates a new spreadsheet and associates it to the calculator
   *
   * @param line (int with the number of lines of the spreadsheet)
   * @param column (int with the number of columns of the spreadsheet )
   */

    public void createSpreadsheet(int line, int column){
        _spreadsheet = new Spreadsheet(line, column);
    }

    public Spreadsheet getSpreadsheet(){
        return _spreadsheet;
    }
}
