package params;

public class RandomProcess {
    private int numberOfSamples;
    private String waysOfGeneration;
    private CorrelationFunction correlationFunction;
    private DistributionLaw distributionLaw;
    private HypothesisCheck hypothesisCheck;

    public CorrelationFunction getCorrelationFunction() {
        return correlationFunction;
    }

    public void setCorrelationFunction(CorrelationFunction correlationFunction) {
        this.correlationFunction = correlationFunction;
    }

    public DistributionLaw getDistributionLaw() {
        return distributionLaw;
    }

    public void setDistributionLaw(DistributionLaw distributionLaw) {
        this.distributionLaw = distributionLaw;
    }

    public HypothesisCheck getHypothesisCheck() {
        return hypothesisCheck;
    }

    public void setHypothesisCheck(HypothesisCheck hypothesisCheck) {
        this.hypothesisCheck = hypothesisCheck;
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    public void setNumberOfSamples(int numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    public String getWaysOfGeneration() {
        return waysOfGeneration;
    }

    public void setWaysOfGeneration(String waysOfGeneration) {
        this.waysOfGeneration = waysOfGeneration;
    }
}
