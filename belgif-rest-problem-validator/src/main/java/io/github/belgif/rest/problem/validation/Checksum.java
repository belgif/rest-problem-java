package io.github.belgif.rest.problem.validation;

class Checksum {

    private Checksum() {
    }

    public static boolean validateModulo97Checksum(long number) {
        int checksum = (int) (number % 100);
        long radix = number / 100;
        long remainder = radix % 97L;
        return remainder + checksum == 97L;
    }

}
