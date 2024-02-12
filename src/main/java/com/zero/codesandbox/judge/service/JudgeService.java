package com.zero.codesandbox.judge.service;

import com.zero.codesandbox.model.ExecuteCodeRequest;
import com.zero.codesandbox.model.ExecuteCodeResponse;

import java.util.List;

/**
 * 判题服务接口
 *
 * @author ZERO
 * @date 2024/2/12
 */
public interface JudgeService {

    /**
     * 获取支持的所有语言
     * @return 结果集
     */
    List<String> getLanguages();

    /**
     * 执行代码
     * @param task 判题请求
     * @return 判题结果
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest task);

}
