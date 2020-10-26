/**
 * Copyright 2019 Lin He,Megan L.Matthews
 * <p>
 * Licensed under the Apache License,Version2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package edu.upenn.sas.matthews.ms.app;

import edu.upenn.sas.matthews.ms.basics.Constants;
import edu.upenn.sas.matthews.ms.basics.protein.IonType;
import edu.upenn.sas.matthews.ms.basics.protein.Peptide;
import edu.upenn.sas.matthews.ms.basics.spec.MSnSpectrum;
import edu.upenn.sas.matthews.ms.io.MSnFileReader;
import edu.upenn.sas.matthews.ms.view.Annotation;
import edu.upenn.sas.matthews.ms.view.MSAnnotator;
import jargs.gnu.CmdLineParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.AttributedString;
import java.util.ArrayList;

public class MS2SpecFinder {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            printUsage();
        }

        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option ms2FolderPathArg = parser.addStringOption('f', "MS2FolderPath");
        CmdLineParser.Option fragErrTolArg = parser.addDoubleOption('e', "FragmentErrorTolerance");
        CmdLineParser.Option isPPMArg = parser.addBooleanOption("isPPM");
        CmdLineParser.Option relaIntenThresholdArg = parser.addDoubleOption('i', "RelaIntenThreshold");
        CmdLineParser.Option specifiedPeptideArg = parser.addStringOption('p', "SpecifiedPeptide");
        CmdLineParser.Option minMatchingNumArg = parser.addIntegerOption('m', "MinimumMatchingPeaks");
        CmdLineParser.Option maxMatchingRelaHThresArg = parser.addIntegerOption('t', "MaximumMatchingIntensityThreshold");

        try {
            parser.parse(args);
        } catch (CmdLineParser.OptionException e) {
            System.err.println(e.getMessage());
            printUsage();
        }

        String ms2FolderPath = (String) parser.getOptionValue(ms2FolderPathArg, "E:\\msdata\\megan\\20200203\\20200221_KBV140");
        Double fragErrTol = (Double) parser.getOptionValue(fragErrTolArg, 0.2);
        Boolean isPPM = (Boolean) parser.getOptionValue(isPPMArg, false);
        Double relaIntenThreshold = (Double) parser.getOptionValue(relaIntenThresholdArg, 2.0);
        String specifiedPeptide = (String) parser.getOptionValue(specifiedPeptideArg, "Q(+.98)GCTVTVSDLYAM(-48.00)NLEPR");
        Integer minMatchingNum = (Integer) parser.getOptionValue(minMatchingNumArg, 4);
        Integer maxMatchingRelaHThres = (Integer) parser.getOptionValue(maxMatchingRelaHThresArg, 2);

        File ms1Dir = new File(ms2FolderPath);
        if (!ms1Dir.exists()) {
            System.err.println("The folder " + ms2FolderPath + " does not exist!");
            System.exit(1);
        }

        print_params(ms2FolderPath, fragErrTol, isPPM, relaIntenThreshold, specifiedPeptide, minMatchingNum,
                maxMatchingRelaHThres);
        File indir = new File(ms2FolderPath);
        final String pattern = ".ms2";
        final Peptide peptide = new Peptide(specifiedPeptide);
        double peptideMH = peptide.getMass() + Constants.WATER_MASS + Constants.PROTON_MASS;
        double[] yIons = peptide.getFragIonMassArr(IonType.Y);
        float errTol = (float) fragErrTol.doubleValue();
        float relaIntenThres = (float) relaIntenThreshold.doubleValue();

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(indir, "Ms2SpecFinderRslt.csv")));
        bw.write("File,Scan#,PrecMz,PrecZ,PrecInten,PrecMH,RT,MaxMatchedYIonIntensity");
        for (int i = 0; i < yIons.length; i++) {
            bw.write(",y" + (i + 1));
        }
        bw.write(",TotalMatchedIntensity,MassDiff\n");

        System.out.print("File,Scan#,PrecMz,PrecZ,PrecInten,PrecMH,RT,MaxMatchedYIonIntensity");
        for (int i = 0; i < yIons.length; i++) {
            System.out.print("\ty" + (i + 1));
        }
        System.out.println("TotalMatchedIntensity\tMassDiff");

        FileFilter ff = file -> file.isFile() && file.getName().endsWith(pattern);
        File[] files = indir.listFiles(ff);
        for (File file : files) {
            MSnFileReader mfr = new MSnFileReader(file.getAbsolutePath());
            while (mfr.hasNext()) {
                MSnSpectrum spec = mfr.next();
//                if (spec.getScanNumber() != 9215) continue;
                double[] mzArr = spec.getMzArr();
                double[] hArr = spec.getIntenArr();
                double maxH = 0;
                int len = mzArr.length;
                for (int i = 0; i < len; i++) {
                    if (hArr[i] > maxH) {
                        maxH = hArr[i];
                    }
                }

                double[] relaHArr = new double[len];
                for (int i = 0; i < len; i++) {
                    relaHArr[i] = hArr[i] / maxH * 100;
                }

                double[] matchedIons = new double[yIons.length];
                double maxMatchedH = 0;
                int matchNum = 0;
                ArrayList<Annotation> alist = new ArrayList<>();
                for (int ionIdx = 0; ionIdx < yIons.length; ionIdx++) {
                    double ion = yIons[ionIdx];
                    int pos = spec.searchMz(ion, errTol, isPPM);
                    if (pos >= 0) {
                        if (relaHArr[pos] < relaIntenThres) {
                            continue;
                        }
                        matchNum++;
                        matchedIons[ionIdx] = relaHArr[pos];
                        if (maxMatchedH < relaHArr[pos]) {
                            maxMatchedH = relaHArr[pos];
                        }

                        AttributedString as = new AttributedString("y" + (ionIdx + 1));
                        alist.add(new Annotation(mzArr[pos], relaHArr[pos], as, false, false));
                    } else {
                        matchedIons[ionIdx] = 0;
                    }
                }

                if (matchNum >= minMatchingNum && maxMatchedH > maxMatchingRelaHThres) {
                    double mz = spec.getPrecMz();
                    int z = spec.getPrecZ();
                    double mH = (mz - Constants.PROTON_MASS) * z + Constants.PROTON_MASS;
                    bw.write(file.getName() + "," + spec.getScanNumber()
                            + "," + mz + "," + z + "," + spec.getTic() + "," + mH + "," + spec.getRt()
                            + "," + maxMatchedH + arr2StrComma(matchedIons) + "," + getSum(matchedIons)
                            + "," + (mH - peptideMH) + "\n");
                    System.out.println(file.getName() + "\t" + spec.getScanNumber()
                            + "\t" + mz + "\t" + z + "\t" + spec.getTic() + "\t" + mH + "\t" + spec.getRt()
                            + "\t" + maxMatchedH + arr2Str(matchedIons) + "\t" + getSum(matchedIons)
                            + "\t" + + (mH - peptideMH));
                    BufferedImage bi = new BufferedImage(2000, 600, BufferedImage.TYPE_INT_BGR);
                    MSAnnotator msv = new MSAnnotator();
                    msv.drawSpectrum(bi, mz, z, mzArr, relaHArr, alist);
                    ImageIO.write(bi, "png", new File(file.getParent(),
                            file.getName().substring(0, file.getName().lastIndexOf('.'))
                                    + "_scan_" + spec.getScanNumber() + "_mz_" + mz + "_z_" + z + ".png"));
                }
            }
            mfr.close();
        }
        bw.close();
    }

    private static void print_params(String ms2FolderPath, double fragErrTol, boolean isPPM, double relaIntenThreshold,
                                     String specifiedPeptide, int minMatchingNum, int maxMatchingRelaHThres) {
        System.out.println("Parameters specified:"
                + "\n\tMS2_Folder_Path: " + ms2FolderPath
                + "\n\tFragment_Error_Tolerance: " + fragErrTol
                + "\n\tisPPM: " + isPPM
                + "\n\tRelative_Intensity_Threshold: " + relaIntenThreshold
                + "\n\tSpecifiedPeptide: " + specifiedPeptide
                + "\n\tMinMatchingPeaksNumber: " + minMatchingNum
                + "\n\tMaxMatchingRelaIntenThres: " + maxMatchingRelaHThres + "\n");
    }

    private static void printUsage() {
        System.err.println("java -jar MS2SpecFinder.jar \n"
                + "\t<-f MS2FolderPath>\n"
                + "\t<-p SpecifiedPeptide>\n"
                + "\t[-e FragmentErrorTolerance (Default: 0.2)]\n"
                + "\t[isPPM (Default: false)]\n"
                + "\t[-i RelaIntenThreshold (Default: 5)]\n"
                + "\t[-m MinimumMatchingPeaks (Default: 3)]\n"
                + "\t[-t MaximumMatchingIntensityThreshold (Default: 99)]\n");
        System.exit(1);
    }

    private static String arr2Str(double[] arr) {
        StringBuilder sb = new StringBuilder();
        for (double d : arr) {
            sb.append("\t");
            sb.append(d);
        }

        return sb.toString();
    }

    private static String arr2StrComma(double[] arr) {
        StringBuilder sb = new StringBuilder();
        for (double d : arr) {
            sb.append(",");
            sb.append(d);
        }

        return sb.toString();
    }

    private static double getSum(double[] arr) {
        double ret = 0;
        for (double d : arr) {
            ret += d;
        }

        return ret;
    }

}
