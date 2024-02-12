package com.zero.codesandbox.judge.service.impl;

import com.zero.codesandbox.judge.CodeSandboxTemplate;
import com.zero.codesandbox.judge.common.JudgeEnum;
import com.zero.codesandbox.judge.common.LanguageEnum;
import com.zero.codesandbox.judge.service.JudgeService;
import com.zero.codesandbox.model.ExecuteCodeRequest;
import com.zero.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * JudgeServiceImpl
 *
 * @author ZERO
 * @date 2024/2/12
 */
@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource(name = "javaCodeSandbox")
    private CodeSandboxTemplate javaCodeSandbox;

    @Resource(name = "cCodeSandbox")
    private CodeSandboxTemplate cCodeSandbox;

    @Resource(name = "cppCodeSandbox")
    private CodeSandboxTemplate cppCodeSandbox;

    @Override
    public List<String> getLanguages() {
        return LanguageEnum.getValues();
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest task) {
        ExecuteCodeResponse result;
        if (ObjectUtils.isEmpty(task)) {
            result = ExecuteCodeResponse.builder()
                    .status(JudgeEnum.SYSTEM_ERROR.getValue())
                    .message("请求参数错误...")
                    .build();
            return result;
        }
        LanguageEnum language = LanguageEnum.getEnumByValue(task.getLanguage());
        if (ObjectUtils.isEmpty(task.getLanguage()) || ObjectUtils.isEmpty(language)) {
            result = ExecuteCodeResponse.builder()
                    .status(JudgeEnum.SYSTEM_ERROR.getValue())
                    .message("编译选项错误...")
                    .build();
        } else {
            CodeSandboxTemplate codeSandboxTemplate;
            switch (language) {
                case JAVA:
                    codeSandboxTemplate = javaCodeSandbox;
                    break;
                case C:
                    codeSandboxTemplate = cCodeSandbox;
                    break;
                default:
                    codeSandboxTemplate = cppCodeSandbox;
                    break;
            }
            result = codeSandboxTemplate.executeCode(task);
        }
        return result;
    }
}
