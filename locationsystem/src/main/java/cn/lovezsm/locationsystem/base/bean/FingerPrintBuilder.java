package cn.lovezsm.locationsystem.base.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class FingerPrintBuilder {

    public static FingerPrint build(String name, File avgFile, File stdFile){
        FingerPrint fingerPrint = null;

        try {

            BufferedReader avgReader = new BufferedReader(new FileReader(avgFile));
            String s = avgReader.readLine();
            int apNum = s.split(" ").length;
            int lineNum = 1;

            while (avgReader.readLine()!=null){
                lineNum++;
            }
            avgReader.close();
            Scanner avgScanner = new Scanner(avgFile);
            Scanner stdScanner = new Scanner(stdFile);
            double[][] avg = new double[lineNum][apNum];
            double[][] std = new double[lineNum][apNum];
            for (int i=0;i<lineNum;i++){
                for (int j =0;j<apNum;j++){
                    avg[i][j] = avgScanner.nextDouble();
                    std[i][j] = stdScanner.nextDouble();
                }
            }
            avgScanner.close();
            stdScanner.close();
            fingerPrint = new FingerPrint(name,avg,std);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fingerPrint;
    }
}
