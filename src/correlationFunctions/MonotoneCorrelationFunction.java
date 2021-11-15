package correlationFunctions;

import org.jfree.data.function.Function2D;

public class MonotoneCorrelationFunction implements Function2D {
    private double _attenuationRate;

    public MonotoneCorrelationFunction(double attenuationRate){
        _attenuationRate = attenuationRate;
    }

    @Override
    public double getValue(double v) {
        return Math.exp(-_attenuationRate*v);
    }
}
