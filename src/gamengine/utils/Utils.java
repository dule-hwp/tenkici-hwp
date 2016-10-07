/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamengine.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lazarusgame.LazarusWorld;

/**
 *
 * @author dusan_cvetkovic
 */
public class Utils {
    public static HashMap<String, ArrayList<Integer[]>> getBattlefildDataFromConfigFile(String battlefieldconfig, Class<?> aClass ) {
        HashMap<String, ArrayList<Integer[]>> configFile = new HashMap<>();
        try {
//            FileInputStream fstream;
//            URL url = aClass.getResource(battlefieldconfig);
            URL url = aClass.getResource(battlefieldconfig);
//            in = new BufferedReader(new InputStreamReader(url.openStream()));
//            fstream = new FileInputStream(url.getPath());
//            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith("//")) {
                    continue;
                }
                String[] splittedLine = strLine.split(" ");
                Integer[] values = new Integer[splittedLine.length - 1];
                for (int j = 1; j < splittedLine.length; j++) {
                    values[j - 1] = Integer.parseInt(splittedLine[j]);
                }

                ArrayList<Integer[]> alIntegerValues = configFile.get(splittedLine[0]);
                if (alIntegerValues == null) {
                    alIntegerValues = new ArrayList<>();
                } else {
                    alIntegerValues = configFile.get(splittedLine[0]);
                }
                alIntegerValues.add(values);
                configFile.put(splittedLine[0], alIntegerValues);

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LazarusWorld.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LazarusWorld.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return configFile;
    }
}
