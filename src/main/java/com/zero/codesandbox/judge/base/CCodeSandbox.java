package com.zero.codesandbox.judge.base;

import cn.hutool.core.io.FileUtil;
import com.zero.codesandbox.judge.CodeSandboxTemplate;
import com.zero.codesandbox.model.ExecuteMessage;
import com.zero.codesandbox.utils.ProcessUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * C 判题
 *
 * @author ZERO
 * @date 2024/2/12
 */
@Component("cCodeSandbox")
public class CCodeSandbox extends CodeSandboxTemplate {

    public static final String GLOBAL_C_FILE_NAME = "main.c";

    public static final String GLOBAL_C_EXEC_NAME = "main";

    @Override
    protected void createSrc(String code, File path) throws IOException {
        File src = new File(path, GLOBAL_C_FILE_NAME);
        FileUtil.writeString(code, src, StandardCharsets.UTF_8);
    }

    @Override
    protected ExecuteMessage handlerCompiler(File path) {
        String filename = path.getPath() + File.separator + GLOBAL_C_FILE_NAME;
        String exec = path.getPath() + File.separator + GLOBAL_C_EXEC_NAME;
        String compileCmd = String.format("gcc -lm -w -O3 -std=gnu11 %s -o %s", filename, exec);
        return ProcessUtil.exec(compileCmd, 2000);
    }

    @Override
    protected String getRunCommand(File path) {
        return path.getPath() + File.separator + GLOBAL_C_EXEC_NAME;
    }
}
