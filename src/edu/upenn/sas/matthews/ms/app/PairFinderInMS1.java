/**
 * Copyright 2019 Lin He, Megan L.Matthews
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
import edu.upenn.sas.matthews.ms.basics.spec.MSnSpectrum;
import edu.upenn.sas.matthews.ms.io.MSnFileReader;
import jargs.gnu.CmdLineParser;

import java.io.*;
import java.util.*;

public class PairFinderInMS1 {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            printUsage();
        }

        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option ms1FolderPathArg = parser.addStringOption('f', "MS1FolderPath");
        CmdLineParser.Option precMassErrTolByPPMArg = parser.addIntegerOption('e', "PrecMassErrTolByPPM");
        CmdLineParser.Option relaIntenThresholdArg = parser.addIntegerOption('i', "RelaIntenThreshold");
        CmdLineParser.Option rtDiffTolArg = parser.addIntegerOption('r', "RtDiffTol");
        CmdLineParser.Option pairProfileSimilarityThresholdArg = parser.addDoubleOption('s', "PairProfSimThres");
        CmdLineParser.Option labelMassDiffArg = parser.addDoubleOption('d', "LabelMassDiff");

        try {
            parser.parse(args);
        } catch (CmdLineParser.OptionException e) {
            System.err.println(e.getMessage());
            printUsage();
        }

        String ms1FolderPath = (String) parser.getOptionValue(ms1FolderPathArg);
        Integer precMassErrTolByPPM = (Integer) parser.getOptionValue(precMassErrTolByPPMArg, 5);
        Integer relaIntenThreshold = (Integer) parser.getOptionValue(relaIntenThresholdArg, 2);
        Double rtDiffTol = (Double) parser.getOptionValue(rtDiffTolArg, 0.5);
        Double labelMassDiff = (Double) parser.getOptionValue(labelMassDiffArg, 6.0138);
        Double pairProfileSimilarityThreshold =
                (Double) parser.getOptionValue(pairProfileSimilarityThresholdArg, 0.75);

        File ms1Dir = new File(ms1FolderPath);
        if (!ms1Dir.exists()) {
            System.err.println("The folder " + ms1FolderPath + " does not exist!");
            System.exit(1);
        }

        print_params(ms1FolderPath, precMassErrTolByPPM, relaIntenThreshold, rtDiffTol, labelMassDiff,
                pairProfileSimilarityThreshold);

        List<PrecInfo> piList = find(ms1Dir.getAbsolutePath(), precMassErrTolByPPM,
                relaIntenThreshold, pairProfileSimilarityThreshold, labelMassDiff);
        Map<String, List<PrecInfo>> groups = group(piList, precMassErrTolByPPM, true, rtDiffTol);

        File outFile = new File(ms1Dir, "PairFinderResult.csv");
        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
        bw.write("file,m/z,z,intensity,rt\n");
        for (String key : groups.keySet()) {
            for (PrecInfo pi : groups.get(key)) {
                bw.write(pi.toString() + "\n");
            }
        }
        bw.close();

        System.out.println("The result has been exported to " + outFile.getAbsolutePath());
    }

    private static void print_params(String ms1FolderPath, int precMassErrTolByPPM, int relaIntenThreshold,
                                     double rtDiffTol, double labelMassDiff, double pairProfileSimilarityThreshold) {
        System.out.println("Parameters specified:"
                + "\n\tMS1_Folder_Path: " + ms1FolderPath
                + "\n\tPrec_Mass_Err_Tol_By_PPM: " + precMassErrTolByPPM
                + "\n\tRelative_Intensity_Threshold: " + relaIntenThreshold
                + "\n\tRT_Difference_Tol_By_Minute: " + rtDiffTol
                + "\n\tPair_Profile_Similarity_Threshold: " + pairProfileSimilarityThreshold
                + "\n\tLabel_Mass_Diff: " + labelMassDiff + "\n");
    }

    private static void printUsage() {
        System.err.println("java -jar PairFinderInMS1.jar \n"
                + "\t<-f MS1_Folder_Path>\n"
                + "\t[-e Prec_Mass_Err_Tol_By_PPM (Default: 5)]\n"
                + "\t[-i Relative_Intensity_Threshold (Default: 2)]\n"
                + "\t[-r RT_Difference_Tol_By_Minute (Default: 0.5)]\n"
                + "\t[-s Pair_Profile_Similarity_Threshold (Default: 0.75)]\n"
                + "\t[-d Label_Mass_Diff (Default: 6.0138)]\n");
        System.exit(1);
    }

    public static Map<String, List<PrecInfo>> group(List<PrecInfo> piList, double errTol, boolean isPPM,
                                                    double rtDiffTol) {
        Map<String, List<PrecInfo>> groups = new HashMap<>();

        for (PrecInfo pi : piList) {
            double err = isPPM ? pi.mz * errTol / 1E6 : errTol;
            if (!groups.containsKey(pi.filename)) {
                groups.put(pi.filename, new ArrayList<>());
            }

            List<PrecInfo> lPiList = groups.get(pi.filename);
            boolean hit = false;
            for (PrecInfo lpi : lPiList) {
                if (pi.z == lpi.z && Math.abs(pi.mz - lpi.mz) < err && Math.abs(pi.rt - lpi.rt) <= rtDiffTol) {
                    if (lpi.h < pi.h) {
                        lpi.mz = pi.mz;
                        lpi.h = pi.h;
                        lpi.rt = pi.rt;
                    }
                    hit = true;
                }
            }

            if (!hit) {
                lPiList.add(pi);
            }
        }

        // remove isotopes;
        for (String key : groups.keySet()) {
            List<PrecInfo> lPiList = groups.get(key);
            Collections.sort(lPiList);
            int i = 1;
            outer: while (i < lPiList.size()) {
                PrecInfo curPi = lPiList.get(i);
                for (int j = 1; j <= i; j++) {
                    PrecInfo prevPi = lPiList.get(i - j);
                    if (curPi.z != prevPi.z) {
                        j++;
                        continue;
                    } else {
                        double err = isPPM ? (prevPi.mz * errTol / 1E6) : errTol;
                        if (curPi.mz - prevPi.mz <= err && Math.abs(curPi.rt - prevPi.rt) <= rtDiffTol) {
                            lPiList.remove(i);
                            continue outer;
                        } else {
                            double stepNum = Math.round((curPi.mz - prevPi.mz) / (Constants.NEUTRON_MASS / curPi.z));
                            if (stepNum > 5) {
                                break;
                            }
                            if (Math.abs(prevPi.mz + stepNum * Constants.NEUTRON_MASS / curPi.z - curPi.mz) <= err
                                    && Math.abs(curPi.rt - prevPi.rt) <= rtDiffTol) {
                                lPiList.remove(i);
                                continue outer;
                            }
                        }
                    }
                }
                i++;
            }
        }
        return groups;
    }

    public static List<PrecInfo> find(String ms1Dir, int precMassErrTolByPPM, int relaIntenThreshold,
                                      double pairProfileSimilarityThreshold, double labelMassDiff)
            throws IOException {
        File dir = new File(ms1Dir);
        if (!dir.exists()) {
            System.err.println("ERROR: cannot find directory " + ms1Dir);
            System.exit(1);
        }

        // get all MS1 files;
        FileFilter filter = filename -> filename.getName().endsWith(".ms1");

        List<PrecInfo> piList = new ArrayList<>();
        File[] ms1Files = dir.listFiles(filter);
        for (File ms1File : ms1Files) {
            find(ms1File, (double) precMassErrTolByPPM, relaIntenThreshold, pairProfileSimilarityThreshold,
                    labelMassDiff, piList);
        }

        return piList;
    }

    public static void find(File file, double errTol, double relaIntenThreshold, double pairProfileSimilarityThreshold,
                            double labelMassDiff, List<PrecInfo> piList) throws IOException {
        MSnFileReader mfr = new MSnFileReader(file.getAbsolutePath());

        while (mfr.hasNext()) {
            double prevMz = 0;
            int prevZ = 0;
            MSnSpectrum spec = mfr.next();
            double[] mzArr = spec.getMzArr();
            double[] hArr = spec.getIntenArr();
            int peakNum = spec.getNumPeaks();
            double maxH = 0;
            for (int i = 0; i < peakNum; i++) {
                maxH = (maxH < hArr[i]) ? hArr[i] : maxH;
            }

            for (int i = 0; i < peakNum; i++) {
                double err = mzArr[i] * errTol / 1E6;
                if (prevZ != 0 && Math.abs(mzArr[i] - prevMz - (Constants.NEUTRON_MASS / prevZ)) <= err) {
                    prevMz = mzArr[i];
                    continue;
                }
                if (hArr[i] / maxH * 100 < relaIntenThreshold) {
                    continue;
                }
                int z = huntsPair(i, spec, errTol, pairProfileSimilarityThreshold, labelMassDiff);
                if (z > 0) {
                    piList.add(new PrecInfo(mzArr[i], hArr[i], z, file.getName(), spec.getRt()));
                    prevMz = mzArr[i];
                    prevZ = z;
                }
            }
        }
        mfr.close();
    }

    public static int huntsPair(int idx, MSnSpectrum spec, double errTol, double pairProfileSimilarityThreshold,
                                double labelMassDiff) {
        double[] mzArr = spec.getMzArr();
        double[] hArr = spec.getIntenArr();
        // try different charge states;
        double mz1 = mzArr[idx];
        int z = 3;
        outer: while (z > 1) {
            double dMz = labelMassDiff / z;
            double mz2 = mz1 + dMz;
            int pos = spec.searchMz(mz2, errTol, true);
            if (pos < 0) {
                z--;
                continue;
            }

            // get the isotope profiles;
            double[] isoPrf1 = new double[] {
                    mz1, mz1 + Constants.NEUTRON_MASS / z, mz1 + 2 * Constants.NEUTRON_MASS / z
            };
            double[] isoPrf2 = new double[] {
                    mz2, mz2 + Constants.NEUTRON_MASS / z, mz2 + 2 * Constants.NEUTRON_MASS / z
            };
            for (int i = 0; i < isoPrf1.length; i++) {
                pos = spec.searchMz(isoPrf1[i], errTol, true);
                if (pos < 0) {
                    z--;
                    continue outer;
                } else {
                    isoPrf1[i] = hArr[pos];
                }

                pos = spec.searchMz(isoPrf2[i], errTol, true);
                if (pos < 0) {
                    z--;
                    continue outer;
                } else {
                    isoPrf2[i] = hArr[pos];
                }
            }

            // evaluate the similarity between two profiles;
            double denom = Math.sqrt(Math.max(norm2(isoPrf1), norm2(isoPrf2)));
            double dist = distance(isoPrf1, isoPrf2) / denom;
            if (dist <= 1 - pairProfileSimilarityThreshold) {
                return z;
            }

            z--;
        }

        return 0;
    }

    public static double norm2(double[] vec) {
        float ret = 0;
        int len = vec.length;
        for (int i = 0; i < len; i++) {
            ret += vec[i] * vec[i];
        }

        return ret;
    }

    public static double distance(double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            System.err.println("In dot production function, v1 and v2 have different length");
            return Float.NaN;
        }

        float ret = 0;
        int len = v1.length;
        for (int i = 0; i < len; i++) {
            ret += Math.pow(v1[i] - v2[i], 2);
        }

        return (float) Math.sqrt(ret);
    }

}

class PrecInfo implements Comparable<PrecInfo>, Serializable {

    double mz;
    double h;
    int z;
    String filename;
    double rt;

    public PrecInfo(double mz, double h, int z, String filename, double rt) {
        this.mz = mz;
        this.h = h;
        this.z = z;
        this.filename = filename;
        this.rt = rt;
    }

    public String toString() {
        return filename + "," + mz + "," + z + "," + h + "," + rt;
    }

    @Override
    public int compareTo(PrecInfo pi) {
        if (mz > pi.mz) {
            return 1;
        } else if (mz < pi.mz) {
            return -1;
        }

        return 0;
    }
}