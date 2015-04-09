package cmu.routing.util;

import java.util.StringTokenizer;

/**
 * 
 * This class performs the operations related to arrays
 * 
 */
public class ArrayUtil
{
    /**
     * 
     * Used to populate the 2-dimensional array with the data that is present in
     * a string. Its takes the raw string and populates the array based on the
     * row and column length
     * 
     * @param array
     *            The array to be populated
     * @param data
     *            The string containing the raw data
     */
    public static void populateArray(double[][] array, String data)
    {
        StringTokenizer dataToken = new StringTokenizer(data, ",[] ");
        int rowCount = array.length;
        int colCount = array[0].length;

        for (int i = 0; i < rowCount; i++)
        {
            for (int j = 0; j < colCount; j++)
            {
                if (dataToken.hasMoreTokens())
                    array[i][j] = Double.parseDouble(dataToken.nextToken());
                else
                    array[i][j] = -1;
            }
        }
    }

    /**
     * 
     * Initializes a 2-dimensional array based on the number of rows and columns
     * taken as input
     * 
     * @param rowCount
     *            Number of rows
     * @param colCount
     *            Number of columns
     * @return The initialized 2-dimensional array
     */
    public static double[][] initArray(int rowCount, int colCount)
    {
        double[][] array = new double[rowCount][];
        for (int i = 0; i < rowCount; i++)
            array[i] = new double[colCount];

        return array;
    }

}
