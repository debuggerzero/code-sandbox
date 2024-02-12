package com.zero.codesandbox.judge;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.zero.codesandbox.judge.common.JudgeEnum;
import com.zero.codesandbox.model.ExecuteCodeRequest;
import com.zero.codesandbox.model.ExecuteCodeResponse;
import com.zero.codesandbox.model.ExecuteMessage;
import com.zero.codesandbox.model.JudgeInfo;
import com.zero.codesandbox.utils.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 代码沙箱模板
 *
 * @author ZERO
 * @date 2024/1/31
 */
public abstract class CodeSandboxTemplate {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    @Value("${judge.scriptPath}")
    private String script;

    /**
     * （模板方法）创建对应的源程序
     *
     * @param code 代码
     * @param path 源程序根路径
     * @throws IOException ex
     */
    protected abstract void createSrc(String code, File path) throws IOException;

    /**
     * 编译（模板方法）
     *
     * @param path 源文件路径
     * @return 执行信息
     */
    protected abstract ExecuteMessage handlerCompiler(File path);

    /**
     * 运行命令（模板方法）
     *
     * @param path 文件路径
     * @return 指令
     */
    protected abstract String getRunCommand(File path);

    /**
     * 检查输入是否合法
     *
     * @param task   任务
     * @param result 响应结果
     * @return true / false
     */
    private boolean checkTask(ExecuteCodeRequest task, ExecuteCodeResponse result) {
        if (ObjectUtils.isEmpty(task.getInputs())) {
            result.setStatus(JudgeEnum.SYSTEM_ERROR.getValue());
            result.setMessage("测试数据不能为空!");
            return false;
        }
        return true;
    }

    /**
     * 执行代码
     *
     * @param executeCodeRequest 请求
     * @return 结果
     */
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        // 检测输入是否合法
        if (!checkTask(executeCodeRequest, executeCodeResponse)) {
            return executeCodeResponse;
        }
        // 创建工作目录
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        File path = new File(globalCodePathName + File.separator + UUID.randomUUID());
        if (!createWorkspace(executeCodeRequest, executeCodeResponse, path)) {
            FileUtil.del(path);
            return executeCodeResponse;
        }
        // 编译
        if (!compiler(executeCodeResponse, path)) {
            FileUtil.del(path);
            return executeCodeResponse;
        }
        // 运行
        runSrc(executeCodeResponse, path);
        // 结束后删除文件避免占用内存
        FileUtil.del(path);
        executeCodeResponse.setStatus(JudgeEnum.ACCEPTED.getValue());
        return executeCodeResponse;
    }

    /**
     * 创建工作路径
     *
     * @param task   请求对象
     * @param result 结果
     * @param path   路径
     * @return true / false
     */
    private boolean createWorkspace(ExecuteCodeRequest task, ExecuteCodeResponse result, File path) {
        try {
            if (!FileUtil.exist(path)) {
                FileUtil.mkdir(path);
            }
            List<String> inputs = task.getInputs();
            for (int i = 0; i < inputs.size(); i++) {
                File file = new File(path, i + ".in");
                FileUtil.writeString(inputs.get(i), file, StandardCharsets.UTF_8);
            }
            createSrc(task.getCode(), path);
        } catch (IOException ex) {
            result.setStatus(JudgeEnum.SYSTEM_ERROR.getValue());
            result.setMessage("服务器工作目录出错：" + ex);
            return false;
        }
        return true;
    }

    private boolean compiler(ExecuteCodeResponse result, File path) {
        ExecuteMessage message = handlerCompiler(path);
        if (ObjectUtils.isNotEmpty(message.getErrorMessage())) {
            result.setStatus(JudgeEnum.COMPILE_ERROR.getValue());
            result.setMessage(message.getErrorMessage());
            return false;
        }
        return true;
    }

    private void runSrc(ExecuteCodeResponse result, File path) {
        String cmd = "script process inputFile tmpFile";
        cmd = cmd.replace("script", script);
        cmd = cmd.replace("process", getRunCommand(path).replace(" ", "@"));
        cmd = cmd.replace("tmpFile", path.getPath() + File.separator + "tmp.out");
        List<JudgeInfo> judgeInfos = new ArrayList<>();
        for (int i = 0; ; i++) {
            File inFile = new File(path, i + ".in");
            if (!inFile.exists()) {
                break;
            }
            ExecuteMessage exec = ProcessUtil.exec(cmd.replace("inputFile", inFile.getPath()), 10000);
            JudgeInfo judgeInfo = JSONUtil.toBean(exec.getMessage(), JudgeInfo.class);
            if (ObjectUtils.isEmpty(judgeInfo)) {
                judgeInfo = JudgeInfo.builder()
                        .status(JudgeEnum.SYSTEM_ERROR.getValue())
                        .memoryUsed(-1L)
                        .timeUsed(-1L)
                        .build();
            }
            if (judgeInfo.getStatus().equals(JudgeEnum.ACCEPTED.getValue())) {
                String output = FileUtil.readString(path.getPath() + File.separator + "tmp.out", StandardCharsets.UTF_8);
                judgeInfo.setOutput(output);
            }
            if (StringUtils.isNotEmpty(exec.getErrorMessage())) {
                judgeInfo.setStatus(JudgeEnum.RUNTIME_ERROR.getValue());
                judgeInfo.setTimeUsed(-1L);
                judgeInfo.setMemoryUsed(-1L);
                judgeInfo.setErrorMessage(exec.getErrorMessage());
            }
            judgeInfos.add(judgeInfo);
            FileUtil.del(new File(path.getPath(), "tmp.out"));
        }
        result.setJudgeInfo(judgeInfos);
    }
}
