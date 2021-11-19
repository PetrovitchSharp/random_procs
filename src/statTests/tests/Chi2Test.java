package statTests.tests;

import org.apache.commons.math3.stat.StatUtils;
import org.jfree.data.function.Function2D;
import params.HypothesisCheck;
import statTests.tables.PearsonTable;

import java.util.Arrays;

/**
 * Методы, реализующие критерий Пирсона
 */
public class Chi2Test {
    /**
     * Тест Хи-квадрат (неприменим для случая КФ)
     * @param procSample СП
     * @param realFunction Теоретическая функция
     * @param checkParams Параметры для теста
     * @return Пройден \ не пройден тест
     */
    public static boolean chi2Test(double[] procSample, Function2D realFunction, HypothesisCheck checkParams){
        var table = new PearsonTable();

        var testValue = chi2TestValue(procSample, realFunction, checkParams.getNumberOfDegreesOfFreedom());
        var critValue = table.getCritivalValue(
                checkParams.getNumberOfDegreesOfFreedom(),
                checkParams.getSignificanceLevel()
        );

        return testValue <= critValue;
    }

    /**
     * Вычисление статистики для критерия Хи-квадрат
     * @param proc СП
     * @param realFunction Теоретическая функция
     * @param freedomDegrees Число степеней сводобы
     * @return Значение статистики
     */
    private static double chi2TestValue(double[] proc, Function2D realFunction, int freedomDegrees){
        var max = StatUtils.max(proc);
        var min = StatUtils.min(proc);
        Arrays.sort(proc);
        var shift = (max - min) / (freedomDegrees + 1);
        var statistic = 0.;

        while (min <= max - shift){
            var expDensity = getExperimentalDensity(proc, min, min +shift);
            var realDensity = realFunction.getValue(min +shift) - realFunction.getValue(min);
            if (min == max-shift){
                expDensity++;
            }
            statistic += Math.pow(expDensity - realDensity, 2)/ realDensity;
            min += shift;
        }

        return statistic * (freedomDegrees + 2);
    }

    /**
     * Вычисление экспериментальной плотности
     * @param proc СП
     * @param min Левая граница интервала
     * @param max Правая граница интервала
     * @return Экспериментальная плотность на интервале
     */
    private static double getExperimentalDensity(double[] proc, double min, double max){
        var counter = 0;
        for (double v : proc) {
            if (v < min) {
                continue;
            }
            if (v >= max) {
                break;
            }
            if (v >= min) {
                counter++;
            }
        }

        return (double) counter / proc.length;
    }
}
