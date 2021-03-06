/*
This file is part of Socks via HTTP.

This package is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Socks via HTTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Socks via HTTP; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

// Title :        ThreadBind.java
// Version :      1.2
// Copyright :    Copyright (c) 2001-2002
// Author :       Florent CUETO  & Sebastien LEBRETON <socksviahttp@cqs.dyndns.org>
// Description :  Creates a SocketServer and waits for an incoming connection

package socksviahttp.server.engine;

import java.net.*;
import java.io.*;
import socksviahttp.core.net.*;

public class ThreadBind extends Thread
{
  public static final int LISTEN_TIMEOUT = 120000; // 2mn
  protected ServerSocket serverSocket;
  protected String remoteIp;

  protected int localPort = -1;
  protected String localAddress = null;
  protected boolean waiting = true;
  protected Connection conn = null;

  public ThreadBind(String remoteIp)
  {
    super();

    this.remoteIp = remoteIp;
    try
    {
      serverSocket = new ServerSocket(0);
      serverSocket.setSoTimeout(LISTEN_TIMEOUT);
      localPort = serverSocket.getLocalPort();
      localAddress = serverSocket.getInetAddress().getHostAddress();
    }
    catch (IOException e)
    {
      //configuration.printlnError("Unexpected Exception while creating ServerSocket in ThreadBind : " + e);
      // TO DO : throw an exception
    }
  }

  public void run()
  {
    try
    {
      Socket s = serverSocket.accept();
      if ((!s.getInetAddress().getHostAddress().equals(remoteIp)))
      {
        // Log
        //configuration.printlnWarn("<CLIENT> Incoming Socks connection refused from IP " + s.getInetAddress().getHostAddress());

        // Close the socket
        s.close();
      }
      else
      {
        //configuration.printlnInfo("<CLIENT> Incoming Socks connection accepted from IP " + s.getInetAddress().getHostAddress());
        conn = new Connection(s);
      }
      waiting = false;
    }
    catch (InterruptedIOException iioe)
    {
      // TO DO : timeout
    }
    catch (Exception e)
    {
      //configuration.printlnError("<CLIENT> Unexpected Exception while listening in SocksConnectionServer : " + e);
    }

    try
    {
      // Close the ServerSocket
      serverSocket.close();
    }
    catch (IOException e){}
  }
  public String getLocalAddress()
  {
    return localAddress;
  }
  public int getLocalPort()
  {
    return localPort;
  }
  public Connection getConn()
  {
    return conn;
  }
  public boolean isWaiting()
  {
    return waiting;
  }
}
