package correlationFunctions;

import org.jfree.data.function.Function2D;

public class OscillatingCorrelationFunction implements Function2D {
    private double _attenuationRate, _oscillationFrequency;

    public OscillatingCorrelationFunction(double attenuationRate, double oscillationFrequency){
        _attenuationRate = attenuationRate;
        _oscillationFrequency = oscillationFrequency;
    }

    @Override
    public double getValue(double v) {
        return Math.exp(-_attenuationRate*v) * Math.cos(_oscillationFrequency*v);
    }
}
