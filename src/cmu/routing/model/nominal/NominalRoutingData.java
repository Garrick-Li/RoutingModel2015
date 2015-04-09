package cmu.routing.model.nominal;

import java.io.IOException;
import java.util.Properties;

import cmu.routing.constants.RoutingFileConstants;
import cmu.routing.model.RoutingData;
import cmu.routing.util.ArrayUtil;
import cmu.routing.util.FileUtil;

/**
 * 
 * This class hold all the data that is related to the nominal routing
 * optimization model
 * 
 */
public class NominalRoutingData implements RoutingData
{
    private int strings1;
    private int strings2;
    private int long_strings1;
    private int long_strings2;
    private int strings;
    private int flightLegs;
    private int groundArcs;
    private int maintLoc;
    private int airCrafts;
    private int long_long_string1 = 474659;
    private int long_long_string2 = 648822;
    private double[][] long_string_index1 = null;
    private double[][] long_string_index_true1 = null;
    private double[][] long_string_index2 = null;
    private double[][] long_string_index_true2 = null;
    private double[][] true_string1 = null;
    private double[][] true_string2 = null;
    private double[][] cover = null;
    private double[][] cover2 = null;
    private double[][] cover1 = null;
    private double[][] propDelay = null;
    private double[][] countStrings = null;
    private double[][] countStrings1 = null;
    private double[][] countStrings2 = null;
    private double[][] countGround = null;
    private double[][] flightMaintStringsIn = null;
    private double[][] flightMaintStringsOut = null;
    private double[][] flightMaintStringsIn1 = null;
    private double[][] flightMaintStringsOut1 = null;
    private double[][] flightMaintStringsIn2 = null;
    private double[][] flightMaintStringsOut2 = null;
    private double[][] flightMaintGroundIn = null;
    private double[][] flightMaintGroundOut = null;
    private double[][] stringsInSolution = null;
    private Properties contents = null;

    public NominalRoutingData(String baseInputFile1, String baseInputFile2, String baseInputFile3, String incDataFile, String hatDataFile, String mechanism)
            throws IOException
    {
        populateData(baseInputFile1, baseInputFile2, baseInputFile3, incDataFile, hatDataFile, mechanism);
    }

