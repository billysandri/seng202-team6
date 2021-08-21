package seng202.group6;

import seng202.group6.Models.Crime;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ParserService {
    public static ArrayList<Crime> csvToArrayList(File file) throws FileNotFoundException {

        ArrayList<Crime> crimeList = new ArrayList<Crime>();

        Scanner fileReader = new Scanner(file);
        fileReader.nextLine(); //Skip header line
        while (fileReader.hasNextLine()) {
            String data = fileReader.nextLine();
            String[] fields =  data.split(",");

            Crime toBeAdded = buildCrimeFromFields(fields);

            crimeList.add(toBeAdded);


        }
        return crimeList;
    }

    private static Crime buildCrimeFromFields(String[] fields) {
        Crime crime = new Crime (
                fields[0], //Case Num
                fields[1], //Date
                fields[4], //Primary Description
                fields[5], //Secondary Description
                fields[7], //Arrest
                fields[8], //Domestic
                Integer.parseInt(fields[9]), //Beat
                Integer.parseInt(fields[10]), //Ward
                fields[6], //Location Description
                fields[14], //Latitude
                fields[15] //Longitude
        );
        return crime;
    }
}
