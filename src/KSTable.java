import java.util.Map;

/**
 * Таблица критических значений для критерия Колмогорова-Смирнова
 */
public class KSTable {
    /**
     * Таблица критических значений для критерия Колмогорова-Смирнова
     */
    private final double[][] _ksTable = {
            {0.32260, 0.36866, 0.40925, 0.44562, 0.48893, 0.58042},
            {0.30829, 0.35242, 0.39122, 0.42614, 0.46770, 0.55588},
            {0.29577, 0.33815, 0.37543, 0.40902, 0.44905, 0.53422},
            {0.28470, 0.32549, 0.36143, 0.39382, 0.43247, 0.51490},
            {0.27481, 0.31417, 0.34890, 0.38021, 0.41762, 0.49753},
            {0.26589, 0.30397, 0.33760, 0.36793, 0.40420, 0.48182},
            {0.25778, 0.29472, 0.32733, 0.35678, 0.39201, 0.46750},
            {0.25039, 0.28627, 0.31796, 0.34659, 0.38086, 0.45440},
            {0.24360, 0.27851, 0.30936, 0.33724, 0.37062, 0.44234},
            {0.23735, 0.27136, 0.30143, 0.32860, 0.36117, 0.43119},
            {0.23156, 0.26473, 0.29408, 0.32061, 0.35241, 0.42085},
            {0.22617, 0.25858, 0.28724, 0.31317, 0.34426, 0.41122},
            {0.22115, 0.25283, 0.28087, 0.30623, 0.33666, 0.40223},
            {0.21646, 0.24746, 0.27490, 0.29974, 0.32954, 0.39380},
            {0.21205, 0.24242, 0.26931, 0.29365, 0.32286, 0.38588},
            {0.20790, 0.23768, 0.26404, 0.28791, 0.31657, 0.37843},
            {0.20399, 0.23320, 0.25908, 0.28250, 0.31063, 0.37139}
    };

    /**
     * Соответствие уровня значимости индексу столбца в матрице
     */
    private final Map<Double, Integer> _signLevels = Map.ofEntries(
            Map.entry(0.2,0),
            Map.entry(0.1,1),
            Map.entry(0.05,2),
            Map.entry(0.025,3),
            Map.entry(0.01, 4),
            Map.entry(0.005, 5)
    );

    /**
     * Получение критического значения
     * @param degreesOfFreedom Количество степеней свободы
     * @param significanceLevel Количество уровней значимости
     * @return Критическое значение
     */
    public double getCritivalValue(int degreesOfFreedom, double significanceLevel){
        return _ksTable[degreesOfFreedom-10][_signLevels.get(significanceLevel)];
    }
}
