package charts;

import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import params.CorrelationFunction;

import java.util.Arrays;

import static math.Calculations.getCorrelation;
import static math.Calculations.getTimeStep;
import static math.Utils.getLeftShiftedArray;
import static math.Utils.getRightShiftedArray;

/**
 * Методы, генерирующие данные для визуализации
 */
public class DataFactory {
    /**
     * Подготовка набора данных для визуализации эмпирической функции распределения
     * @param proc СП
     * @param lenOfSeries Количество точек графика
     * @return Набор данных для визуализации эмпирической функции распределения
     */
    public static XYSeriesCollection getDistributionFunctionData(double[] proc, int lenOfSeries){
        Arrays.sort(proc);

        var shift = proc.length / (lenOfSeries - 1);

        var series = new XYSeries("dist function");
        for (var i = 0; i < proc.length; i+=shift){
            series.add(proc[i],(double) i/proc.length);
        }
        series.add(proc[proc.length-1],1);

        return new XYSeriesCollection(series);
    }

    /**
     * Подготовка набора данных для визуализации гистограммы распределения
     * @param proc СП
     * @param numOfBins Количество столбцов гистограммы
     * @return Набор данных для визуализации гистограммы распределения
     */
    public static HistogramDataset getDensityFunctionData(double[] proc, int numOfBins){
        var hist = new HistogramDataset();
        hist.addSeries("density", proc, numOfBins);
        hist.setType(HistogramType.SCALE_AREA_TO_1);

        return hist;
    }

    /**
     * Подготовка набора данных для визуализации эмпирической корреляционной функции
     * @param proc СП
     * @param lenOfSeries Количество точек графика
     * @param corrFunction Параметры КФ
     * @return Набор данных для визуализации эмпирической корреляционной функции
     */
    public static XYSeriesCollection getCorrelationFunctionData(double[] proc, int lenOfSeries, CorrelationFunction corrFunction){
        var series = new XYSeries("corr function");
        var shift = proc.length / (lenOfSeries - 1);
        var timeStep = getTimeStep(corrFunction, lenOfSeries);

        for (var i = 0; i < proc.length; i+=shift){
            var ls = getLeftShiftedArray(proc, i);
            var rs = getRightShiftedArray(proc, i);
            series.add(timeStep*i, getCorrelation(ls, rs));
        }
        series.add(timeStep*(proc.length - 1), 0);

        return new XYSeriesCollection(series);
    }
}
