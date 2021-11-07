import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ViewSwing {

    /**поле основного JFrame*/
    private static JFrame jFrame;
    /**поле панели корреляционной функции*/
    private static JPanel jpCorrelationFunction;
    /**поле панели закона распределения*/
    private static JPanel jpDistributionLaw;
    private static RandomProcess randomProcess;
    private static CorrelationFunction correlationFunction;
    private static DistributionLaw distributionLaw;
    private static HypothesisCheck hypothesisCheck;

    ViewSwing(){
        begin();
    }

    /** Метод создания форм с вводом данных*/
    private void begin(){
        jFrame = new JFrame("Генерация случайного процесса");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setPreferredSize(new Dimension(400, 630));

        JPanel jPanel = new JPanel(new VerticalLayout());

        jPanel.add(new JLabel("Параметры случайного процесса"));
        jPanel.add(new JLabel("количество отсчетов"));

        JTextField numberOfSamples = new JTextField(5);
        numberOfSamples.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!CheckParameters.isNumberOfSamples(numberOfSamples.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_NUMBER_OF_SAMPLES);
                    numberOfSamples.setText("");
                }
            }
        });
        jPanel.add(numberOfSamples);

        setJpCorrelationFunction();
        setJpDistributionLaw();

        jPanel.add(new JLabel("способ генерации случайных величин"));
        String[] sWaysOfGeneration = {"корреляционная функция", "с заданным законном распределения"};
        JRadioButton rbCorrelationFunction = new JRadioButton("корреляционная функция",true);
        JRadioButton rbDistributionLaw = new JRadioButton("с заданным законном распределения",false);
        ButtonGroup waysOfGeneration = new ButtonGroup();
        waysOfGeneration.add(rbCorrelationFunction);
        waysOfGeneration.add(rbDistributionLaw);
        rbCorrelationFunction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rbCorrelationFunction.isSelected()) {
                    jpCorrelationFunction.setPreferredSize(new Dimension(jpDistributionLaw.getWidth(),180));
                    jpCorrelationFunction.setVisible(true);
                    jpDistributionLaw.setVisible(false);
                    jpDistributionLaw.setPreferredSize(new Dimension(0,0));
                }
            }
        });
        rbDistributionLaw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rbDistributionLaw.isSelected()) {
                    jpDistributionLaw.setPreferredSize(new Dimension(jpCorrelationFunction.getWidth(),200));
                    jpDistributionLaw.setVisible(true);
                    jpCorrelationFunction.setVisible(false);
                    jpCorrelationFunction.setPreferredSize(new Dimension(0,0));
                }
            }
        });

        jPanel.add(rbCorrelationFunction);
        jPanel.add(rbDistributionLaw);
        jPanel.add(jpCorrelationFunction);
        jPanel.add(jpDistributionLaw);

        jPanel.add(new JLabel("Проверка гипотезы"));
        jPanel.add(new JLabel("критерий значимости"));

        String[] sSignificanceCriterion = {"критерий согласия Пирсона", "критерий согласия Колмогорова"};
        JComboBox significanceCriterion = new JComboBox(sSignificanceCriterion);
        significanceCriterion.setEditable(true);
        jPanel.add(significanceCriterion);

        jPanel.add(new JLabel("уровень значимости"));
        String[] sSignificanceLevel = {"0.001", "0.01", "0.025", "0.05", "0.1", "0.2"};
        JComboBox significanceLevel = new JComboBox(sSignificanceLevel);
        significanceLevel.setEditable(true);
        jPanel.add(significanceLevel);

        jPanel.add(new JLabel("количество степеней свободы"));
        JTextField numberOfDegreesOfFreedom = new JTextField(5);
        numberOfDegreesOfFreedom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!CheckParameters.isNumberOfDegreesOfFreedom(numberOfDegreesOfFreedom.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_NUMBER_OF_DEGREES_OF_FREEDOM);
                    numberOfDegreesOfFreedom.setText("");
                }
            }
        });
        jPanel.add(numberOfDegreesOfFreedom);

        JButton check = new JButton("Проверить гипотезу");
        check.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    if (numberOfDegreesOfFreedom.getText() == null) throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);
                    hypothesisCheck = new HypothesisCheck();
                    hypothesisCheck.setNumberOfDegreesOfFreedom(Integer.parseInt(numberOfDegreesOfFreedom.getText()));
                    hypothesisCheck.setSignificanceCriterion(significanceCriterion.getSelectedItem().toString());
                    hypothesisCheck.setSignificanceLevel(Double.parseDouble(significanceLevel.getSelectedItem().toString()));

                    //TODO: вызов метода проверки гипотезы, вывод сообщения
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(null,ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);
                }
            }
        });
        jPanel.add(check);

        JButton result = new JButton("Расчитать характеристики");
        result.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (numberOfSamples.getText() == null || (correlationFunction == null && distributionLaw == null)) throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);
                    randomProcess = new RandomProcess();
                    randomProcess.setCorrelationFunction(correlationFunction);
                    randomProcess.setDistributionLaw(distributionLaw);
                    randomProcess.setHypothesisCheck(hypothesisCheck);
                    randomProcess.setWaysOfGeneration(rbDistributionLaw.isSelected() ? sWaysOfGeneration[1] : sWaysOfGeneration[0]);
                    randomProcess.setNumberOfSamples(numberOfSamples.getText().isEmpty() ? null : Integer.parseInt(numberOfSamples.getText()));

                    //TODO: вызов метода рассчета результата, вызов метода для вывода информации
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(null,exception.getMessage());
                }
            }
        });
        jPanel.add(result);

        jFrame.add(jPanel);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    /** Метод создания панели корреляционной функции*/
    private void setJpCorrelationFunction(){
        jpCorrelationFunction = new JPanel(new VerticalLayout());
        jpCorrelationFunction.setPreferredSize(new Dimension(400,180));
        jpCorrelationFunction.add(new JLabel("вид корреляционной функции"));
        String[] sKindCorrelationFunction = { "Монотонная","Колебательная"};
        JComboBox kindCorrelationFunction = new JComboBox(sKindCorrelationFunction);
        kindCorrelationFunction.setEditable(true);

        JPanel jpMonotone = new JPanel(new VerticalLayout());
        jpMonotone.add(new JLabel("значение частоты колебания"));
        JTextField oscillationFrequencyValue  = new JTextField(5);
        oscillationFrequencyValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!CheckParameters.isNumberFrom0_1to10(oscillationFrequencyValue.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_OSCILLATION_FREQUENCY_VALUE);
                    oscillationFrequencyValue.setText("");
                }
            }
        });
        jpMonotone.add(oscillationFrequencyValue);
        jpMonotone.setVisible(false);

        kindCorrelationFunction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (kindCorrelationFunction.getSelectedItem().toString().equals(sKindCorrelationFunction[1])){
                    jpMonotone.setVisible(true);
                    jpMonotone.setPreferredSize(new Dimension(jpCorrelationFunction.getWidth(),50));
                } else{
                    jpMonotone.setVisible(false);
                    jpMonotone.setPreferredSize(new Dimension(0,0));
                }
            }
        });
        jpCorrelationFunction.add(kindCorrelationFunction);

        jpCorrelationFunction.add(new JLabel("показатель затухания"));
        JTextField attenuation_rates = new JTextField(5);
        attenuation_rates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!CheckParameters.isNumberFrom0_1to10(attenuation_rates.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_ATTENUATION_RATES);
                    attenuation_rates.setText("");
                }
            }
        });
        jpCorrelationFunction.add(attenuation_rates);
        jpCorrelationFunction.add(jpMonotone);
        JButton generation = new JButton("Сгенерировать");
        generation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (attenuation_rates.getText().isEmpty()
                            || (kindCorrelationFunction.getSelectedItem().toString().equals(sKindCorrelationFunction[1])
                            && oscillationFrequencyValue.getText().isEmpty()))
                        throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);

                    correlationFunction = new CorrelationFunction();
                    correlationFunction.setKindCorrelationFunction(kindCorrelationFunction.getSelectedItem().toString());
                    correlationFunction.setAttenuationRates(Double.parseDouble(attenuation_rates.getText()));
                    correlationFunction.setOscillationFrequencyValue(!oscillationFrequencyValue.getText().isEmpty()?Double.parseDouble(oscillationFrequencyValue.getText()):null);

                    //TODO: вызов метода генерации, вывод окошка с результатами
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(null,ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);
                }
            }
        });
        jpCorrelationFunction.add(generation);
        jpCorrelationFunction.setVisible(true);
    }

    /** Метод создания панели закона распределения*/
    private void setJpDistributionLaw(){
        jpDistributionLaw = new JPanel(new VerticalLayout());
        jpDistributionLaw.add(new JLabel("вид распределения"));
        String[] sKindDistributionLaw = {"Нормальное", "Равномерное","Показательное"};
        JComboBox kindDistributionLaw = new JComboBox(sKindDistributionLaw);
        kindDistributionLaw.setEditable(true);
        jpDistributionLaw.add(kindDistributionLaw);

        JPanel jpNormal = getJPNormal();
        jpNormal.setVisible(true);

        JPanel jpUniform = getJPUniform();
        jpUniform.setVisible(false);

        JPanel jpExponential = getJPExponential();
        jpExponential.setVisible(false);

        kindDistributionLaw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (kindDistributionLaw.getSelectedItem().toString().equals(sKindDistributionLaw[0])){
                    jpUniform.setVisible(false);
                    jpUniform.setPreferredSize(new Dimension(0,0));

                    jpExponential.setVisible(false);
                    jpExponential.setPreferredSize(new Dimension(0,0));

                    jpNormal.setVisible(true);
                    jpNormal.setPreferredSize(new Dimension(400,100));
                } else if (kindDistributionLaw.getSelectedItem().toString().equals(sKindDistributionLaw[1])){
                    jpNormal.setVisible(false);
                    jpNormal.setPreferredSize(new Dimension(0,0));

                    jpExponential.setVisible(false);
                    jpExponential.setPreferredSize(new Dimension(0,0));

                    jpUniform.setVisible(true);
                    jpUniform.setPreferredSize(new Dimension(400,100));
                } else{
                    jpUniform.setVisible(false);
                    jpUniform.setPreferredSize(new Dimension(0,0));

                    jpNormal.setVisible(false);
                    jpNormal.setPreferredSize(new Dimension(0,0));

                    jpExponential.setVisible(true);
                    jpExponential.setPreferredSize(new Dimension(400,100));
                }
            }
        });
        jpDistributionLaw.add(jpNormal);
        jpDistributionLaw.add(jpUniform);
        jpDistributionLaw.add(jpExponential);
        JButton generation = new JButton("Сгенерировать");
        generation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    distributionLaw = new DistributionLaw();
                    distributionLaw.setKindDistributionLaw(kindDistributionLaw.getSelectedItem().toString());

                    if (kindDistributionLaw.getSelectedItem().toString().equals(sKindDistributionLaw[0])){

                        if (((JTextField)jpNormal.getComponent(1)).getText().isEmpty()
                                && ((JTextField)jpNormal.getComponent(3)).getText().isEmpty())  throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);

                        distributionLaw.setExpectedValue(Double.parseDouble(((JTextField)jpNormal.getComponent(1)).getText()));
                        distributionLaw.setDispersion(Double.parseDouble(((JTextField)jpNormal.getComponent(3)).getText()));
                    } else if (kindDistributionLaw.getSelectedItem().toString().equals(sKindDistributionLaw[1])){

                        if (((JTextField)jpUniform.getComponent(1)).getText().isEmpty()
                                && ((JTextField)jpUniform.getComponent(3)).getText().isEmpty())  throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);
                        distributionLaw.setRight(Double.parseDouble(((JTextField)jpUniform.getComponent(1)).getText()));
                        distributionLaw.setLeft(Double.parseDouble(((JTextField)jpUniform.getComponent(3)).getText()));

                        if (distributionLaw.getLeft()>distributionLaw.getRight()) throw new Exception(ExceptionMessage.EXCEPTION_LEFT_IS_MORE_RIGHT);
                    } else{

                        if (((JTextField)jpExponential.getComponent(1)).getText().isEmpty()) throw new Exception(ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);

                        distributionLaw.setIntensity(Double.parseDouble(((JTextField)jpExponential.getComponent(1)).getText()));
                    }

                    //TODO: вызов метода генерации, вывод окошка с результатами
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(null,ExceptionMessage.EXCEPTION_NOT_ALL_PARAMETERS);
                }
            }
        });
        jpDistributionLaw.add(generation);
        jpDistributionLaw.setVisible(false);
        jpDistributionLaw.setPreferredSize(new Dimension(0,0));
    }

    /** Метод создания панели нормального распределения*/
    private JPanel getJPNormal(){
        JPanel jpNormal = new JPanel(new VerticalLayout());
        jpNormal.add(new JLabel("математическое ожидание"));
        JTextField expected_value = new JTextField(5);
        expected_value.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!CheckParameters.isNumberFrom_100to100(expected_value.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_EXPECTED_VALUE);
                    expected_value.setText("");
                }
            }
        });
        jpNormal.add(expected_value);
        jpNormal.add(new JLabel("дисперсия"));
        JTextField dispersion = new JTextField(5);
        dispersion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!CheckParameters.isNumberFrom0_1to10(dispersion.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_DISPERSION);
                    dispersion.setText("");
                }
            }
        });
        jpNormal.add(dispersion);
        return jpNormal;
    }

    /** Метод создания панели равномерного распределения*/
    private JPanel getJPUniform(){
        JPanel jpUniform = new JPanel(new VerticalLayout());
        jpUniform.add(new JLabel("правая граница"));
        JTextField right = new JTextField(5);
        right.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!CheckParameters.isNumberFrom_100to100(right.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_RIGHT);
                    right.setText("");
                }
            }
        });
        jpUniform.add(right);
        jpUniform.add(new JLabel("левая граница"));
        JTextField left = new JTextField(5);
        left.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!CheckParameters.isNumberFrom_100to100(left.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_LEFT);
                    left.setText("");
                }
            }
        });
        jpUniform.add(left);
        jpUniform.setPreferredSize(new Dimension(0,0));
        return jpUniform;
    }

    /** Метод создания панели показательного распределения*/
    private JPanel getJPExponential(){
        JPanel jpExponential = new JPanel(new VerticalLayout());
        jpExponential.add(new JLabel("интенсивность"));
        JTextField intensity = new JTextField(5);
        intensity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!CheckParameters.isIntensity(intensity.getText())){
                    JOptionPane.showMessageDialog(null, ExceptionMessage.EXCEPTION_INTENSITY);
                    intensity.setText("");
                }
            }
        });
        jpExponential.add(intensity);
        jpExponential.setPreferredSize(new Dimension(0,0));
        return jpExponential;
    }

    public static void main(String[] args) {
        new ViewSwing();
    }

    // Менеджер вертикального расположения компонентов
    private  class VerticalLayout implements LayoutManager
    {
        private Dimension size = new Dimension();

        // Следующие два метода не используются
        public void addLayoutComponent   (String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        // Метод определения минимального размера для контейнера
        public Dimension minimumLayoutSize(Container c) {
            return calculateBestSize(c);
        }
        // Метод определения предпочтительного размера для контейнера
        public Dimension preferredLayoutSize(Container c) {
            return calculateBestSize(c);
        }
        // Метод расположения компонентов в контейнере
        public void layoutContainer(Container container)
        {
            // Список компонентов
            Component list[] = container.getComponents();
            int currentY = 5;
            for (int i = 0; i < list.length; i++) {
                // Определение предпочтительного размера компонента
                Dimension pref = list[i].getPreferredSize();
                // Размещение компонента на экране
                list[i].setBounds(5, currentY, pref.width, pref.height);
                // Учитываем промежуток в 5 пикселов
                currentY += 5;
                // Смещаем вертикальную позицию компонента
                currentY += pref.height;
            }
        }
        // Метод вычисления оптимального размера контейнера
        private Dimension calculateBestSize(Container c)
        {
            // Вычисление длины контейнера
            Component[] list = c.getComponents();
            int maxWidth = 0;
            for (int i = 0; i < list.length; i++) {
                int width = list[i].getWidth();
                // Поиск компонента с максимальной длиной
                if ( width > maxWidth )
                    maxWidth = width;
            }
            // Размер контейнера в длину с учетом левого отступа
            size.width = maxWidth + 5;
            // Вычисление высоты контейнера
            int height = 0;
            for (int i = 0; i < list.length; i++) {
                height += 5;
                height += list[i].getHeight();
            }
            size.height = height;
            return size;
        }
    }
}
