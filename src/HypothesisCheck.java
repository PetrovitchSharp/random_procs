public class HypothesisCheck {
    private String significanceCriterion;
    private Double significanceLevel;
    private int numberOfDegreesOfFreedom;

    public String getSignificanceCriterion() {
        return significanceCriterion;
    }

    public void setSignificanceCriterion(String significanceCriterion) {
        this.significanceCriterion = significanceCriterion;
    }

    public Double getSignificanceLevel() {
        return significanceLevel;
    }

    public void setSignificanceLevel(Double significanceLevel) {
        this.significanceLevel = significanceLevel;
    }

    public int getNumberOfDegreesOfFreedom() {
        return numberOfDegreesOfFreedom;
    }

    public void setNumberOfDegreesOfFreedom(int numberOfDegreesOfFreedom) {
        this.numberOfDegreesOfFreedom = numberOfDegreesOfFreedom;
    }
}
