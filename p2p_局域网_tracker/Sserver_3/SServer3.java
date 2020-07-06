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

public class SServer3 extends JFrame{
    public SServer3() {
		setTitle("P2P文件共享系统_Server3");//窗口标题
		setLayout(null);//设置布局 setLayout(new BorderLayout())不同布局要求不同
		setSize(450, 250);//窗口大小：宽、高
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭选项
		JPanel server = new Server(); //panel类：面板容器类
		server.setBorder(BorderFactory.createTitledBorder("Server3 Start...")); 
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
        new SServer3().setVisible(true);
    }
}
class Server extends JPanel implements ActionListener {
    JButton jb0,jb1,jb2;
    public Server() {
		this.setLayout(null);
        setSize(380, 180);//设置宽、高

        jb2 = new JButton("资源IP");
        
        jb2.setBounds(140, 90, 100, 30);
        
        this.add(jb2);
		jb2.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jb2){
            try {
                ServerSocket serverSocket = new ServerSocket(3104);//创建绑定到特定端口的服务器Socket。
                Socket socket = null;//需要接收的客户端Socket
                int count = 0;//记录客户端数量
                System.out.println("资源服务器启动");
                //定义一个死循环，不停的接收客户端连接
                while (true) {
                    socket = serverSocket.accept();//侦听并接受到此套接字的连接
                    InetAddress inetAddress=socket.getInetAddress();//获取客户端的连接
                    ServerThread3 thread=new ServerThread3(socket,inetAddress);//自己创建的线程类
                    thread.start();//启动线程
                    count++;//如果正确建立连接
                    System.out.println("请求资源的客户端数量：" + count);//打印客户端数量
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }    
        }
    }
    class ServerThread3 extends Thread {
        Socket socket = null;
        InetAddress inetAddress=null;//接收客户端的连接
    
        public ServerThread3(Socket socket,InetAddress inetAddress) {
            this.socket = socket;
            this.inetAddress=inetAddress;
        }
        @Override
		public void run() {
            try {
                //3.获得输入流
                InputStream is=socket.getInputStream();
                BufferedReader br=new BufferedReader(new InputStreamReader(is));
                //获得输出流
                OutputStream os=socket.getOutputStream();
                PrintWriter pw=new PrintWriter(os);
                //4.读取用户输入信息
                String info=null;
                if(!((info=br.readLine())==null)){
                    System.out.println("用户请求的资源为："+info);
                }

                String dirIP="无此资源";
                //打开待读取的文件
		        BufferedReader br0 = new BufferedReader(new FileReader("/Users/huangkai/Desktop/Dir.txt"));
                String line=null;
                while((line=br0.readLine())!=null) {
                    //对字符串进一步处理
                    String[] str = line.split(",");//字符串转行为字符串数组
                    if(str[0].equals(info)) {
                        dirIP = str[1];
                    }
                }
                //给客户一个响应
                pw.write(dirIP);
                pw.flush();

                br.close();
                br0.close();
                pw.close();
                os.close();
                br.close();
                is.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
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