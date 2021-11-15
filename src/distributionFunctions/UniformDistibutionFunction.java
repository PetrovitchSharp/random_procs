package distributionFunctions;

import org.jfree.data.function.Function2D;

public class UniformDistibutionFunction implements Function2D {
    private double _left,_right;

    public UniformDistibutionFunction(double left, double right){
        _left = left;
        _right = right;
    }

    @Override
    public double getValue(double v) {
        return (v-_left)/(_right-_left);
    }
}
