package charts;

import correlationFunctions.MonotoneCorrelationFunction;
import correlationFunctions.OscillatingCorrelationFunction;
import densityFunctions.ExponentialDensityFunction;
import densityFunctions.GaussianDensityFunction;
import densityFunctions.UniformDensityFunction;
import distributionFunctions.ExponentialDistributionFunction;
import distributionFunctions.GaussianDistributionFunction;
import distributionFunctions.UniformDistibutionFunction;
import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.function.Function2D;
import org.jfree.data.xy.XYSeries;
import params.CorrelationFunction;
import params.DistributionLaw;

import static charts.DataFactory.*;
import static math.Calculations.getTimeStep;

/**
 * Методы, генерирующие объекты-графики
 */
public class ChartsFactory {
    /**
     * Создание объекта-графика для эмпирической функции распределения
     * @param proc СП
     * @return График эмпирической функции распределения
     */
    public static XYSeries getExperimentalDistributionFunctionChart(double[] proc){
        var lenOfSeries = getNumOfDistFunctionSamples(proc.length);
        return getExperimentalDistributionFunctionData(proc, lenOfSeries);
    }

    /**
     * Создание объекта-гистограммы плотности
     * @param proc СП
     * @return Гистограмма плотности
     */
    public static JFreeChart getExperimentalDensityFunctionHistogram(double[] proc){
        var numOfBins = getNumOfBins(proc.length);
        var data = getExperimentalDensityFunctionData(proc, numOfBins);

        return ChartFactory.createHistogram(
                "Гистограмма распределения",
                "Значение случ. величины",
                "Частота",
                data
        );
    }

    /**
     * Создание объекта-графика для эмпирической корреляционной функции
     * @param proc СП
     * @param corrFuncParams Параметры КФ
     * @return График эмпирической КФ
     */
    public static XYSeries getExperimentalCorrelationFunctionChart(double[] proc, CorrelationFunction corrFuncParams){
        var lenOfSeries = getNumOfCorrFunctionSamples(corrFuncParams, proc.length, 0.01);
        return getExperimentalCorrelationFunctionData(proc, lenOfSeries, corrFuncParams);
    }

    /**
     * Создание объекта-графика для эмпирической корреляционной функции
     * @param proc СП
     * @param corrFuncParams Параметры КФ
     * @return График эмпирической КФ
     */
    public static XYSeries getExperimentalCorrelationFunctionChart_1(double[] proc, CorrelationFunction corrFuncParams){
        var lenOfSeries = getNumOfCorrFunctionSamples(corrFuncParams, proc.length, 0.01);
        Function2D func = null;
        if (corrFuncParams.getKindCorrelationFunction().equals("Монотонная")){
            func = new MonotoneCorrelationFunction(corrFuncParams.getAttenuationRates());
        }else {
            func = new OscillatingCorrelationFunction(corrFuncParams.getAttenuationRates(),corrFuncParams.getOscillationFrequencyValue());
        }
        return getExperimentalCorrelationFunctionData_1(proc, lenOfSeries, corrFuncParams, func);
    }

    /**
     * Создание объекта-графика для теоретической функции распределения
     * @param distFuncParams Параметры распределения
     * @param proc СП
     * @return График теоретической функции распределения
     */
    public static XYSeries getTheoreticalDistributionFunctionChart(DistributionLaw distFuncParams, double[] proc){
        Function2D func = null;
        var min = StatUtils.min(proc);
        var max = StatUtils.max(proc);

        if (distFuncParams.getKindDistributionLaw().equals("Равномерное")){
            func = new UniformDistibutionFunction(distFuncParams.getLeft(), distFuncParams.getRight());
        }
        if (distFuncParams.getKindDistributionLaw().equals("Показательное")){
            func = new ExponentialDistributionFunction(distFuncParams.getIntensity());
        }
        if (distFuncParams.getKindDistributionLaw().equals("Нормальное")){
            func = new GaussianDistributionFunction(distFuncParams.getExpectedValue(), distFuncParams.getDispersion());
        }

        return getTheoreticalDistributionFunctionData(func, min, max, 1000);
    }

    /**
     * Создание объекта-графика для теоретической плотности вероятности
     * @param distFuncParams Параметры распределения
     * @param proc СП
     * @return График теоретической плотности вероятности
     */
    public static XYSeries getTheoreticalDensityFunctionChart(DistributionLaw distFuncParams, double[] proc){
        Function2D func = null;
        var min = StatUtils.min(proc);
        var max = StatUtils.max(proc);

        if (distFuncParams.getKindDistributionLaw().equals("Равномерное")){
            func = new UniformDensityFunction(distFuncParams.getLeft(), distFuncParams.getRight());
        }
        if (distFuncParams.getKindDistributionLaw().equals("Показательное")){
            func = new ExponentialDensityFunction(distFuncParams.getIntensity());
        }
        if (distFuncParams.getKindDistributionLaw().equals("Нормальное")){
            func = new GaussianDensityFunction(distFuncParams.getExpectedValue(), distFuncParams.getDispersion());
        }

        return getTheoreticalDensityFunctionData(func, min, max, 1000);
    }

    /**
     * Создание объекта-графика для теоретической корреляционной функции
     * @param corrFuncParams Параметры КФ
     * @param proc СП
     * @return График теоретической КФ
     */
    public static XYSeries getTheoreticalCorrelationFunctionChart(CorrelationFunction corrFuncParams, double[] proc){
        Function2D func = null;
        var min = StatUtils.min(proc);
        var max = StatUtils.max(proc);

        if (corrFuncParams.getKindCorrelationFunction().equals("Монотонная")){
            func = new MonotoneCorrelationFunction(corrFuncParams.getAttenuationRates());
        }
        if (corrFuncParams.getKindCorrelationFunction().equals("Колебательная")){
            func = new OscillatingCorrelationFunction(corrFuncParams.getAttenuationRates(), corrFuncParams.getOscillationFrequencyValue());
        }

        return getTheoreticalCorrelationFunctionData(func,corrFuncParams, 1000);
    }

    /**
     * Расчет количества столбцов гистограммы по формуле Стерджесса
     * @param numOfSamples Количество отсчетов
     * @return Количество столбцов гистограммы
     */
    private static int getNumOfBins(int numOfSamples){
        return (int)Math.round(1 + 3.322*Math.log10(numOfSamples));
    }

    /**
     * Расчет количества отсчетов для графика функции распределения
     * @param numOfSamples Количество отсчетов
     * @return Количество отсчетов для графика функции распределения
     */
    private static int getNumOfDistFunctionSamples(int numOfSamples){
        //return (int)Math.round(Math.exp(2)*Math.log(numOfSamples));
        return 25;
    }

    /**
     * Расчет количества отсчетов для графика КФ
     * @param corrFunc Параметры КФ
     * @param numOfSamples Количество отсчетов
     * @param eps Погрешность аппроксимации
     * @return Количество отсчетов для графика КФ
     */
    private static int getNumOfCorrFunctionSamples(CorrelationFunction corrFunc, int numOfSamples, double eps){
        var T = getTimeStep(corrFunc, numOfSamples) * numOfSamples;
        var N1 = (int)Math.round(T/eps);
        var in = (int)Math.ceil((double) numOfSamples / N1);


        //return in+1;
        return 30;
    }
}
