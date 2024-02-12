package com.zero.codesandbox;

import com.zero.codesandbox.judge.service.JudgeService;
import com.zero.codesandbox.model.ExecuteCodeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

@SpringBootTest
class MainApplicationTests {

    @Resource
    JudgeService judgeService;

    @Test
    void contextLoads() {
        // System.out.println(judgeService.getLanguages());
        ExecuteCodeRequest request = new ExecuteCodeRequest();
        request.setLanguage("java");
        request.setCode("import java.util.*;\n" +
                "\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        \n" +
                "        Scanner in = new Scanner(System.in);\n" +
                "        \n" +
                "        int a = in.nextInt();\n" +
                "        int b = in.nextInt();\n" +
                "        \n" +
                "        System.out.print(a + b);\n" +
                "        in.close()\n" +
                "        \n" +
                "    }\n" +
                "}");
        request.setInputs(Arrays.asList("1 2", "1 3", "3 4"));
        System.out.println(judgeService.executeCode(request));
    }

}
