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
package edu.upenn.sas.matthews.ms.basics.spec;

public class MSnSpectrum extends MassSpectrum {

    int msStage;
    int precScanNumber;

    double precMz;
    int precZ;

    public MSnSpectrum(Peak[] peakArr, int msStage) {
        super(peakArr);
        this.msStage = msStage;
    }

    /**
     * Get the stage of the current MSn spectrum.
     * @return the stage of the current MSn spectrum.
     */
    public int getMsStage() {
        return msStage;
    }

    /**
     * Get the precursor scan number.
     * @return the scan number of the precursor scan.
     */
    public int getPrecScanNumber() {
        return precScanNumber;
    }

    /**
     * Set the precursor scan number.
     * @param precScanNumber the number of the precursor scan.
     */
    public void setPrecScanNumber(int precScanNumber) {
        this.precScanNumber = precScanNumber;
    }

    /**
     * Get the m/z value of the precursor peak.
     * @return the m/z value of the precursor peak.
     */
    public double getPrecMz() {
        return precMz;
    }

    /**
     * Set the m/z value of the precursor peak.
     * @param precMz the m/z value of the precursor peak.
     */
    public void setPrecMz(double precMz) {
        this.precMz = precMz;
    }

    /**
     * Get the charge state of the precursor peak.
     * @return the charge state of the precursor peak.
     */
    public int getPrecZ() {
        return precZ;
    }

    /**
     * Set the charge state of the precursor peak.
     * @param precZ the charge state of the precursor peak.
     */
    public void setPrecZ(int precZ) {
        this.precZ = precZ;
    }

    /**
     * Get the string presentation of the MSn spectrum.
     * @return the string presentation of the MSn spectrum.
     */
    @Override
    public String toString() {
        return "MS" + msStage + " Spectrum, Scan " + scanNumber + ", RT " + rt;
    }

}
