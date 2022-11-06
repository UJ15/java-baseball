package baseball.engine;

import baseball.model.BallStatus;
import baseball.model.Numbers;

public interface BaseBallGameEngine {
    BallStatus createBallStatus(Numbers answer, Numbers userNumbers);

    Numbers parseToNumbers(String number);

    Numbers generateAnswer();

    boolean isCorrect(BallStatus ballStatus);
}
