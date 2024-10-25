JAVA_FILE_PATH = "/home/shurui/projects/test_case_generation/target/Subject.java"
XML_FILE_PATH = "/home/shurui/projects/test_case_generation/target/24Assign1all.xml"
OPENAI_API_KEY = ""
DEEPSEEK_API_KEY = ""

TEST_TIME_LIMIT = 50
TEST_CASE_LIMIT = 33

EVERY_TEST_METHOD_LIMIT = 2
INSTRUCTION_LIMIT = 1500
MAX_RETRY = 1
LOG_PATH = "/home/shurui/projects/test_case_generation/results/log.txt"
RESULT_PATH = "/home/shurui/projects/test_case_generation/results/result.txt"

SYSTEM_PROMPT = "You are a helpful assistant for a software engineer. You are asked to generate test cases for a Java class and its subclasses. You should provide test cases for the methods in the classes to ensure comprehensive test coverage. The test cases should cover various scenarios and edge cases for the methods. You can use the provided Java class and XML file to generate the test cases. Please follow the instructions and generate the test cases accordingly."

MAIN_CLASS_FORMAT = """"
public class Subject {[CLASSFIELDS]
}
"""

CLASS_FORMAT = """
    public class [CLASS_NAME] {
        [METHODS]
    }
"""

PARAMETERS = """
        /** The string to be parsed. */
        private char[] chars = null;
        /** The current position in the string. */
        private int pos = 0;
        /** The maximum position in the string. */
        private int len = 0;
        /** The start of a token. */
        private int i1 = 0;
        /** The end of a token. */
        private int i2 = 0;
        /** Indicates whether names stored in the map should be converted to lower case. */
        private boolean lowerCaseNames = false;
"""

PROMPT12 = """Please generate test cases for the Java class `Subject` and its subclasses [CLASSNAME].

Your test case should cover These methods below:
```java
[MYEXPECTEDMETHODS]
```

Please format each test case as follows:

```java
@Test
public void testXXXX() throws Throwable {
    if (debug)
        System.out.format("%n%s%n", "RegressionY_TestZ.testXXXX");
    // Test code here
}
```

- Replace `testXXXX` with a unique test method name.
- Replace `"RegressionY_TestZ.testXXXX"` with the corresponding test identifier.
- Insert the actual test code where indicated.

Include any necessary code headers and base headers in your output, such as import statements and class definitions.
Your test case code should only include the test methods and the necessary headers (There is no need for a main test class!). Do not include any additional code or comments.
For example, your output should look like this:

```java
import org.junit.Test;
import static org.junit.Assert.*;

public class SubjectTest {
    //MUST INCLUDE
    public static boolean debug = false;
    private final Subject.NumberTool numberTool = new Subject().new NumberTool();
    private final Subject.TextTool textTool = new Subject().new TextTool();
    
    @Test
    public void test0001() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "Regression0_Test0.test0001");
        // Test code here
    }

    @Test
    public void test0002() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "Regression0_Test0.test0002");
        // Test code here
    }
    // Additional test methods...
}
```

Ensure that each test case is well-structured and follows Java coding conventions. The tests should cover various scenarios and edge cases for the methods in the `Subject` class and its subclasses.
TestCase start index: [STARTINDEX]
Please Generate 1 to 2 test cases. Each cases should include NO LESS THAN 100 INSTRUCTIONS!
Additionally, here is a very important instruction: Remember, do not directly use private and protected methods, try to use public methods to cover private methods!

Your generate test cases:
"""

PROMPT3 = """Please generate test cases for the Java class `Subject` and its subclasses [CLASSNAME].

Your test case should cover These methods below:
```java
[MYEXPECTEDMETHODS]
```

Please format each test case as follows:

```java
@Test
public void testXXXX() throws Throwable {
    if (debug)
        System.out.format("%n%s%n", "RegressionY_TestZ.testXXXX");
    // Test code here
}
```

- Replace `testXXXX` with a unique test method name.
- Replace `"RegressionY_TestZ.testXXXX"` with the corresponding test identifier.
- Insert the actual test code where indicated.

Include any necessary code headers and base headers in your output, such as import statements and class definitions.
Your test case code should only include the test methods and the necessary headers (There is no need for a main test class!). Do not include any additional code or comments.
For example, your output should look like this:

```java
import org.junit.Test;
import static org.junit.Assert.*;

public class SubjectTest {
    //MUST INCLUDE
    public static boolean debug = false;
    
    @Test
    public void test0001() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "Regression0_Test0.test0001");
        Subject.ParameterTool parameterTool = new Subject().new ParameterTool();
        // Test code here
    }

    @Test
    public void test0002() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "Regression0_Test0.test0002");
        Subject.ParameterTool parameterTool = new Subject().new ParameterTool();
        // Test code here
    }
    // Additional test methods...
}
```

Ensure that each test case is well-structured and follows Java coding conventions. The tests should cover various scenarios and edge cases for the methods in the `Subject` class and its subclasses.
TestCase start index: [STARTINDEX]
Please Generate 1 to 3 test cases. Each cases should include NO LESS THAN 100 INSTRUCTIONS!
Additionally, here is a very important instruction: Remember, do not directly use private or protected methods or fields!
Try to use public methods to cover private methods and fields! 

Your generate test cases:
"""


RESULT_FORMAT = """
[IMPORTS]

public class SubjectTest {
    //MUST INCLUDE
    public static boolean debug = false;
    private final Subject.NumberTool numberTool = new Subject().new NumberTool();
    private final Subject.TextTool textTool = new Subject().new TextTool();

[TESTCASES]

}
"""
