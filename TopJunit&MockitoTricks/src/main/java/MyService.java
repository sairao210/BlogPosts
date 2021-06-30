import java.util.List;
import java.util.stream.Collectors;

/**
 * Sample service which has functions to shaw case the capabilities of JUnit and Mockito
 */
public class MyService {

    public Integer functionToMock(Integer input) throws Exception {
        return input * 2 + 10;
    }

    public List<Integer> functionToMock(List<Integer> inputList) {
        return inputList.stream()
                .map(input -> input * 2 + 10)
                .collect(Collectors.toList());
    }
}
