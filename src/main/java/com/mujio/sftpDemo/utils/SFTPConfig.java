package com.mujio.sftpDemo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
* @Description: 获取sftp连接参数
* @Author: mujio
* @Date: 2020/7/5 0005
*/
@Service
@Slf4j
public class SFTPConfig {
	// 是否启用sftp
	@Value("${sftp.isUseSftp}")
	private boolean isUseSftp;
	@Value("${sftp.server.ip}")
	private String ip;
	@Value("${sftp.server.username}")
	private String username;
	@Value("${sftp.server.password}")
	private String password;
	@Value("${sftp.server.port}")
	private String port;

	public boolean getIsUseSftp() {
		return isUseSftp;
	}
	public String getIP() {
		return ip;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPort() {
		return port;
	}
}
