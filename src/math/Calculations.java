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
     * @param proc СП
     * @param shift Сдвиг
     * @return Коэффициент автокорреляции
     */
    public static double getCorrelation(double[] proc, int shift){
        var len = proc.length;

        var sumOfShifts = 0.;
        var sumOfSquaredDeviations = 0.;

        var mean = getMean(proc);
        for (var pr:proc) {
            sumOfSquaredDeviations += Math.pow(pr - mean,2);
        }

        for (var i = 0; i < len - shift; i++){
            sumOfShifts += (proc[i] - mean)*(proc[i+shift] - mean);
        }

        return sumOfShifts / sumOfSquaredDeviations;
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
        var tauMax = 3/corrFunction.getAttenuationRates();
        return tauMax / numOfSamples;
    }
}
