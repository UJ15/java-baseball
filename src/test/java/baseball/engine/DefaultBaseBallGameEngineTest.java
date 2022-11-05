package baseball.engine;

import baseball.model.BallStatus;
import baseball.model.Numbers;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DefaultBaseBallGameEngineTest {
    private BaseBallGameEngine gameEngine;

    static class NullSourceProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(null, new Numbers(List.of(1, 2, 3))),
                    Arguments.of(new Numbers(List.of(1, 2, 3)), null),
                    Arguments.of(null, null)
            );
        }
    }

    static class ValidSourceProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(new Numbers(List.of(1, 2, 3)), new Numbers(List.of(1, 2, 3)), new int[]{3, 0}),
                    Arguments.of(new Numbers(List.of(1, 2, 3)), new Numbers(List.of(3, 2, 1)), new int[]{1, 2}),
                    Arguments.of(new Numbers(List.of(1, 2, 3)), new Numbers(List.of(4, 5, 6)), new int[]{0, 0})
            );
        }
    }

    @BeforeEach
    void setUp() {
        gameEngine = new DefaultBaseBallGameEngine(new NextStepNumberGenerator());
    }

    @Nested
    class DescribeParseToNumbers {
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"\n", "\t"})
        @DisplayName("사용자 입력 숫자가 null이거나 비었을경우, 공백문자일 경우 테스트")
        void userInputNullAndEmpty(String userInput) {
            Assertions.assertThatThrownBy(() -> gameEngine.parseToNumbers(userInput))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"asd", "12a", "1234", "!@#"})
        @DisplayName("사용자 입력에 문자가 포함되어 있거나 길이를 초과했을경우")
        void isNotValidUserInput(String notValidInput) {
            Assertions.assertThatThrownBy(() -> gameEngine.parseToNumbers(notValidInput))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("사용자 입력이 유효한 숫자일경우")
        void isValidUserInput() {
            String userInput = "123";
            Numbers actualNumbers = gameEngine.parseToNumbers(userInput);

            //해당 객체의 리스트 내에 값이 있는지 확인하기 위해 containAnswer메서드를 이용했다.
            Assertions.assertThat(actualNumbers.containAnswer(1)).isTrue();
            Assertions.assertThat(actualNumbers.containAnswer(2)).isTrue();
            Assertions.assertThat(actualNumbers.containAnswer(3)).isTrue();
        }
    }

    @Nested
    class DescribeCreateBallStatus {
        @ParameterizedTest
        @ArgumentsSource(NullSourceProvider.class)
        @DisplayName("인자중 null이 들어왔을 경우 예외 발생 테스트")
        void nullAndEmptyParameter(Numbers answer, Numbers userInput) {
            Assertions.assertThatThrownBy(() -> gameEngine.createBallStatus(answer, userInput))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ArgumentsSource(ValidSourceProvider.class)
        @DisplayName("유효한 인자가 들어왔을 경우 BallStatus 생성 테스트")
        void validParameter(Numbers answer, Numbers userInput, int[] expectedStrikeAndBall) {
            BallStatus actual = gameEngine.createBallStatus(answer, userInput);

            int[] actualStrikeAndBall = new int[]{actual.getStrike(), actual.getBall()};

            Assertions.assertThat(expectedStrikeAndBall).isEqualTo(actualStrikeAndBall);
        }
    }
}