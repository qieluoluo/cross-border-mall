package com.delmon.enums;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),

    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    // 业务错误
    BUSINESS_ERROR(1001, "业务处理失败"),
    USER_NOT_EXIST(1002, "用户不存在"),
    USER_PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "账号已被禁用"),
    ROLE_NOT_EXIST(1005, "角色不存在"),
    PERMISSION_DENIED(1006, "权限不足"),

    // 数据错误
    DATA_NOT_EXIST(2001, "数据不存在"),
    DATA_DUPLICATE(2002, "数据已存在"),
    DATA_SAVE_FAILED(2003, "数据保存失败"),
    DATA_UPDATE_FAILED(2004, "数据更新失败"),
    DATA_DELETE_FAILED(2005, "数据删除失败"),

    // 系统错误
    SYSTEM_ERROR(500, "系统内部错误"),
    DB_ERROR(5001, "数据库操作失败"),
    NETWORK_ERROR(5002, "网络连接失败"),
    TIMEOUT_ERROR(5003, "请求超时"),

    // 参数校验错误
    VALIDATION_ERROR(3001, "参数校验失败"),

    // 文件错误
    FILE_UPLOAD_ERROR(4001, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(4002, "文件下载失败"),
    FILE_FORMAT_ERROR(4003, "文件格式错误"),
    FILE_SIZE_EXCEED(4004, "文件大小超限");
    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
