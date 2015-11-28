package net.ctomer.sorina;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sorina.
 */
public class HashMapBasedMonthProcessor implements MonthProcessor {

    private static final int SMALLEST_THREE_DIGIT_31_MULTIPLIER = 100/31 + 1;
    private static final int BIGGEST_THREE_DIGIT_31_MULTIPLIER = 999/31;

    public void findFebruary(){
        // STEP 1 - work out the combinations for JAN and MAY
        ArrayList<HashMap<Integer,Character>> divisibleBy31Combinations = getDivisibleBy31Combinations();
        for(HashMap<Integer,Character> valid31Combination : divisibleBy31Combinations ){

            // STEP 2 - from the remaining digits work out combinations for APR for each JAN + MAY combination
            ArrayList<HashMap<Integer,Character>> valid30Combinations = getValid30Combinations(valid31Combination);
            if(valid30Combinations != null && valid30Combinations.size() > 0) {
                for(HashMap<Integer,Character> valid30Combination:valid30Combinations) {

                    // STEP 3 - the remaining digits are FEB - work out if any combinations are divisible by 28
                    List<Integer> febDigits = extractFebDigits(valid31Combination,valid30Combination);
                    int febNumber = getValid28Combination(febDigits);
                    if(febNumber > 0){
                        System.out.println(String.format("FEB = %s", febNumber));
                        return;
                    }
                }
            }
        }
    }
    // Work out APR after JAN and MAY
    private ArrayList<HashMap<Integer,Character>> getValid30Combinations(HashMap<Integer,Character> valid31Combination){
        ArrayList<HashMap<Integer,Character>> valid30Combinations = new ArrayList<>();
        int aKey = getValueForAChar(valid31Combination);
        if(aKey <= 0) return null; // shouldn't happen

        //Find the P in APR - it cannot start with zero because R = 0
        for(int i = 1; i <= 9; i++){
            if(valid31Combination.get(i) != null) continue; // used in JAN & MAY
            int aprNumber = aKey + i;
            if(aprNumber%3 == 0){
                HashMap<Integer,Character> valid30Combination = new HashMap<>(2);
                valid30Combination.put(i,'P');
                valid30Combinations.add(valid30Combination);
            }
        }
        return valid30Combinations;
    }

    private ArrayList<HashMap<Integer,Character>> getDivisibleBy31Combinations(){
        ArrayList<HashMap<Integer,Character>> validNumbersPossibilities = new ArrayList<>();

        for(int i=SMALLEST_THREE_DIGIT_31_MULTIPLIER;i<=BIGGEST_THREE_DIGIT_31_MULTIPLIER;i++){
            // this is JAN
            int iThreeDigit31Multiple = i * 31;
            HashMap<Integer,Character> janMap = generateJanMap(iThreeDigit31Multiple);
            if(janMap == null) continue;

            for(int j=SMALLEST_THREE_DIGIT_31_MULTIPLIER;j<=BIGGEST_THREE_DIGIT_31_MULTIPLIER;j++){
                if( i==j) continue;
                // this is MAY
                int jThreeDigit31Multiple = j * 31;
                HashMap<Integer,Character> janMayMap = generateJanAndMayMap(jThreeDigit31Multiple, janMap);
                if(janMayMap == null) continue;
                validNumbersPossibilities.add(janMayMap);
            }
        }
        return validNumbersPossibilities;
    }
    // have the number for JAN - allow only where none of the digits can be zero and they must all be different
    private HashMap<Integer,Character> generateJanMap(int janThreeDigitNumber) {
        int janFirstDigit = findFirstDigit(janThreeDigitNumber);
        if(janFirstDigit == 0) return null;
        int janSecondDigit = findSecondDigit(janThreeDigitNumber);
        if(janSecondDigit == 0 || janSecondDigit == janFirstDigit) return null;
        int janThirdDigit = findThirdDigit(janThreeDigitNumber);
        if(janThirdDigit == 0 || janThirdDigit == janFirstDigit || janThirdDigit == janSecondDigit) return null;

        HashMap<Integer,Character> validJanMap = new HashMap<>(3);
        validJanMap.put(janFirstDigit,'J');
        validJanMap.put(janSecondDigit,'A');
        validJanMap.put(janThirdDigit,'N');
        return validJanMap;
    }
    // have the number for MAY - allow only non zero digits different from JAN except for second digit
    private HashMap<Integer,Character> generateJanAndMayMap(int mayThreeDigitNumber, HashMap<Integer,Character> janMap){
        int mayFirstDigit = findFirstDigit(mayThreeDigitNumber);
        if(mayFirstDigit == 0 || janMap.get(mayFirstDigit) != null) return null;
        // Second digit corresponds to A - must be the same as Jan
        int maySecondDigit = findSecondDigit(mayThreeDigitNumber);
        if(maySecondDigit != getValueForAChar(janMap)) return null;
        int mayThirdDigit = findThirdDigit(mayThreeDigitNumber);
        if(mayThirdDigit == mayFirstDigit || mayThirdDigit == maySecondDigit || mayThirdDigit == 0 || janMap.get(mayThirdDigit) != null) return null;

        HashMap<Integer,Character> janAndMayMap = copyHashMap(janMap);
        janAndMayMap.put(mayFirstDigit,'M');
        janAndMayMap.put(mayThirdDigit,'Y');
        return janAndMayMap;
    }

    // ideally generate permutations programmatically by recursion
    private int getValid28Combination(List<Integer> digits){
        int n1 = digits.get(0)*100 + digits.get(1)*10 + digits.get(2);
        if (n1 % 28 == 0) return n1;

        int n2 = digits.get(0)*100 + digits.get(2)*10 + digits.get(1);
        if (n2 % 28 == 0) return n2;

        int n3 = digits.get(1)*100 + digits.get(0)*10 + digits.get(2);
        if (n3 % 28 == 0) return n3;

        int n4 = digits.get(1)*100 + digits.get(2)*10 + digits.get(0);
        if (n4 % 28 == 0) return n4;

        int n5 = digits.get(2)*100 + digits.get(0)*10 + digits.get(1);
        if (n5 % 28 == 0) return n5;

        int n6 = digits.get(2)*100 + digits.get(1)*10 + digits.get(0);
        if (n6 % 28 == 0) return n6;
        return -1;
    }
    private int getValueForAChar(HashMap<Integer,Character> values){
        for(Integer key:values.keySet()){
            if(values.get(key).equals('A')){
                return key;
            }
        }
        return -1;
    }

    //Feb digits are the one that are not used in JAN, APR, MAY - extract any non matching
    private ArrayList<Integer> extractFebDigits(HashMap<Integer,Character> aprMap, HashMap<Integer,Character> janMayMap){
        ArrayList<Integer> febDigits = new ArrayList<>(3);
        for(int i=1;i<=9;i++){
            if(aprMap.get(i) == null && janMayMap.get(i) == null){
                febDigits.add(i);
            }
        }
        return febDigits;
    }
    // better to use our own that cloneable for copying hash maps
    private HashMap<Integer,Character> copyHashMap(HashMap<Integer,Character> source){
        HashMap<Integer,Character> dest = new HashMap<>();
        for(Integer key:source.keySet()){
            dest.put(key,source.get(key));
        }
        return dest;
    }
    private int findSecondDigit(int n){
        return (n%100 - n%10)/10;
    }
    private int findFirstDigit(int n){
        return (n - n%100)/100;
    }
    private int findThirdDigit(int n){
        return n%10;
    }
}