    @Override
    public void populateData(String baseInputFile1, String baseInputFile2, String baseInputFile3, String incDataFile, String hatDataFile, String mechanism)
            throws IOException
    {
        contents = FileUtil.readBaseFile(baseInputFile3);
        groundArcs = Integer.parseInt(contents
                .getProperty(RoutingFileConstants.GROUND_ARCS));
        maintLoc = Integer.parseInt(contents
                .getProperty(RoutingFileConstants.MAINTENANCE_LOC));
        airCrafts = 61;
        flightMaintGroundOut = ArrayUtil.initArray(maintLoc, groundArcs);
        ArrayUtil
                .populateArray(
                        flightMaintGroundOut,
                        contents.getProperty(RoutingFileConstants.FLIGHT_MAINTENANCE_GROUND_OUT));
        flightMaintGroundIn = ArrayUtil.initArray(maintLoc, groundArcs);
        ArrayUtil
                .populateArray(
                        flightMaintGroundIn,
                        contents.getProperty(RoutingFileConstants.FLIGHT_MAINTENANCE_GROUND_IN));
        countGround = ArrayUtil.initArray(1, groundArcs);
        ArrayUtil.populateArray(countGround,
                contents.getProperty(RoutingFileConstants.COUNT_GROUND));
        
        contents = FileUtil.readBaseFile(baseInputFile1);
        strings1 = Integer.parseInt(contents
                .getProperty(RoutingFileConstants.FEASIBLE_STRINGS));
        System.out.println("strings1: " + strings1);
        long_strings1 = Integer.parseInt(contents
                .getProperty("num_long_strings"));
        System.out.println("long_strings1: " + long_strings1);
        long_string_index1 = ArrayUtil.initArray(1, long_long_string1);
        ArrayUtil.populateArray(long_string_index1,
                contents.getProperty("long_string_index"));
        true_string1 = ArrayUtil.initArray(1, long_long_string1);
        ArrayUtil.populateArray(true_string1,
                contents.getProperty("true_string"));
        long_string_index_true1 = ArrayUtil.initArray(1, strings1);
        int m=0;
        for (int i = 0; i < long_long_string1; i++){
            if (true_string1[0][i] == 1){
                long_string_index_true1[0][m] = long_string_index1[0][i];
                m++;
            }
        }
        System.out.println("Final index is: "+String.valueOf(m-1));
        long_string_index1 = null;
        true_string1 = null;
        
        flightLegs = Integer.parseInt(contents
                .getProperty(RoutingFileConstants.DAILY_FLIGHT_LEGS));
        System.out.println("flightLegs: " + flightLegs);


        cover1 = ArrayUtil.initArray(flightLegs, strings1);
        ArrayUtil.populateArray(cover1,
                contents.getProperty(RoutingFileConstants.COVER));

        countStrings1 = ArrayUtil.initArray(1, strings1);
        ArrayUtil.populateArray(countStrings1,
                contents.getProperty(RoutingFileConstants.COUNT_STRINGS));

        flightMaintStringsIn1 = ArrayUtil.initArray(maintLoc, strings1);
        ArrayUtil
                .populateArray(
                        flightMaintStringsIn1,
                        contents.getProperty(RoutingFileConstants.FLIGHT_MAINTENANCE_STRINGS_IN));

        flightMaintStringsOut1 = ArrayUtil.initArray(maintLoc, strings1);
        ArrayUtil
                .populateArray(
                        flightMaintStringsOut1,
                        contents.getProperty(RoutingFileConstants.FLIGHT_MAINTENANCE_STRINGS_OUT));

        
        contents = FileUtil.readInputFile(baseInputFile2, incDataFile, mechanism);
        strings2 = Integer.parseInt(contents
                .getProperty(RoutingFileConstants.FEASIBLE_STRINGS));
        System.out.println("strings2: " + strings2);
        long_strings2 = Integer.parseInt(contents
                .getProperty("num_long_strings"));
        System.out.println("long_strings2: " + long_strings2);
        long_string_index2 = ArrayUtil.initArray(1, long_long_string2);
        ArrayUtil.populateArray(long_string_index2,
                contents.getProperty("long_string_index"));
        true_string2 = ArrayUtil.initArray(1, long_long_string2);
        ArrayUtil.populateArray(true_string2,
                contents.getProperty("true_string"));
        long_string_index_true2 = ArrayUtil.initArray(1, strings2);
        m=0;
        for (int i = 0; i < long_long_string2; i++){
            if (true_string2[0][i] == 1){
                long_string_index_true2[0][m] = long_string_index2[0][i];
                m++;
            }
        }
        System.out.println("Final index is: " + String.valueOf(m-1));
        long_string_index2 = null;
        true_string2 = null;
        
        strings = long_strings1 + long_strings2;
        cover2 = ArrayUtil.initArray(flightLegs, strings2);
        ArrayUtil.populateArray(cover2,
                contents.getProperty(RoutingFileConstants.COVER));
        cover = ArrayUtil.initArray(flightLegs, strings);
        for (int i = 0; i < flightLegs; i++){
            for (int j = 0; j < strings1; j++){
                if (long_string_index_true1[0][j] != 0)
                cover[i][(int) long_string_index_true1[0][j] - 1] = cover1[i][j];
            }
        }
        for (int i = 0; i < flightLegs; i++){
            for (int j = 0; j < strings2; j++){
                if (long_string_index_true2[0][j] != 0)
                cover[i][(int) long_string_index_true2[0][j] + long_strings1 - 1] = cover2[i][j];
            }
        }
        cover1 = null;
        cover2 = null;
        
        countStrings2 = ArrayUtil.initArray(1, strings2);
        ArrayUtil.populateArray(countStrings2,
                contents.getProperty(RoutingFileConstants.COUNT_STRINGS));
        
        countStrings = ArrayUtil.initArray(1, strings);
        for (int j = 0; j < strings1; j++){
            if (long_string_index_true1[0][j] != 0)
            countStrings[0][(int) long_string_index_true1[0][j] - 1] = countStrings1[0][j];  
        }
        for (int j = 0; j < strings2; j++){
            if (long_string_index_true2[0][j] != 0)
            countStrings[0][(int) long_string_index_true2[0][j] + long_strings1 - 1] = countStrings2[0][j];
        }
        countStrings1 = null;
        countStrings2 = null;
        
        flightMaintStringsIn2 = ArrayUtil.initArray(maintLoc, strings2);
        ArrayUtil
                .populateArray(
                        flightMaintStringsIn2,
                        contents.getProperty(RoutingFileConstants.FLIGHT_MAINTENANCE_STRINGS_IN));
        flightMaintStringsOut2 = ArrayUtil.initArray(maintLoc, strings2);
        ArrayUtil
                .populateArray(
                        flightMaintStringsOut2,
                        contents.getProperty(RoutingFileConstants.FLIGHT_MAINTENANCE_STRINGS_OUT));
        
        flightMaintStringsIn = ArrayUtil.initArray(maintLoc, strings);
        flightMaintStringsOut = ArrayUtil.initArray(maintLoc, strings);
        for (int i = 0; i < maintLoc; i++){
            for (int j = 0; j < strings1; j++){
                if (long_string_index_true1[0][j] != 0)
                    flightMaintStringsIn[i][(int) long_string_index_true1[0][j] - 1] = flightMaintStringsIn1[i][j];
            }
        }
        for (int i = 0; i < maintLoc; i++){
            for (int j = 0; j < strings2; j++){
                if (long_string_index_true2[0][j] != 0)
                    flightMaintStringsIn[i][(int) long_string_index_true2[0][j] + long_strings1 - 1] = flightMaintStringsIn2[i][j];
            }
        }
        for (int i = 0; i < maintLoc; i++){
            for (int j = 0; j < strings1; j++){
                if (long_string_index_true1[0][j] != 0)
                    flightMaintStringsOut[i][(int) long_string_index_true1[0][j] - 1] = flightMaintStringsOut1[i][j];
            }
        }
        for (int i = 0; i < maintLoc; i++){
            for (int j = 0; j < strings2; j++){
                if (long_string_index_true2[0][j] != 0)
                    flightMaintStringsOut[i][(int) long_string_index_true2[0][j] + long_strings1 - 1] = flightMaintStringsOut2[i][j];
            }
        }
        flightMaintStringsIn1 = null;
        flightMaintStringsIn2 = null;
        flightMaintStringsOut1 = null;
        flightMaintStringsOut2 = null;
        
        propDelay = ArrayUtil.initArray(1, strings);
        ArrayUtil.populateArray(propDelay,
                contents.getProperty(RoutingFileConstants.PROPOGATION_DELAY));
        
        stringsInSolution = ArrayUtil.initArray(1, strings);
        for (int i = 0; i < strings; i++){
            stringsInSolution[0][i] = i+1;
        }
        
        //contents = FileUtil.readHatFile(hatDataFile);
        
        
    }

    public int getStrings()
    {
        return strings;
    }

    public int getGroundArcs()
    {
        return groundArcs;
    }

    public int getMaintLocations()
    {
        return maintLoc;
    }

    public int getFlightLegs()
    {
        return flightLegs;
    }

    public int getAirCrafts()
    {
        return airCrafts;
    }

    public double[][] getPropDelay()
    {
        return propDelay;
    }

    public double[][] getCover()
    {
        return cover;
    }

    public double[][] getStringsInSolution()
    {
        return stringsInSolution;
    }

    public double[][] getFlightMaintStringsIn()
    {
        return flightMaintStringsIn;
    }

    public double[][] getFlightMaintGroundIn()
    {
        return flightMaintGroundIn;
    }

    public double[][] getFlightMaintStringsOut()
    {
        return flightMaintStringsOut;
    }

    public double[][] getFlightMaintGroundOut()
    {
        return flightMaintGroundOut;
    }

    public double[][] getCountStrings()
    {
        return countStrings;
    }

    public double[][] getCountGround()
    {
        return countGround;
    }
}
