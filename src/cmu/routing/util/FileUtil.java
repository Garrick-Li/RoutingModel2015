package cmu.routing.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

import cmu.routing.constants.RoutingFileConstants;

/**
 * 
 * This class performs the file operations for this model
 * 
 */
public class FileUtil
{

    /**
     * 
     * Used to read the input file containing the values of the real world
     * routing problem. This method populates a Property object with the
     * key-value pairs of the required properties.
     * 
     * 
     * @param baseInputFile
     *            The input file containing the data
     * @param incDataFile
     *            The optional input data file to override the data of the base
     *            input file
     * @param mechanism
     *            The mechanism number
     * @return The key-value pair of the required properties
     * @throws IOException
     *             Thrown when any error while finding or parsing the input file
     */
    public static Properties readInputFile(String baseInputFile1,
            String incDataFile, String mechanism) throws IOException
    {
        Properties keyValue = new Properties();
        try
        {
            loadFile(baseInputFile1, keyValue);
            if (incDataFile != null && incDataFile.length() != 0)
            {
                loadFile(incDataFile, keyValue);
            }
            loadFile(RoutingFileConstants.COMMON_INPUT_FILE_PREFIX + mechanism + ".dat", keyValue);

        } catch (FileNotFoundException e)
        {
            System.out.println("Input file not found");
            throw e;
        } catch (IOException e)
        {
            System.out.println("Not able to read the contents of the file");
            throw e;
        }
        return keyValue;
    }
    
    public static Properties readBaseFile(String baseInputFile1) throws IOException
    {
        Properties keyValue = new Properties();
        try
        {
            loadFile(baseInputFile1, keyValue);
        } catch (FileNotFoundException e)
        {
            System.out.println("Input file not found");
            throw e;
        } catch (IOException e)
        {
            System.out.println("Not able to read the contents of the file");
            throw e;
        }
        return keyValue;
    }
    
    public static Properties readHatFile(String baseInputFile1) throws IOException
    {
        Properties keyValue = new Properties();
        try
        {
            loadFile(baseInputFile1, keyValue);
        } catch (FileNotFoundException e)
        {
            System.out.println("Input file not found");
            throw e;
        } catch (IOException e)
        {
            System.out.println("Not able to read the contents of the file");
            throw e;
        }
        return keyValue;
    }

    private static void loadFile(String inputFile, Properties keyValue)
            throws IOException
    {
        File input = new File(inputFile);
        BufferedReader bReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(input)));
        File tempInput = new File(inputFile + ".tmp");
        BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(tempInput)));

        String line = null;
        while ((line = bReader.readLine()) != null)
        {
            line = line.replace(" ", "");
            if (line.startsWith("//") || (line.length() == 0))
            {
            } else if (line.endsWith(";"))
            {
                line = line.substring(0, line.length() - 1);
                bWriter.write(line + "\n");
            } else
                bWriter.write(line + " \\\n");
        }
        bReader.close();
        bWriter.close();

        bReader = new BufferedReader(new InputStreamReader(new FileInputStream(
                tempInput)));
        keyValue.load(bReader);
        tempInput.delete();
    }
}
