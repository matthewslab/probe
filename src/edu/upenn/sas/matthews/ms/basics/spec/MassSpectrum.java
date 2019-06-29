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

import java.util.Arrays;

public class MassSpectrum {

	double[] mzArr;
	double[] intenArr;

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
		intenArr = new double[peakNum];
		for (int i = 0; i < peakNum; i++) {
			mzArr[i] = peakArr[i].mz;
			intenArr[i] = peakArr[i].h;
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
	 * Get the array of m/z values.
	 * @return an array of m/z values.
	 */
	public double[] getMzArr() {
		return mzArr;
	}

	/**
	 * Get the array of intensity values.
	 * @return an array of intensity values.
	 */
	public double[] getIntenArr() {
		return intenArr;
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

	/**
	 * Look for the index of a specified m/z value in the peak list, w/ an error tolerance.
	 * @param targetMz, the target m/z value.
	 * @param errTol, the error tolerance.
	 * @param isPPM, true if the error tolerance is PPM-unit, false otherwise.
	 * @return
	 */
	public int searchMz(double targetMz, double errTol, boolean isPPM) {
		if (mzArr.length == 0) {
			return -1;
		}

		// Calculate the absolute error tolerance
		errTol = isPPM ? targetMz * errTol / 1E6 : errTol;

		int pos = Arrays.binarySearch(mzArr, targetMz);
		if (pos < 0) {
			pos = -(pos + 1);

			if (pos == 0) {
				return -1;
			}

			if (pos == mzArr.length) {
				double err = Math.abs(targetMz - mzArr[pos - 1]);
				if (err <= errTol) {
					return pos - 1;
				} else {
					return -1;
				}
			}

			double err = Math.abs(targetMz - mzArr[pos]);
			if (err <= errTol) {
				if (err < Math.abs(targetMz - mzArr[pos - 1])) {
					return pos;
				} else {
					return pos - 1;
				}
			}

			err = Math.abs(targetMz - mzArr[pos - 1]);
			if (err <= errTol) {
				return pos - 1;
			} else {
				return -1;
			}
		}

		return pos;
	}

}
