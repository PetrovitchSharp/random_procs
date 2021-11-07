public class CheckParameters {

    public static boolean isNumberOfSamples(String str) {
        try {
            int number = Integer.parseInt(str);
            if (number < 500 || number > 1000) return false;
        } catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    public static boolean isNumberOfDegreesOfFreedom(String str) {
        try {
            int number = Integer.parseInt(str);
            if (number < 10 || number > 26) return false;
        } catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    public static boolean isNumberFrom0_1to10(String str) {
        try {
            double number = Double.parseDouble(str);
            if (number < 0.1 || number > 10) return false;
        } catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    public static boolean isNumberFrom_100to100(String str) {
        try {
            double number = Double.parseDouble(str);
            if (number < -100 || number > 100) return false;
        } catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    public static boolean isIntensity(String str) {
        try {
            double number = Double.parseDouble(str);
            if (number < 0.01 || number > 5) return false;
        } catch(NumberFormatException e){
            return false;
        }
        return true;
    }
}
