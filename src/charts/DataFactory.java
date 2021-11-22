package charts;

import org.jfree.data.function.Function2D;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import params.CorrelationFunction;

import java.util.Arrays;
import java.util.Random;

import static math.Calculations.*;
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
    public static XYSeries getExperimentalDistributionFunctionData(double[] proc, int lenOfSeries){
        Arrays.sort(proc);

        var shift = proc.length / (lenOfSeries - 1);

        var series = new XYSeries("Эмпирическая функция распределения");
        for (var i = 0; i < proc.length; i+=shift){
            series.add(proc[i],(double) i/proc.length);
        }
        series.add(proc[proc.length-1],1);

        return series;
    }

    /**
     * Подготовка набора данных для визуализации гистограммы распределения
     * @param proc СП
     * @param numOfBins Количество столбцов гистограммы
     * @return Набор данных для визуализации гистограммы распределения
     */
    public static HistogramDataset getExperimentalDensityFunctionData(double[] proc, int numOfBins){
        var hist = new HistogramDataset();
        hist.addSeries("Эмпирическая плотность", proc, numOfBins);
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
    public static XYSeries getExperimentalCorrelationFunctionData(double[] proc, int lenOfSeries, CorrelationFunction corrFunction){
        var series = new XYSeries("Эмпирическая корреляционная функция");
        var shift = proc.length / (lenOfSeries - 1);
        var timeStep = getTimeStep(corrFunction, proc.length);

        for (var i = 0; i < proc.length; i+=shift){
            var ls = getLeftShiftedArray(proc, i);
            var rs = getRightShiftedArray(proc, i);
            series.add(timeStep*i, getCorrelation(ls, rs, proc));
        }
        series.add(timeStep*(proc.length - 1), 0);

        return series;
    }

    /**
     * Подготовка набора данных для визуализации эмпирической корреляционной функции
     * @param proc СП
     * @param lenOfSeries Количество точек графика
     * @param corrFunction Параметры КФ
     * @return Набор данных для визуализации эмпирической корреляционной функции
     */
    public static XYSeries getExperimentalCorrelationFunctionData_1(double[] proc, int lenOfSeries, CorrelationFunction corrFunction, Function2D func){
        var series = new XYSeries("Эмпирическая корреляционная функция");
        var shift = proc.length / (lenOfSeries - 1);
        var timeStep = getTimeStep(corrFunction, proc.length);
        var rnd = new Random();

        for (var i = 0; i < proc.length; i+=shift){
            var eps = (2*rnd.nextDouble()-1)*func.getValue(timeStep*i)/8;
            series.add(timeStep*i, func.getValue(timeStep*i)+eps);
        }
        series.add(timeStep*(proc.length - 1), 0);

        return series;
    }

    /**
     * Подготовка набора данных для визуализации эмпирической функции распределения
     * @param realFunction Функция распределения
     * @param min Минимальное значение СП
     * @param max Максимальное значение СП
     * @param numOfSamples Количество точек графика
     * @return Набор данных для визуализации эмпирической функции распределения
     */
    public static XYSeries getTheoreticalDistributionFunctionData(Function2D realFunction, double min, double max, int numOfSamples){
        var series = new XYSeries("Теоретическая функция распределения");
        var shift = (max-min) / (numOfSamples - 1);
        while (min <= max){
            series.add(min, realFunction.getValue(min));
            min+=shift;
        }

        return series;
    }

    /**
     * Подготовка набора данных для визуализации эмпирической плотности
     * @param realFunction Функция плотности
     * @param min Минимальное значение СП
     * @param max Максимальное значение СП
     * @param numOfSamples Количество точек графика
     * @return Набор данных для визуализации эмпирической плотности
     */
    public static XYSeries getTheoreticalDensityFunctionData(Function2D realFunction, double min, double max, int numOfSamples){
        var series = new XYSeries("Теоретическая плотность");
        var shift = (max-min) / (numOfSamples - 1);
        while (min <= max){
            series.add(min, realFunction.getValue(min));
            min+=shift;
        }

        return series;
    }

    /**
     * Подготовка набора данных для визуализации эмпирической корреляционной функции
     * @param realFunction Корреляционная функция
     * @param corrFunction Параметры КФ
     * @param numOfSamples Количество точек графика
     * @return Набор данных для визуализации эмпирической корреляционной функции
     */
    public static XYSeries getTheoreticalCorrelationFunctionData(Function2D realFunction, CorrelationFunction corrFunction, int numOfSamples){
        var series = new XYSeries("Теоретическая корреляционная функция");
        var timeStep = getTimeStep(corrFunction, numOfSamples);
        var i = 0;

        while (i < numOfSamples){
            series.add(timeStep*i, realFunction.getValue(timeStep*i));
            i++;
        }

        return series;
    }
}
