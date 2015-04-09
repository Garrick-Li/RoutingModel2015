package cmu.routing.model;

import java.io.IOException;

public interface RoutingData
{
    /**
     * Parse the input file and populate the object with the data
     * 
     * @param baseInputFile Base input file
     * @param incDataFile Overriding input file with additional details 
     * @param mechanism Mechanism number
     * @throws IOException Thrown when issues with loading the file
     */




    void populateData(String baseInputFile1, String baseInputFile2,
            String baseInputFile3, String incDataFile, String hatDataFile,
            String mechanism) throws IOException;
}
