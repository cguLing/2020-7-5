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
 

public class P2p extends JFrame {
	public P2p() {
		setTitle("P2P文件共享系统_Client");//窗口标题
		setLayout(null);//设置布局 setLayout(new BorderLayout())不同布局要求不同
		setSize(800, 350);//窗口大小：宽、高
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭选项
		JPanel client = new Client(); //panel类：面板容器类
		JPanel server = new Server(); //panel类：面板容器类
		server.setBounds(10, 10, 290, 300);//Layout需=null，组件矩形的大小
		client.setBounds(300, 10, 450, 300);//= setLocation() + setSize()
		client.setBorder(BorderFactory.createTitledBorder("Client")); 
		server.setBorder(BorderFactory.createTitledBorder("Server")); 
		add(client);//组件加入面板
		add(server);
	}

	public static void main(String[] args) {
		 try{
		 UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		 }
		 //windows窗口更好看
		 //javax.swing.plaf.metal.MetalLookAndFeel——metal
		 //com.sun.java.swing.plaf.motif.MotifLookAndFeel——motif
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
		new P2p().setVisible(true);//数据模型已构造好，允许JVM可以根据数据模型执行paint方法开始画图
	}
}

class Client extends JPanel implements ActionListener {
	JFileChooser jfc;
	String ip;
	String url="192.168.1.102";//云服务器的URL，电脑IP暂代
    Timer timer;
    JProgressBar jpb;
    JButton jb1,jb2;
    JButton jb3,jb4;
    JTextField jtf1,jtf2;
	JTextField jtf3;
	String fileName="xxx";
	File dir;

    public Client() {
        //进度条
        timer = new Timer(50, this);
        jpb = new JProgressBar();
        //设置进度条的方向，SwingConstants.VERTICAL 或 SwingConstants.HORIZONTAL
        jpb.setOrientation(JProgressBar.HORIZONTAL);
        jpb.setMinimum(0);//设置最小进度值，最大进度值，当前进度值
        jpb.setMaximum(100);
        jpb.setValue(0);
        jpb.setStringPainted(true);//接收时的显示标志，是否绘制百分比文本（进度条中间显示的百分数）

        this.setLayout(null);
        jfc = new JFileChooser();//获取打开文件对象

        jb1 = new JButton("确定");
        jb2 = new JButton("请选择文件存储位置");
		jb3 = new JButton("接收");
        jb4 = new JButton("请求");
        jtf1 = new JTextField("127.0.0.1");
        jtf2 = new JTextField("");
        jtf3 = new JTextField("请输入请求文件名");
        jtf1.setBounds(10, 30, 200, 25);
        jb1.setBounds(220, 30, 80, 25);
        jtf2.setBounds(10, 70, 400, 50);
        jtf3.setBounds(10, 130, 200, 25);
        jb2.setBounds(10, 170, 160, 25);
        jb3.setBounds(150, 210, 80, 25);
        jb4.setBounds(10, 210, 80, 25);
        jpb.setBounds(10, 250, 250, 20);
        this.add(jb1);
        this.add(jb2);
        this.add(jb3);
        this.add(jb4);
        this.add(jtf1);
        this.add(jtf2);
        this.add(jtf3);
        this.add(jpb);

        jb1.addActionListener(this);
        jb2.addActionListener(this);
		jb3.addActionListener(this);
		jb4.addActionListener(this);

        new s_server().start();//由线程启动
    }

    public void actionPerformed(ActionEvent e) {
		int result;
		File file1=null;
		File file2=null;
        if (e.getSource() == timer) {
            int value = jpb.getValue();//进度条的现在值
            jpb.setValue(0);
            if (value < 100) {
                value++;
                jpb.setValue(value);
            } else {
                timer.stop();
            }
        }

        else if ((JButton) e.getSource() == jb2) {//选择文件后显示状态实时更新为对应的选择路径
            result = jfc.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
				file1 = jfc.getSelectedFile();
				jb2.setText(file1.getPath());//显示文件路径
				dir = new File(file1.getPath());
            }
        }

