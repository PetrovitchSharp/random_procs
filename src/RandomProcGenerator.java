import params.RandomProcess;

import java.util.Date;
import java.util.Random;

import static math.Calculations.getTimeStep;

/**
 * Методы генерации СП
 */
public class RandomProcGenerator {
    /**
     * Генерация случайного процесса
     * @param proc Параметры случайного процесса
     * @return Случайный процесс
     */
    public static double[] generateRandomProc(RandomProcess proc){
        double[] sample = null;

        if (proc.getWaysOfGeneration().equals("с заданным законном распределения")){
            if (proc.getDistributionLaw().getKindDistributionLaw().equals("Нормальное")){
                sample = getNormalDistribution(
                        proc.getNumberOfSamples(),
                        proc.getDistributionLaw().getDispersion(),
                        proc.getDistributionLaw().getExpectedValue()
                );
            }
            if (proc.getDistributionLaw().getKindDistributionLaw().equals("Равномерное")){
                sample = getUniformDistribution(
                        proc.getNumberOfSamples(),
                        proc.getDistributionLaw().getLeft(),
                        proc.getDistributionLaw().getRight()
                );
            }
            if (proc.getDistributionLaw().getKindDistributionLaw().equals("Показательное")){
                sample = getExponentialDistribution(
                        proc.getNumberOfSamples(),
                        proc.getDistributionLaw().getIntensity()
                );
            }
        }
        if (proc.getWaysOfGeneration().equals("корреляционная функция")){
            if (proc.getCorrelationFunction().getKindCorrelationFunction().equals("Монотонная")){

                sample = getMonotonousCorrFunction(
                        proc.getNumberOfSamples(),
                        proc.getCorrelationFunction().getAttenuationRates(),
                        getTimeStep(
                                proc.getCorrelationFunction(),
                                proc.getNumberOfSamples()
                        )
                );
            }
            if (proc.getCorrelationFunction().getKindCorrelationFunction().equals("Колебательная")){
                sample = getOscillatoryCorrFunction(
                        proc.getNumberOfSamples(),
                        proc.getCorrelationFunction().getAttenuationRates(),
                        proc.getCorrelationFunction().getOscillationFrequencyValue(),
                        getTimeStep(
                                proc.getCorrelationFunction(),
                                proc.getNumberOfSamples()
                        )
                );
            }
        }

        return sample;
    }

    /**
     * Генерация равномерного на [0;1) распределения
     * @param numOfSamples Количество отсчетов
     * @return Массив чисел, равномерно распределенных на [0;1)
     */
    private static double[] generateUniformDistribution(int numOfSamples){
        var rnd = new Random();
        return rnd.doubles(numOfSamples).toArray();
    }

    /**
     * Получение равномерного на [a;b] распределения
     * @param numOfSamples Количество отсчетов
     * @param left Левая граница равномерного распределения
     * @param right Правая граница равномерного распределения
     * @return Массив чисел, равномерно распределенных на [a;b]
     */
    private static double[] getUniformDistribution(int numOfSamples, double left, double right){
        var sample = generateUniformDistribution(numOfSamples);
        for (var i = 0; i < numOfSamples; i++) {
            sample[i] = (right-left)*sample[i] + left;
        }
        return sample;
    }

    /**
     * Получение экспоненциального распределения с заданной интенсивностью
     * @param numOfSamples Количество отсчетов
     * @param intensity Интенсивность
     * @return Массив чисел, имеющих экспоненциальное распределение
     */
    private static double[] getExponentialDistribution(int numOfSamples, double intensity){
        var sample = generateUniformDistribution(numOfSamples);
        for (var i = 0; i < numOfSamples; i++) {
            sample[i] = -(1./intensity)*Math.log(1-sample[i]);
        }
        return sample;
    }

