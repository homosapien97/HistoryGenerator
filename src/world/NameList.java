package world;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Generates a list of names based on a multiline comma-separated text file.
 * Default city name list from http://data.okfn.org/data/core/world-cities
 * Default river name list from http://www.naturalearthdata.com/downloads/50m-physical-vectors/
 *
 * Created by homosapien97 on 4/15/17.
 */
public class NameList extends ArrayList<String> {
    public static final String DEFAULT_CITY_PATH = "CityNames.txt";
    public static final String DEFAULT_RIVER_PATH = "RiverNames.txt";
    public NameList(NameType nt) {
        if(nt == NameType.CITY) {
            System.out.println("Creating Name List Cities");
            try {
                Scanner s = new Scanner(new File(DEFAULT_CITY_PATH));
                while (s.hasNextLine()) {
                    this.add(s.nextLine().split(",")[0]);
                }
                s.close();
            } catch (FileNotFoundException e) {
                System.err.println(e);
            }
        } else if (nt == NameType.CITY.RIVER) {
            System.out.println("Creating Name List Rivers");
            try {
                Scanner s = new Scanner(new File(DEFAULT_RIVER_PATH));
                while(s.hasNext()) {
                    String l1 = s.nextLine();
                    String l2 = s.nextLine();
                    String l3 = s.nextLine();
                    String l4 = s.nextLine();
                    String l5 = s.nextLine();
                    this.add(l3.split(":")[1].trim());
                }
                s.close();
            } catch(FileNotFoundException e) {
                System.err.println(e);
            }
        }
    }
}
