package com.zero.codesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 进程执行信息
 *
 * @author ZERO
 * @date 2024/1/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteMessage {

    /**
     * 信息
     */
    private String message;

    /**
     * 错误信息
     */
    private String errorMessage;

}
