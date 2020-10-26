/**
 * Copyright 2019 Lin He, Megan L. Matthews
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
package edu.upenn.sas.matthews.ms.basics.protein;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Peptide {

    private String seq;
    public Residue[] resArray;
    private float mass;

    private final char DELIM_START = '(';
    private final char DELIM_END = ')';

    public Peptide(String seq) {
        this.seq = seq;
        resArray = toResArr();

        mass = 0;
        if (resArray != null) {
            char[] bSeqArr = new char[resArray.length];
            for (int i = 0, max = resArray.length; i < max; i++) {
                Residue r = resArray[i];
                mass += r.getMonoMass();
                bSeqArr[i] = r.getCode().charAt(0);
            }
        }
    }

    public float getMass() {
        return mass;
    }

    public Residue[] getResArr() {
        return resArray;
    }

    private Residue[] toResArr() {
        if (seq == null || seq.length() == 0) {
            return null;
        }

        List<Residue> resList = new ArrayList<>();

        for (int i = seq.length() - 1; i >= 0; i--) {
            int begin = i;
            int end = i + 1;
            if (seq.charAt(i) == DELIM_END) {
                while (--i >= 0 && seq.charAt(i) != DELIM_START) {}
                i--;
                if (i > 0 && seq.charAt(i) == DELIM_END) {
                    while (--i > 0 && seq.charAt(i) != DELIM_START) {}
                    i--;
                }
            }
            begin = i;
            BasicResidue r = i >= 0 ? BasicResidue.getBasicResidueByCode(seq.charAt(begin)) : null;
            if (r == null) {
                resList.clear();
                break;
            }
            if (end - begin == 1) {
                resList.add(r);
            } else {
                String modStr = seq.substring(begin + 1, end);
                float modMass = Float.parseFloat(modStr.substring(1, modStr.indexOf(DELIM_END)));
                if (modStr.indexOf(DELIM_END) != modStr.length() - 1) {
                    modMass += Double.parseDouble(modStr.substring(modStr.indexOf(DELIM_START, 2) + 1,
                            modStr.length() - 1));
                }
                resList.add(new ModifiedResidue(r, modMass));
            }
        }

        int len = resList.size();
        Residue[] ret = new Residue[len];
        for (int i = 0; i < len; i++) {
            ret[i] = resList.get(len - i - 1);
        }

        return ret;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Residue res : resArray) {
            sb.append(res.getCodeInSeq());
        }
        return sb.toString();
    }

    public String getBasicSeq() {
        StringBuilder sb = new StringBuilder();
        for (Residue res : resArray) {
            sb.append(res.getCode());
        }
        return sb.toString();
    }

    public double[] getFragIonMassArr(IonType ionType) {
        double[] ret = new double[resArray.length - 1];
        double[] ntermMassArr = new double[resArray.length - 1];
        double[] ctermMassArr = new double[resArray.length - 1];

        // calculate the mass arrays;
        double ntermMass = 0;
        for (int i = 0; i < resArray.length - 1; i++) {
            ntermMass += resArray[i].getMonoMass();
            ntermMassArr[i] = ntermMass;
            ctermMassArr[resArray.length - i - 2] = mass - ntermMass;
        }

        Arrays.fill(ret, 0);
        boolean isNTerm = ionType.isNTerm();
        double massShift = ionType.getMassShift();
        double[] massArr = isNTerm ? ntermMassArr : ctermMassArr;
        for (int i = 0; i < ret.length; i++) {
            ret[i] = massArr[i] + massShift;
        }

        return ret;
    }

}