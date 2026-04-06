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

    private static final int LOG_PREVIEW_MAX_LENGTH = 500;

    private static String safePreview(String message) {
        if (StringUtils.isBlank(message)) {
            return "";
        }
        return message.length() > LOG_PREVIEW_MAX_LENGTH
                ? message.substring(0, LOG_PREVIEW_MAX_LENGTH) + "...(truncated)"
                : message;
    }

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
                log.error("关闭进程流失败，error={}", e.getMessage(), e);
            }
        }
    }

    public static ExecuteMessage exec(String cmd, long milliseconds) {
        Runtime runtime = Runtime.getRuntime();
        final Process exec;
        long startTime = System.currentTimeMillis();
        log.info("开始执行命令，cmd={}, timeoutMs={}", cmd, milliseconds);
        try {
            exec = runtime.exec(cmd);
            if (!exec.waitFor(milliseconds, TimeUnit.MILLISECONDS)) {
                if (exec.isAlive()) {
                    exec.destroy();
                }
                long cost = System.currentTimeMillis() - startTime;
                log.warn("命令执行超时，cmd={}, timeoutMs={}, costMs={}", cmd, milliseconds, cost);
                throw new InterruptedException();
            }
        } catch (IOException e) {
            long cost = System.currentTimeMillis() - startTime;
            log.error("命令执行异常，cmd={}, timeoutMs={}, costMs={}, error={}", cmd, milliseconds, cost, e.getMessage(), e);
            return new ExecuteMessage(null, e.getMessage());
        } catch (InterruptedException e) {
            long cost = System.currentTimeMillis() - startTime;
            log.warn("命令执行被中断或超时，cmd={}, timeoutMs={}, costMs={}", cmd, milliseconds, cost);
            return new ExecuteMessage(null, "timeOut");
        }
        ExecuteMessage res = new ExecuteMessage();
        int exitCode = exec.exitValue();
        String stdout = getResponseMessage(exec.getInputStream());
        String stderr = getResponseMessage(exec.getErrorStream());
        res.setMessage(stdout);
        res.setErrorMessage(stderr);
        long cost = System.currentTimeMillis() - startTime;
        log.info(
                "命令执行完成，cmd={}, exitCode={}, costMs={}, stdoutLength={}, stderrLength={}, stdoutPreview={}, stderrPreview={}",
                cmd,
                exitCode,
                cost,
                StringUtils.length(stdout),
                StringUtils.length(stderr),
                safePreview(stdout),
                safePreview(stderr)
        );
        return res;
    }

}
