package com.project.layoutmulticlient;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ChatConnection {

    private Handler mUpdateHandler;
    private ChatServer mChatServer;
    ArrayList<CommonChat> commonChats = new ArrayList<CommonChat>();
    ArrayList<Question> sampleQuesList = new ArrayList<Question>();

    public Msg createMessage(String key, String message) {
        return new Msg(key,message);
    }

    public Msg createMessage(String key, ArrayList<Question> questions) {
        return new Msg(key,questions);
    }

    private static final String TAG = "ChatConnection";

    private int mPort = -1;
    Context mContext;

    public ChatConnection(Handler handler, Context c) {
        mUpdateHandler = handler;
        mContext = c;
        //if the user is a server create the server socket
        if(NsdChatActivity.mUserChoice.equals("server")) {
            sampleQuesList.add(new Question("ques statement 1?", "option-1", "option-2", "option-3", "option-4"));
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
    /*
    public void sendMessage(String msg) {
        for (CommonChat chatClient : commonChats) {
            chatClient.sendMessage("Msg", msg);
        }
    }
    */
    public int getLocalPort() {
        return mPort;
    }

    public void setLocalPort(int port) {
        mPort = port;
    }

    /*
    public synchronized void updateMessages(String Msg, boolean local) {
        Log.e(TAG, "Updating message: " + Msg);

        if (local) {
            Msg = "sent: " + Msg;
        } else {
            Msg = "received: " + Msg;
        }

        Bundle messageBundle = new Bundle();
        messageBundle.putString("Msg", Msg);

        Message message = new Message();
        message.setData(messageBundle);
        mUpdateHandler.sendMessage(message);

    }
    */

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
            /*
            public void doSomethingOnAllThreads() {
                for (ServerThread serverThread : serverThreads) {
                    serverThread.otherMethod();
                }
            }
            */
        }

        public void tearDown() {
            //interrupt all server threads
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
                //setSocket(sv_soc);
                //if (mChatClient == null) {
                int port = sv_soc.getPort();
                InetAddress address = sv_soc.getInetAddress();
                Log.d(TAG, "commonConnection being called from ServerThread!");
                commonConnection(address, port, sv_soc);
                //}
            }
            /*
            @Override
            public void finalize() throws Throwable {
                Log.d(TAG, "Finalize");
                super.finalize();
            }
            */
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

        private Thread mSendThread;
        private Thread mRecThread;
        Socket sv_soc = null;

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
                        ((NsdChatActivity)mContext).runOnUiThread(new Runnable() {
                            public void run() {
                                ((NsdChatActivity) mContext).progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(mContext, "Waiting for password verification...", Toast.LENGTH_SHORT).show();
                            }
                        });
                        sendMessage(createMessage("passclient",NsdChatActivity.client_pass));
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
                /*
                while (Thread.currentThread().isAlive()) {
                    try {
                        String msg = mMessageQueue.take();
                        sendMessage("Msg", msg);
                    } catch (InterruptedException ie) {
                        Log.d(CLIENT_TAG, "Message sending loop interrupted, exiting");
                    }
                }
                */
            }
        }

        //Receiving Thread
        class ReceivingThread implements Runnable {

            @Override
            public void run() {

                //BufferedReader input;
                try {
                    /*
                    input = new BufferedReader(new InputStreamReader(
                            sv_soc.getInputStream()));
                    */
                    ObjectInputStream input = new ObjectInputStream(sv_soc.getInputStream());
                    while (!Thread.currentThread().isInterrupted()) {
                        Msg message = (Msg) input.readObject();
                        if (message != null) {
                            Log.d(CLIENT_TAG, "Read from the stream: " + message.getKey() + ": " + message.getMessage());
                            if (message.getKey().equals("end")) {
                                Log.d(CLIENT_TAG, "The end!");
                                break;
                            }
                            if (NsdChatActivity.mUserChoice.equals("server")) {
                                if (message.getKey().equals("passclient")) {
                                    if (!NsdChatActivity.server_pass.equals(message.getMessage())) {
                                        /*
                                        ((NsdChatActivity)mContext).runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(mContext, "Password mismatch!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        */
                                        Log.d(TAG, "pass client: " + message.getMessage());
                                        sendMessage(createMessage("passcheck", "mismatch"));
                                        //commonChats.remove(CommonChat.this);
                                        //Thread.currentThread().interrupt();
                                    }
                                    else {
                                        pass_verified = true;
                                        sendMessage(createMessage("passcheck", "matched"));
                                        sendMessage(createMessage("passcheck", "mismatch"));
                                        //sendMessage(createMessage("ques",sampleQuesList));
                                    }
                                }
                            }
                            //hello
                            else if (NsdChatActivity.mUserChoice.equals("client")) {
                                if (message.getKey().equals("passcheck")) {
                                    if (message.getMessage().equals("matched")) {
                                        pass_verified = true;
                                        ((NsdChatActivity) mContext).runOnUiThread(new Runnable() {
                                            public void run() {
                                                ((NsdChatActivity) mContext).progressBar.setVisibility(View.GONE);
                                                Toast.makeText(mContext, "Password matched successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        /*
                                        Intent intent = new Intent(mContext, Participant_details.class);
                                        mContext.startActivity(intent);
                                        */
                                    }
                                    else {
                                        ((NsdChatActivity) mContext).runOnUiThread(new Runnable() {
                                            public void run() {
                                                ((NsdChatActivity) mContext).progressBar.setVisibility(View.GONE);
                                                Toast.makeText(mContext, "Password mismatch!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                                else if(message.getKey().equals("ques")) {
                                    ((NsdChatActivity) mContext).runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(mContext, "Ques received!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Intent intent = new Intent(mContext, display_question.class);
                                    intent.putExtra("quesMsg",message);
                                    mContext.startActivity(intent);
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
                /*
                else if (sv_soc.getOutputStream() == null) {

                    Log.d(CLIENT_TAG, "Socket output stream is null!");
                }


                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(sv_soc.getOutputStream())), true);
                */
                ObjectOutputStream out = new ObjectOutputStream(sv_soc.getOutputStream());

                out.writeObject(m);
                out.flush();
                //updateMessages(Msg, true);
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