package edu.upenn.sas.matthews.ms.basics.protein;

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
public interface Residue {

    /**
     * Gets the single-letter code of this Residue.
     * @return the single-letter code of this Residue.
     */
    public String getCode();

    /**
     * Gets the 3-letter symbol of this residue.
     * @return the 3-letter symbol of this residue.
     */
    public String getSymbol();

    /**
     * Gets the name of this residue.
     * @return the name of this residue.
     */
    public String getName();

    /**
     * Gets the mono mass of this residue.
     * @return the mono mass of this residue.
     */
    public float getMonoMass();

    /**
     * Gets the average mass of this residue.
     * @return the average mass of this residue.
     */
    public float getAverMass();

    /**
     * Gets the string that represents this residue in a protein sequence.
     * @return the string that represents this residue in a protein sequence.
     */
    public String getCodeInSeq();

    public String toString();

}
