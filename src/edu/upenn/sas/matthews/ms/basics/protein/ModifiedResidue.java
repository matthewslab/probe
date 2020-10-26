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

import java.text.DecimalFormat;

public class ModifiedResidue implements Residue {

    private String code;
    private String codeInSeq;
    private BasicResidue bRes;
    private String name;
    private double monoMass;
    private double modMass;

    public ModifiedResidue(BasicResidue bRes, double modMass) {
        this.bRes = bRes;
        code = bRes.getCode();
        this.modMass = modMass;
        monoMass = bRes.getMonoMass() + modMass;

        DecimalFormat df = new DecimalFormat("#.##");
        String modMassInSeq = df.format(modMass);
        codeInSeq = bRes.getCode() + "(" + ((modMass > 0) ? ("+" + modMassInSeq) : (modMassInSeq)) + ")";
        name = codeInSeq;
    }

    public BasicResidue getBasicResidue() {
        return bRes;
    }

    public double getMonoMass() {
        return monoMass;
    }

    @Override
    public String getCode() {
        return code;
    }

    public String getCodeInSeq() {
        return codeInSeq;
    }

    public double getModMass() {
        return modMass;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return codeInSeq;
    }

    @Override
    public String getSymbol() {
        return bRes.getSymbol();
    }

}
