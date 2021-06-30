import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class MockitoTricks {

    @Mock
    private MyService service;

    @Test
    public void mockDifferentResultForEachInvocation() throws Exception {
        when(service.functionToMock(anyInt()))
                .thenReturn(3)
                .thenReturn(4)
                .thenThrow(new Exception("test exception"));

        assertEquals(3, service.functionToMock(1));
        assertEquals(4, service.functionToMock(1));
        assertThrows(Exception.class, () -> service.functionToMock(1));
    }

    @Test
    public void mockResultBasedOnInput() throws Exception {
        when(service.functionToMock(anyInt()))
                .thenAnswer((Answer<Integer>) invocation -> {
                    Integer input = invocation.<Integer>getArgument(0);
                    return input * 4;
                });

        assertEquals(36, service.functionToMock(9));
        assertEquals(64, service.functionToMock(16));

    }

    @Test
    public void captureTheArgumentsPassedToTheMock() throws Exception {
        ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.TYPE);
        when(service.functionToMock(intCaptor.capture())).thenReturn(10);

        service.functionToMock(25);
        service.functionToMock(30);
        service.functionToMock(35);

        assertEquals(3, intCaptor.getAllValues().size());
        assertIterableEquals(Arrays.asList(25,30,35), intCaptor.getAllValues());
    }

    @Test
    public void customArgumentMatcher() throws Exception {
        service.functionToMock(Arrays.asList(2, 5));
        service.functionToMock(Arrays.asList(22, 34));
        service.functionToMock(Arrays.asList(-8, -7));

        verify(service, times(3))
                .functionToMock(argThat((ArgumentMatcher<List<Integer>>) argument -> {
            // any custom logic with complex objects
            return argument.size() == 2;
        }));
    }


    @ParameterizedTest
    @MethodSource("sourceFor_exampleParametrizedTest")
    public void exampleParametrizedTest(Integer input, Integer expectedOutput) throws Exception {
        when(service.functionToMock(eq(input))).thenCallRealMethod();

        assertEquals(expectedOutput, service.functionToMock(input));
    }

    public static Stream<Arguments> sourceFor_exampleParametrizedTest() {
        return Stream.of(
                Arguments.of( 0, 10 ),
                Arguments.of( 2, 14 ),
                Arguments.of( -6, -2 )
        );
    }

}
