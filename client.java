import java.net.*;
import java.io.*;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Base64;

public class client extends JFrame {
 
    Socket socket;

    BufferedReader br;
    PrintWriter out;

    // declare the component
    private JLabel heading = new JLabel("Private Room");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);


    public client(){
        try {
            String str = takingInputGUI();
            socket = new Socket(str,7777);
            System.out.println("connection done");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

           
            createGUI();
            handleEvent();
            startReading();
            // startWriting();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String takingInputGUI() {
        String ip = JOptionPane.showInputDialog("server ip address");
        return ip;
    }

    // creating the GUI
    private void createGUI(){

        String name = JOptionPane.showInputDialog("enter your name");
        this.setTitle( name);
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //CODING FOR COMPONENT

        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setIcon(new ImageIcon("clogo.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);



        //frame layout area
        this.setLayout(new BorderLayout());

        //adding component to frame
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);
        this.setVisible(true);
    }

    private void handleEvent(){
        messageInput.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
                if(e.getKeyCode()==10){
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me:" + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
            
        });
    }


    public void startReading(){

        //thread-read karke deta rahega
        Runnable r1=()->{

            System.out.println("reader started");
            try{
            while(true){
                String msg = br.readLine();
                
                if(msg.equals("exit")){
                   // System.out.println("server terminated the chatt");
                    JOptionPane.showMessageDialog(this,"server terminated the chat");
                    messageInput.setEnabled(false);
                    socket.close();
                    break;
                }
               // System.out.println("server :"+ msg);
               messageArea.append("server:" + msg + "\n");
            }
        }catch( Exception e){
            // e.printStackTrace();
            System.out.println("connection is closed");
        }
        };
        new Thread(r1).start();
    }
    public void startWriting(){
        // thread-data user lega aur clinet ko bejega
        Runnable r2=()->{
            System.out.println("writer started");
            try{
            while(!socket.isClosed()){
                
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                   
                    out.println(content);
                    out.flush();

                    if(content.equals("exit")){
                        socket.close();
                        break;
                    }
            }
           
        }catch (Exception e){
           // e.printStackTrace();
           System.out.println("connection is closed");
        }
        };
        new Thread(r2).start();
    }
    public static void main(String[] args) {
        System.out.println("this is client ..going to connect");
        new client();
    }
};
