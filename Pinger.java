package ru.nppame.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.Date;

public class Pinger {
  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println(ping("192.168.0.77", "192.168.0.111"));
  }
  
  public static boolean ping(String host) throws IOException, InterruptedException {
    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    ProcessBuilder processBuilder = new ProcessBuilder(new String[] { "ping", isWindows ? "-n" : "-c", "1", host });
    Process proc = processBuilder.start();
    int returnVal = proc.waitFor();
    return (returnVal == 0);
  }
  
  public static boolean ping(String sourceIp, String hostIp) throws IOException, InterruptedException {
    int timer = 0;
    ProcessBuilder processBuilder = new ProcessBuilder(new String[] { "ping", "-S", sourceIp, hostIp });
    Process proc = processBuilder.start();
    int returnVal = proc.waitFor();
    InputStream is = proc.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    String line;
    while ((line = br.readLine()) != null)
      System.out.println(line.toCharArray()); 
    System.out.println("Program terminated!");
    return (returnVal == 0);
  }
  
  public static long pseudoPing(String hostAddress) {
    InetAddress inetAddress = null;
    try {
      inetAddress = InetAddress.getByName(hostAddress);
    } catch (UnknownHostException e) {
      System.out.println("Problem, unknown host:");
      e.printStackTrace();
      return -1L;
    } 
    try {
      Date start = new Date();
      if (inetAddress.isReachable(5000)) {
        Date stop = new Date();
        return stop.getTime() - start.getTime();
      } 
    } catch (IOException e) {
      System.out.println("Problem, a network error has occurred:");
      e.printStackTrace();
      return -1L;
    } catch (IllegalArgumentException e) {
      System.out.println("Problem, timeout was invalid:");
      e.printStackTrace();
      return -1L;
    } 
    return -1L;
  }
  
  public static long pseudoPing(String hostAddress, Integer port) {
    InetAddress inetAddress = null;
    InetSocketAddress socketAddress = null;
    SocketChannel sc = null;
    long timeToRespond = -1L;
    try {
      inetAddress = InetAddress.getByName(hostAddress);
    } catch (UnknownHostException e) {
      e.printStackTrace();
      System.out.println("Problem, unknown host:");
      return -1L;
    } 
    try {
      socketAddress = new InetSocketAddress(inetAddress, port.intValue());
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      System.out.println("Problem, port may be invalid:");
      return -1L;
    } 
    try {
      sc = SocketChannel.open();
      sc.configureBlocking(true);
      Date start = new Date();
      if (sc.connect(socketAddress)) {
        Date stop = new Date();
        timeToRespond = stop.getTime() - start.getTime();
      } 
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Problem, connection could not be made:");
      return -1L;
    } 
    try {
      sc.close();
    } catch (IOException e) {
      e.printStackTrace();
    } 
    return timeToRespond;
  }
  
  public static boolean irPing(String sourceIP, String hostIP) {
    return false;
  }
}
