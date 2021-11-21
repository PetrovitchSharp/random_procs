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
import org.jfree.data.general.DatasetUtils;
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
    public static JFreeChart getExperimentalDistributionFunctionChart(double[] proc){
        var lenOfSeries = getNumOfDistFunctionSamples(proc.length);
        var data = getExperimentalDistributionFunctionData(proc, lenOfSeries);

        return ChartFactory.createXYStepChart(
                "Эмпирическая функция распределения",
                "Значение случ. величины",
                "F(x)",
                data
        );
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
    public static JFreeChart getExperimentalCorrelationFunctionChart(double[] proc, CorrelationFunction corrFuncParams){
        var lenOfSeries = getNumOfCorrFunctionSamples(corrFuncParams, proc.length, 0.1);
        var data = getExperimentalCorrelationFunctionData(proc, lenOfSeries, corrFuncParams);

        return ChartFactory.createXYLineChart(
                "Эмпирическая корреляционная функция",
                "Сдвиг СП",
                "Коэффициент автокорреляции",
                data
        );
    }

    /**
     * Создание объекта-графика для теоретической функции распределения
     * @param distFuncParams Параметры распределения
     * @param proc СП
     * @return График теоретической функции распределения
     */
    public static JFreeChart getTheoreticalDistributionFunctionChart(DistributionLaw distFuncParams, double[] proc){
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

        var data = getTheoreticalDistributionFunctionData(func, min, max, 1000);

        return ChartFactory.createXYLineChart(
                "Теоретическая функция распределения",
                "Значение случ. величины",
                "F(x)",
                data
        );
    }

    /**
     * Создание объекта-графика для теоретической плотности вероятности
     * @param distFuncParams Параметры распределения
     * @param proc СП
     * @return График теоретической плотности вероятности
     */
    public static JFreeChart getTheoreticalDensityFunctionChart(DistributionLaw distFuncParams, double[] proc){
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

        var data = getTheoreticalDensityFunctionData(func, min, max, 1000);

        return ChartFactory.createXYLineChart(
                "Теоретическая плотность распределения",
                "Значение случ. величины",
                "Частота",
                data
        );
    }

    /**
     * Создание объекта-графика для теоретической корреляционной функции
     * @param corrFuncParams Параметры КФ
     * @param proc СП
     * @return График теоретической КФ
     */
    public static JFreeChart getTheoreticalCorrelationFunctionChart(CorrelationFunction corrFuncParams, double[] proc){
        Function2D func = null;
        var min = StatUtils.min(proc);
        var max = StatUtils.max(proc);

        if (corrFuncParams.getKindCorrelationFunction().equals("Монотонная")){
            func = new MonotoneCorrelationFunction(corrFuncParams.getAttenuationRates());
        }
        if (corrFuncParams.getKindCorrelationFunction().equals("Колебательная")){
            func = new OscillatingCorrelationFunction(corrFuncParams.getAttenuationRates(), corrFuncParams.getOscillationFrequencyValue());
        }

        var data = getTheoreticalCorrelationFunctionData(func, min, max, 1000);

        return ChartFactory.createXYLineChart(
                "Теоретическая корреляционная функция",
                "Сдвиг СП",
                "Коэффициент автокорреляции",
                data
        );
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
        return (int)Math.round(Math.exp(1)*Math.log(numOfSamples));
    }

    /**
     * Расчет количества отсчетов для графика КФ
     * @param corrFunc Параметры КФ
     * @param numOfSamples Количество отсчетов
     * @param eps Погрешность аппроксимации
     * @return Количество отсчетов для графика КФ
     */
    private static int getNumOfCorrFunctionSamples(CorrelationFunction corrFunc, int numOfSamples, double eps){
        return (int)Math.round(numOfSamples / getTimeStep(corrFunc, numOfSamples) * numOfSamples / eps);
    }
}
