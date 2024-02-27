package br.ufma.lsdi.bpad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AlgorithmParam {

    //private int numSlots;

    private double theta;

    private double phi;

    private int numObs;

    private double sensitivity;

}
