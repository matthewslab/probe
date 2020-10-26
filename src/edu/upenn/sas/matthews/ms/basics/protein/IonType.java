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

public enum IonType {

    A("A", -26.9871, "N_TERM"),
    B("B", 1.0078, "N_TERM"),
    Y("Y", 19.0184, "C_TERM"),
    B_WATER_LOSS("B_WATER_LOSS", -17.00277, "N_TERM"),
    B_AMMONIA_LOSS("B_AMMONIA_LOSS", -16.01875, "N_TERM"),
    Y_WATER_LOSS("Y_WATER_LOSS", 1.00783, "C_TERM"),
    Y_AMMONIA_LOSS("Y_AMMONIA_LOSS", 1.99185, "C_TERM");

    private String name;
    private double massShift;
    private boolean isNTerm;

    IonType(String name, double massShift, String terminal) {
        this.name = name;
        this.massShift = massShift;
        this.isNTerm = terminal.equalsIgnoreCase("N_TERM") ? true : false;
    }

    public double getMassShift() {
        return massShift;
    }

    public boolean isNTerm() {
        return this.isNTerm;
    }

}