    /**
     * Получение нормального распределения с заданным средним и дисперсией
     * @param numOfSamples Количество отсчетов
     * @param dispersion Дисперсия
     * @param mean Среднее
     * @return Массив чисел, имеющих нормальное распределение
     */
    private static double[] getNormalDistribution(int numOfSamples, double dispersion, double mean){
        var sample = getStandardDistribution(numOfSamples, 120);
        for (var i = 0; i < numOfSamples; i++) {
            sample[i] = sample[i]*Math.sqrt(dispersion)+mean;
        }

        return sample;
    }

    /**
     * Получение стандартного распределения
     * @param numOfSamples Количество отсчетов
     * @param count Размерность сумм
     * @return Массив чисел, имеющих стандартное распределение
     */
    private static double[] getStandardDistribution(int numOfSamples, int count){
        var sample = generateUniformDistribution(numOfSamples*count);
        var standard = new double[numOfSamples];
        for (var i = 0; i < numOfSamples; i++) {
            for (var j = 0; j < count; j++){
                standard[i]+=sample[count*i+j];
            }
        }

        var mean = (double)count/2;
        var std = Math.sqrt(count/12.);

        for (var i = 0; i < numOfSamples; i++) {
            standard[i] = (standard[i] - mean) / std;
        }
        return standard;
    }

    /**
     * Получение СП с монотонной КФ
     * @param numOfSamples Количество отсчетов
     * @param attenuationRate Показатель затухания
     * @param delta Шаг по временной шкале
     * @return Массив чисел с заданной монотонной КФ
     */
    private static double[] getMonotonousCorrFunction(int numOfSamples, double attenuationRate, double delta){
        var sample = new double[numOfSamples];
        var rnd = new Random();
        rnd.setSeed(new Date().getTime());

        var gamma = attenuationRate * delta;
        var p = Math.exp(-gamma);

        var b = Math.exp(-gamma);
        var a = Math.sqrt(1-Math.pow(p,2));

        sample[0] = a*(2*rnd.nextDouble()-1);

        for (var i = 1; i < numOfSamples; i++) {
            sample[i] = a*(2*rnd.nextDouble()-1) + b*sample[i-1];
        }

        return sample;
    }

    /**
     * Получение СП с колебательной КФ
     * @param numOfSamples Количество отсчетов
     * @param attenuationRate Показатель затухания
     * @param oscillationFrequency Частота колебаний
     * @param delta Шаг по временной шкале
     * @return Массив чисел с заданной колебательной КФ
     */
    private static double[] getOscillatoryCorrFunction(int numOfSamples, double attenuationRate, double oscillationFrequency, double delta){
        var sample = new double[numOfSamples];
        var rnd = new Random(new Date().getTime());
        double x_prev, x_curr;

        var gamma = attenuationRate * delta;
        var p = Math.exp(-gamma);
        var gamma_0 = oscillationFrequency * delta;
        var alpha_0 = p*(Math.pow(p,2)-1)*Math.cos(gamma_0);
        var alpha_1 = 1 - Math.pow(p,4);

        var sub =  Math.sqrt(Math.pow(alpha_1,2)-4*Math.pow(alpha_0,2));
        var pw = Math.pow(alpha_1,2);
        var sgn = (Math.pow(alpha_1,2) > sub)?1:-1;

        var a_0 = Math.sqrt((Math.pow(alpha_1,2) - sgn*sub)/2);
        var a_1 = alpha_0/alpha_1;
        var b_1 = 2*p*Math.cos(gamma_0);
        var b_2 = -Math.pow(p,2);

        x_prev = (2*rnd.nextDouble()-1);
        sample[0] = x_prev = a_0*x_prev;
        x_curr = (2*rnd.nextDouble()-1);
        sample[1] = a_0*x_curr + x_prev*a_1 + b_1*sample[0];
        x_prev = x_curr;

        for (var i = 2; i < numOfSamples; i++) {
            x_curr = (2*rnd.nextDouble()-1);
            sample[i] = x_curr*a_0 + x_prev*a_1 + b_1*sample[i-1] + b_2*sample[i-2];
            x_prev = x_curr;
        }

        for (var i = 0; i < numOfSamples; i++) {
            sample[i] /= (1./3);
        }

        return sample;
    }
}
