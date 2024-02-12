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

    @Override
    public List<String> getLanguages() {
        return LanguageEnum.getValues();
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest task) {
        ExecuteCodeResponse result;
        LanguageEnum language = LanguageEnum.getEnumByValue(task.getLanguage());
        if (ObjectUtils.isEmpty(task.getLanguage()) || ObjectUtils.isEmpty(language)) {
            result = new ExecuteCodeResponse();
            result.setStatus(JudgeEnum.SYSTEM_ERROR.getValue());
            result.setMessage("编译选项错误...");
        } else {
            CodeSandboxTemplate codeSandboxTemplate;
            switch (language) {
                case JAVA:
                    codeSandboxTemplate = javaCodeSandbox;
                    break;
                default:
                    System.out.println("选择了其他语言");
                    codeSandboxTemplate = javaCodeSandbox;
                    break;
            }
            result = codeSandboxTemplate.executeCode(task);
        }
        return result;
    }
}
