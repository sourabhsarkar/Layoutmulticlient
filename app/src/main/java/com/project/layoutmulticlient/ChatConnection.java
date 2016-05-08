package com.project.layoutmulticlient;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ChatConnection {

    private ChatServer mChatServer;
    ArrayList<CommonChat> commonChats = new ArrayList<CommonChat>();

    ArrayList<Score> scoreList = new ArrayList<Score>();

    //for passsing normal messages
    public Msg createMessage(String key, String message) {
        return new Msg(key,message);
    }

    //for passing questions
    public Msg createMessage(String key, ArrayList<Question> questions) {
        return new Msg(key,questions);
    }

    private static final String TAG = "ChatConnection";

    private int mPort = -1;
    Context mContext;

    public ChatConnection(Context c) {
        mContext = c;
        //if the user is a server create the server socket
        if(NsdChatActivity.mUserChoice.equals("server")) {
            mChatServer = new ChatServer();
        }
    }

    public void tearDown() {
        if(mChatServer!=null)
            mChatServer.tearDown();
        for (CommonChat chatClient : commonChats) {
            chatClient.tearDown();
        }
    }

    //Creating and storing CommonChat objects
    public void commonConnection(InetAddress address, int port, Socket s) {
        for (CommonChat obj : commonChats) {
            if(obj.mAddress == address) {
                commonChats.remove(obj);
            }
        }
        CommonChat mChatClient = new CommonChat(address, port, s);
        commonChats.add(mChatClient);
    }

    public void sendAllMessage(String key, String msg) {
        for (CommonChat chatClient : commonChats) {
            if(chatClient.pass_verified)
                chatClient.sendMessage(createMessage(key, msg));
        }
    }

    public void sendAllMessage(String key, ArrayList<Question> quesList) {
        for (CommonChat chatClient : commonChats) {
            if(chatClient.pass_verified)
                chatClient.sendMessage(createMessage(key, quesList));
        }
    }

    public int getLocalPort() {
        return mPort;
    }

    public void setLocalPort(int port) {
        mPort = port;
    }

    private class ChatServer {

        ArrayList<ServerThread> serverThreads = new ArrayList<ServerThread>();
        ServerSocket mServerSocket = null;
        //Thread mThread = null;

        //Creating the thread for chat
        public ChatServer() {
            new ThreadHandler().start();
        }

        public class ThreadHandler extends Thread
        {
            @Override
            public void run() {
                Socket sv_soc= null;

                // Since discovery will happen via Nsd, we don't need to care which port is
                // used.  Just grab an available one  and advertise it via Nsd.
                try {
                    //Creating server socket
                    mServerSocket = new ServerSocket(0);
                    setLocalPort(mServerSocket.getLocalPort());
                    Log.d(TAG, "ServerSocket Created, awaiting connection");
                } catch (IOException e) {
                    Log.e(TAG, "Error creating ServerSocket: ", e);
                    e.printStackTrace();
                }
                while(Thread.currentThread().isAlive()) {
                    try {
                        //Accepting client
                        if(!mServerSocket.isClosed()) {
                            sv_soc = mServerSocket.accept();
                            Log.d(TAG, "Connected..." + sv_soc.getInetAddress() + " " + sv_soc.getPort());
                        }
                        else {
                            Log.e(TAG,"ServerSocket is null!");
                            break;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught!", e);
                    }
                    //Starting thread for each client
                    if (sv_soc != null) {
                        ServerThread sv_thread = new ServerThread(sv_soc);
                        serverThreads.add(sv_thread);
                        sv_thread.t.start();
                    }
                }
            }
        }

        //interrupts all server threads
        public void tearDown() {
            for (ServerThread serverThread : serverThreads)
                serverThread.t.interrupt();
            try {
                mServerSocket.close();
            } catch (IOException ioe) {
                Log.e(TAG, "Error when closing server socket.");
            }
        }

        class ServerThread implements Runnable {
            Socket sv_soc;
            Thread t;
            public ServerThread(Socket s) {
                sv_soc = s;
                t=new Thread(this);
            }
            @Override
            public void run() {
                int port = sv_soc.getPort();
                InetAddress address = sv_soc.getInetAddress();
                Log.d(TAG, "commonConnection being called from ServerThread!");
                commonConnection(address, port, sv_soc);
            }
        }
    }

    //Class handle the chatting
    private class CommonChat {

        private InetAddress mAddress;
        private int PORT;

        private final String CLIENT_TAG = "CommonChat";

        public String username = null;
        public String email = null;
        public String ph_no = null;
        public boolean pass_verified = false;
        public boolean ques_received = false;
        public boolean contest_ended = false;

        public int score;

        private Thread mSendThread;
        private Thread mRecThread;
        Socket sv_soc = null;

        ObjectOutputStream out = null;

        Intent intent = new Intent(mContext, ContestRules.class);

        //Constructor
        public CommonChat(InetAddress address, int port, Socket s) {

            Log.d(CLIENT_TAG, "Creating CommonChat");
            mAddress = address;
            PORT = port;
            sv_soc = s;

            mSendThread = new Thread(new SendingThread());
            mSendThread.start();
        }

        //Sending Thread
        class SendingThread implements Runnable {

            BlockingQueue<String> mMessageQueue;
            private int QUEUE_CAPACITY = 10;

            public SendingThread() {
                mMessageQueue = new ArrayBlockingQueue<String>(QUEUE_CAPACITY);
            }

            @Override
            public void run() {
                try {
                    //Log.d(TAG, mAddress + " " + PORT);
                    if(NsdChatActivity.mUserChoice.equals("client")) {
                        //Creating Client socket
                        sv_soc = new Socket(mAddress, PORT);
                        Log.d(CLIENT_TAG, "Client-side socket initialized.");
                        sendMessage(createMessage("clientpass",NsdChatActivity.client_pass));
                        sendMessage(createMessage("clientname",NsdChatActivity.clientName));
                        sendMessage(createMessage("clientemail",NsdChatActivity.clientEmail));
                        sendMessage(createMessage("clientphno",NsdChatActivity.clientPhNo));
                    }
                    else {
                        Log.d(CLIENT_TAG, "Socket already initialized. skipping!");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to initialize Client-side socket!", e);
                    e.printStackTrace();
                }
                mRecThread = new Thread(new ReceivingThread());
                mRecThread.start();
            }
        }

        //Receiving Thread
        class ReceivingThread implements Runnable {

            ObjectInputStream input = null;
            @Override
            public void run() {

                try {
                    input = new ObjectInputStream(sv_soc.getInputStream());
                    while (!Thread.currentThread().isInterrupted()) {
                        Msg message = (Msg) input.readObject();
                        if (message != null) {
                            Log.d(CLIENT_TAG, "Read from the stream: " + message.getKey() + ": " + message.getMessage());
                            if (message.getKey().equals("end")) {
                                Log.d(CLIENT_TAG, "The end!");
                                break;
                            }
                            if (NsdChatActivity.mUserChoice.equals("server")) {
                                if (message.getKey().equals("clientpass")) {
                                    if (!NsdChatActivity.server_pass.equals(message.getMessage())) {
                                        Log.d(TAG, "pass client: " + message.getMessage());
                                        sendMessage(createMessage("passcheck", "mismatch"));
                                    }
                                    else {
                                        pass_verified = true;
                                        sendMessage(createMessage("passcheck", "matched"));
                                        sendMessage(createMessage("contest_details", NsdChatActivity.con_details_str));
                                    }
                                }
                                else if (message.getKey().equals("clientname")) {
                                    username = message.getMessage();
                                }
                                else if (message.getKey().equals("clientemail")) {
                                    email = message.getMessage();
                                }
                                else if (message.getKey().equals("clientphno")) {
                                    ph_no = message.getMessage();
                                }
                                else if(message.getKey().equals("score")) {
                                    contest_ended = true;
                                    score = Integer.parseInt(message.getMessage());

                                    Score scoreItem = new Score();
                                    scoreItem.setUsername(username);
                                    scoreItem.setMarks(score);
                                    scoreList.add(scoreItem);
                                    Collections.sort(scoreList);

                                    if(ContestantResultList.context != null)
                                        ((ContestantResultList) ContestantResultList.context).runOnUiThread(new Runnable() {
                                            public void run() {
                                                ContestantResultList.adapter.notifyDataSetChanged();
                                            }
                                        });
                                }
                            }
                            else if (NsdChatActivity.mUserChoice.equals("client")) {
                                if (message.getKey().equals("passcheck")) {
                                    if (message.getMessage().equals("matched")) {
                                        pass_verified = true;
                                    }
                                    else {
                                        ((NsdChatActivity) mContext).runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(mContext, "Password mismatch!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                                else if(message.getKey().equals("ques")) {
                                    if(!ques_received) {
                                        ques_received = true;
                                        intent.putExtra("quesMsg", message);
                                        mContext.startActivity(intent);
                                    }
                                }
                                else if(message.getKey().equals("timer")) {
                                    intent.putExtra("timer", Long.parseLong(message.getMessage()));
                                }
                                else if(message.getKey().equals("contest_details")) {
                                    intent.putExtra("contest_details", message.getMessage());
                                }
                            }
                        }
                    }
                    input.close();
                } catch (IOException e) {
                    Log.e(CLIENT_TAG, "Server loop error: ", e);
                } catch (ClassNotFoundException e) {
                    Log.e(CLIENT_TAG, "Class not found exception: ", e);
                }
            }
        }

        public void tearDown() {
            try {
                sv_soc.close();
            } catch (IOException ioe) {
                Log.e(CLIENT_TAG, "Error when closing server socket.");
            }
        }

        public void sendMessage(Msg m) {
            try {
                if (sv_soc == null) {
                    Log.d(CLIENT_TAG, "Socket is null!");
                }
                if(out == null)
                    out = new ObjectOutputStream(sv_soc.getOutputStream());

                out.writeObject(m);
                out.flush();
                out.reset();
            } catch (UnknownHostException e) {
                Log.d(CLIENT_TAG, "Unknown Host", e);
            } catch (IOException e) {
                Log.d(CLIENT_TAG, "I/O Exception", e);
            } catch (Exception e) {
                Log.d(CLIENT_TAG, "Error3", e);
            }
            Log.d(CLIENT_TAG, "Client sent message: " + m.getKey() + ": " + m.getMessage());
        }
    }
}