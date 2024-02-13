package com.zero.codesandbox.controller;

import com.zero.codesandbox.judge.service.JudgeService;
import com.zero.codesandbox.model.ExecuteCodeRequest;
import com.zero.codesandbox.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 判题沙箱控制层
 *
 * @author ZERO
 * @date 2024/2/12
 */
@RestController
@RequestMapping("/sandbox")
public class CodeSandboxController {

    @Resource
    private JudgeService judgeService;

    /**
     * 获取判题机支持的所有语言
     *
     * @return 返回结果集
     */
    @GetMapping("/languages")
    public List<String> getLanguages() {
        return judgeService.getLanguages();
    }

    /**
     * 执行代码
     *
     * @param task 任务
     * @return 结果
     */
    @PostMapping("/judge")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest task) {
        return judgeService.executeCode(task);
    }

}
