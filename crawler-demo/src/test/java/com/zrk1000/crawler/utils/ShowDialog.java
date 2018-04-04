package com.zrk1000.crawler.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by rongkang on 2017-07-02.
 */
public class ShowDialog {

    static{
        System.setProperty("java.awt.headless", "false");
    }
    public static String img(String path) {
        return img(Toolkit.getDefaultToolkit().getImage(path));
    }

    public static String img(byte[] bytes) {
        return img(Toolkit.getDefaultToolkit().createImage(bytes));
    }

    public static String img(BufferedImage bufferedImage) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img(Toolkit.getDefaultToolkit().createImage(out.toByteArray()));
    }

    public static String img(Image image) {
        ImageIcon icon = new ImageIcon(image);
        icon.setImage(icon.getImage().getScaledInstance(100, 50, Image.SCALE_DEFAULT));
        System.out.println("请在弹框中输入验证码");
        return (String) JOptionPane.showInputDialog(null, "请输入验证码", "", JOptionPane.QUESTION_MESSAGE, icon, null, null);
    }

    public static String text() {
        return text("请输入短信验证码");
    }

    public static String text(String msg) {
        System.out.println(msg);
        return (String) JOptionPane.showInputDialog(null, msg, "", JOptionPane.QUESTION_MESSAGE, null, null, null);
    }



}
