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

public class ChartsClass {
    /**
     * Создание объекта-графика для эмпирической функции распределения
     * @param proc СП
     * @return График эмпирической функции распределения
     */
    public static JFreeChart getExperimentalDistributionFunctionChart(double[] proc){
        var lenOfSeries = CalculateClass.getNumOfDistFunctionSamples(proc.length);
        var data = CalculateClass.getDistributionFunctionData(proc, lenOfSeries);

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
        var numOfBins = CalculateClass.getNumOfBins(proc.length);
        var data = CalculateClass.getDensityFunctionData(proc, numOfBins);

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
        var lenOfSeries = CalculateClass.getNumOfCorrFunctionSamples(corrFuncParams, proc.length);
        var data = CalculateClass.getCorrelationFunctionData(proc, lenOfSeries);

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

        var data = DatasetUtils.sampleFunction2D(func, min, max, 1000, "theoretical dist");

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

        var data = DatasetUtils.sampleFunction2D(func, min, max, 1000, "theoretical dens");

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

        var data = DatasetUtils.sampleFunction2D(func, min, max, 1000, "theoretical corr");

        return ChartFactory.createXYLineChart(
                "Теоретическая корреляционная функция",
                "Сдвиг СП",
                "Коэффициент автокорреляции",
                data
        );
    }


}
