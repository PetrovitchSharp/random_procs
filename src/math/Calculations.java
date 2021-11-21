package math;

import params.CorrelationFunction;
import params.RandomProcess;

/**
 * Математические расчеты
 */
public class Calculations {
    /**
     * Расчет теоретического мат. ожидания
     * @param proc Параметры СП
     * @return Теоретическое мат. ожидание
     */
    public static double getMathematicalExpectation(RandomProcess proc){
        var mathExp = 0.;

        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Равномерное")){
            mathExp = (proc.getDistributionLaw().getLeft() + proc.getDistributionLaw().getRight()) / 2;
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Нормальное")){
            mathExp = proc.getDistributionLaw().getExpectedValue();
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Показательное")){
            mathExp = 1. / proc.getDistributionLaw().getIntensity();
        }

        return mathExp;
    }

    /**
     * Расчет теоретической дисперсии
     * @param proc Параметры СП
     * @return Теоретическая дисперсия
     */
    public static double getTheoreticalDispertion(RandomProcess proc){
        var disp = 0.;

        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Равномерное")){
            disp = Math.pow(proc.getDistributionLaw().getRight() - proc.getDistributionLaw().getLeft(), 2) / 12;
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Нормальное")){
            disp = proc.getDistributionLaw().getDispersion();
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Показательное")){
            disp = 1. / Math.pow(proc.getDistributionLaw().getIntensity(),2);
        }

        return disp;
    }

    /**
     * Расчет теоретического коэффициента асимметрии
     * @param proc Параметры СП
     * @return Теоретический коэффициент асимметрии
     */
    public static double getTheoreticalAsymmetryCoefficient(RandomProcess proc){
        var asymCoef = 0.;

        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Равномерное")){
            asymCoef = 0;
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Нормальное")){
            asymCoef = 0;
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Показательное")){
            asymCoef = 2;
        }

        return asymCoef;
    }

    /**
     * Расчет теоретического коэффициента эксцесса
     * @param proc Параметры СП
     * @return Теоретический коэффициент эксцесса
     */
    public static double getTheoreticalKurtosisCoefficient(RandomProcess proc){
        var mathExp = 0.;

        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Равномерное")){
            mathExp = -1.2;
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Нормальное")){
            mathExp = 0;
        }
        if (proc.getDistributionLaw().getKindDistributionLaw().equals("Показательное")){
            mathExp = 6;
        }

        return mathExp;
    }

    /**
     * Расчет выборочного среднего
     * @param proc Случайный процесс
     * @return Выборочное среднее
     */
    public static double getMean(double[] proc){
        return getInitialMoment(proc,1);
    }

    /**
     * Расчет выборочной несмещенной дисперсии
     * @param proc Случайный процесс
     * @return Выборочная несмещенная дисперсия
     */
    public static double getExperimentalDispertion(double[] proc){
        var n = proc.length;

        return ((double) n / (n - 1))*getCentralMoment(proc,2);
    }

    /**
     * Расчет выборочного несмещенного коэффициента асимметрии
     * @param proc Случайный процесс
     * @return Выборочный несмещенный коэффициент асимметрии
     */
    public static double getExperimentalAsymmetryCoefficient(double[] proc){
        var n = proc.length;

        return (Math.sqrt(n*(n-1))/(n-2))*getCentralMoment(proc,3)
                /Math.pow(getCentralMoment(proc,2),1.5);
    }

    /**
     * Расчет выборочного несмещенного коэффициента эксцесса
     * @param proc Случайный процесс
     * @return Выборочный несмещенный коэффициент эксцесса
     */
    public static double getExperimentalKurtosisCoefficient(double[] proc){
        var n = proc.length;

        return (Math.pow(n,2)-1)/((n-2)*(n-3))
                *(getCentralMoment(proc,4)/Math.pow(getCentralMoment(proc,2),2)
                - 3 + 6./(n+1));
    }

    /**
     * Расчет автокорреляции СП в заданной точке
     * @param originalProcess Обрезанный СП
     * @param shiftedProcess Сдвинутый СП
     * @return Коэффициент автокорреляции
     */
    public static double getCorrelation(double[] originalProcess, double[] shiftedProcess){
        var originalMean = getMean(originalProcess);
        var shiftedMean = getMean(shiftedProcess);
        var len = originalProcess.length;

        var sumOfShifts = 0.;
        var originalSumOfSquaredDeviations = 0.;
        var shiftedSumOfSquaredDeviations = 0.;

        for (var i = 0; i < len; i++){
            sumOfShifts += (originalProcess[i] - originalMean)*(shiftedProcess[i] - shiftedMean);
            originalSumOfSquaredDeviations += Math.pow((originalProcess[i] - originalMean),2);
            shiftedSumOfSquaredDeviations += Math.pow((shiftedProcess[i] - shiftedMean), 2);
        }

        return sumOfShifts / Math.sqrt(originalSumOfSquaredDeviations) / Math.sqrt(shiftedSumOfSquaredDeviations);
    }

    /**
     * Расчет k-го начального выборочного момента
     * @param proc Случайный процесс
     * @param k Порядок момента
     * @return k-й начальный выборочный момент
     */
    private static double getInitialMoment(double[] proc, int k){
        var sum = 0.;
        for (var num:proc) {
            sum+=Math.pow(num,k);
        }

        return sum/proc.length;
    }

    /**
     * Расчет k-го центрального выборочного момента
     * @param proc Случайный процесс
     * @param k Порядок момента
     * @return k-й центральный выборочный момент
     */
    private static double getCentralMoment(double[] proc, int k){
        var sum = 0.;
        var mean = getInitialMoment(proc,1);

        for (var num:proc) {
            sum+=Math.pow((num-mean),k);
        }

        return sum/proc.length;
    }

    /**
     * Расчет "временного" сдвига для КФ
     * @param corrFunction Параметры КФ
     * @param numOfSamples Количество отсчетов
     * @return Сдвиг для КФ
     */
    public static double getTimeStep(CorrelationFunction corrFunction, int numOfSamples){
        double tau, tauMax;
        if (corrFunction.getKindCorrelationFunction().equals("Монотонная")){
            var alpha = corrFunction.getAttenuationRates();
            tau = 1. / alpha;
            tauMax = 3*tau;
        }
        else{
            var alpha = corrFunction.getAttenuationRates();
            var omega = corrFunction.getOscillationFrequencyValue();
            var mu = omega / alpha;

            tau = alpha / (Math.pow(alpha,2) + Math.pow(omega,2));
            tauMax = 3 * (1 + Math.pow(mu,2)) * tau;
        }
        return tauMax / numOfSamples;
    }
}