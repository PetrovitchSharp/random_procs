package charts;

import org.jfree.data.function.Function2D;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import params.CorrelationFunction;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import static math.Calculations.getCorrelation;
import static math.Calculations.getTimeStep;

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
        var sorted = Arrays.copyOf(proc, proc.length);

        Arrays.sort(sorted);

        var shift = sorted.length / (lenOfSeries - 1);

        var series = new XYSeries("Эмпирическая функция распределения");
        for (var i = 0; i < sorted.length; i+=shift){
            series.add(sorted[i],(double) i/sorted.length);
        }
        series.add(sorted[sorted.length-1],1);

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
     * @return Набор данных для визуализации эмпирической корреляционной функции
     */
    public static XYSeries getExperimentalCorrelationFunctionData(double[] proc, int lenOfSeries){
        var series = new XYSeries("Эмпирическая корреляционная функция");
        var shift = proc.length / (lenOfSeries - 1);

        var last_twenty = new ArrayBlockingQueue<Double>(20);

        for (var i = 0; i < proc.length; i++){
            series.add(i, getCorrelation(proc,i));
            if (i >= 20){
                last_twenty.poll();
            }

            last_twenty.add(getCorrelation(proc,i));

            if (checkLastTwenty(last_twenty, 0.05)) break;
        }

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
            if (i == 0){
                eps = 0;
            }
            series.add(i*timeStep, func.getValue(timeStep*i)+eps);
        }
        series.add((proc.length - 1)*timeStep, 0.05);

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
            series.add(i*timeStep, realFunction.getValue(timeStep*i));
            i++;
        }

        return series;
    }

    /**
     * Проверка последних 20 показателей корреляции
     * @param lt Массив из последних 20 значений корреляции
     * @param eps Ограничение на величину корреляции
     * @return Находятся ли последние 20 значений в заданном коридоре или нет
     */
    private static boolean checkLastTwenty(ArrayBlockingQueue<Double> lt, double eps){
        for (var val:
                lt) {
            if (Math.abs(val) > eps) return false;
        }

        return true;
    }
}
