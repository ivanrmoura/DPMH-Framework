package br.ufma.lsdi.bpad.util;

import net.sourceforge.jFuzzyLogic.FIS;

public class FileUtil {

    public static FIS readFIS(){
        String fileName = "fcls\\FIS.fcl";
        FIS fis = FIS.load(fileName,true);

        if( fis == null ) {
            System.err.println("Can't load file: '" + fileName + "'");
            return null;
        }
        return fis;
    }
}
