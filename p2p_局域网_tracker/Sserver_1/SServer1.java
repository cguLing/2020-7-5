import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SServer1 extends JFrame{
    public SServer1() {
		setTitle("P2P文件共享系统_Server1");//窗口标题
		setLayout(null);//设置布局 setLayout(new BorderLayout())不同布局要求不同
		setSize(450, 250);//窗口大小：宽、高
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭选项
		JPanel server = new Server(); //panel类：面板容器类
		server.setBorder(BorderFactory.createTitledBorder("Server1 Start...")); 
		server.setBounds(25, 10, 400, 200);
		add(server);
    }
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch (ClassNotFoundException e) {//执行try之后可能报的错误
            e.printStackTrace();//报错后需执行的程序
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        new SServer1().setVisible(true);
    }
}
class Server extends JPanel implements ActionListener {
    JButton jb0,jb1,jb2;
    public Server() {
		this.setLayout(null);
        setSize(380, 180);//设置宽、高

        jb0 = new JButton("目录文件");
        
		jb0.setBounds(140, 90, 100, 30);
        
		this.add(jb0);
        jb0.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jb0){
            try {
                ServerSocket serverSocket = new ServerSocket(3106);//创建绑定到特定端口的服务器Socket。
                Socket socket = null;//需要接收的客户端Socket
                int count = 0;//记录客户端数量
                System.out.println("目录服务器启动");
                //定义一个死循环，不停的接收客户端连接
                while (true) {
                    socket = serverSocket.accept();//侦听并接受到此套接字的连接
                    InetAddress inetAddress=socket.getInetAddress();//获取客户端的连接
                    ServerThread1 thread=new ServerThread1(socket,inetAddress);//自己创建的线程类
                    thread.start();//启动线程
                    count++;//如果正确建立连接
                    System.out.println("查询目录的客户端数量：" + count);//打印客户端数量
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }    
        }
    }
    class ServerThread1 extends Thread {
        Socket socket = null;
        InetAddress inetAddress=null;//接收客户端的连接
    
        public ServerThread1(Socket socket,InetAddress inetAddress) {
            this.socket = socket;
            this.inetAddress=inetAddress;
        }
        @Override
		public void run() {
			// 创建文件流用来读取文件中的数据
			try {
                File file = new File("/Users/huangkai/Desktop/Dir.txt");//获取了文件路径
                FileInputStream fos = new FileInputStream(file);//文件字节输入流，对文件数据以字节的形式进行读取操作
                // 创建网络输出流并提供数据包装器
                OutputStream netOut = socket.getOutputStream();//获取输出流：客户端发来的数据。
                //创建输出流对象(带缓冲)
                //DataOutputStream用BufferedOutputStream修饰，是为了使这个输出流带缓冲
                OutputStream doc = new DataOutputStream(
                        new BufferedOutputStream(netOut));

                byte[] buf = new byte[20480];// 创建文件读取缓冲区（缓冲区足够包括文件的大小）
                int num = fos.read(buf);// 读文件，read方法是一个一个字节逐渐读取，最后返回-1
                while (num != (-1)) {// 是否读完文件
                    doc.write(buf, 0, num);// 从0到第num个的字符；把文件数据写出网络缓冲区
                    doc.flush();// 冲刷出流，将所有缓冲的数据强制发送到目的地 ：刷新缓冲区把数据写往客户端

                    num = fos.read(buf);// 继续从文件中读取数据

                }
                fos.close();//冲刷并关闭输出流
                doc.close();//冲刷并关闭输出流
            }catch (FileNotFoundException e1) {//找不到文件或此路径为目录
                JOptionPane.showMessageDialog(null, "文件目录错误或找不到此文件", "错误",
                        JOptionPane.ERROR_MESSAGE);
            }catch (IOException e1) {
            	e1.printStackTrace();
        	} 
        }
    }
	class Win extends WindowAdapter {
		/**
		 接收窗口事件的抽象适配器类
		 当通过打开、关闭、激活或停用、图标化或取消图标化而改变了窗口状态时，
		 将调用该侦听器对象中的相关方法，并将 WindowEvent 传递给该方法。 
		 */
		public void windowClosing(WindowEvent event) {
			event.getWindow().dispose();//关闭窗体，并释放一部分资源
			System.exit(0);
		}
	}
}