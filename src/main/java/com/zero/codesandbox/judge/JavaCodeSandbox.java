package com.zero.codesandbox.judge;

import cn.hutool.core.io.FileUtil;
import com.zero.codesandbox.model.ExecuteMessage;
import com.zero.codesandbox.utils.ProcessUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Java 判题
 *
 * @author ZERO
 * @date 2024/2/11
 */
@Service("javaCodeSandbox")
public class JavaCodeSandbox extends CodeSandboxTemplate{

    public static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    @Override
    protected void createSrc(String code, File path) {
        File src = new File(path, GLOBAL_JAVA_CLASS_NAME);
        FileUtil.writeString(code, src, StandardCharsets.UTF_8);
    }

    @Override
    protected ExecuteMessage handlerCompiler(File path) {
        String file = path.getPath() + File.separator + GLOBAL_JAVA_CLASS_NAME;
        String compileCmd = String.format("javac -encoding utf-8 %s", file);
        return ProcessUtil.exec(compileCmd, 2000);
    }

    @Override
    protected String getRunCommand(File path) {
        return String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main", path.getPath());
    }
}
