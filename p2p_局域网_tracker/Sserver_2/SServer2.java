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
import java.io.OutputStreamWriter;
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

public class SServer2 extends JFrame{
    public SServer2() {
		setTitle("P2P文件共享系统_Server2");//窗口标题
		setLayout(null);//设置布局 setLayout(new BorderLayout())不同布局要求不同
		setSize(450, 250);//窗口大小：宽、高
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭选项
		JPanel server = new Server(); //panel类：面板容器类
		server.setBorder(BorderFactory.createTitledBorder("Server Start2...")); 
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
        new SServer2().setVisible(true);
    }
}
class Server extends JPanel implements ActionListener {
    JButton jb0,jb1,jb2;
    public Server() {
		this.setLayout(null);
        setSize(380, 180);//设置宽、高

		jb1 = new JButton("接收文件");
        
		jb1.setBounds(140, 90, 100, 30);
        
		this.add(jb1);
		jb1.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jb1){
            try {
                ServerSocket serverSocket = new ServerSocket(3120);//创建绑定到特定端口的服务器Socket。
                Socket socket = null;//需要接收的客户端Socket
                int count = 0;//记录客户端数量
                System.out.println("文件服务器启动");
                //定义一个死循环，不停的接收客户端连接
                while (true) {
                    socket = serverSocket.accept();//侦听并接受到此套接字的连接
                    InetAddress inetAddress=socket.getInetAddress();//获取客户端的连接
                    ServerThread2 thread=new ServerThread2(socket,inetAddress);//自己创建的线程类
                    thread.start();//启动线程
                    count++;//如果正确建立连接
                    System.out.println("上传文件的客户端数量：" + count);//打印客户端数量
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }    
        }
    }
    class ServerThread2 extends Thread {
        Socket socket = null;
        InetAddress inetAddress=null;//接收客户端的连接
    
        public ServerThread2(Socket socket,InetAddress inetAddress) {
            this.socket = socket;
            this.inetAddress=inetAddress;
        }
        @Override
		public void run() {
			// 创建文件流用来读取文件中的数据
			try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());  
                // 文件名和长度  
                String fileName = dis.readUTF();  
                long fileLength = dis.readLong();  
                File directory = new File("/Users/huangkai/Desktop");  //保证目录存在
                /*if(!directory.exists()) {  
                    directory.mkdir();  
                }  */
                File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);  
                FileOutputStream fos = new FileOutputStream(file);  
  
                // 开始接收文件  
                byte[] bytes = new byte[1024];  
                int length = 0;  
                while((length = dis.read(bytes, 0, bytes.length)) != -1) {  
                    fos.write(bytes, 0, length);  
                    fos.flush();  
                }  

                File file1 = new File("/Users/huangkai/Desktop/Dir.txt");
                FileOutputStream fos1 = null;
                if(!file1.exists()){
                    file1.createNewFile();//如果文件不存在，就创建该文件
                    fos1 = new FileOutputStream(file1);//首次写入获取
                }else{
                    //如果文件已存在，那么就在文件末尾追加写入
                    fos1 = new FileOutputStream(file1,true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
                }
                //更新目录【文件名,IP】
                OutputStreamWriter os = new OutputStreamWriter(fos1, "UTF-8");//指定以UTF-8格式写入文件
                PrintWriter pw=new PrintWriter(os);
                String s=fileName+","+socket.getInetAddress().getHostAddress();
                pw.println(s);//每输入一个数据，自动换行，便于我们每一行每一行地进行读取
                
                pw.close();
                os.close();
                fos.close(); 
                dis.close();  
                socket.close();  
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