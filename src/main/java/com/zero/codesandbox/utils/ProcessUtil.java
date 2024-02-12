package com.zero.codesandbox.utils;

import com.zero.codesandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 进程工具类
 *
 * @author ZERO
 * @date 2024/2/11
 */
@Slf4j
public class ProcessUtil {

    /**
     * 通用方法获取输出信息
     *
     * @param inputStream 输入流
     * @return 字符集
     */
    private static String getResponseMessage(InputStream inputStream) {
        // 分批获取进程的正常输出
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            List<String> outputStrList = new ArrayList<>();
            // 逐行读取
            String compileOutputLine;
            while ((compileOutputLine = bufferedReader.readLine()) != null) {
                outputStrList.add(compileOutputLine);
            }
            return StringUtils.join(outputStrList, "\n");
        } catch (IOException e) {
            return e.getMessage();
        } finally {
            try {
                inputStream.close();
                bufferedReader.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    public static ExecuteMessage exec(String cmd, long milliseconds) {
        Runtime runtime = Runtime.getRuntime();
        final Process exec;
        try {
            exec = runtime.exec(cmd);
            if (!exec.waitFor(milliseconds, TimeUnit.MILLISECONDS)) {
                if (exec.isAlive()) {
                    exec.destroy();
                }
                throw new InterruptedException();
            }
        } catch (IOException e) {
            return new ExecuteMessage(null, e.getMessage());
        } catch (InterruptedException e) {
            return new ExecuteMessage(null, "timeOut");
        }
        ExecuteMessage res = new ExecuteMessage();
        res.setMessage(getResponseMessage(exec.getInputStream()));
        res.setErrorMessage(getResponseMessage(exec.getErrorStream()));
        return res;
    }

}
