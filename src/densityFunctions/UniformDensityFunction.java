package densityFunctions;

import org.jfree.data.function.Function2D;

public class UniformDensityFunction implements Function2D {
    private double _left,_right;

    public UniformDensityFunction(double left, double right){
        _left = left;
        _right = right;
    }

    @Override
    public double getValue(double v) {
        return 1./(_right - _left);
    }
}
