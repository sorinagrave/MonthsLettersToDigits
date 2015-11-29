package net.ctomer.sorina.monthsletterstodigits;

/**
 * Created by sorina.
 */
public class BitwiseBasedMonthProcessor implements MonthProcessor {
    private static final int SMALLEST_THREE_DIGIT_31_MULTIPLE = 124;
    private static final int BIGGEST_THREE_DIGIT_31_MULTIPLE = 992;

    private char aDigitChar;
    private int janNumber;

    public void findFebruary(){
        for(janNumber = SMALLEST_THREE_DIGIT_31_MULTIPLE; janNumber <= BIGGEST_THREE_DIGIT_31_MULTIPLE; janNumber += 31) {
            // this is JAN
            char[] janDigits = String.valueOf(janNumber).toCharArray();
            aDigitChar = janDigits[1];
            // we know R = 0, add it to the cumulative map in position 0 to begin each combination
            int currentMaskBitMap = 0x1;
            for (char c : janDigits) {
                int cMask = (0x1 << Character.getNumericValue(c));
                if((cMask & currentMaskBitMap) > 0){
                    // digit repeating - exclude this combination and reset the mask
                    currentMaskBitMap = 0x1;
                    break;
                }
                // add distinct digit to the mask
                currentMaskBitMap |= cMask;
            }
            if(currentMaskBitMap != 0x1){
                // currentMaskBitMap hasn't been reset, therefore it is valid
                int febNumber = processMayMask(currentMaskBitMap);
                if(febNumber > 0){
                    System.out.println("FEB = " + febNumber);
                    return;
                }
            }
        }
    }
    private int processMayMask(int currentJanMaskBitMap){
        for(int mayNumber =  SMALLEST_THREE_DIGIT_31_MULTIPLE; mayNumber <= BIGGEST_THREE_DIGIT_31_MULTIPLE; mayNumber +=31){
            if(mayNumber == janNumber){
                // can't have MAY = JAN
                continue;
            }
            char[] mayDigits = String.valueOf(mayNumber).toCharArray();
            // second letter must be A
            if(mayDigits[1] != aDigitChar){
                continue;
            }
            int currentMaskBitMap = currentJanMaskBitMap;
            for (int k = 0; k < mayDigits.length; k++) {
                if(k == 1){
                    // second letter should be A, this is added already to the mask, skip it
                    continue;
                }
                int cMask = (0x1 << Character.getNumericValue(mayDigits[k]));
                if((cMask & currentMaskBitMap) > 0){
                    // digit repeating - exclude this combination and reset the mask
                    currentMaskBitMap = currentJanMaskBitMap;
                    break;
                }
                // add distinct digit to the mask
                currentMaskBitMap |= cMask;
            }
            if(currentMaskBitMap != currentJanMaskBitMap){
                // currentMaskBitMap hasn't been reset, therefore it is valid
                int febNumber = processAprMask(currentMaskBitMap);
                if(febNumber > 0) return febNumber;
            }
        }
        return -1;
    }
    private int processAprMask(int currentJanMayMaskBitMap){
        for(int k = 1; k < 10; k++){
            // take the unused mask digits one by one except for zero where we know it will not be valid
            int pLetterBitMask = (0x1 << k);
            if((pLetterBitMask & currentJanMayMaskBitMap) == 0){
                // if current digit added to "A" digit is divisible by 3, that is'P'
                if((k + Character.getNumericValue(aDigitChar))%3 == 0){
                    currentJanMayMaskBitMap |= pLetterBitMask;
                    int nFeb = processFebMask(currentJanMayMaskBitMap);
                    if(nFeb > 0){
                        return nFeb;
                    }
                }
            }
        }
        return -1;
    }
    private int processFebMask(int currentJanMayAprMaskBitMap){
        int nCurrent = 0;
        int[] febDigits = new int[3];
        for(int k = 1; k < 10; k++){
            // take the remaining digits one by one by applying 1-9 mask
            if(((0x1 << k) & currentJanMayAprMaskBitMap) == 0){
                febDigits[nCurrent] = k;
                nCurrent++;
            }
        }
        int febNum = processFebCombination(febDigits, 1, 0, 2);
        if (febNum > 0) {
            return febNum;
        }
        febNum = processFebCombination(febDigits, 1, 2, 0);
        if (febNum > 0) {
            return febNum;
        }
        febNum = processFebCombination(febDigits, 0, 1, 2);
        if (febNum > 0) {
            return febNum;
        }
        febNum = processFebCombination(febDigits, 0, 2, 1);
        if (febNum > 0) {
            return febNum;
        }
        febNum = processFebCombination(febDigits, 2, 1, 0);
        if (febNum > 0) {
            return febNum;
        }
        febNum = processFebCombination(febDigits, 2, 0, 1);
        if (febNum > 0) {
            return febNum;
        }
        return -1;
    }
    private int processFebCombination (int[] febDigits, int i, int j, int k){
        // divisibility by 4 checked from the last 2 digits
        if(((febDigits[k] & 0x11) == 0 && (febDigits[j] & 0x1) == 0) ||
                ((febDigits[k] & 0x11) == 0x10 && (febDigits[j] & 0x1) == 0x1)) {
            int febNumber = febDigits[i] * 100 + febDigits[j] * 10 + febDigits[k];
            if (febNumber % 7 == 0) {
                return febNumber;
            }
        }
        return -1;
    }
}
