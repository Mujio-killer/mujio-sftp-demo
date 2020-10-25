package com.mujio.sftpDemo.utils;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;


/**
 * @Description: SFTP工具类
 * @Author: GZY
 * @Date: 2020/7/5 0005
 */
@Configurable
@Component
public class SFTPUtils {

    private static Logger LOG = LoggerFactory.getLogger(SFTPUtils.class);
    private ChannelSftp sftp = null;
    private Session sshSession = null;

    /**
     * 通过SFTP连接服务器
     */
    public boolean connect(String username, String host, String port, String password, boolean isUseSftp) {
        if (isUseSftp) {
            try {
                JSch jsch = new JSch();
                LOG.info("--------创建session--------");
                sshSession = jsch.getSession(username, host, Integer.parseInt(port));
                sshSession.setPassword(password);
                Properties sshConfig = new Properties();
                // 使用非交互式连接
                sshConfig.put("StrictHostKeyChecking", "no");
                sshSession.setConfig(sshConfig);
                sshSession.connect();
                LOG.info("--------创建session成功--------");
                LOG.info("--------打开通道--------");
                Channel channel = sshSession.openChannel("sftp");
                channel.connect();
                sftp = (ChannelSftp) channel;
                LOG.info("--------通道打开，连接至端口：" + port + "--------");
                LOG.info("--------SFTP Session：" + this.sshSession + "--------");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }

        return true;
    }

    /**
     * 关闭连接
     */
    public void disconnect(boolean isUseSftp) {
        if (isUseSftp) {
            if (this.sftp != null) {
                if (this.sftp.isConnected()) {
                    this.sftp.disconnect();
                    LOG.info("--------sftp连接已关闭--------");
                }
            }
            if (this.sshSession != null) {
                if (this.sshSession.isConnected()) {
                    this.sshSession.disconnect();
                    LOG.info("--------sshSession已关闭--------");
                }
            }
        }

    }

    /**
     * 下载单个文件(到不存在的目录)
     *
     * @param remotPath：远程下载目录(以路径符号结束)
     * @param remoteFileName：下载文件名
     * @param localPath：本地保存目录(以路径符号结束)
     * @param localFileName：保存文件名
     * @return
     */
    public boolean downloadFile(String remotePath, String remoteFileName, String localPath, String localFileName, boolean isUseSftp) {
        if (isUseSftp) {
            FileOutputStream fieloutput = null;
            try {
                createDir(localPath, isUseSftp);
                File file = new File(localPath + localFileName);
                fieloutput = new FileOutputStream(file);
                sftp.get(remotePath + remoteFileName, fieloutput);
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (SftpException e) {
                e.printStackTrace();
            } finally {
                if (null != fieloutput) {
                    try {
                        fieloutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            createDir(localPath, isUseSftp);
            boolean isSuccessCopy = copyFile(remotePath + remoteFileName, localPath + localFileName);
            return isSuccessCopy;
        }

        return false;
    }

    /**
     * 上传单个文件
     *
     * @param remotePath：远程保存目录
     * @param remoteFileName：保存文件名
     * @param localPath：本地上传目录(以路径符号结束)
     * @param localFileName：上传的文件名
     * @return
     */
    public boolean uploadFile(String remotePath, String remoteFileName, String localPath, String localFileName, boolean isUseSftp) {
        if (isUseSftp) {
            FileInputStream in = null;
            try {
                createDir(remotePath, isUseSftp);
                File file = new File(localPath + localFileName);
                in = new FileInputStream(file);
                sftp.put(in, remoteFileName);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            createDir(remotePath, isUseSftp);
            boolean isSuccessCopy = copyFile(localPath + localFileName, remotePath + remoteFileName);
            return isSuccessCopy;
        }

        return false;
    }

    /**
     * 创建目录
     *
     * @param createpath
     * @return
     */
    public boolean createDir(String createpath, boolean isUseSftp) {
        if (isUseSftp) {
            try {
                LOG.info("--------创建目录:" + createpath + "--------");
                String pathArry[] = createpath.split("/");
                StringBuffer filePath = new StringBuffer("/");
                for (String path : pathArry) {
                    if (path.equals("")) {
                        continue;
                    }
                    filePath.append(path + "/");

                    if (isDirExist(filePath.toString(), isUseSftp)) {
                        sftp.cd(filePath.toString());
                    } else {
                        // 建立目录
                        sftp.mkdir(filePath.toString());
                        // 进入并设置为当前目录
                        sftp.cd(filePath.toString());
                    }

                }
                this.sftp.cd(createpath);
                LOG.info("--------进入当前目录:" + createpath + "--------");
                return true;
            } catch (SftpException e) {
                e.printStackTrace();
            }
        } else {
            File file = new File(createpath);
            //如果文件夹不存在则创建
            if (!file.exists() && !file.isDirectory()) {
                file.mkdirs();
            } else {
                return true;
            }
        }

        return false;
    }


    /**
    * @Description: isDirExist 判断目录是否存在
    * @Param: [directory, isUseSftp]
    * @return: boolean
    * @Author: GZY
    * @Date: 2020/7/5 0005
    */
    public boolean isDirExist(String directory, boolean isUseSftp) {
        boolean isDirExistFlag = false;
        if (isUseSftp) {
            try {
                SftpATTRS sftpATTRS = sftp.lstat(directory);
                isDirExistFlag = true;
                return sftpATTRS.isDir();
            } catch (Exception e) {
                    isDirExistFlag = false;
            }
        } else {
            File file = new File(directory);
            if (!file.exists() && !file.isDirectory()) {
                isDirExistFlag = false;
            } else {
                isDirExistFlag = true;
            }
        }

        return isDirExistFlag;
    }

    /**
    * @Description: 复制文件到指定目录
    * @Param: oldPath 原始目录
    * @Param: newPath 目标目录
    * @return: boolean
    * @Author: GZY
    * @Date: 2020/7/5 0005
    */
    public static boolean copyFile(String oldPath, String newPath) {
        FileOutputStream fs = null;
        InputStream inStream = null;
        try {
            int byteread = 0;
            if (!oldPath.equals(newPath)) {
                File oldfile = new File(oldPath);
                // 文件存在时
                if (oldfile.exists()) {
                    // 读入原文件
                    inStream = new FileInputStream(oldPath);
                    fs = new FileOutputStream(newPath);
                    byte[] buffer = new byte[1444];
                    while ((byteread = inStream.read(buffer)) != -1) {
                        fs.write(buffer, 0, byteread);
                    }
                    inStream.close();
                } else {
                    LOG.error("--------" + oldPath + "文件不存在--------");
                    return false;
                }
            }
            LOG.debug("--------文件拷贝完成--------");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

