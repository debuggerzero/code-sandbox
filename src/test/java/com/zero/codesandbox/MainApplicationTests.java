package com.zero.codesandbox;

import com.zero.codesandbox.judge.service.JudgeService;
import com.zero.codesandbox.model.ExecuteCodeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;

@SpringBootTest
class MainApplicationTests {

    @Resource
    JudgeService judgeService;

    @Test
    void contextLoads() {
        // System.out.println(judgeService.getLanguages());

        // java
        // ExecuteCodeRequest request = new ExecuteCodeRequest();
        // request.setLanguage("java");
        // request.setCode("import java.util.*;\n" +
        //         "\n" +
        //         "public class Main {\n" +
        //         "    public static void main(String[] args) {\n" +
        //         "        \n" +
        //         "        Scanner in = new Scanner(System.in);\n" +
        //         "        \n" +
        //         "        int a = in.nextInt();\n" +
        //         "        int b = in.nextInt();\n" +
        //         "        \n" +
        //         "        System.out.print(a + b);\n" +
        //         "        in.close()\n" +
        //         "        \n" +
        //         "    }\n" +
        //         "}");
        // request.setInputs(Arrays.asList("1 2", "1 3", "3 4"));
        // System.out.println(judgeService.executeCode(request));

        // cpp
        // ExecuteCodeRequest request = new ExecuteCodeRequest();
        // request.setLanguage("cpp");
        // request.setCode("#include <iostream>\n" +
        //         "#include <algorithm>\n" +
        //         "#include <vector>\n" +
        //         "\n" +
        //         "using namespace std;\n" +
        //         "\n" +
        //         "struct Thing {\n" +
        //         "    int kind;\n" +
        //         "    int w;\n" +
        //         "    int v;\n" +
        //         "};\n" +
        //         "\n" +
        //         "int main() {\n" +
        //         "    \n" +
        //         "    int n, m;\n" +
        //         "    cin >> n >> m;\n" +
        //         "    \n" +
        //         "    vector<Thing> things;\n" +
        //         "    \n" +
        //         "    for (int i = 0; i < n; i++) {\n" +
        //         "        int v, w, s;\n" +
        //         "        cin >> v >> w >> s;\n" +
        //         "        if (s < 0) {\n" +
        //         "            things.push_back({-1, w, v});\n" +
        //         "        }\n" +
        //         "        else if (s == 0) {\n" +
        //         "            things.push_back({0, w, v});\n" +
        //         "        }\n" +
        //         "        else {\n" +
        //         "            for (int t = 1; t <= s; t *= 2) {\n" +
        //         "                things.push_back({-1, w * t, v * t});\n" +
        //         "                s -= t;\n" +
        //         "            }\n" +
        //         "            if (s > 0) things.push_back({-1, w * s, v * s});\n" +
        //         "        }\n" +
        //         "    }\n" +
        //         "    \n" +
        //         "    vector<int> dp(m + 1, 0);\n" +
        //         "    for (auto thing : things) {\n" +
        //         "        if (thing.kind < 0) {\n" +
        //         "            for (int i = m; i >= thing.v; i--) {\n" +
        //         "                dp[i] = max(dp[i], dp[i - thing.v] + thing.w);\n" +
        //         "            }\n" +
        //         "        }\n" +
        //         "        else {\n" +
        //         "            for (int i = thing.v; i <= m; i++) {\n" +
        //         "                dp[i] = max(dp[i], dp[i - thing.v] + thing.w);\n" +
        //         "            }\n" +
        //         "        }\n" +
        //         "    }\n" +
        //         "    \n" +
        //         "    cout << dp[m];\n" +
        //         "    \n" +
        //         "    return 0;\n" +
        //         "}");
        // request.setInputs(Collections.singletonList("4 5\n" +
        //         "1 2 -1\n" +
        //         "2 4 1\n" +
        //         "3 4 0\n" +
        //         "4 5 2"));
        // System.out.println(judgeService.executeCode(request));

        // c
        // ExecuteCodeRequest request = new ExecuteCodeRequest();
        // request.setLanguage("c");
        // request.setCode("#include <stdio.h>\n" +
        //         "\n" +
        //         "int main() {\n" +
        //         "    \n" +
        //         "    int a, b;\n" +
        //         "    scanf(\"%d%d\", &a, &b);\n" +
        //         "    \n" +
        //         "    printf(\"%d\", a + b);\n" +
        //         "    \n" +
        //         "    return 0;\n" +
        //         "}");
        // request.setInputs(Arrays.asList("1 2", "1 3", "3 4"));
        // System.out.println(judgeService.executeCode(request));


    }

}
