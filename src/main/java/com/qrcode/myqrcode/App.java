package com.qrcode.myqrcode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.swetake.util.Qrcode;

/**
 * 生成二维码工具
 * 　　1、符号规格　　

　　从版本1（21×21模块）到版本40（177×177 模块），每提高一个版本，每边增加4个模块。

　　2、数据类型与容量（参照最大规格符号版本40-L级）：

　　数字数据：7,089个字符

　　字母数据: 4,296个字符

　　8位字节数据: 2,953个字符

　　汉字数据：1,817个字符

　　3、数据表示方法：

　　深色模块表示二进制"1"，浅色模块表示二进制"0"。

　　4、纠错能力（同符号规格下，纠错能力越高，二维码容量越低）：

　　L级：约可纠错7%的数据码字

　　M级：约可纠错15%的数据码字

　　Q级：约可纠错25%的数据码字

　　H级：约可纠错30%的数据码字
 *
 */
public class App
{
    public static void main(String[] args)
    {
        System.out.println("start qrcode!");
        
        // 内容可手工替换
        String content ="https://henryliyunfeng.github.io/";
        // 容错级别 共有四级：可选L(7%)、M(15%)、Q(25%)、H(30%)(最高H)
        char errorCorrect = 'L';
        // 编码模式：Numeric 数字, Alphanumeric 英文字母,Binary 二进制,Kanji 汉字(第一个大写字母表示)
        char mode = 'B';
        // 二维码的版本号：也象征着二维码的信息容量
        int  version = 10;
        
        try
        {
            BufferedImage image = createQrcode(content, errorCorrect, mode, version);
            
            String format = "jpg";
            File file = new File("D://temp//henryliyunfeng.jpg");
            
            writeToFile(image, format, file);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * @Description: 生成BufferedImage对象
     * @param content
     *            二维码存放的信息
     * @param errorCorrect
     *            容错级别 
     *            表示的字符串长度： 容错率(ECC) 显示编码模式(EncodeMode)及版本(Version)有关二维码的纠错级别(排错率)，
     *            共有四级：可选L(7%)、M(15%)、Q(25%)、H(30%)(最高H)。
     *            纠错信息同样存储在二维码中，纠错级别越高，纠错信息占用的空间越多，那么能存储的有用信息就越少, 
     *            对二维码清晰度的要求越小
     * @param mode
     *            编码模式 编码模式：Numeric 数字, Alphanumeric 英文字母,Binary 二进制,Kanji
     *            汉字(第一个大写字母表示)
     * @param version
     *            二维码的版本号 二维码的版本号：也象征着二维码的信息容量；二维码可以看成一个黑白方格矩阵，版本不同，
     *            矩阵长宽方向方格的总数量分别不同。 1-40总共40个版本，版本1为21*21矩阵，版本每增1，二维码的两个边长都增4；
     *            版本2 为25x25模块，最高版本为是40，是177*177的矩阵；
     * @return
     * @throws IOException
     */
    public static BufferedImage createQrcode(String content, char errorCorrect, char mode, int version)
        throws IOException
    {
        BufferedImage image = null;
        if (null == content || "".equals(content))
        {
            
        }
        else
        {
            Qrcode qrcode = new Qrcode();
            qrcode.setQrcodeErrorCorrect(errorCorrect);
            qrcode.setQrcodeEncodeMode(mode);
            
            qrcode.setQrcodeVersion(version);
            // 获取内容的字节数组，设置编码格式
            byte[] bytes = content.getBytes("UTF-8");
            // 图片尺寸,会根据version的变大，而变大，自己需要计算
            int imgSize = 67 + 12 * (version - 1);
            image = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_BYTE_BINARY);
            // 获取画笔
            Graphics2D gs = image.createGraphics();
            // 设置背景色 白色
            gs.setBackground(Color.WHITE);
            gs.clearRect(0, 0, imgSize, imgSize);
            // 设定图像颜色 黑色
            gs.setColor(Color.BLACK);
            // 设置偏移量，不设置可能导致二维码生产错误(解析失败出错)
            int pixoff = 2;
            // 二维码输出
            if (bytes.length > 0 && bytes.length < 120)
            {
                boolean[][] s = qrcode.calQrcode(bytes);
                for (int i = 0; i < s.length; i++)
                {
                    for (int j = 0; j < s.length; j++)
                    {
                        if (s[j][i])
                        {
                            //注意j * 3 + pixoff, i * 3 + pixoff, 3, 3中的pixoff和3也会影响二维码像素，但是影响并不会很大，
                            //二维码像素主要受version影响
                            gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);//填充矩形区域
                        }
                    }
                }
            }
            gs.dispose();
            image.flush();
        }
        return image;
    }
    
    /** 
     * 二维码输出到文件 
     *  @param image 二维码内容 
     * @param format 图片格式 例如jpg、gif等
     * @param file 输出文件 
     * @return 
     * */
    public static File writeToFile(BufferedImage image, String format, File file)
        throws IOException
    {
        if (null == file)
        {
            file = new File("D:" + File.separator + "temp.jpg");//自己的默认存储路径
        }
        ImageIO.write(image, format, file);
        return file;
    }
    
    /**
     * @Description: 转换BufferedImage->输出流  
     * @param image 二维码内容
     * @param format 图片格式 例如jpg、gif等
     * @param outPutStream 输出流
     * @return
     * @throws IOException
     */
    public static OutputStream writeToStream(BufferedImage image, String format, OutputStream outPutStream)
        throws IOException
    {
        if (null == outPutStream)
        {
            outPutStream = new ByteArrayOutputStream();
        }
        ImageIO.write(image, format, outPutStream);
        return outPutStream;
    }
}
