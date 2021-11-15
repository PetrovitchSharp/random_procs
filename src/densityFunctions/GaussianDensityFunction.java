package densityFunctions;

import org.jfree.data.function.Function2D;

public class GaussianDensityFunction implements Function2D {
    private double _mean, _dispersion;

    public GaussianDensityFunction(double mean, double dispersion){
        _mean = mean;
        _dispersion = dispersion;
    }

    @Override
    public double getValue(double v) {
        return 1./Math.sqrt(2*Math.PI*_dispersion) * Math.exp(-Math.pow((v - _mean),2)/(2*_dispersion));
    }
}
