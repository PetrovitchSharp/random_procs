import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;

public class ChartsClass {
    /**
     * Создание объекта-графика для эмпирической функции распределения
     * @param proc СП
     * @return График эмпирической функции распределения
     */
    public static JFreeChart getDistributionFunctionChart(double[] proc){
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
    public static JFreeChart getDensityFunctionHistogram(double[] proc){
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
     * @return График КФ
     */
    public static JFreeChart getCorrelationFunctionChart(double[] proc, CorrelationFunction corrFuncParams){
        var lenOfSeries = CalculateClass.getNumOfCorrFunctionSamples(corrFuncParams, proc.length);
        var data = CalculateClass.getCorrelationFunctionData(proc, lenOfSeries);

        return ChartFactory.createXYLineChart(
                "Корреляционная функция",
                "Сдвиг СП",
                "Коэффициент автокорреляции",
                data
        );
    }


}
