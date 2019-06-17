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

public class MassSpectrum {

	double[] mzArr;
	double[] hArr;

    int scanNumber;
	float rt;
	double tic;

	/**
	 * Get an instance of a mass spectrum, by default it is an MS1 spectrum.
	 * @param peakArr an array of Peak instances.
	 */
	public MassSpectrum(Peak[] peakArr) {
		if (peakArr == null || peakArr.length == 0)
			return;

		int peakNum = peakArr.length;
		mzArr = new double[peakNum];
		hArr = new double[peakNum];
		for (int i = 0; i < peakNum; i++) {
			mzArr[i] = peakArr[i].mz;
			hArr[i] = peakArr[i].h;
		}
	}

	/**
	 * Get the retention time of the spectrum.
	 * @return the retention time of the spectrum.
	 */
	public float getRt() {
		return rt;
	}

	/**
	 * Set the retention time of the spectrum.
	 * @param rt the given retention time.
	 */
	public void setRt(float rt) {
		this.rt = rt;
	}

	/**
	 * Get the scan number of a mass spectrum.
	 * @return
	 */
	public int getScanNumber() {
		return scanNumber;
	}

	/**
	 * Set the scan number of a mass spectrum.
	 * @param scanNumber the scan number of the current spectrum.
	 */
	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}

	/**
	 * Get the number of peaks of the current spectrum.
	 * @return the number of peaks of the current spectrum.
	 */
	public int getNumPeaks() {
		if (mzArr == null)
			return 0;

		return mzArr.length;
	}

	/**
	 * Get the TIC value of the current spectrum.
	 * @return the TIC value of the current spectrum.
	 */
	public double getTic() {
		return tic;
	}

	/**
	 * Set the TIC value of the current spectrum.
	 * @param tic the TIC value of the current spectrum.
	 */
	public void setTic(float tic) {
        this.tic = tic;
    }

	/**
	 * Get the string presentation of the spectrum.
	 * @return the string presentation of the spectrum.
	 */
	public String toString() {
		return "MS1 Spectrum, Scan " + scanNumber + ", RT " + rt;
	}
	
}