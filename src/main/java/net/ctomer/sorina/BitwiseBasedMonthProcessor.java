package net.ctomer.sorina;

/**
 * Created by sorina.
 */
public class BitwiseBasedMonthProcessor implements MonthProcessor {
    private static final int SMALLEST_THREE_DIGIT_31_MULTIPLE = 124;
    private static final int BIGGEST_THREE_DIGIT_31_MULTIPLE = 992;

    int aLetterBitMask;
    int aDigit;
    int janNumber;
    int mayNumber;

    public int findFebruary(){
        for(janNumber = SMALLEST_THREE_DIGIT_31_MULTIPLE; janNumber <= BIGGEST_THREE_DIGIT_31_MULTIPLE; janNumber += 31) {
            // this is JAN
            char[] janDigits = String.valueOf(janNumber).toCharArray();
            aDigit = Character.getNumericValue(janDigits[1]);
            aLetterBitMask = 0x1 << aDigit;
            // we know R = 0, add it to the cumulative map to begin each combination
            int currentMaskBitMap = 0x1;
            for (char c : janDigits) {
                int cMask = (0x1 << Character.getNumericValue(c));
                if((cMask & currentMaskBitMap) > 0){
                    // digit repeating - exclude this combo and reset mask
                    currentMaskBitMap = 0x1;
                    break;
                }
                // add distinct digit to the mask
                currentMaskBitMap |= cMask;
            }
            if(currentMaskBitMap != 0x1){
                // currentMaskBitMap hasn't been reset, therefore it is valid
                int febNumber = processMayMask(currentMaskBitMap);
                if(febNumber > 0) return febNumber;
            }
        }
        return -1;
    }
    private int processMayMask(int currentJanMaskBitMap){
        for(mayNumber =  SMALLEST_THREE_DIGIT_31_MULTIPLE; mayNumber <= BIGGEST_THREE_DIGIT_31_MULTIPLE; mayNumber+=31){
            if(mayNumber == janNumber){
                // can't have MAY = JAN
                continue;
            }
            char[] mayDigits = String.valueOf(mayNumber).toCharArray();
            // second letter must be A
            if((aLetterBitMask & (0x1 << Character.getNumericValue(mayDigits[1]))) != aLetterBitMask){
                continue;
            }
            int currentMaskBitMap = currentJanMaskBitMap;
            for (int k = 0; k < mayDigits.length; k++) {
                if(k == 1){
                    // second letter is A, this is added already to the mask, skip it
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
        //Find the last 4 unused digits to identify P in APR - XOR on map
        int unusedDigits = ~currentJanMayMaskBitMap;

        for(int k = 1; k < 10; k++){
            // take the remaining digits one by one except for zero where we know it will not be valid
            int pLetterBitMask = (0x1 << k);
            if((pLetterBitMask & unusedDigits) > 0){
                // if current digit added to "A" digit is divisible by 3, that is'P'
                if((k + aDigit)%3 == 0){
                    currentJanMayMaskBitMap |= pLetterBitMask;
                    int nFeb = processFebMask(currentJanMayMaskBitMap);
                    if(nFeb > 0){
                        System.out.println("FEB = " + nFeb);
                        return nFeb;
                    }
                }
            }
        }
        return -1;
    }
    private int processFebMask(int currentJanMayAprMaskBitMap){
        //Find the last 4 unused digits to identify P in APR - XOR on map
        int unusedDigits = ~currentJanMayAprMaskBitMap;
        int nCurrent = 0;
        int[] febDigits = new int[3];
        for(int k = 1; k < 10; k++){
            // take the remaining digits one by one by applying 1-9 mask
            if(((0x1 << k) & unusedDigits) > 0){
                febDigits[nCurrent] = k;
                nCurrent++;
            }
        }
        return getValid28Combination(febDigits);
    }
    // ideally generate permutations programmatically by recursion
    private int getValid28Combination(int[] febDigits){
        if((febDigits[2] & 0x11) == 0 || ((febDigits[2] & 0x11) == 0x10 && (febDigits[0] & 0x1) == 0x1)){
            int n1 = febDigits[1]*100 + febDigits[0]*10 + febDigits[2];
            if(n1 % 7 == 0){
                return n1;
            }
        }
        if((febDigits[2] & 0x11) == 0 || ((febDigits[2] & 0x11) == 0x10 && (febDigits[1] & 0x1) == 0x1)){
            int n1 = febDigits[0]*100 + febDigits[1]*10 + febDigits[2];
            if(n1 % 7 == 0){
                return n1;
            }
        }
        if((febDigits[0] & 0x11) == 0 || ((febDigits[0] & 0x11) == 0x10 && (febDigits[1] & 0x1) == 0x1)){
            int n1 = febDigits[2]*100 + febDigits[1]*10 + febDigits[0];
            if(n1 % 7 == 0){
                return n1;
            }
        }
        if((febDigits[0] & 0x11) == 0 || ((febDigits[0] & 0x11) == 0x10 && (febDigits[2] & 0x1) == 0x1)){
            int n1 = febDigits[1]*100 + febDigits[2]*10 + febDigits[0];
            if(n1 % 7 == 0){
                return n1;
            }
        }
        if((febDigits[1] & 0x11) == 0 || ((febDigits[1] & 0x11) == 0x10 && (febDigits[2] & 0x1) == 0x1)){
            int n1 = febDigits[0]*100 + febDigits[2]*10 + febDigits[1];
            if(n1 % 7 == 0){
                return n1;
            }
        }
        if((febDigits[1] & 0x11) == 0 || ((febDigits[1] & 0x11) == 0x10 && (febDigits[0] & 0x1) == 0x1)){
            int n1 = febDigits[2]*100 + febDigits[0]*10 + febDigits[1];
            if(n1 % 7 == 0){
                return n1;
            }
        }
        return -1;
    }
}
