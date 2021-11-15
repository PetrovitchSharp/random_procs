package distributionFunctions;

import org.apache.commons.math3.special.Erf;
import org.jfree.data.function.Function2D;

public class GaussianDistributionFunction implements Function2D {
    private double _mean, _dispersion;

    public GaussianDistributionFunction(double mean, double dispersion){
        _mean = mean;
        _dispersion = dispersion;
    }

    @Override
    public double getValue(double v) {
        return .5 * ( 1. + Erf.erf((v-_mean)/Math.sqrt(2*_dispersion)));
    }
}
