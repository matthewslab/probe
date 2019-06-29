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
package edu.upenn.sas.matthews.ms.io;

import edu.upenn.sas.matthews.ms.basics.spec.MSnSpectrum;
import edu.upenn.sas.matthews.ms.basics.spec.Peak;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MSnFileReader {

    private boolean hasNext = false;
    private BufferedReader reader;
    private int curScanNum = 0;
    private int precScanNum = 0;
    private float precInt = 0;
    private int msStage = 1;
    private float curPrecMz = 0;
    private int curPrecZ = 0;
    private float curRt = 0;
    private List<Peak> curPeakList;
    private List<String> hLines;
    private List<String> iLines;
    private List<String> zLines;

    private String filename;

    public MSnFileReader(String file) throws IOException {
        filename = file;
        reader = new BufferedReader(new FileReader(file));
        curPeakList = new ArrayList<>();
        zLines = new ArrayList<>();
        hLines = new ArrayList<>();
        iLines = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("H\t")) {
                hLines.add(line);
            } else if (line.startsWith("S\t")) {
                hasNext = true;
                String[] elems = line.split("\t");
                if (elems.length == 3) {
                    msStage = 1;
                } else if (elems.length > 3) {
                    msStage = 2;
                    curPrecMz = Float.parseFloat(elems[3]);
                }
                curScanNum = Integer.parseInt(elems[1]);
                break;

            }
        }
    }

    /**
     * Close the file handler of the reader.
     * @throws IOException
     */
    public void close() throws IOException {
        reader.close();
    }

    /**
     * Check whether hit the end of the file.
     * @return false if the end of the file is reached, otherwise true.
     * @throws IOException
     */
    public boolean hasNext() throws IOException {
        if (hasNext) {
            return true;
        } else {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("S\t")) {
                    hasNext = true;
                    String[] elems = line.split("\t");
                    if (elems.length == 3) {
                        msStage = 1;
                    } else if (elems.length > 3) {
                        msStage = 2;
                        curPrecMz = Float.parseFloat(elems[3]);
                    }
                    curScanNum = Integer.parseInt(elems[1]);
                    break;
                }
            }
        }
        return hasNext;
    }

    /**
     * Read the next spectrum in the file.
     * @return an MSn spectrum.
     * @throws IOException
     */
    public MSnSpectrum next() throws IOException {
        hLines.clear();
        iLines.clear();
        zLines.clear();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("S\t")) {
                hasNext = true;
                if (curPeakList.size() > 0) {
                    Peak[] peakArr = curPeakList.toArray(new Peak[0]);
                    curPeakList.clear();
                    MSnSpectrum ret = new MSnSpectrum(peakArr, msStage);

                    // get the precMz and scanNum for the next spectrum;
                    String[] elems = line.split("\t");
                    if (elems.length == 3) {
                        msStage = 1;
                    } else if (elems.length > 3) {
                        msStage = 2;
                        curPrecMz = Float.parseFloat(elems[3]);
                    }
                    curScanNum = Integer.parseInt(elems[1]);
                    ret.setScanNumber(curScanNum);
                    ret.setRt(curRt);
                    ret.setTic(precInt);
                    if (msStage > 1) {
                        ret.setPrecScanNumber(precScanNum);
                    }
                    ret.setPrecMz(curPrecMz);
                    ret.setPrecZ(curPrecZ);

                    return ret;
                }

                String[] elems = line.split("\t");
                if (elems.length != 4) {
                    System.err.println("Failed in parsing S line: " + line);
                    System.exit(1);
                }
                curScanNum = Integer.parseInt(elems[1]);
                curPrecMz = Float.parseFloat(elems[3]);
            } else if (line.startsWith("I\t")) {
                iLines.add(line);
                if (line.contains("RetTime")) {
                    String[] elems = line.split("\t");
                    if (elems.length != 3) {
                        System.err.println("Failed in parsing RetTime line: " + line);
                        System.exit(1);
                    }
                    curRt = Float.parseFloat(elems[2]);
                } else if (line.contains("PrecursorScan")) {
                    String[] elems = line.split("\t");
                    if (elems.length != 3) {
                        System.err.println("Failed in parsing PrecursorScan line: " + line);
                        System.exit(1);
                    }
                    precScanNum = Integer.parseInt(elems[2]);
                } else if (line.contains("PrecursorInt")) {
                    String[] elems = line.split("\t");
                    if (elems.length != 3) {
                        System.err.println("Failed in parsing PrecursorInt line: " + line);
                        System.exit(1);
                    }
                    precInt = Float.parseFloat(elems[2]);
                }
            } else if (line.startsWith("Z\t")) {
                zLines.add(line);
                String[] elems = line.split("\t");
                if (elems.length != 3) {
                    System.err.println("Failed in parsing Z line: " + line);
                    System.exit(1);
                }
                curPrecZ = Integer.parseInt(elems[1]);
                msStage = 2;
            } else if (line.charAt(0) < 'A' || line.charAt(0) > 'Z') {
                String[] elems = line.split(" ");
                if (elems.length < 2) {
                    System.err.println("Failed in parsing peak line: " + line);
                    System.exit(1);
                }
                curPeakList.add(new Peak(Float.parseFloat(elems[0]),
                        Float.parseFloat(elems[1])));
            }
        }

        hasNext = false;

        // the last spectrum;
        Peak[] peakArr = curPeakList.toArray(new Peak[0]);
        curPeakList.clear();

        MSnSpectrum ret = new MSnSpectrum(peakArr, msStage);
        ret.setScanNumber(curScanNum);
        ret.setRt(curRt);
        ret.setTic(precInt);
        ret.setPrecMz(curPrecMz);
        ret.setPrecZ(curPrecZ);
        if (msStage > 1) {
            ret.setPrecScanNumber(precScanNum);
        }

        return ret;
    }

}
