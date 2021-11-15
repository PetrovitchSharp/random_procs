package densityFunctions;

import org.jfree.data.function.Function2D;

public class ExponentialDensityFunction implements Function2D {
    private double _intensity;

    public ExponentialDensityFunction(double intensity){
        _intensity = intensity;
    }

    @Override
    public double getValue(double v) {
        return _intensity*Math.exp(-_intensity*v);
    }
}
