package statTests.tests;

import org.apache.commons.math3.stat.StatUtils;
import org.jfree.data.function.Function2D;
import org.jfree.data.xy.XYSeries;
import params.HypothesisCheck;
import params.RandomProcess;
import statTests.tables.KSTable;

import static charts.DataFactory.getExperimentalCorrelationFunctionData_1;
import static charts.DataFactory.getExperimentalDistributionFunctionData;

/**
 * Методы, реализующие критерий Колмогорова-Смирнова
 */
public class KSTest {
    /**
     * Тест Колмогорова-Смирнова
     * @param procSample Выборка точек из СП
     * @param realFunction Теоретическая функция (ФР или КФ)
     * @param checkParams Параметры для теста
     * @param typeOfGenerating Тип генерации СП (0 - ФР, 1 - КФ)
     * @param randProc Параметры СП
     * @return Пройден \ Не пройден тест
     */
    public static boolean ksTest(double[] procSample, Function2D realFunction, HypothesisCheck checkParams, int typeOfGenerating, RandomProcess randProc){
        var table = new KSTable();
        XYSeries subsample;

        if (typeOfGenerating == 0) {
            subsample = getExperimentalDistributionFunctionData(
                    procSample,
                    checkParams.getNumberOfDegreesOfFreedom());
        }
        else {
            subsample = getExperimentalCorrelationFunctionData_1(
                    procSample,
                    checkParams.getNumberOfDegreesOfFreedom(),
                    randProc.getCorrelationFunction(),
                    realFunction);
        }


        var testValue = ksTestValue(subsample, realFunction, randProc, typeOfGenerating);
        var critValue = table.getCritivalValue(
                checkParams.getNumberOfDegreesOfFreedom(),
                checkParams.getSignificanceLevel()
        );

        return testValue <= critValue;
    }

    /**
     * Вычисление статистики для критерия Колмогорова-Смирнова
     * @param procSample Выборка точек из СП
     * @param realFunction Теоретическая функция
     * @return Значение статистики
     */
    private static double ksTestValue(XYSeries procSample, Function2D realFunction, RandomProcess randProc, int type){
        var len = procSample.getItemCount();
        var d = new double[len];

        for (var i = 0; i < len; i++){
            var expVal = realFunction.getValue((double) procSample.getX(i));
            var realVal =(double) procSample.getY(i);
            d[i] = Math.max(
                    expVal - realVal,
                    realVal - expVal
            );
        }

        return StatUtils.max(d) * Math.sqrt(len);
    }

}
