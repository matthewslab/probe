/**
 * Copyright 2019 Lin He, Megan L. Matthews
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
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
package edu.upenn.sas.matthews.ms.basics.protein;

public class BasicResidue implements Residue {

    private String code;
    private String symbol;
    private String name;
    private String composition;
    private double monoMass;

    private static final String BASIC_RES_STRING = "ARNDCEQGHILKMFPSTWYV";

    // Basic residues;
    public static final BasicResidue[] BASIC_RESIDUES = {
            new BasicResidue("A", "Ala", "Alanine", "C3H5NO", 71.03711),
            new BasicResidue("R", "Arg", "Arginine", "C6H12N4O", 156.10111),
            new BasicResidue("N", "Asn", "Asparagine", "C4H6N2O2", 114.04293),
            new BasicResidue("D", "Asp", "Aspartic Acid", "C4H5NO3", 115.02694),
            new BasicResidue("C", "Cys", "Cysteine", "C3H5NOS", 160.03065),
            new BasicResidue("E", "Glu", "Glutamic Acid", "C5H7NO3", 129.04259),
            new BasicResidue("Q", "Gln", "Glutamine", "C5H8N2O2", 128.05858),
            new BasicResidue("G", "Gly", "Glycine", "C2H3NO", 57.02146),
            new BasicResidue("H", "His", "Histidine", "C6H7N3O", 137.05891),
            new BasicResidue("I", "Ile", "Isoleucine", "C6H11NO", 113.08406),
            new BasicResidue("L", "Leu", "Leucine", "C6H11NO", 113.08406),
            new BasicResidue("K", "Lys", "Lysine", "C6H12N2O", 128.09496),
            new BasicResidue("M", "Met", "Methionine", "C5H9NOS", 131.04049),
            new BasicResidue("F", "Phe", "Phenylalanine", "C9H9NO", 147.06841),
            new BasicResidue("P", "Pro", "Proline", "C5H7NO", 97.05276),
            new BasicResidue("S", "Ser", "Serine", "C3H5NO2", 87.03203),
            new BasicResidue("T", "Thr", "Threonine", "C4H7NO2", 101.04768),
            new BasicResidue("W", "Trp", "Tryptophan", "C11H10N2O", 186.07931),
            new BasicResidue("Y", "Tyr", "Tyrosine", "C9H9NO2", 163.06333),
            new BasicResidue("V", "Val", "Valine", "C5H9NO", 99.06841)
    };

    private BasicResidue(String code, String symbol, String name, String composition, double monoMass) {
        this.code = code;
        this.symbol = symbol;
        this.name = name;
        this.composition = composition;
        this.monoMass = monoMass;
    }

    public static BasicResidue getBasicResidueByCode(char code) {
        int idx = BASIC_RES_STRING.indexOf(code);
        if (idx == -1) {
            return null;
        }
        return BASIC_RESIDUES[idx];
    }

    public String getCode() {
        return code;
    }

    public String getComposition() {
        return composition;
    }

    public double getMonoMass() {
        return monoMass;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getCodeInSeq() {
        return code;
    }

    public String toString() {
        return code;
    }

}