		else if ((JButton) e.getSource() == jb4){ //请求按钮[将文件名发给服务器，接收服务器给的IP并填入jtf1]
			try {
				Socket server = new Socket(url, 3104);
				//输出流
				OutputStream os=server.getOutputStream();
				PrintWriter pw=new PrintWriter(os);
				//输入流
				InputStream is=server.getInputStream();
				BufferedReader br=new BufferedReader(new InputStreamReader(is));
				//3.利用流按照一定的操作，对socket进行读写操作
				String info=new String(jtf3.getText());
				pw.write(info);
				pw.flush();
				server.shutdownOutput();
				//接收服务器的相应
				String reply=null;
				while(!((reply=br.readLine())==null)){
					jtf1.setText(reply);
				}
				//4.关闭资源
				br.close();
				is.close();
				pw.close();
				os.close();
				server.close();
			} catch (UnknownHostException t) {
				t.printStackTrace();
			} catch (IOException t) {
				t.printStackTrace();
			}
			ip = new String(jtf1.getText());
		}
        else if ((JButton) e.getSource() == jb1) {//确定按钮：[连接ip客户端，将请求文件名发送]
			ip = new String(jtf1.getText());
			try {
				Socket server = new Socket(ip, 37194);
				OutputStream os=server.getOutputStream();
				PrintWriter pw=new PrintWriter(os);
				String info=new String(jtf3.getText());
				pw.write(info);
				pw.flush();
				server.shutdownOutput();
				pw.close();
				os.close();
				server.close();
			} catch (UnknownHostException t) {
				t.printStackTrace();
			} catch (IOException t) {
				t.printStackTrace();
			}
        }
        else if ((JButton) e.getSource() == jb3) {//接收后触发事件
            try {
				// 使用本地文件系统接受网络数据并存为新文件
				file2 = new File(dir,fileName);//新建一个文件并命名
                if(!file2.isFile()) {
                    file2.createNewFile();
				}
                RandomAccessFile raf = new RandomAccessFile(file2, "rw");//向该文件写入数据，设置可读可写
                // 通过Socket连接文件服务器
                Socket server = new Socket(ip, 3108);
                // 创建网络接受流接受服务器文件数据

                InputStream netIn = server.getInputStream();

                InputStream in = new DataInputStream(new BufferedInputStream(
						netIn));//封装输入流

                // 创建缓冲区缓冲网络数据
                byte[] buf = new byte[20480];
                timer.start();

                int num = in.read(buf);

                while (num != (-1)) {// 是否读完所有数据

                    raf.write(buf, 0, num);// 将数据写往文件

                    raf.skipBytes(num);// 顺序写文件字节

                    num = in.read(buf);// 继续从网络中读取文件

                }

                in.close();
                raf.close();
            }

            catch (IOException q) {
                System.out.println("异常");

            }
        }
    }

    class s_server extends Thread {
        String message;
        float a, b, c;
        int i;

        public void run() {

            while (true) {//循环接收UDP包
                try {
                    byte[]bytes = new byte[1024];
                    //使用 DatagramSocket接受数据，设置端口和接受字节的最大值
                    DatagramSocket ds = new DatagramSocket(3019);//端口
                    DatagramPacket dp = new DatagramPacket(bytes,1024);//包大小
                    ds.receive(dp);
                    message = new String(bytes,0,bytes.length);
                    message = message.trim();//trim()去除头尾的字符空格
					fileName = message;
                    jtf2.setText("文件" + message + "待接收\n请设置IP:"+dp.getAddress()+"接收！");//对jtf2文本框刷新显示消息
                    ds.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }
}

class Server extends JPanel implements ActionListener { 
	/*
	class Server ；自定义的类
	extends JPanel ；继承Java的JPanel类，组件类
	implements ActionListener ； 是实现 ActionListener 接口，为动作监听接口，是Java swing 监听窗体动作的一个接口
	*/
	JButton jb0,jb1;//按钮
	JButton jb2,jb3;
	JFileChooser jfc;//文件选择窗口
	JTextField jtf,jtf0;//文本框，允许编辑单行文本
	String st, st1;
	String url="192.168.1.102";
	JProgressBar jpb;//进度条
	Timer timer;//线程定时
	Socket s;

	public Server() {
		this.setLayout(null);
		setSize(300, 300);//设置宽、高
		// 进度条
		jpb = new JProgressBar();
		jpb.setOrientation(JProgressBar.HORIZONTAL);//设置进度条的方向：平行
		jpb.setMinimum(0);//最小值为0
		jpb.setMaximum(100);//最大值为100
		jpb.setValue(0);//赋初始值
		jpb.setStringPainted(true);//true：进度条呈现进度字符串
		timer = new Timer(50, this); // 创建一个计时器，计时间隔为50毫秒

		jfc = new JFileChooser();
		/*
		JFileChooser()：构造一个指向用户默认目录的 JFileChooser
		JFileChooser(File currentDirectory)：使用给定的 File 作为路径来构造一个 JFileChooser
		*/
		jtf0 = new JTextField("请求文件");
		jtf = new JTextField("请浏览选择待发送文件");//文本框内默认文字
		jb0 = new JButton("浏览");//按钮
		jb1 = new JButton("共享");
		jb2 = new JButton("上传");
		jb3 = new JButton("目录");
		jtf0.setBounds(10, 30, 200, 30);//x、y；宽、高
		jtf.setBounds(10, 80, 150, 30);
		jb0.setBounds(170, 80, 100, 30);
		jb1.setBounds(10, 130, 100, 30);
		jb2.setBounds(10, 170, 100, 30);
		jb3.setBounds(10, 210, 100, 30);
		jpb.setBounds(10, 250, 200, 20);
		this.add(jpb);//添加事件监听器
		this.add(jtf0);
		this.add(jtf);
		this.add(jb0);
		this.add(jb1);
		this.add(jb2);
		this.add(jb3);

		jb0.addActionListener(this);//添加指定的动作侦听器，以接收发自此按钮的动作事件——this
		jb1.addActionListener(this);
		jb2.addActionListener(this);
		jb3.addActionListener(this);

		new s_server().start();//由线程启动
	}
	//实现事件监听器接口中的方法
	public void actionPerformed(ActionEvent e) {
		/*当特定于组件的动作（比如被按下）发生时，
		由组件（比如 Button）生成此高级别事件。
		事件被传递给每一个 ActionListener 对象，
		这些对象是使用组件的 addActionListener 方法注册的，用以接收这类事件。
		实现 ActionListener 接口的对象在发生事件时获取此 ActionEvent */
		int result;
		File file1=null;
		File file2=null;
		if (e.getSource() == timer) {//监听到有动作发生的某个控件（定时器）
			int value = jpb.getValue();//返回进度条的当前值
			jpb.setValue(0);//将进度条回复0值
			if (value < 100) {//若当前进度未满
				value++;//自加
				jpb.setValue(value);//将进度条重新赋值
			} else {
				timer.stop();//进度条已满则停止Timer，使它停止向其监听器发送动作事件
				jb0.setText("浏览");//浏览按钮
			}
		}
		else if ((JButton) e.getSource() == jb0) {//浏览按钮被按下
			jfc.setApproveButtonText("确定");//文件选择窗口中的 approvebutton 内使用的文本
			jfc.setDialogTitle("选择文件窗口");//文件选择窗口中的标题栏
			result = jfc.showOpenDialog(this);
			//null———显示在当前电脑显示器屏幕的中央
			//this———显示在当前你编写的程序屏幕中央
			//返回一个DialogResult，对应你按的按钮【如：单击“关闭”按钮会隐藏窗体，并将DialogResult属性设置为DialogResult.Cancel  】
			if (result == JFileChooser.APPROVE_OPTION) // 当用户按下文件选择窗口中的确定
			{
				st = new String(jfc.getSelectedFile().getPath());//文件路径
				//getSelectedFile()返回选中的文件
				//getPath()将此抽象路径名转换为一个路径名字符串
				jb0.setText(st);
				st1 = new String(jfc.getSelectedFile().getName());//文件名
				jtf.setText("文件" + st1 + "待发送！");

				try {
					//用来发送和接收数据报包的套接字（UDP）
					//DatagramSocket(int port, InetAddress laddr) 创建数据报套接字，将其绑定到指定的本地地址。
					DatagramSocket ds = new DatagramSocket();
					/*数据报包（2）
					1、DatagramPacket(byte[] buf, int offset, int length, InetAddress address, int port) 
					构造数据报包，用来将长度为 length 偏移量为 offset 的包发送到指定主机上的指定端口号。 
					2、DatagramPacket(byte[] buf, int length, InetAddress address, int port) 
					构造数据报包，用来将长度为 length 的包发送到指定主机上的指定端口号。 
					3、DatagramPacket(byte[] buf, int length) 
					构造 DatagramPacket，用来接收长度为 length 的数据包
					*/
					DatagramPacket dp = new DatagramPacket(jfc.getSelectedFile().getName().getBytes(),jfc.getSelectedFile().getName().getBytes().length,InetAddress.getByName("255.255.255.255"),3019);
					//255.255.255.255广播地址
					ds.send(dp);//发送数据包
					ds.close();//关闭套接字
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}

		else if ((JButton) e.getSource() == jb1) {//共享按钮被按下
			jb1.setEnabled(false);//设置控件是否可用：false——不可用
			new Thread(){

				@Override
				public void run() {
					// 创建文件流用来读取文件中的数据
					try {
						File file = new File(jb0.getText());//获取了文件路径
						FileInputStream fos = new FileInputStream(file);//文件字节输入流，对文件数据以字节的形式进行读取操作
						// 创建网络服务器接受客户请求
						ServerSocket ss = new ServerSocket(3108);//用于服务器端，监听客户端连接：端口——3108
						Socket client = ss.accept();//accept()方法处于阻塞状态，直到有客户端连接，创建一个服务端Socket，与客户端交互
						timer.start();//启动 Timer，使它开始向其监听器发送动作事件

						// 创建网络输出流并提供数据包装器
						OutputStream netOut = client.getOutputStream();//获取输出流：客户端发来的数据。
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
						jb1.setEnabled(true);//按钮更新状态：true——可使用
					} catch (FileNotFoundException e1) {//找不到文件或此路径为目录
						JOptionPane.showMessageDialog(null, "请选择文件", "错误",
								JOptionPane.ERROR_MESSAGE);
						jb1.setEnabled(true);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "IO异常", "错误",
								JOptionPane.ERROR_MESSAGE);
						jb1.setEnabled(true);
					}
				}
			}.start();//运行此线程
		}
		else if ((JButton) e.getSource() == jb2){//上传按钮
			jb2.setEnabled(false);
			new Thread(){

				@Override
				public void run() {
					try {
						File file = new File(jb0.getText());//获取了文件路径
						FileInputStream fis = new FileInputStream(file);//文件字节输入流，对文件数据以字节的形式进行读取操作
						Socket client = new Socket(url,3120);
						timer.start();//启动 Timer，使它开始向其监听器发送动作事件
						DataOutputStream dos = new DataOutputStream(client.getOutputStream()); 
						// 文件名和长度  
						dos.writeUTF(file.getName());  
						dos.flush();  
						dos.writeLong(file.length());  
						dos.flush();  
						// 开始传输文件  
						byte[] bytes = new byte[1024];  
						int length = 0;  
						long progress = 0;  
						while((length = fis.read(bytes, 0, bytes.length)) != -1) {  
							dos.write(bytes, 0, length);  
							dos.flush();  
						}  
						fis.close();  
						dos.close();  
						client.close();  
						jb2.setEnabled(true);
					} catch (FileNotFoundException e1) {//找不到文件或此路径为目录
						JOptionPane.showMessageDialog(null, "请选择文件", "错误",
								JOptionPane.ERROR_MESSAGE);
						jb2.setEnabled(true);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "IO异常", "错误",
								JOptionPane.ERROR_MESSAGE);
						jb2.setEnabled(true);
					}
				}
			}.start();
		}
		else if ((JButton) e.getSource() == jb3){//目录按钮
			try {
				// 使用本地文件系统接受网络数据并存为新文件
				file2 = new File("目录.txt");//新建一个文件并命名
                if(!file2.isFile()) {
                    file2.createNewFile();
				}
                RandomAccessFile raf = new RandomAccessFile(file2, "rw");//向该文件写入数据，设置可读可写
                // 通过Socket连接文件服务器
                Socket server = new Socket(url, 3106);
                // 创建网络接受流接受服务器文件数据
                InputStream netIn = server.getInputStream();

                InputStream in = new DataInputStream(new BufferedInputStream(
						netIn));//封装输入流
                // 创建缓冲区缓冲网络数据
                byte[] buf = new byte[20480];
                timer.start();
                int num = in.read(buf);
                while (num != (-1)) {// 是否读完所有数据
                    raf.write(buf, 0, num);// 将数据写往文件
                    raf.skipBytes(num);// 顺序写文件字节
                    num = in.read(buf);// 继续从网络中读取文件
                }
                in.close();
				raf.close();
				server.close();
				netIn.close();
            }
            catch (IOException q) {
                System.out.println("异常");
            }
		}
	}

	class s_server extends Thread {
        @Override
		public void run() {
			// 创建文件流用来读取文件中的数据
			try {
				// 创建网络服务器接受客户请求
				ServerSocket ss = new ServerSocket(3102);//用于服务器端，监听客户端连接：端口——3102
				Socket client = ss.accept();//accept()方法处于阻塞状态，直到有客户端连接，创建一个服务端Socket，与客户端交互
				//获得输入流
				InputStream is=client.getInputStream();
				BufferedReader br=new BufferedReader(new InputStreamReader(is));
				//读取用户输入信息
				String info=null;
				while(!((info=br.readLine())==null)){
					jtf0.setText(info);
				}
				br.close();
            	is.close();
            	client.close();
            	ss.close();
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