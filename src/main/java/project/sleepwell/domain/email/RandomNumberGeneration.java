package project.sleepwell.domain.email;

import java.util.Random;

//인증 코드 만들기
public class RandomNumberGeneration {

    public static final String makeRandomNumber() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }
}
