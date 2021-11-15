package distributionFunctions;

import org.jfree.data.function.Function2D;

public class ExponentialDistributionFunction implements Function2D {
    private double _intensity;

    public ExponentialDistributionFunction(double intensity){
        _intensity = intensity;
    }


    @Override
    public double getValue(double v) {
        return 1. - Math.exp(-_intensity*v);
    }
}
