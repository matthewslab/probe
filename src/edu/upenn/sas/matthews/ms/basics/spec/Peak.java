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

public class Peak {

	final double mz;
	final double h;

	/**
	 * Get an instance of Peak.
	 * @param mz the m/z value of a peak.
	 * @param h the intensity value of a peak.
	 */
	public Peak(double mz, double h) {
		this.mz = mz;
		this.h = h;
	}

	/**
	 * Get the string presentation of the peak.
	 * @return the string presentation of the peak.
	 */
	public String toString() {
		return mz + " (" + h + ")";
	}

}
