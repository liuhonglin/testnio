package com.lhl.test.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

/**
 * Created by liuhonglin on 2018/1/22 15:25.
 */
public class Test {

    public static void readByNio(String file) {
        FileInputStream fis = null;
        FileChannel fchannel = null;

        int LF = 10; // 换行符 \n
        int CR = 13; // 回车符 \r

        try {
            // 获取通道
            fis = new FileInputStream(file);
            fchannel = fis.getChannel();

            // 文件大小
            int size = (int) fchannel.size();

            // 指定缓冲区
            ByteBuffer rbuffer = ByteBuffer.allocate(1024);

            byte[] temp = new byte[0];
            int len;
            while((len = fchannel.read(rbuffer)) != -1) {
                // 注意先调用flip方法反转Buffer,再从Buffer读取数据.
                // flip方法将Buffer从写模式切换到读模式。调用flip()方法会将position设回0，并将limit设置成之前position的值。
                //Buffer bf = buffer.flip();
                // System.out.println(bf.limit());

                //byte[] bytes = buffer.array();
                //System.out.print(new String(bytes, 0, len, "utf-8"));
                // buffer.clear();


                int readSize = rbuffer.position();
                byte[] bs = new byte[readSize];
                rbuffer.flip(); // rbuffer.rewind();
                rbuffer.get(bs);
                rbuffer.clear();


                boolean hasLF = false;
                int nextLineStartNum = 0;

                for (int i = 0; i < readSize; i++) {
                    if (bs[i] == LF) {
                        hasLF = true;

                        int tempLength = temp.length;
                        int lineLength = i - nextLineStartNum;
                        byte[] currentLineByte = new byte[tempLength + lineLength];
                        System.arraycopy(temp, 0, currentLineByte, 0, temp.length);
                        temp = new byte[0];

                        System.arraycopy(bs, nextLineStartNum, currentLineByte, tempLength, lineLength);

                        System.out.println(new String(currentLineByte, "GBK")); // 输出一行


                        if (i + 1 < readSize && bs[i +1] == CR) {
                            nextLineStartNum = i + 2;
                        } else {
                            nextLineStartNum = i + 1;
                        }
                    }
                }

                if (hasLF) {
                    // 后一行前半部分
                    temp = new byte[bs.length - nextLineStartNum];
                    System.arraycopy(bs, nextLineStartNum, temp, 0, temp.length);
                } else {
                    // 兼容单次读取内容不足一行的情况
                    byte[] toTemp = new byte[temp.length + bs.length];
                    System.arraycopy(temp, 0, toTemp, 0, temp.length); //
                    System.arraycopy(bs, 0, toTemp, toTemp.length, bs.length);
                    temp = toTemp;
                }
            }

            if (temp != null && temp.length > 0) {
                // 兼容最后一行没有换行
                System.out.println(new String(temp, "GBK"));
            }

            rbuffer = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fchannel.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        readByNio("C:\\Users\\liuhonglin\\Desktop\\订单模块设计.TXT");
    }
}
